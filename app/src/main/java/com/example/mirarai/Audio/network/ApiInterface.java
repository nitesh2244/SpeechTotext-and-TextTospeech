package com.example.mirarai.Audio.network;

import com.example.mirarai.Audio.model.AudioResponse;
import com.example.mirarai.Audio.model.TextToAudioResponse;
import com.example.mirarai.auth.model.LoginResponse;
import com.example.mirarai.auth.model.SignUpResponse;
import com.example.mirarai.avatarImage.model.AvatarResponse;

import java.util.HashMap;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiInterface {

    @Multipart
    @POST("user/audioFileUpload")
    Call<AudioResponse> uploadAudioFile(@Part MultipartBody.Part file);

    /* @POST("user/textToAudio")
    Call<TextToAudioResponse> textToAudio(@Body HashMap<String, String> mapObject);*/

    @POST("user/askQuestion")
    Call<TextToAudioResponse> textToAudio(@Body HashMap<String, String> mapObject);

    //PORT 8010
    @POST("auth/login")
    Call<LoginResponse> loginApi(@Body HashMap<String, String> mapObject);

    @POST("auth/signup")
    Call<SignUpResponse> signUpApi(@Body HashMap<String, String> mapObject);

    @Multipart
    @POST("user/uploadsAvatarImage")
    Call<AvatarResponse> uploadAvatar(@Part MultipartBody.Part fileUrl);

}
