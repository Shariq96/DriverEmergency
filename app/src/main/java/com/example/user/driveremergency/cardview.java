package com.example.user.driveremergency;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class cardview extends AppCompatActivity {

    TextView customername;
    TextView source;
    TextView dest;
    TextView fare;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardview);

        customername = (TextView) findViewById(R.id.Drivername);
        source = (TextView) findViewById(R.id.source1);
        dest = (TextView) findViewById(R.id.Dest1);
        fare = (TextView) findViewById(R.id.time);


    }
}
