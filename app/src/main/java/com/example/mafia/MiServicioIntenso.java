package com.example.mafia;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import static androidx.core.content.ContextCompat.getSystemService;

public class MiServicioIntenso extends JobIntentService {
    String LOG_TAG = "MiServicioIntenso";
    static final int JOB_ID = 12111;
    static final int ID = 1;
    static final String ID_CHANNEL = "nombreCanal";
    private static final long TIEMPO_REFRESCO = 500;
    private static final int PERMISO_GPS = 15;
    private static String LATITUD = "LATITUD";
    private static String LONGITUD = "LONGITUD";
    private static String ALTITUD = "ALTITUD";
    private static final String LONGITUD_BASE = "LONGITUD_BASE";
    private static final String LATITUD_BASE = "LATITUD_BASE";
    private static final String PREFERENCIAS = "preferencias";
    private static final String ALARMA_SONIDO = "ALARMA_SONIDO";
    private static final String ALARMA_PROXIMIDAD = "ALARMA_PROXIMIDAD";
    private static final String MAXIMA_DISTANCIA = "MAXIMA_DISTANCIA";
    private static String ESTADO_SEGUIMIENTO = "ESTADO_SEGUIMIENTO";

    double latitud, longitud, altitud = 0;
    LocationManager locationManager;
    LocationListener locationListener;
    static Context contextoPrincipal;
    static ActividadPrincipal actividadPrincipal;
    OnBateriaCambia onBateriaCambia = new OnBateriaCambia();
    ManejadorBD manejadorBD = new ManejadorBD(actividadPrincipal);



    public MiServicioIntenso() {
    }


