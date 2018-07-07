package com.example.user.driveremergency;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splashscreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        SharedPreferences myPref = getSharedPreferences("MyPref", MODE_PRIVATE);
        final boolean logger = myPref.getBoolean("login", false);
        final Thread myThread = new Thread() {
            @Override
            public void run() {
                try {
                    if (logger == false) {
                        sleep(3000);

                        Intent intent = new Intent(getApplicationContext(), LoginController.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        myThread.start();
    }
}
