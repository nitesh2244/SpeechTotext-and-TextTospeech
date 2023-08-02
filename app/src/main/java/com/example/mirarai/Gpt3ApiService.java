package com.example.mirarai;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface Gpt3ApiService {

    @POST("/message")
    Call<MessageResponse> generateResponse(@Body HashMap<String,String> map);



}
