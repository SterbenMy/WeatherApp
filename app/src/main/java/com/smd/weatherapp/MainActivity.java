package com.smd.weatherapp;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    final String API_KEY = "your_token";
    final String API_URL = "https://api.openweathermap.org/data/2.5/weather?";

    String todayweather;
    double lat, lon;
    int PERMISSION_ID = 44;
    FusedLocationProviderClient mFusedLocationClient;

    RelativeLayout relativeLayout;
    TextView txtAddress, txtTime, txtDate, txtStatus, txtTemp, txtTemp_min;
    TextView txtTemp_max, txtSunrise, txtSunset, txtWind, txtPressure, txtHumidity;
    TextView txtSunrise1,txtSunset1,txtWind1,txtPressure1,txtHumidity1;
    ImageButton btnSetting, btnNext,btnRefresh, btnNotification;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtAddress = findViewById(R.id.txtAddress);
        txtTime = findViewById(R.id.txtTime);
        txtDate = findViewById(R.id.txtDate);
        txtStatus = findViewById(R.id.txtStatus);
        txtTemp = findViewById(R.id.txtTemp);
        txtTemp_min = findViewById(R.id.txtTemp_min);
        txtTemp_max = findViewById(R.id.txtTemp_max);
        txtSunrise = findViewById(R.id.txtSunrise);
        txtSunset = findViewById(R.id.txtSunset);
        txtWind = findViewById(R.id.txtWind);
        txtPressure = findViewById(R.id.txtPressure);
        txtHumidity = findViewById(R.id.txtHumidity);
        btnSetting = findViewById(R.id.btnSetting);
        btnNext = findViewById(R.id.btnNext);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnNotification = findViewById(R.id.btnNotification);
        txtSunrise1 = findViewById(R.id.txtSunrise1);
        txtSunset1 = findViewById(R.id.txtSunset1);
        txtWind1 = findViewById(R.id.txtWind1);
        txtPressure1 = findViewById(R.id.txtPressure1);
        txtHumidity1 = findViewById(R.id.txtHumidity1);

        clock();

        btnNext.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, byCity.class);
            startActivity(intent);
        });

        btnNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, setNotification.class);
