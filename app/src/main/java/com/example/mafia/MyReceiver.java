package com.example.mafia;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
String LOG_TAG = "MyReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Log.d(LOG_TAG, "Arrannnnnnnnnnnnnco  el servicio");
            MiServicioIntenso.encolarTrabajo(context, new Intent());

       }

    }
}