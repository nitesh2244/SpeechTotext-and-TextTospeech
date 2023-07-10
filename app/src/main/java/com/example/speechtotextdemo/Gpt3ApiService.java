package com.example.speechtotextdemo;

import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface Gpt3ApiService {

    @POST("/message")
    Call<MessageResponse> generateResponse(@Body HashMap<String,String> map);

}
