package com.example.mafia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ActividadPrincipal extends AppCompatActivity {

    TextView etiquetaMonitoreo, etiquetaDistancia;
    Button seguimiento, datos, borrarDatos, compartir;
    CheckBox alarmaPantalla, alarmaProximidad;
    SeekBar distanciaProximidad;
    LocationManager locationManager;
    LocationListener locationListener;
    double latitud,longitud,altitud;


    private static final long TIEMPO_REFRESCO = 2500;
    private static final int PERMISO_GPS = 15;
    private  static final String ALARMA_SONIDO = "ALARMA_SONIDO";
    private  static final String ALARMA_PROXIMIDAD = "ALARMA_PROXIMIDAD";
    private  static final String MAXIMA_DISTANCIA = "MAXIMA_DISTANCIA";
    private  static final String LONGITUD_BASE = "LONGITUD_BASE";
    private  static final String LATITUD_BASE = "LATITUD_BASE";
    private static String ESTADO_SEGUIMIENTO = "ESTADO_SEGUIMIENTO";
    private static String LATITUD = "LATITUD";
    private static String LONGITUD = "LONGITUD";
    private static String ALTITUD = "ALTITUD";
    private static final String PREFERENCIAS = "preferencias";
    public static ActividadPrincipal ap2;
    ManejadorBD manejadorBD = new ManejadorBD(this);
    ActividadPrincipal ap = this;
    int maximoDistancia;
    float latitudBase,longitudBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_principal);
        ap2=this;



        etiquetaMonitoreo = findViewById(R.id.etiquetaMonitoreo);
        etiquetaDistancia = findViewById(R.id.textViewDistancia);
        seguimiento = findViewById(R.id.activarMonitoreo);
        datos = findViewById(R.id.botonDatos);
        borrarDatos = findViewById(R.id.botonBorrarDatos);
        compartir = findViewById(R.id.botonCompartir);
        alarmaPantalla = findViewById(R.id.checkAlarmaPantalla);
        alarmaProximidad = findViewById(R.id.checkAlarmaPRoximidad);
        distanciaProximidad = findViewById(R.id.seekBarDistancia);
        etiquetaDistancia.setText("0");
        distanciaProximidad.setProgress(0);
        etiquetaMonitoreo.setText("Monitoreo inactivo");

        if(isMyServiceRunning(MiServicioIntenso.class)){

            etiquetaMonitoreo.setText("Monitoreo activo");

        }


        //manejadorBD.insertar("test", "test", "test", "test");


        seguimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isMyServiceRunning(MiServicioIntenso.class)){

                    etiquetaMonitoreo.setText("Monitoreo inactivo");





                }else{

                    etiquetaMonitoreo.setText("Monitoreo activo");
                    MiServicioIntenso.encolarTrabajo(getApplicationContext(), new Intent(), ap);
                    tets();

                }




            }
        });

        borrarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(manejadorBD.borrar()){
                    Toast.makeText(ap, "Datos borrados correctamente", Toast.LENGTH_SHORT).show();
                }

            }
        });

        alarmaProximidad.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(maximoDistancia==0){
                    maximoDistancia=20;  //Default 20 metros
                }

                if(isChecked){

                    comprobarAlarmaProximidad(true,maximoDistancia);

                }else{

                    comprobarAlarmaProximidad(false,maximoDistancia);

                }



            }
        });

        alarmaPantalla.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    comprobarAlarmaSonido(true);

                }else{

                    comprobarAlarmaSonido(false);

                }

            }
        });

        distanciaProximidad.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                etiquetaDistancia.setText(""+progress);
                maximoDistancia=progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        datos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intento = new Intent(ActividadPrincipal.this,activityMostrarBD.class);
                startActivity(intento);

            }
        });

        compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(ActividadPrincipal.this, "hola", Toast.LENGTH_SHORT).show();
                pedirPermisos();
                exportarCSV();

            }
        });



    }

    void crearCanalNotificaciones() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel =
                    new NotificationChannel(MiServicioIntenso.ID_CHANNEL, "Queremos Marcha",
                            NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setShowBadge(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

    }

    public void tets() {

        Context context;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                latitudBase = (float)location.getLatitude();
                longitudBase = (float)location.getLongitude();

                editor.putFloat(LATITUD,(float)location.getLatitude());
                editor.putFloat(LONGITUD,(float)location.getLongitude());
                editor.putFloat(ALTITUD,(float)location.getAltitude());
                editor.putFloat(LATITUD_BASE,latitudBase);
                editor.putFloat(LONGITUD_BASE,longitudBase);
                editor.apply();


                Log.i("Estado",""+sharedPreferences.getFloat(LATITUD,0)+""+sharedPreferences.getFloat(LONGITUD,0)
                            +""+sharedPreferences.getFloat(ALTITUD,0));

                /*latitud = location.getLatitude();
                longitud = location.getLongitude();
                altitud = location.getAltitude();*/

            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISO_GPS);
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
                TIEMPO_REFRESCO, 0, locationListener);


    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISO_GPS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "No tienes persmiso", Toast.LENGTH_SHORT).show();
                } else {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            TIEMPO_REFRESCO, 0, locationListener);
                }
            } else {
                Toast.makeText(this, "Debes darme permiso para continuar", Toast.LENGTH_SHORT).show();
            }

        }


    }

    public void comprobarAlarmaSonido(boolean sonido){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ALARMA_SONIDO,sonido);
        editor.apply();

    }

    public void comprobarAlarmaProximidad(boolean proximidad, int maximo){

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(ALARMA_PROXIMIDAD,proximidad);
        editor.putInt(MAXIMA_DISTANCIA,maximo);
        editor.apply();

    }


    private boolean isMyServiceRunning(Class<?> MiServicioIntenso) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MiServicioIntenso.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    public void exportarCSV() {

        File carpeta = new File(Environment.getExternalStorageDirectory() + "/ExportarSQLiteCSV");
        String archivoAgenda = carpeta.toString() + "/" + "Movimientos.csv";

        boolean isCreate = false;
        if(!carpeta.exists()) {
            isCreate = carpeta.mkdir();
        }

        try {
            FileWriter fileWriter = new FileWriter(archivoAgenda);

            SQLiteDatabase db = manejadorBD.getWritableDatabase();



            Cursor fila = db.rawQuery("select * from LOCALIZACION", null);

            if(fila != null && fila.getCount() != 0) {
                fila.moveToFirst();
                do {

                    fileWriter.append(fila.getString(0));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(1));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(2));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(3));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(4));
                    fileWriter.append(",");
                    fileWriter.append(fila.getString(5));
                    fileWriter.append("\n");

                } while(fila.moveToNext());
            } else {
                Toast.makeText(this, "No hay registros.", Toast.LENGTH_LONG).show();
            }

            db.close();
            fileWriter.close();
            Toast.makeText(this, "SE CREO EL ARCHIVO CSV EXITOSAMENTE", Toast.LENGTH_LONG).show();

            /*Intent intento = new Intent();
            intento.setAction(Intent.ACTION_SEND);
            intento.setPackage("com.whatsapp");
            intento.setType("text/plain");
            intento.putExtra(Intent.EXTRA_FROM_STORAGE,Uri.parse(archivoAgenda));
            startActivity(intento);*/

        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    public void pedirPermisos() {
        // PERMISOS PARA ANDROID 6 O SUPERIOR
        if(ContextCompat.checkSelfPermission(ActividadPrincipal.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    ActividadPrincipal.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0
            );

        }
    }



}



