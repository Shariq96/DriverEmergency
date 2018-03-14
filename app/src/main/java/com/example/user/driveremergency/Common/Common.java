package com.example.user.driveremergency.Common;

import com.example.user.driveremergency.Remote.IGoogleApi;
import com.example.user.driveremergency.Remote.RetrofitClient;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by farrukh on 13/03/2018.
 */

public class Common {

    public static final String baseUrl = "http://maps.googleapis.com";
    public static IGoogleApi getGoogleApi(){
        return RetrofitClient.getClient(baseUrl).create(IGoogleApi.class);
    }

}
