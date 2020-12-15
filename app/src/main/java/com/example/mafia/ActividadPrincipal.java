package com.example.mafia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
    private static String ESTADO_SEGUIMIENTO = "ESTADO_SEGUIMIENTO";
    private static String LATITUD = "LATITUD";
    private static String LONGITUD = "LONGITUD";
    private static String ALTITUD = "ALTITUD";
    private static final String PREFERENCIAS = "preferencias";
    MiServicioIntenso miServicioIntenso = new MiServicioIntenso();
    ManejadorBD manejadorBD = new ManejadorBD(miServicioIntenso);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividad_principal);


        etiquetaMonitoreo = findViewById(R.id.etiquetaMonitoreo);
        etiquetaDistancia = findViewById(R.id.textViewDistancia);
        seguimiento = findViewById(R.id.activarMonitoreo);
        datos = findViewById(R.id.botonDatos);
        borrarDatos = findViewById(R.id.botonBorrarDatos);
        compartir = findViewById(R.id.botonCompartir);
        alarmaPantalla = findViewById(R.id.checkAlarmaPantalla);
        alarmaProximidad = findViewById(R.id.checkAlarmaPRoximidad);
        distanciaProximidad = findViewById(R.id.seekBarDistancia);

        //manejadorBD.insertar("test", "test", "test", "test");


        seguimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                MiServicioIntenso.encolarTrabajo(getApplicationContext(), new Intent());
                tets();

            }
        });

        borrarDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manejadorBD.borrar();

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

                editor.putFloat(LATITUD,(float)location.getLatitude());
                editor.putFloat(LONGITUD,(float)location.getLongitude());
                editor.putFloat(ALTITUD,(float)location.getAltitude());
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
}



