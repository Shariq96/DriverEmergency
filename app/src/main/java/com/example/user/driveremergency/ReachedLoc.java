package com.example.user.driveremergency;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import com.google.android.gms.maps.SupportMapFragment;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.user.driveremergency.MainActivity.cancelfragment;
import static com.example.user.driveremergency.MainActivity.userToken;
import static com.example.user.driveremergency.MainActivity.wholeurl;
import static com.example.user.driveremergency.MainActivity.mobile_no;
import static com.example.user.driveremergency.ride_acceptance.Trip_id;
import static com.example.user.driveremergency.MainActivity.lat;
import static com.example.user.driveremergency.MainActivity.longi;
import static com.example.user.driveremergency.MainActivity.myloc;


/**
 * Created by User on 12/8/2017.
 */

public class ReachedLoc extends Fragment implements FragmentChangeListner{
    Button btn;
    View view;
    String url = wholeurl + "/useracc/postStartRide";
    CancelationFragment cf = new CancelationFragment();
    MainActivity mA = new MainActivity();
    private Button btncall, btncancel;

    @Nullable
   @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_bottom,container,false);

        btn = (Button)view.findViewById(R.id.btn);
        btncall = (Button) view.findViewById(R.id.contact_user);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    post(url);
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?saddr=" + lat + "," + longi + "&daddr=" + myloc.getLatitude() + "," + myloc.getLongitude()));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        btncancel = view.findViewById(R.id.cancel_ride);

        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 7/10/2018 cancellaton wala
                cancelfragment.setVisibility(View.VISIBLE);
                replaceFragment2(cf);

            }
        });

        btncall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(Intent.ACTION_CALL); //use ACTION_CALL class
                callIntent.setData(Uri.parse("tel:" + mobile_no));    //this is the phone number calling
                //check permission
                //If the device is running Android 6.0 (API level 23) and the app's targetSdkVersion is 23 or higher,
                //the system asks the user to grant approval.
                if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    //request permission from user if the app hasn't got the required permission
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CALL_PHONE},   //request specific permission from user
                            10);
                    return;
                } else {     //have got permission
                    try {
                        startActivity(callIntent);  //call activity and make phone call
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity().getApplicationContext(), "yourActivity is not founded", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return view;
    }

       OkHttpClient Client = new OkHttpClient();
    public void post(String url) throws IOException {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("Trip_id",Trip_id);
        urlBuilder.addQueryParameter("Driver_id", mA.driver_Id);
        urlBuilder.addQueryParameter("Customer_id",mA.customer_id);
        urlBuilder.addQueryParameter("StateUpdate", "EnRouteToPatient");
        urlBuilder.addQueryParameter("Status_id","1");

        urlBuilder.addQueryParameter("usertoken", userToken);
        String url1 = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url1)
                .build();

       /* RequestBody body = RequestBody.create(JSON,jbobj.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();*/

        Client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //   Toast.makeText(getActivity().getApplicationContext(), "somethng went wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String myResponse = response.body().string();
                //  myResponse = myResponse.substring(1, myResponse.length() - 1); // yara
                myResponse = myResponse.replace("\\", "");
                final String hello = myResponse;
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (hello.equals("true")) {
                            Toast.makeText(getActivity().getApplicationContext(),"Donme",Toast.LENGTH_LONG).show();
                            reachedToPatient rtp = new reachedToPatient();
                            replaceFragment(rtp);

                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "tch tch", Toast.LENGTH_LONG).show();

                        }
                    }
                });

            }
        });

    }

    @Override
    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment, fragment.toString());
        fragmentTransaction.commit();
        fragmentManager.popBackStack();
    }

    public void replaceFragment2(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame1, fragment, fragment.toString());
        fragmentTransaction.commit();
    }
}







