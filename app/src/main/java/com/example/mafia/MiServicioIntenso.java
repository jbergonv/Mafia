package com.example.mafia;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

import static androidx.core.content.ContextCompat.getSystemService;

public class MiServicioIntenso extends JobIntentService {
String LOG_TAG = "MiServicioIntenso";
    static final int JOB_ID = 12111;
    static final int ID = 1;
    static final String ID_CHANNEL = "nombreCanal" ;
    private static final long TIEMPO_REFRESCO = 500;
    private static final int PERMISO_GPS = 15;
    private static String LATITUD = "LATITUD";
    private static String LONGITUD = "LONGITUD";
    private static String ALTITUD = "ALTITUD";
    private static final String PREFERENCIAS = "preferencias";
    ManejadorBD manejadorBD = new ManejadorBD(this);
    double latitud,longitud,altitud=0;
    LocationManager locationManager;
    LocationListener locationListener;
    static Context contextoPrincipal;
    OnBateriaCambia onBateriaCambia = new OnBateriaCambia();


    public MiServicioIntenso() {
    }


    static void encolarTrabajo(Context context, Intent work){
        contextoPrincipal=context;
        enqueueWork(context, MiServicioIntenso.class, JOB_ID, work);

    }

    static void borrarBD(){

        MiServicioIntenso miServicioIntenso = new MiServicioIntenso();

        ManejadorBD manejadorBD = new ManejadorBD(miServicioIntenso);
        manejadorBD.borrar();

    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED");
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        intentFilter.addAction("android.intent.action.BATTERY_LOW");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        getBaseContext().registerReceiver(onBateriaCambia,intentFilter);

        while(true){
            Log.d(LOG_TAG, "Comienzo a currar");
            mandarNotificacion(getApplicationContext());
            try {
                Thread.sleep(1000*30);
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

        intent.putExtra("MENSAJE", "El número es: "+random.nextInt(100000));


        PendingIntent pendingIntent = PendingIntent.getActivity(applicationContext,
                ID+random.nextInt(10), intent, 0);


        Notification notification = new NotificationCompat.Builder(applicationContext, ID_CHANNEL)

        .setContentTitle("Notificación de prueba").setContentText("Un texto divertido").
                        setSmallIcon(R.drawable.ic_launcher_background).
                setContentIntent(pendingIntent).build();

        notificationManager.notify(1,notification);

    }

    class OnBateriaCambia extends BroadcastReceiver {



        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)){
                Log.i("ESTADO", "Cable conectado");
                insertar("Cable conectado");

            }else if (intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)){
                Log.i("ESTADO", "Cable desconectado");
                Toast.makeText(context, "Cable desconectado", Toast.LENGTH_SHORT).show();
            }else if(intent.getAction().equals(Intent.ACTION_BATTERY_LOW)){
                Log.i("ESTADO", "Batería baja");
            }else if(intent.getAction().equals("android.net.wifi.STATE_CHANGE")){
                Log.i("ESTADO", "Cambio wifi");
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(500);
                }
            }else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)){

                Log.i("ESTADO","Pantalla encendida");
                //permisosGPS();
                insertar("Pantalla encendida");


            }
        }
    }

    public void permisosGPS() {

        /*locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                latitud = location.getLatitude();
                longitud = location.getLongitude();
                altitud = location.getAltitude();

            }
        };*/


        /*if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(actividadPrincipal, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISO_GPS);
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                TIEMPO_REFRESCO, 0, locationListener);*/


    }




    /*public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISO_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "No tienes persmiso", Toast.LENGTH_SHORT).show();
                }else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            TIEMPO_REFRESCO, 0, locationListener);
                }
            }else{
                Toast.makeText(this, "Debes darme permiso para continuar", Toast.LENGTH_SHORT).show();
            }

        }
    }*/

    public void insertar(String motivo){


        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        manejadorBD.insertar("fecha","bateria",""+sharedPreferences.getFloat(LATITUD,0)+""+sharedPreferences.getFloat(LONGITUD,0)
                +""+sharedPreferences.getFloat(ALTITUD,0),"direccion",motivo);


    }



}