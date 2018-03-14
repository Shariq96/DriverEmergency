package com.example.user.driveremergency.Remote;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by farrukh on 13/03/2018.
 */

public class RetrofitClient {

    private static Retrofit _retrofit = null;

    public static Retrofit getClient(String baseURL){

        if (_retrofit == null){
            _retrofit = new Retrofit.Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return _retrofit;
    }
}
