package com.example.mirarai.auth.repo;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.example.mirarai.Audio.callback.AllResponseListener;
import com.example.mirarai.Audio.network.ApiInterface;
import com.example.mirarai.Audio.network.RetrofitService;
import com.example.mirarai.auth.model.LoginResponse;
import com.example.mirarai.auth.model.SignUpResponse;
import com.example.mirarai.util.Utils;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginRepository {

    private static LoginRepository loginRepository;
    private final ApiInterface apiInterface;

    public LoginRepository() {
        apiInterface = RetrofitService.getRetrofit2().create(ApiInterface.class);
    }

    public static LoginRepository getInstance() {
        if (loginRepository == null) {
            loginRepository = new LoginRepository();
        }
        return loginRepository;
    }

    public void loginRemote(HashMap<String, String> mapObject, AllResponseListener allResponseListener){

        Call<LoginResponse> initLogin = apiInterface.loginApi(mapObject);
        initLogin.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                Log.d("LoginResponse", "onResponse: "+response.body());

                if (response.isSuccessful() && response.body() != null) {
                    allResponseListener.onSuccessResponse(response.body());
                } else {
                    allResponseListener.onError(Utils.getServerError(response.code(), response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d("Error", "onResponse: "+t.getMessage());
                allResponseListener.onError(t.getMessage());
            }
        });

    }


    public void signUp(HashMap<String, String> mapObject, AllResponseListener allResponseListener){

        Call<SignUpResponse> initLogin = apiInterface.signUpApi(mapObject);
        initLogin.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {
                Log.d("LoginResponse", "onResponse: "+response.body());

                if (response.isSuccessful() && response.body() != null) {
                    allResponseListener.onSuccessResponse(response.body());

                } else {
                    allResponseListener.onError(Utils.getServerError(response.code(), response.errorBody()));
                }
            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {
                Log.d("Error", "onResponse: "+t.getMessage());
                allResponseListener.onError(t.getMessage());
            }
        });

    }
}
