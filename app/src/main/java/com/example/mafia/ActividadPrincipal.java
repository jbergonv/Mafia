package com.example.mafia;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;

public class ActividadPrincipal extends AppCompatActivity {

    TextView etiquetaMonitoreo, etiquetaDistancia;
    Button seguimiento,datos,borrarDatos,compartir;
    CheckBox alarmaPantalla,alarmaProximidad;
    SeekBar distanciaProximidad;
    private static String ESTADO_SEGUIMIENTO = "ESTADO_SEGUIMIENTO";

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





        seguimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MiServicioIntenso.encolarTrabajo(getApplicationContext(), new Intent());

            }
        });


    }

    void crearCanalNotificaciones(){
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

}