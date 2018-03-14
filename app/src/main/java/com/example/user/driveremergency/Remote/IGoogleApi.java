package com.example.user.driveremergency.Remote;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by farrukh on 13/03/2018.
 */

public interface IGoogleApi {

    @GET
    Call<String> getPath(@Url String url);
}
