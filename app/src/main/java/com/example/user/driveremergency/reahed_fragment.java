package com.example.user.driveremergency;

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
import com.google.android.gms.vision.Frame;

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
import static com.example.user.driveremergency.MainActivity.btn1;
import static com.example.user.driveremergency.MainActivity.fl;


/**
 * Created by User on 12/8/2017.
 */

public class reahed_fragment extends Fragment implements FragmentChangeListner{
    Button btn;
    View view, view1;
    String url = "http://7665883c.ngrok.io/api/useracc/postStartRide";

    MainActivity mA = new MainActivity();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reached,container,false);
        view1 = getLayoutInflater().inflate(R.layout.activity_main,container,false);
        btn = (Button)view.findViewById(R.id.btn_end);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    post(url);
                } catch (IOException e) {
                    e.printStackTrace();
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
        urlBuilder.addQueryParameter("StateUpdate", "Reached Dest and Trip Ended");
        urlBuilder.addQueryParameter("Status_id","1");
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
                            fl.setVisibility(View.GONE);
                            btn1.setVisibility(View.GONE);

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







