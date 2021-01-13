package com.screens;

import com.seesame.MyResponse;
import com.seesame.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAGtLyLPk:APA91bEOhZUQGD1PdamL4m9fbC4jfJhCypBpzaD5co6us6eVcI4ew6DKE2OthnI-JkCfqanfYOJcN7W_twS1uiFWzV4YVw0mgXFiTtlVuPnbBvVXFForH17jfVuoz1zEwZDCdL0e2hcN"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}
