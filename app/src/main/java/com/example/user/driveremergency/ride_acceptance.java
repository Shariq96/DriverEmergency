package com.example.user.driveremergency;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.view.View.GONE;
import static com.example.user.driveremergency.MainActivity.JSON;
import static com.example.user.driveremergency.MainActivity.btn1;
import static com.example.user.driveremergency.MainActivity.fl;
import static com.example.user.driveremergency.MainActivity.longi;
import static com.example.user.driveremergency.MainActivity.mobile_no;
import static com.example.user.driveremergency.MainActivity.mymob;
import static com.example.user.driveremergency.MainActivity.userToken;
import static com.example.user.driveremergency.MainActivity.lat;
public class ride_acceptance extends AppCompatActivity {
    public static String Trip_id;
    CancelationFragment cf = new CancelationFragment();
    bottomsheet_fragment bf = new bottomsheet_fragment();
    String hello;
    String url = "http://192.168.0.101:51967/api/useracc/postnotifyUser";
    OkHttpClient Client = new OkHttpClient();
    private CardView bottompanel;
    private FrameLayout cancelfragment;
    private FrameLayout bottomsheet;

    private Button ride_accept;
    private Button ride_cancel;

    private String token;
    private JSONObject jbobj;
    jbobject jb = new jbobject();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_acceptance);


        ride_accept = findViewById(R.id.response_ride);
        ride_cancel = findViewById(R.id.response_cancel);

        cancelfragment = findViewById(R.id.frame1);
        bottomsheet = findViewById(R.id.bottom_sheet);
        bottompanel = findViewById(R.id.bottom_panel);


        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        r.play();


        ride_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                token = FirebaseInstanceId.getInstance().getToken();
                jbobj = jb.resptoreq(mymob, lat, longi, userToken, token, mobile_no);
                try {

                    post(url, jbobj);
                    r.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Intent intent_mainActivity = new Intent();
                setResult(RESULT_OK, intent_mainActivity);
                finish();
            }
        });

        ride_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent_mainActivity = new Intent();
                setResult(RESULT_CANCELED, intent_mainActivity);
                finish();


            }
        });


    }

    public void replaceFragment2(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame1, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }

    public void setBottomsheet(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ;
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.bottom_sheet, fragment, fragment.toString());
        fragmentTransaction.addToBackStack(fragment.toString());
        fragmentTransaction.commit();
    }

    public void post(String url, JSONObject jbobj) throws IOException {
        RequestBody body = RequestBody.create(JSON, jbobj.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

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
                ride_acceptance.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (hello.equals("false")) {
                            Toast.makeText(getApplicationContext(), "tch tch", Toast.LENGTH_LONG).show();


                        } else {
                            Toast.makeText(getApplicationContext(), "DONE", Toast.LENGTH_LONG).show();
                            Trip_id = hello;
                            //  btn1.setVisibility(View.VISIBLE);

                        }
                    }
                });
            }
        });

    }

}
