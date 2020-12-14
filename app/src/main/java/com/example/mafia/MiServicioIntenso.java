package com.example.mafia;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;
import androidx.core.app.NotificationCompat;

public class MiServicioIntenso extends JobIntentService {
String LOG_TAG = "MiServicioIntenso";
    static final int JOB_ID = 12111;
    static final int ID = 1;
    static final String ID_CHANNEL = "nombreCanal" ;
    OnBateriaCambia onBateriaCambia = new OnBateriaCambia();
    public MiServicioIntenso() {
    }


    static void encolarTrabajo(Context context, Intent work){
        enqueueWork(context, MiServicioIntenso.class, JOB_ID, work);

    }


    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.ACTION_POWER_CONNECTED");
        intentFilter.addAction("android.intent.action.ACTION_POWER_DISCONNECTED");
        intentFilter.addAction("android.intent.action.BATTERY_LOW");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
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
            }
        }
    }
}