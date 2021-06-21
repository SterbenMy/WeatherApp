package com.smd.weatherapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class MyNotification extends BroadcastReceiver {

    NotificationManager notificationManager;
    String Channer_ID = "notification";
    @Override
    public void onReceive(Context context, Intent intent) {

        NotificationCompat.Builder notificBuilder = new NotificationCompat.Builder(context,Channer_ID)
                .setContentTitle("Weather")
                .setContentText("Open me, look at today weather")
                .setSmallIcon(R.drawable.info);

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);
        notificBuilder.setContentIntent(pendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(1,notificBuilder.build());


        Toast.makeText( context,"Look in WeatherApp", Toast.LENGTH_LONG).show();
        Log.v("omg", "notifiacare");
    }
}