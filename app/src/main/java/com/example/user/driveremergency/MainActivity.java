package com.example.user.driveremergency;

import android.Manifest;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.driveremergency.Common.Common;
import com.example.user.driveremergency.Remote.IGoogleApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener , NavigationView.OnNavigationItemSelectedListener,
        FragmentChangeListner {


    //Navigation

    private static final String TAG = "MainActivity";
    NavigationMapRoute navigationMapRoute;

    protected GeoDataClient mGeoDataClient;
    protected PlaceDetectionClient mPlaceDetectionClient;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    MapView mapView;
    public static String driver_Id;
    private GoogleApiClient client;
    private LocationRequest request;
    private Location lastLocation;
    public static Location  myloc;
    JSONObject jbobj;
    SharedPreferences.Editor editor;
    public static FrameLayout fl , f2;

    private Marker currentLocation;
    public static final int REQUEST_LOCATION_CODE = 99;
    public static String mobile_no, lat,longi, userToken,customer_id,click_action;
    public static LatLng currentLocationlatlang;
   public static  Button btn1;
    public String url1 = "http://192.168.0.101:51967/api/driver/post";
    private List<LatLng> polyLineList;
    private Marker pickupLocationMarker;
    private float v;
    private double latitude, longitude;
    private Handler handler;
    private LatLng startPosition, endPosition, curentLocation;
    private int index,next;
    private Button btnGo;
    private EditText edtPlace;
    private String destination;
    private PolylineOptions polylineOptions, blackPolylineOptions;
    private Polyline blackPolyline, greyPolyline;
    private IGoogleApi mService;
    Runnable drawPathRunnable = new Runnable() {
        @Override
        public void run() {
            if (index<polyLineList.size()-1){
                index++;
                next = index+1;
            }
            if (index<polyLineList.size()-1){
                startPosition = polyLineList.get(index);
                endPosition = polyLineList.get(next);
            }
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0,1);
            valueAnimator.setDuration(3000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    v = animation.getAnimatedFraction();
                    longitude = v*endPosition.longitude+(1-v)*startPosition.longitude;
                    latitude = v*endPosition.latitude+(1-v)*startPosition.latitude;

                    LatLng newPos = new LatLng(latitude,longitude);
                    pickupLocationMarker.setPosition(newPos);
                    pickupLocationMarker.setAnchor(0.5f,0.5f);
                    pickupLocationMarker.setRotation(getBearing(startPosition,newPos));
                    mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(newPos)
                                    .zoom(15.5f)
                                    .build()
                    ));
                }
            });
            valueAnimator.start();
            handler.postDelayed(this,3000);
        }
    };
    public static String mymob = "03131313131";
    //ambulance animation
    SharedPreferences myPref;
    String aatag = "LOCAION_SEND";
    String token = FirebaseInstanceId.getInstance().getToken();
    CancelationFragment cf = new CancelationFragment();
    String hello;
    private SwitchCompat mStatus;
    private TrackLocation Locate;
    private LatLng[] ltlong = new LatLng[3];
    private Location mLastLocation;
    private TextView mStatusText;
    private SharedPreferences MyPref;
    private double lat1, lng1;
    private String locObj1;
    private FrameLayout cancelfragment;
    private FrameLayout bottomsheet;
    private FrameLayout bottompanel;


    private float getBearing(LatLng startPosition, LatLng endPosition) {

        double lat = Math.abs(startPosition.latitude - endPosition.latitude);
        double lng = Math.abs(startPosition.longitude - endPosition.longitude);

        if (startPosition.latitude < endPosition.latitude && startPosition.longitude < endPosition.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude < endPosition.longitude)
            return (float) ((90-Math.toDegrees(Math.atan(lng / lat)))+90);
        else if (startPosition.latitude >= endPosition.latitude && startPosition.longitude >= endPosition.longitude)
            return (float) ((Math.toDegrees(Math.atan(lng / lat)))+180);
        else if (startPosition.latitude < endPosition.latitude && startPosition.longitude >= endPosition.longitude)
            return (float) ((90-Math.toDegrees(Math.atan(lng / lat)))+270);
    return -1;
    }

    OkHttpClient Client = new OkHttpClient();
    private BroadcastReceiver mMsgReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {


            displayAlert(intent);
        }
    };
    private void run() {
        lat1 = currentLocationlatlang.latitude;
        lng1 = currentLocationlatlang.longitude;
        locObj1 = String.valueOf(lat1) + "," + String.valueOf(lng1);
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url1).newBuilder();
        urlBuilder.addQueryParameter("id", myPref.getString("id", "1"));
        urlBuilder.addQueryParameter("status", "2");
        urlBuilder.addQueryParameter("locObj", locObj1);
        String url1 = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url1)
                .build();
        Client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(aatag, "UnSuck");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String myResponse = response.body().string();

                myResponse = myResponse.substring(1, myResponse.length() - 1); // yara
                myResponse = myResponse.replace("\\", "");
                String hello = myResponse;
                Log.d(aatag, "SuccessFull");

            }
        });

    }

    private void getDirections() {

        curentLocation = new LatLng(currentLocationlatlang.latitude,currentLocationlatlang.longitude);
        double destiantionLat = 24.884574;
        double destinationLng = 67.151115;
        String requestApi = null;

        String requestApi1 = null;
        String requestApi2 = null;

        try {
            requestApi = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+curentLocation.latitude+","+curentLocation.longitude+"&"+
                    "destination="+destiantionLat+","+destinationLng+"&"+
                    "key="+"AIzaSyC6RCXIx_uZBrlUjS037Ggi0Ml5erS17mI";
                    Log.d("requestApi", requestApi);

         /*   requestApi1 = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+curentLocation.latitude+","+curentLocation.longitude+"&"+
                    "destination="+destiantionLat+","+destinationLng+"&"+
                    "key="+"AIzaSyC6RCXIx_uZBrlUjS037Ggi0Ml5erS17mI";
            Log.d("requestApi", requestApi);

            requestApi2 = "https://maps.googleapis.com/maps/api/directions/json?"+
                    "mode=driving&"+
                    "transit_routing_preference=less_driving&"+
                    "origin="+curentLocation.latitude+","+curentLocation.longitude+"&"+
                    "destination="+destiantionLat+","+destinationLng+"&"+
                    "key="+"AIzaSyC6RCXIx_uZBrlUjS037Ggi0Ml5erS17mI";
            Log.d("requestApi", requestApi);*/


                    mService.getPath(requestApi)
                            .enqueue(new retrofit2.Callback<String>() {
                                @Override
                                public void onResponse(retrofit2.Call<String> call, retrofit2.Response<String> response) {

                                    try {
                                        JSONObject jsonObject = new JSONObject(response.body().toString());
                                        JSONArray jsonArray = jsonObject.getJSONArray("routes");
                                        for (int i = 0;i < jsonArray.length();i++){
                                            JSONObject route = jsonArray.getJSONObject(i);
                                            JSONObject poly = route.getJSONObject("overview_polyline");
                                            String polyline = poly.getString("points");
                                            polyLineList = decodePoly(polyline);

                                        }

                                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                        for (LatLng latLng:polyLineList)
                                            builder.include(latLng);
                                            LatLngBounds bounds = builder.build();
                                        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
                                        mMap.animateCamera(mCameraUpdate);

                                        polylineOptions = new PolylineOptions();
                                        polylineOptions.color(Color.GRAY);
                                        polylineOptions.width(5);
                                        polylineOptions.startCap(new SquareCap());
                                        polylineOptions.endCap(new SquareCap());
                                        polylineOptions.jointType(JointType.ROUND);
                                        polylineOptions.addAll(polyLineList);
                                        greyPolyline = mMap.addPolyline(polylineOptions);

                                        blackPolylineOptions = new PolylineOptions();
                                        blackPolylineOptions.color(Color.RED);
                                        blackPolylineOptions.width(5);
                                        blackPolylineOptions.startCap(new SquareCap());
                                        blackPolylineOptions.endCap(new SquareCap());
                                        blackPolylineOptions.jointType(JointType.ROUND);
                                        blackPolyline = mMap.addPolyline(blackPolylineOptions);

                                        mMap.addMarker(new MarkerOptions().position(polyLineList.get(polyLineList.size() - 1 ))
                                        .title("Pickup Location"));

                                        ValueAnimator polylineAnimator = ValueAnimator.ofInt(0,100);
                                        polylineAnimator.setDuration(2000);
                                        polylineAnimator.setInterpolator(new LinearInterpolator());
                                        polylineAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                            @Override
                                            public void onAnimationUpdate(ValueAnimator animation) {
                                                List<LatLng> points = greyPolyline.getPoints();
                                                int percentValue = (int) animation.getAnimatedValue();
                                                int size = points.size();
                                                int newPoints = (int) (size * (percentValue/100.0f));
                                                List<LatLng> p = points.subList(0,newPoints);
                                                blackPolyline.setPoints(p);
                                            }
                                        });
                                        polylineAnimator.start();

                                        pickupLocationMarker = mMap.addMarker(new MarkerOptions().position(curentLocation)
                                        .flat(true)
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ambulance_icon)));

                                        handler = new Handler();
                                        index = 1;
                                        next = 1;
                                        handler.postDelayed(drawPathRunnable,3000);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(retrofit2.Call<String> call, Throwable t) {

                                    Toast.makeText(MainActivity.this, ""+t.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });

        }
        catch (Exception e){

        }


    }

    //copied method
    private List decodePoly(String encoded) {

        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMsgReciver);
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        Locate = new TrackLocation(getApplicationContext());


        OkHttpClient Client = new OkHttpClient();
        myPref = getSharedPreferences("MyPref", MODE_PRIVATE);


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mapView = findViewById(R.id.map);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigattionView = findViewById(R.id.nav_view);
        navigattionView.setNavigationItemSelectedListener(this);


        cancelfragment = findViewById(R.id.frame1);
        bottomsheet = findViewById(R.id.bottom_sheet);
        bottompanel = findViewById(R.id.bottom_panel);


        MyPref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
        editor = MyPref.edit();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMsgReciver,
                new IntentFilter("myFunction"));

        mStatusText = findViewById(R.id.mStatusText);
        mStatus = findViewById(R.id.status_switch);
        //      f2 = (FrameLayout)findViewById(R.id.frame1);
        //       fl = (FrameLayout)findViewById(R.id.frame);
        //     btn1 = (Button) findViewById(R.id._SearchDirections);
        //     btn1.setVisibility(GONE);
        //     btn1.setOnClickListener(new View.OnClickListener() {
        //         @Override
        //         public void onClick(View v) {
        //              f2.setVisibility(v.VISIBLE);
        //              replaceFragment2(cf);
        //          }
        //      });

        mStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    Intent intent = new Intent(MainActivity.this, TrackerGps.class);
                    startService(intent);
                    mStatusText.setText("Online");
                } else {
                    Intent intent = new Intent(MainActivity.this, TrackerGps.class);
                    getApplicationContext().stopService(intent);
                    run();
                    mStatusText.setText("Offline");
                }
            }
        });

        mGeoDataClient = Places.getGeoDataClient(this, null);
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);


        polyLineList = new ArrayList<>();
        //  btnGo = (Button) findViewById(R.id._SearchDirections);
        edtPlace = (EditText) findViewById(R.id._destionation_route);


     /* btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  destination = edtPlace.getText().toString();
                //replacing space with + for fetching data
           //     destination = destination.replace(" ", "+");
                destination = "SHAUKAT+OMAR+MEMORIAL+HOSPITAL";
                Log.d("AnimationTesting: ", destination);

                getDirections();


            }
        });*/

        mService = Common.getGoogleApi();
    }

        private void displayAlert(Intent intent) {
            mobile_no = intent.getStringExtra("usermobile_no");
            intent.getStringExtra("lat");
            customer_id = intent.getStringExtra("customer_id");
            userToken = intent.getStringExtra("token");
            click_action = intent.getStringExtra("ClickAction");
            longi = intent.getStringExtra("longi");
            Intent intent1 = new Intent(MainActivity.this, ride_acceptance.class);
            intent1.putExtra("mobile_no", intent.getStringExtra("usermobile_no"));
            intent1.putExtra("lat ", intent.getStringExtra("lat"));
            intent1.putExtra("usr_token ", userToken = intent.getStringExtra("token"));
            intent1.putExtra("cust_id", customer_id = intent.getStringExtra("customer_id"));
            intent1.putExtra("long ", longi = intent.getStringExtra("longi"));
            intent1.putExtra("click_action", click_action = intent.getStringExtra("ClickAction"));
            startActivityForResult(intent1, 12345);
            // startActivity(intent1);
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

        if (id == R.id.nav_history) {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_policies) {

        } else if (id == R.id.nav_about) {


        } else if (id == R.id.nav_howitworks) {
            startActivity(new Intent(MainActivity.this, ride_acceptance.class));
            finish();

        } else if (id == R.id.nav_signout) {
            editor.putBoolean("login", false);
            editor.apply();
            startActivity(new Intent(MainActivity.this, LoginController.class));
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
         currentLocationlatlang = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocationlatlang);
        markerOptions.title("CurrentLocation");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        currentLocation = mMap.addMarker(markerOptions);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocationlatlang));
        mMap.animateCamera(CameraUpdateFactory.zoomBy(10));
        if (client != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {


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
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setMyLocationEnabled(true);
                mMap.setTrafficEnabled(true);
                mMap.setBuildingsEnabled(false);
                mMap.getUiSettings().setZoomControlsEnabled(true);
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

    private void setDirections()
    {   Object[] dataTransfer = new Object[3];
        String url = getDirectionUrl();
        GetDirectionData gdd = new GetDirectionData();
        dataTransfer[0] =mMap;
        dataTransfer[1]= url;
        dataTransfer[2] = new LatLng(Double.valueOf(lat),Double.valueOf(longi));


        gdd.execute(dataTransfer);

    }
    private String getDirectionUrl() {
        StringBuilder gDirectionUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Location currentLocation = LocationServices.FusedLocationApi.getLastLocation(client);
        gDirectionUrl.append("origin="+currentLocation.getLatitude()+","+currentLocation.getLongitude());
        gDirectionUrl.append("&destination="+24.879239+","+67.018527);
        gDirectionUrl.append("&key="+"AIzaSyA4ja7O-DvuqA6ivaurF3_FJUuXneVh63s");
        return (gDirectionUrl.toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            bottompanel.setVisibility(GONE);
            bottomsheet.setVisibility(View.VISIBLE);

        }
        if (resultCode == Activity.RESULT_CANCELED) {

            cancelfragment.setVisibility(View.VISIBLE);
            replaceFragment2(cf);
            finish();
            //Canceled logic
        }
    }
}