//                intent.putExtra("messageFirstActivity",todayweather);
                startActivity(intent);
            }
        });

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });
        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent);
            }
        });

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        task -> {
                            Location location = task.getResult();
                            if (location == null) {
                                requestNewLocationData();
                            } else {
                                lat = location.getLatitude();
                                lon = location.getLongitude();
                                new weatherTask().execute();
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );

    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lat = mLastLocation.getLatitude();
            lon = mLastLocation.getLongitude();
            new weatherTask().execute();
        }
    };

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                getLastLocation();
            } else {
                Toast.makeText(MainActivity.this, "Permission Denied\nBye", Toast.LENGTH_SHORT).show();
                closeNow();
            }
        }
    }

    private void closeNow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            finishAffinity();
        } else {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        relativeLayout = findViewById(R.id.Main);
        boolean perfSync = prefs.getBoolean("sync",false);
        if(perfSync){
            relativeLayout.setBackground(getDrawable(R.drawable.bg_gradient_dark));
            btnRefresh.setBackground(getDrawable(R.drawable.refreshwhite));
            btnSetting.setBackground(getDrawable(R.drawable.settingswhite));
            btnNext.setBackground(getDrawable(R.drawable.rightwhite));
            btnNotification.setBackground(getDrawable(R.drawable.infowhite));
            txtAddress.setTextColor(Color.WHITE);
            txtTime.setTextColor(Color.WHITE);
            txtDate.setTextColor(Color.WHITE);
            txtStatus.setTextColor(Color.WHITE);
            txtTemp.setTextColor(Color.WHITE);
            txtTemp_min.setTextColor(Color.WHITE);
            txtTemp_max.setTextColor(Color.WHITE);
            txtSunrise.setTextColor(Color.WHITE);
            txtSunset.setTextColor(Color.WHITE);
            txtWind.setTextColor(Color.WHITE);
            txtPressure.setTextColor(Color.WHITE);
            txtHumidity.setTextColor(Color.WHITE);
            txtSunrise1.setTextColor(Color.WHITE);
            txtSunset1.setTextColor(Color.WHITE);
            txtHumidity1.setTextColor(Color.WHITE);
            txtPressure1.setTextColor(Color.WHITE);
            txtWind1.setTextColor(Color.WHITE);
        }else{
            relativeLayout.setBackground(getDrawable(R.drawable.bg_gradient_blue));
            btnRefresh.setBackground(getDrawable(R.drawable.refresh));
            btnSetting.setBackground(getDrawable(R.drawable.settings));
            btnNext.setBackground(getDrawable(R.drawable.right));
            btnNotification.setBackground(getDrawable(R.drawable.info));
            txtAddress.setTextColor(Color.BLACK);
            txtTime.setTextColor(Color.BLACK);
            txtDate.setTextColor(Color.BLACK);
            txtStatus.setTextColor(Color.BLACK);
            txtTemp.setTextColor(Color.BLACK);
            txtTemp_min.setTextColor(Color.BLACK);
            txtTemp_max.setTextColor(Color.BLACK);
            txtSunrise.setTextColor(Color.BLACK);
            txtSunset.setTextColor(Color.BLACK);
            txtWind.setTextColor(Color.BLACK);
            txtPressure.setTextColor(Color.BLACK);
            txtHumidity.setTextColor(Color.BLACK);
            txtSunrise1.setTextColor(Color.BLACK);
            txtSunset1.setTextColor(Color.BLACK);
            txtHumidity1.setTextColor(Color.BLACK);
            txtPressure1.setTextColor(Color.BLACK);
            txtWind1.setTextColor(Color.BLACK);
        }
        if (checkPermissions()) {
           getLastLocation();

        }
    }

    class weatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            findViewById(R.id.loader).setVisibility(View.VISIBLE);
            findViewById(R.id.container).setVisibility(View.GONE);
            findViewById(R.id.errorText).setVisibility(View.GONE);
        }

        protected String doInBackground(String... args) {
            String response;
            Intent intent = getIntent();
            String city = intent.getStringExtra("City");
            if (city != null ){
                response = HttpRequest.excuteGet(API_URL + "q=" + city + "&appid=" + API_KEY);
                Log.v("Link", API_URL + "q=" + city + "&appid=" + API_KEY);
            } else {
                response = HttpRequest.excuteGet(API_URL + "lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY);
                Log.v("Link", API_URL + "lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY);
            }
            return response;

        }

        @Override
        protected void onPostExecute(String result) {


            try {
                JSONObject jsonObj = new JSONObject(result);
                JSONObject main = jsonObj.getJSONObject("main");
                JSONObject sys = jsonObj.getJSONObject("sys");
                JSONObject wind = jsonObj.getJSONObject("wind");
                JSONObject weather = jsonObj.getJSONArray("weather").getJSONObject(0);

                Long updatedAt = jsonObj.getLong("dt");
                String updatedAtText = "Updated at: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.ENGLISH).format(new Date(updatedAt * 1000));
                Double tempResult = main.getDouble("temp") - 273.15;
                int tempResultF = (int) Math.rint(tempResult);

                Double tempMinResult = main.getDouble("temp_min") - 273.15;
                int tempMinResultF = (int) Math.rint(tempMinResult);
                Double tempMaxResult = main.getDouble("temp_max") - 273.15;
                int tempMaxResultF = (int) Math.rint(tempMaxResult);
                String temp = tempResultF + " °C";
                String tempMin = "Min Temp: " + tempMinResultF + "°C";
                String tempMax = "Max Temp: " + tempMaxResultF + "°C";
                String pressure = main.getString("pressure");
                String humidity = main.getString("humidity");

                todayweather = temp;
                Long sunrise = sys.getLong("sunrise");
                Long sunset = sys.getLong("sunset");
                String windSpeed = wind.getString("speed");
                String weatherDescription = weather.getString("description");

                String address = jsonObj.getString("name") + ", " + sys.getString("country");

                txtAddress.setText(address);
                txtDate.setText(updatedAtText);
                txtStatus.setText(weatherDescription.toUpperCase());
                txtTemp.setText(temp);
                txtTemp_min.setText(tempMin);
                txtTemp_max.setText(tempMax);
                txtSunrise.setText(new SimpleDateFormat("HH:mm ", Locale.ENGLISH).format(new Date(sunrise * 1000)));
                txtSunset.setText(new SimpleDateFormat("HH:mm ", Locale.ENGLISH).format(new Date(sunset * 1000)));
                txtWind.setText(windSpeed);
                txtPressure.setText(pressure);
                txtHumidity.setText(humidity);


                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.container).setVisibility(View.VISIBLE);


            } catch (JSONException e) {
                findViewById(R.id.loader).setVisibility(View.GONE);
                findViewById(R.id.errorText).setVisibility(View.VISIBLE);
            }

        }

    }

    private void clock() {
        final Handler hander = new Handler();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            hander.post(() -> {
                getTime();
                clock();
            });
        }).start();
    }

    void getTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        txtTime.setText(simpleDateFormat.format(new Date()));
    }
}
