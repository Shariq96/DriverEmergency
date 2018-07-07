package com.example.user.driveremergency;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

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
import okhttp3.Response;

public class HistoryActivity extends AppCompatActivity {
    public static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    public String url = "http://192.168.0.102:51967//api/driver/getHistory";
    SharedPreferences myPref;
    JSONArray array = null;
    JSONObject jsonObj = null;
    private List<historyClass> persons;
    private RecyclerView rv;
    private ListView historylist;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        rv = (RecyclerView) findViewById(R.id.rv);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        rv.setHasFixedSize(true);
        myPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        id = myPref.getString("id", null);

        initializeData();
        initializeAdapter();

    }

    private void initializeData() {
        persons = new ArrayList<>();
        OkHttpClient client = new OkHttpClient();
        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();
        urlBuilder.addQueryParameter("id", "5");
        String url1 = urlBuilder.build().toString();
        Request request = new Request.Builder()
                .url(url1)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String myResponse = response.body().string();
                myResponse = myResponse.replace("\\", "");
                myResponse = myResponse.substring(1, myResponse.length() - 1);
                try {
                    array = new JSONArray(myResponse);
                    for (int i = 0; i < array.length(); i++) {

                        jsonObj = array.getJSONObject(i);
                        persons.add(new historyClass(jsonObj.getString("time"),
                                jsonObj.getString("Source"),
                                jsonObj.getString("Destination"),
                                jsonObj.getString("FinalFare"),
                                jsonObj.getString("DriverName"),
                                jsonObj.getString("Vehicle_type"),
                                jsonObj.getString("Vehicle_plate")));


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void initializeAdapter() {
        CustomAdapter adapter = new CustomAdapter(persons);
        rv.setAdapter(adapter);
    }
}
