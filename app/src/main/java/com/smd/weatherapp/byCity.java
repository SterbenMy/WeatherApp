package com.smd.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class byCity extends AppCompatActivity {
    RelativeLayout relativeLayout;
    ImageButton imgBack,imgRefresh,imgSettings;
    EditText etCity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_by_city);

        final EditText edxText = findViewById(R.id.etCity);
         imgBack = findViewById(R.id.btnBack);
         imgRefresh = findViewById(R.id.btnRefresh);
         imgSettings = findViewById(R.id.btnSetting);
        etCity=findViewById(R.id.etCity);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edxText.setText(null);
            }
        });
        imgSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(byCity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        edxText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String nCity = edxText.getText().toString();
                Intent intent = new Intent(byCity.this, MainActivity.class);
                intent.putExtra("City", nCity);
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        relativeLayout = findViewById(R.id.byCity);
        boolean perfSync = prefs.getBoolean("sync", false);
        if (perfSync) {
            relativeLayout.setBackground(getDrawable(R.drawable.bg_gradient_dark));
            imgBack.setBackground(getDrawable(R.drawable.leftwhite));
            imgRefresh.setBackground(getDrawable(R.drawable.refreshwhite));
            imgSettings.setBackground(getDrawable(R.drawable.settingswhite));
            etCity.setTextColor(Color.WHITE);
            etCity.setHintTextColor(Color.WHITE);
            ColorStateList colorStateList = ColorStateList.valueOf(Color.WHITE);
            etCity.setBackgroundTintList(colorStateList);


        } else {
            relativeLayout.setBackground(getDrawable(R.drawable.bg_gradient_blue));
            imgBack.setBackground(getDrawable(R.drawable.left));
            imgRefresh.setBackground(getDrawable(R.drawable.refresh));
            imgSettings.setBackground(getDrawable(R.drawable.settings));
            etCity.setTextColor(Color.BLACK);
            etCity.setHintTextColor(Color.BLACK);
            ColorStateList colorStateList = ColorStateList.valueOf(Color.BLACK);
            etCity.setBackgroundTintList(colorStateList);
        }
    }
}