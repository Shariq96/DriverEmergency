package com.example.user.driveremergency;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.user.driveremergency.MainActivity.destlat;
import static com.example.user.driveremergency.MainActivity.destlongi;
import static com.example.user.driveremergency.MainActivity.fl;
import static com.example.user.driveremergency.MainActivity.longi;
import static com.example.user.driveremergency.MainActivity.lat;
import static com.example.user.driveremergency.MainActivity.userToken;
import static com.example.user.driveremergency.ride_acceptance.Trip_id;

public class payment extends AppCompatActivity {

    Geocoder geocoder;
    Geocoder geocoder1;
    String url = "http://192.168.0.101:51967/api/driver/payment";
    List<Address> addresses;
    List<Address> desti;
    String fare2;
    OkHttpClient Client = new OkHttpClient();
    private TextView source, dest, fare, distance;
    private int baseFee = 300;
    private int costPerKM = 10;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        String dest111 = destlat;
        String destlogn = destlongi;
        source = (TextView) findViewById(R.id.textView4);
        dest = (TextView) findViewById(R.id.textView);
        fare = (TextView) findViewById(R.id.textView5);
        distance = (TextView) findViewById(R.id.textfare);
        btn = (Button) findViewById(R.id.btncash);
        float result[] = new float[10];

        Location.distanceBetween(Double.parseDouble(lat), Double.parseDouble(longi), Double.parseDouble(destlat), Double.parseDouble(destlongi), result);
        int distance = (int) result[0] / 1000;
        dest.setText("Distance: " + distance + "km");
        fare2 = String.valueOf((distance * costPerKM) + baseFee);
        fare.setText(fare2);

        geocoder = new Geocoder(this, Locale.getDefault());
        geocoder1 = new Geocoder(this, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(longi), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            desti = geocoder1.getFromLocation(Double.parseDouble(destlat), Double.parseDouble(destlongi), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        } catch (IOException e) {
            e.printStackTrace();
        }
        String source1 = addresses.get(0).getAddressLine(0);
        String dest1 = desti.get(0).getAddressLine(0);
        source.setText(source1);
        dest.setText(dest1);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    post(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void post(String url) throws IOException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("trip_id", Trip_id);
        urlBuilder.addQueryParameter("payment", fare2);
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
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String myResponse = response.body().string();
                //  myResponse = myResponse.substring(1, myResponse.length() - 1); // yara
                myResponse = myResponse.replace("\\", "");
                final String hello = myResponse;
                payment.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (hello.equals("true")) {
                            Toast.makeText(getApplicationContext(), "Donme", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "tch tch", Toast.LENGTH_LONG).show();

                        }
                    }
                });

            }
        });

    }

}
