package com.example.mirarai.Audio.repository;

import android.util.Log;

import com.example.mirarai.Audio.callback.AllResponseListener;
import com.example.mirarai.Audio.model.AudioResponse;
import com.example.mirarai.Audio.model.TextToAudioResponse;
import com.example.mirarai.Audio.network.ApiInterface;
import com.example.mirarai.Audio.network.RetrofitService;
import com.example.mirarai.util.Utils;

import java.io.File;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AudioRepository {

    private static AudioRepository audioRepository;
    private final ApiInterface apiInterface;

    public AudioRepository() {
        apiInterface = RetrofitService.getRetrofit().create(ApiInterface.class);
    }

    public static AudioRepository getInstance() {
        if (audioRepository == null) {
            audioRepository = new AudioRepository();
        }
        return audioRepository;
    }

    public void uploadAudio(File audio, AllResponseListener allResponseListener) {

        RequestBody requestFile = RequestBody.create(MediaType.parse("audio/m4a"), audio);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", audio.getName(), requestFile);

        Call<AudioResponse> initUpload = apiInterface.uploadAudioFile(filePart);


        initUpload.enqueue(new Callback<AudioResponse>() {
            @Override
            public void onResponse(Call<AudioResponse> call, Response<AudioResponse> response) {
                Log.d("AudioUploadResponse", "onResponse: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    allResponseListener.onSuccessResponse(response.body());

                } else {
                    allResponseListener.onError(Utils.getServerError(response.code(), response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<AudioResponse> call, Throwable t) {
                Log.d("ApiError", "onResponse: " + t.getMessage());
                allResponseListener.onError(t.getMessage());

            }
        });
    }


    public void textToAudio(HashMap<String, String> map, AllResponseListener allResponseListener) {

        Call<TextToAudioResponse> initUpload = apiInterface.textToAudio(map);


        initUpload.enqueue(new Callback<TextToAudioResponse>() {
            @Override
            public void onResponse(Call<TextToAudioResponse> call, Response<TextToAudioResponse> response) {
                Log.d("ApiResponseAudioResponse", "onResponse: " + response.body());
                if (response.isSuccessful() && response.body() != null) {

                    allResponseListener.onSuccessResponse(response.body());

                } else {
                    allResponseListener.onError(Utils.getServerError(response.code(), response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<TextToAudioResponse> call, Throwable t) {
                Log.d("ApiError", "onResponse: " + t.getMessage());
                allResponseListener.onError(t.getMessage());

            }
        });
    }

}
