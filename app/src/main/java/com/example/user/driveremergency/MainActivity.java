package com.example.user.driveremergency;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener , NavigationView.OnNavigationItemSelectedListener,
        FragmentChangeListner{
    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest request;
    private Location lastLocation;
    private Location  myloc;
    JSONObject jbobj;
    public static FrameLayout fl , f2;
    public static String driver_Id ="5";
    public static String Trip_id;
    private Marker currentLocation;
    public static final int REQUEST_LOCATION_CODE = 99;
    public static String mobile_no, lat,longi, userToken,customer_id,click_action;

   public static  Button btn1;
    CancelationFragment cf = new CancelationFragment();
    private LatLng[] ltlong = new LatLng[3];
    String hello;
    String url = "http://7665883c.ngrok.io/api/useracc/postnotifyUser";
    String token = FirebaseInstanceId.getInstance().getToken();
    String mymob = "03131313131";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMsgReciver,
                new IntentFilter("myFunction"));
        f2 = (FrameLayout)findViewById(R.id.frame1);
        fl = (FrameLayout)findViewById(R.id.frame);
        btn1 = (Button)findViewById(R.id.button2);
        btn1.setVisibility(GONE);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f2.setVisibility(v.VISIBLE);
                replaceFragment2(cf);
            }
        });
        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }
    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMsgReciver);
        super.onDestroy();
    }
    private BroadcastReceiver mMsgReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            displayAlert(intent);
        }
    };
        jbobject jb = new jbobject();
    private void displayAlert(Intent intent) {
        mobile_no = intent.getStringExtra("usermobile_no");
        lat = intent.getStringExtra("lat");
        userToken = intent.getStringExtra("token");
        customer_id = intent.getStringExtra("customer_id");
        longi = intent.getStringExtra("longi");
        click_action = intent.getStringExtra("ClickAction");
        if (click_action == null) {
            final LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("EMERGENCYY RIDE")
                    .setCancelable(
                            false)
                    .setPositiveButton("Accept",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    ReachedLoc startRide = new ReachedLoc();
                                    fl.setVisibility(View.VISIBLE);
                                    replaceFragment(startRide);
                                    dialog.cancel();
                                    btn1.setVisibility(View.VISIBLE);
                                    jbobj = jb.resptoreq(mymob, lat, longi, userToken, token, mobile_no);
                                    try {
                                        post(url, jbobj);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    r.stop();

                                }
                            }).setNegativeButton("Reject",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            r.stop();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else
            fl.setVisibility(GONE);
            btn1.setVisibility(GONE);
    }

    OkHttpClient Client = new OkHttpClient();

    public void post(String url, JSONObject jbobj) throws IOException {
        RequestBody body = RequestBody.create(JSON,jbobj.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getApplicationContext(), "somethng went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String myResponse = response.body().string();

                myResponse = myResponse.substring(1, myResponse.length() - 1); // yara
                myResponse = myResponse.replace("\\", "");

                //JSONObject jarray = null;
                //  jarray = new JSONObject(myResponse);
                //api_pass = jarray.getString("password");

                //api_pass = jarray.getJSONObject(0).getString("password");
                hello = myResponse;
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (hello.equals("false")) {
                            Toast.makeText(getApplicationContext(),"tch tch",Toast.LENGTH_LONG).show();


                        } else {
                            Toast.makeText(getApplicationContext(), "DONE", Toast.LENGTH_LONG).show();
                            Trip_id = hello;
                            btn1.setVisibility(View.VISIBLE);

                        }
                    }
                });
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        request = new LocationRequest();
        request.setInterval(1000);
        request.setFastestInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);

        }

    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);

            }
            return false;
        } else
            return true;
    }


    @Override
    public void onConnectionSuspended(int i) {
        request = new LocationRequest();
        request.setInterval(1000);
        request.setFastestInterval(1000);
        request.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);

        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        lastLocation = location;
        myloc =location;
        if (currentLocation != null) {
            currentLocation.remove();
        }
        LatLng latlang = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latlang);
        markerOptions.title("CurrentLocation");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocation = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latlang));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MarkerOptions options = new MarkerOptions();
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        LatLng civiccenter = new LatLng(24.900394, 67.072483);
        googleMap.addMarker(new MarkerOptions().position(civiccenter)
                .title("Ambulance0"));
        LatLng lyari = new LatLng(24.871741, 67.008319);
        googleMap.addMarker(new MarkerOptions().position(lyari)
                .title("Ambulance2"));
        LatLng gurumandir = new LatLng(24.880173, 67.039353);
        googleMap.addMarker(new MarkerOptions().position(gurumandir)
                .title("Ambulance1"));
        ltlong[0] = civiccenter;
        ltlong[1] = gurumandir;
        ltlong[2] = lyari;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleApiClient();
                mMap.setMyLocationEnabled(true);
                mMap.setTrafficEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setCompassEnabled(true);
                mMap.getUiSettings().setRotateGesturesEnabled(true);


            }
        } else {
            googleApiClient();
            mMap.setMyLocationEnabled(true);
            mMap.setTrafficEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setRotateGesturesEnabled(true);


        }
    }

    protected synchronized void googleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .addApi(LocationServices.API)
                .build();
        client.connect();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //permission is granted
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            googleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "PermissionDenied", Toast.LENGTH_LONG).show();
                }
        }
    }
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");



    @Override
    public void replaceFragment(Fragment fragment) {
            FragmentManager fragmentManager = getSupportFragmentManager();;
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame, fragment, fragment.toString());
            fragmentTransaction.addToBackStack(fragment.toString());
            fragmentTransaction.commit();
    }
    public void replaceFragment2(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame1, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }
}
