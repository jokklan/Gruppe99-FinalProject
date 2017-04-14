package com.example.jokklan.finalproject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "com.example.jokklan.finalproject.alarm";

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("AlarmReceiver", "I'm running");

        Intent serviceIntent = new Intent(context, LocationIntentService.class);
        //serviceIntent.putExtra("foo", "bar");
        context.startService(serviceIntent);
    }
}