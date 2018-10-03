package com.example.user.driveremergency;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.brkckr.circularprogressbar.CircularProgressBar;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.user.driveremergency.MainActivity.JSON;
import static com.example.user.driveremergency.MainActivity.longi;
import static com.example.user.driveremergency.MainActivity.mobile_no;
import static com.example.user.driveremergency.MainActivity.mymob;
import static com.example.user.driveremergency.MainActivity.userToken;
import static com.example.user.driveremergency.MainActivity.lat;
import static com.example.user.driveremergency.MainActivity.wholeurl;
import static com.example.user.driveremergency.MainActivity.destlat;
import static com.example.user.driveremergency.MainActivity.destlongi;


public class ride_acceptance extends AppCompatActivity {
    public static String Trip_id;
    CancelationFragment cf = new CancelationFragment();
    bottomsheet_fragment bf = new bottomsheet_fragment();
    String hello;
    String url = wholeurl + "/useracc/postnotifyUser";
    OkHttpClient Client = new OkHttpClient();
    private CardView bottompanel;
    private FrameLayout cancelfragment;
    private FrameLayout bottomsheet;

    private Button ride_accept;
    private Button ride_cancel;

    private String token;
    private JSONObject jbobj;
    jbobject jb = new jbobject();
    int i = 0;
    private TextView sec;
    private CircularProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_acceptance);

//        progressbar.setProgressValue(0);

        ride_accept = findViewById(R.id.response_ride);
        ride_cancel = findViewById(R.id.response_cancel);
        sec = (TextView) findViewById(R.id.sec);
        cancelfragment = findViewById(R.id.frame1);
        bottomsheet = findViewById(R.id.bottom_sheet);
        bottompanel = findViewById(R.id.bottom_panel);


        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
        sec.setText("60");
        r.play();
        final CountDownTimer Counter2 = new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sec.setText("seconds remaining: " + millisUntilFinished / 1000);
//                progressbar.setProgressValueWithAnimation((int)i*100/(5000/1000));
            }

            @Override
            public void onFinish() {
                Toast.makeText(getApplicationContext(), "Time Limit Exceeded", Toast.LENGTH_SHORT).show();
                Intent intent_mainActivity = new Intent(ride_acceptance.this, MainActivity.class);
                startActivity(intent_mainActivity);
                finish();
                r.stop();
            }
        }.start();

        ride_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                token = FirebaseInstanceId.getInstance().getToken();
                jbobj = jb.resptoreq(mymob, lat, longi, userToken, token, mobile_no, destlat, destlongi);
                try {
                    Counter2.cancel();
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
                Counter2.cancel();
                Intent intent_mainActivity = new Intent();
                setResult(RESULT_CANCELED, intent_mainActivity);
                finish();
                r.stop();
                finish();


            }
        });


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            Toast.makeText(getApplicationContext(), "Can't Go Back",
                    Toast.LENGTH_LONG).show();

        return false;
        // Disable back button.............
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