    static void encolarTrabajo(Context context, Intent work, ActividadPrincipal ap) {
        contextoPrincipal = context;
        actividadPrincipal = ap;

        enqueueWork(context, MiServicioIntenso.class, JOB_ID, work);


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("destruido","me he destruido");

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    static void borrarBD() {

        MiServicioIntenso miServicioIntenso = new MiServicioIntenso();

        ManejadorBD manejadorBD = new ManejadorBD(actividadPrincipal);
        manejadorBD.borrar();

    }


    @Override
    public boolean stopService(Intent name) {
        stopSelf();
        return super.stopService(name);

    }


    @Override


    protected void onHandleWork(@NonNull Intent intent) {


        IntentFilter intentFilter = new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED");
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        intentFilter.addAction("android.intent.action.BATTERY_LOW");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        getBaseContext().registerReceiver(onBateriaCambia, intentFilter);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MiServicioIntenso.this);

        while (sharedPreferences.getBoolean(ESTADO_SEGUIMIENTO,false)) {
            Log.d(LOG_TAG, "Comienzo a currar");
            mandarNotificacion(getApplicationContext());
            try {
                Thread.sleep(1000 * 30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private void mandarNotificacion(Context applicationContext) {


        NotificationManager notificationManager =
                (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(applicationContext, MainActivity.class);
        Random random = new Random();

        intent.putExtra("MENSAJE", "El número es: " + random.nextInt(100000));


        PendingIntent pendingIntent = PendingIntent.getActivity(applicationContext,
                ID + random.nextInt(10), intent, 0);


        Notification notification = new NotificationCompat.Builder(applicationContext, ID_CHANNEL)

                .setContentTitle("Notificación de prueba").setContentText("Un texto divertido").
                        setSmallIcon(R.drawable.ic_launcher_background).
                        setContentIntent(pendingIntent).build();

        notificationManager.notify(1, notification);

    }

    class OnBateriaCambia extends BroadcastReceiver {


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {


            if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                Log.i("ESTADO", "Cable conectado");
                try {
                    insertar("Cable conectado");
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                Log.i("ESTADO", "Cambio red");
                if (!comprobarConexion()) {

                    Log.i("ESTADO", "Sin conexion");
                    try {
                        insertar("Sin conexión");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {

                Log.i("ESTADO", "Pantalla encendida");
                comprobarSonido();

                //permisosGPS();
                try {
                    insertar("Pantalla encendida");
                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (intent.getAction().equals(Intent.ACTION_BATTERY_LOW)) {
                Log.i("ESTADO", "Batería baja");
                try {
                    insertar("Bateria baja");
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            }
        }


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void insertar(String motivo) throws IOException {


            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            double auxLatitud = (double) sharedPreferences.getFloat(LATITUD, 0);
            double auxLongitud = (double) sharedPreferences.getFloat(LONGITUD, 0);


            manejadorBD.insertar(getDateTime(), bateriaActual(), "" + sharedPreferences.getFloat(LATITUD, 0) + "," + sharedPreferences.getFloat(LONGITUD, 0)
                    + "," + sharedPreferences.getFloat(ALTITUD, 0), calcularLocalizacion(auxLatitud, auxLongitud), motivo); //calcularLocalizacion(auxLatitud, auxLongitud)


        }

        public boolean comprobarConexion() {

            boolean connected = false;
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network
                connected = true;
            } else {
                connected = false;
            }

            return connected;

        }

        public void comprobarSonido() {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

            if (sharedPreferences.getBoolean(ALARMA_SONIDO, false)) {

                Context context;
                MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.sonido);
                mediaPlayer.start();

            }

        }

        private String getDateTime() {

            SimpleDateFormat dateFormat = new SimpleDateFormat(

                    "dd-MM-yyyy HH:mm:ss", Locale.getDefault());

            Date date = new Date();

            return dateFormat.format(date);

        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private String bateriaActual() {

            String aux = "";

            BatteryManager bm = (BatteryManager) getSystemService(BATTERY_SERVICE);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                int percentage = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                aux = percentage + "%";
            }

            return aux;

        }


        private String calcularLocalizacion(double latitud, double longitud) throws IOException {

            String aux = "";

            try {

                Geocoder geocoder;
                List<Address> addresses;
                geocoder = new Geocoder(this, Locale.getDefault());

                addresses = geocoder.getFromLocation(latitud, longitud, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


                 aux = addresses.get(0).getLocality() + ", " + addresses.get(0).getCountryName(); //addresses.get(0).getAddressLine(0) + ", " +

                return aux;


            }catch (Exception e){

                e.printStackTrace();

            }

            return aux;
        }





/*
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public boolean checkAlarmaDistancia(double latitud, double longitud){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(sharedPreferences.getBoolean(ALARMA_PROXIMIDAD,false)){

            if(calcularDistancia(longitud,latitud)){

                Log.i("ESTADO"," Me he pasado de la distancia");
                try {
                    insertar("Distancia de seguridad sobrepasada");
                    Log.i("ESTADO","  he insertado la distancia");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return true;

            }

        }

        return false;
    }

    private boolean calcularDistancia(double longitud, double latitud){



        Log.i("ESTADO"," calculo la distancia");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        double distanciaMaxima = (double)sharedPreferences.getFloat(MAXIMA_DISTANCIA,0);
        double latitudBase = (double)sharedPreferences.getFloat(LATITUD_BASE,0);
        double longitudBase = (double)sharedPreferences.getFloat(LONGITUD_BASE,0);

        Location locationBase = new Location("");
        locationBase.setLatitude(latitudBase);
        locationBase.setLongitude(longitudBase);

        Location loc2 = new Location("");
        loc2.setLatitude(longitud);
        loc2.setLongitude(latitud);

        float distanceInMeters = locationBase.distanceTo(loc2);

        if(distanceInMeters>distanciaMaxima){
            return true;
        }else{
            return false;
        }

    }

    public double dameLatitud(){

        Log.i("ESTADO"," doy latitud");

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MiServicioIntenso.this);
        double latitud = (double)pref.getFloat(LATITUD,0);
        return latitud;

    }

    public double dameLongitud(){

        Log.i("ESTADO"," doy longitud");

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MiServicioIntenso.this);
        double longitud = (double)pref.getFloat(LONGITUD,0);
        return longitud;

    }


*/

    }
