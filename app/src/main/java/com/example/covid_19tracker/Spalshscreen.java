package com.example.covid_19tracker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Spliterator;

public class Spalshscreen extends AppCompatActivity {
    AVLoadingIndicatorView av;
    CountDownTimer timer;
    int conect_count = 0;
    MaterialTextView connect_txt;
    double latitude, longitude;
    String CityName="",StateName="";
    private static final int REQUEST_CODE_LOCATION_PERMISSION=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalshscreen);
        av = findViewById(R.id.loading);
        connect_txt = findViewById(R.id.connect_txt);
        if(ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Spalshscreen.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);
        }
        else
        {
            getCurrentLocation();
        }
        timer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                av.show();
            }

            @Override
            public void onFinish() {
                if(conect_count<3) {
                    if (isNetworkConnected()) {
                        connect_txt.setText(R.string.current_location);
                        timer.start();
                        if (!CityName.equals("") && !StateName.equals("")) {
                            Intent intent = new Intent(Spalshscreen.this, MainActivity.class);
                            ArrayList<String> loc_arr = new ArrayList<>();
                            loc_arr.add(CityName);
                            loc_arr.add(StateName);
                            intent.putStringArrayListExtra("location_array",loc_arr);
                            startActivity(intent);
                            timer.cancel();
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Location Not found! Try again later", Toast.LENGTH_SHORT).show();
                            timer.cancel();
                            finish();
                        }
                    } else {
                        conect_count++;
                        timer.start();

                    }
                }
                else {
                    connect_txt.setTextColor(Color.YELLOW);
                    connect_txt.setText(R.string.no_internet);
                    Toast.makeText(getApplicationContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
                }

            }
        };
        timer.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==REQUEST_CODE_LOCATION_PERMISSION && grantResults.length>0)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                getCurrentLocation();
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Location Premission Deined!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void getCurrentLocation(){
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(Spalshscreen.this).requestLocationUpdates(locationRequest,new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                LocationServices.getFusedLocationProviderClient(Spalshscreen.this).removeLocationUpdates(this);
                if(locationResult!=null && locationResult.getLocations().size()>0){
                    int latestlocationindex = locationResult.getLocations().size()-1;
                    latitude=locationResult.getLocations().get(latestlocationindex).getLatitude();
                    longitude=locationResult.getLocations().get(latestlocationindex).getLongitude();
                }
                Geocoder geocoder = new Geocoder(Spalshscreen.this, Locale.getDefault());
                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation (latitude, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                CityName=addresses.get(0).getLocality();
                StateName=addresses.get(0).getAdminArea();
                Toast.makeText(getApplicationContext(),"Your Current Location is : "+CityName+","+StateName,Toast.LENGTH_LONG).show();

            }
        }, Looper.getMainLooper());
    }

}
