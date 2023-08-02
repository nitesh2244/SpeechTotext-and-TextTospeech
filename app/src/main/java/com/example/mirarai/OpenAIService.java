package com.example.mirarai;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OpenAIService {

    private static final String API_BASE_URL = "http://192.168.1.172:3000";
    private static final String TOKEN = "Bearer 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";

    private Gpt3ApiService service;

    public OpenAIService() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        Gson gson = new GsonBuilder().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        service = retrofit.create(Gpt3ApiService.class);
    }

    public String generateResponse(String prompt) throws Exception {

      /*  MediaType mediaType = MediaType.parse("application/json");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("model", "gpt-3.5-turbo");

        JsonArray messagesArray = new JsonArray();

        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", "You are a helpful assistant.");

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", prompt);

        messagesArray.add(systemMessage);
        messagesArray.add(userMessage);

        jsonObject.add("messages", messagesArray);

        RequestBody body = RequestBody.create(mediaType, jsonObject.toString());*/

        HashMap<String,String> map = new HashMap<>();
        map.put("message",prompt);

        Call<MessageResponse> call = service.generateResponse(map);
        Response<MessageResponse> response = call.execute();

        if (!response.isSuccessful()) {
            throw new Exception("Error: " + response.code() + " " + response.message());
        }

        MessageResponse apiResponse = response.body();
        String responseBody = apiResponse != null ? apiResponse.getMessage() : "";
        return responseBody;
    }
}
