package com.smd.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class setNotification extends AppCompatActivity {

    TimePicker timePicker;
    ImageButton imgBack, imgSettings;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_notification);

        imgBack = findViewById(R.id.btnBack1);
        imgSettings = findViewById(R.id.btnSetting1);

        timePicker = (TimePicker) findViewById(R.id.timePicker);
        findViewById(R.id.buttonAlarm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                            timePicker.getHour(), timePicker.getMinute(), 0);
                } else {
                    calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH),
                            timePicker.getCurrentHour(), timePicker.getCurrentMinute(), 0);
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel channel = new NotificationChannel("notification", "notification", NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setDescription("notification");
                    NotificationManager manager = getSystemService(NotificationManager.class);
                    manager.createNotificationChannel(channel);
                }
                setNotification(calendar.getTimeInMillis());
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(setNotification.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        relativeLayout = findViewById(R.id.notificationActivity);
        boolean perfSync = prefs.getBoolean("sync", false);
        if (perfSync) {
            relativeLayout.setBackground(getDrawable(R.drawable.bg_gradient_dark));
            imgBack.setBackground(getDrawable(R.drawable.leftwhite));
            imgSettings.setBackground(getDrawable(R.drawable.settingswhite));
        } else {
            relativeLayout.setBackground(getDrawable(R.drawable.bg_gradient_blue));
            imgBack.setBackground(getDrawable(R.drawable.left));
            imgSettings.setBackground(getDrawable(R.drawable.settings));
        }
    }

    private void setNotification(long time) {

        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(this, MyNotification.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        am.setRepeating(AlarmManager.RTC, time, AlarmManager.INTERVAL_DAY, pi);
        Toast.makeText(this, "Notification is set", Toast.LENGTH_SHORT).show();
    }

}