package com.example.mirarai.auth.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mirarai.Audio.callback.AllResponseListener;
import com.example.mirarai.auth.model.LoginResponse;
import com.example.mirarai.auth.model.SignUpResponse;
import com.example.mirarai.auth.repo.LoginRepository;

import java.util.HashMap;

public class LoginViewModel extends ViewModel {

    LoginRepository loginRepository;
    MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();
    MutableLiveData<String> isFailed = new MutableLiveData<>();
    private Context context;

    private MutableLiveData<LoginResponse> loginResponseMutableLiveData;
    private MutableLiveData<SignUpResponse> signUpResponseMutableLiveData;

    public LiveData<String> getIsFailed() {
        return isFailed;
    }

    public LiveData<Boolean> getIsConnecting() {
        return isConnecting;
    }

    public LiveData<LoginResponse> observeLoginResponse() {
        if (loginResponseMutableLiveData ==
                null) {
            loginResponseMutableLiveData = new MutableLiveData<>();
        }
        return loginResponseMutableLiveData;
    }

    public LiveData<SignUpResponse> observeSignUpResponse() {
        if (signUpResponseMutableLiveData ==
                null) {
            signUpResponseMutableLiveData = new MutableLiveData<>();
        }
        return signUpResponseMutableLiveData;
    }
    public void init(Context context) {
        this.context = context;
        if (loginResponseMutableLiveData != null) {
            return;
        }
        loginRepository = LoginRepository.getInstance();
    }

    public void login(String email, String password) {
        isConnecting.setValue(true);
        HashMap<String, String> map = new HashMap<>();
        map.put("email", email);
        map.put("password", password);
        loginRepository.loginRemote(map, allResponseListener);
    }

    public void signUp(String name, String email, String password) {
        isConnecting.setValue(true);
        HashMap<String, String> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);
        map.put("password", password);
        loginRepository.signUp(map, allResponseListenerSignUp);
    }

    AllResponseListener allResponseListener = new AllResponseListener() {
        @Override
        public void onSuccessResponse(Object object) {
            loginResponseMutableLiveData.postValue((LoginResponse) object);
        }

        @Override
        public void onError(String error) {
            isFailed.postValue(error);
        }
    };

    AllResponseListener allResponseListenerSignUp = new AllResponseListener() {
        @Override
        public void onSuccessResponse(Object object) {
            signUpResponseMutableLiveData.postValue((SignUpResponse) object);
        }

        @Override
        public void onError(String error) {
            isFailed.postValue(error);
        }
    };
}
