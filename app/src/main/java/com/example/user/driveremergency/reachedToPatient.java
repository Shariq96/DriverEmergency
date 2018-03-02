package com.example.user.driveremergency;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.google.android.gms.maps.model.LatLng;

import android.widget.Toast;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.user.driveremergency.MainActivity.Trip_id;
import static com.example.user.driveremergency.MainActivity.customer_id;
import static com.example.user.driveremergency.MainActivity.driver_Id;
import static com.example.user.driveremergency.MainActivity.lat;
import static com.example.user.driveremergency.MainActivity.longi;
import static com.example.user.driveremergency.MainActivity.myloc;
import static com.example.user.driveremergency.MainActivity.userToken;


/**
 * Created by User on 12/8/2017.
 */

public class reachedToPatient extends Fragment implements FragmentChangeListner{
    Button btn;
    View view;
    String url = "http://192.168.0.104:51967/api/useracc/postStartRide";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reachedpatient,container,false);
        btn = (Button)view.findViewById(R.id.btn_strtRide);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String check = CalcDistance();
                if (check.equals("true"))
                {
                    try {
                        post(url);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getActivity().getApplicationContext(),"Please Reach Destination First",Toast.LENGTH_LONG).show();
                }

            }
        });
        return view;
    }
    public String CalcDistance() {
        String result = "false";
        Location loc1 = new Location("");
        loc1.setLatitude(myloc.getLatitude());
        loc1.setLongitude(myloc.getLongitude());

            Location loc2 = new Location("");
        loc2.setLatitude(Double.parseDouble(lat));
        loc2.setLongitude(Double.parseDouble(longi));
            float distanceInMeters = loc1.distanceTo(loc2);
            if (distanceInMeters  <= 300) {
                result = "true";
                return result;
            }
            else return result;

        }



    OkHttpClient Client = new OkHttpClient();
    public void post(String url) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("Trip_id",Trip_id);
        urlBuilder.addQueryParameter("Driver_id", driver_Id);
        urlBuilder.addQueryParameter("Customer_id",customer_id);
        urlBuilder.addQueryParameter("StateUpdate", "Reached Patient and Start Ride");
        urlBuilder.addQueryParameter("Status_id","2");
        urlBuilder.addQueryParameter("userToken",userToken);
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
                Toast.makeText(getActivity().getApplicationContext(), "somethng went wrong", Toast.LENGTH_LONG).show();
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
                            search_fragment sf = new search_fragment();
                            replaceFragment(sf);

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
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }
}







