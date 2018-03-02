package com.example.user.driveremergency;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.io.IOException;

/**
 * Created by User on 2/15/2018.
 */

public class GetDirectionData extends AsyncTask<Object,String,String> {
    GoogleMap mMap;
    String url;
    String googleDirectionsData;
    String duration, distance;
    LatLng latLng;
    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap)objects[0];
        url = (String)objects[1];
        latLng = (LatLng)objects[2];
        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            googleDirectionsData =downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return googleDirectionsData;
    }

    @Override
    protected void onPostExecute(String s) {
        String [] directionList;
        DataParser parser = new DataParser();
        directionList = parser.parseDirections(s);
        displayDirections(directionList);

    }
    public void displayDirections(String[] directions)
    {
        int count = directions.length;
        for (int i = 0; i < count; i++) {
            PolylineOptions options = new PolylineOptions();
            options.color(Color.BLACK);
            options.width(15);
            options.addAll(PolyUtil.decode(directions[i]));
            mMap.addPolyline(options);

        }
    }

}
