package com.example.mirarai.avatarImage.repository;

import android.util.Log;

import com.example.mirarai.Audio.callback.AllResponseListener;
import com.example.mirarai.Audio.model.AudioResponse;
import com.example.mirarai.Audio.network.ApiInterface;
import com.example.mirarai.Audio.network.RetrofitService;
import com.example.mirarai.Audio.repository.AudioRepository;
import com.example.mirarai.avatarImage.model.AvatarResponse;
import com.example.mirarai.util.Utils;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AvatarRepo {

    private static AvatarRepo avatarRepo;
    private final ApiInterface apiInterface;

    public AvatarRepo() {
        apiInterface = RetrofitService.getRetrofit2().create(ApiInterface.class);
    }

    public static AvatarRepo getInstance() {
        if (avatarRepo == null) {
            avatarRepo = new AvatarRepo();
        }
        return avatarRepo;
    }

    public void uploadAvatar(File avatar, AllResponseListener allResponseListener) {

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), avatar);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", avatar.getName(), requestBody);

        Call<AvatarResponse> initUpload = apiInterface.uploadAvatar(filePart);

        initUpload.enqueue(new Callback<AvatarResponse>() {
            @Override
            public void onResponse(Call<AvatarResponse> call, Response<AvatarResponse> response) {
                Log.d("AudioUploadResponse", "onResponse: " + response.body());
                if (response.isSuccessful() && response.body() != null) {
                    allResponseListener.onSuccessResponse(response.body());
                } else {
                    allResponseListener.onError(Utils.getServerError(response.code(), response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<AvatarResponse> call, Throwable t) {
                Log.d("ApiError", "onResponse: " + t.getMessage());
                allResponseListener.onError(t.getMessage());

            }
        });
    }

}
