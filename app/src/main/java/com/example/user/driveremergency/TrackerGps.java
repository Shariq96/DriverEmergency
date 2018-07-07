package com.example.user.driveremergency;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.user.driveremergency.MainActivity.currentLocationlatlang;

/**
 * Created by User on 2/16/2018.
 */

public class TrackerGps extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    OkHttpClient client = new OkHttpClient();
    GoogleApiClient gClient;
    LocationRequest mLocationRequest = new LocationRequest();
    Location location;
    LatLng  locObj;
    double latitude;
    double longituted;

    private static final long MIN_DISTANCE_FOR_UPDTE = 10;
    private static final long MIN_TIME_FOR_UPDTE = 1000 * 60 * 1;
    LocationManager locationManager;
    public String url = "http://192.168.0.102:51967/api/Driver/post";
    Timer mTimer;
    String TAG = "LOCAION_SEND";
    SharedPreferences myPref;
    double lat, lng;
    String locObj1;
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            lat = currentLocationlatlang.latitude;
            lng = currentLocationlatlang.longitude;
            locObj1 = String.valueOf(lat) + "," + String.valueOf(lng);
            HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
            urlBuilder.addQueryParameter("id", myPref.getString("id", "1"));
            urlBuilder.addQueryParameter("status", "1");
            urlBuilder.addQueryParameter("locObj", locObj1);
            String url1 = urlBuilder.build().toString();
            Request request = new Request.Builder()
                    .url(url1)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.d(TAG, "UnSuck");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String myResponse = response.body().string();

                    myResponse = myResponse.substring(1, myResponse.length() - 1); // yara
                    myResponse = myResponse.replace("\\", "");
                    String hello = myResponse;
                    Log.d(TAG, "SuccessFull");

                }
            });

        }
    };

    @Override
    public void onCreate() {
        locationManager = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_FOR_UPDTE, MIN_DISTANCE_FOR_UPDTE, this);
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        mTimer = new Timer();
        mTimer.schedule(timerTask, 10000, 30 * 1000);
        myPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        gClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);


        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes


        mLocationRequest.setPriority(priority);
        gClient.connect();


//        return super.onStartCommand(intent,flags,startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        this.mTimer.cancel();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longituted = location.getLongitude();
         locObj = new LatLng(latitude,longituted);
    }

    @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public ComponentName startService(Intent service) {
        return super.startService(service);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
       // LocationServices.FusedLocationApi.requestLocationUpdates(gClient, mLocationRequest, (com.google.android.gms.location.LocationListener) getApplicationContext());
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
