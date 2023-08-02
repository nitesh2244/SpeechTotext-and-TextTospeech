package com.example.mirarai.avatarImage.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mirarai.Audio.callback.AllResponseListener;
import com.example.mirarai.avatarImage.model.AvatarResponse;
import com.example.mirarai.avatarImage.repository.AvatarRepo;

import java.io.File;

public class AvatarViewModel {

    AvatarRepo avatarRepo;

    MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();
    MutableLiveData<String> isFailed = new MutableLiveData<>();

    private Context context;

    private MutableLiveData<AvatarResponse> avatarResponseMutableLiveData;

    public LiveData<String> getIsFailed() {
        return isFailed;
    }

    public LiveData<Boolean> getIsConnecting() {
        return isConnecting;
    }

    public LiveData<AvatarResponse> observeUploadAvatarResponse() {

        if (avatarResponseMutableLiveData ==
                null) {
            avatarResponseMutableLiveData = new MutableLiveData<>();
        }
        return avatarResponseMutableLiveData;
    }

    public void init(Context context) {
        this.context = context;
        if (avatarResponseMutableLiveData != null) {
            return;
        }
        avatarRepo = AvatarRepo.getInstance();
    }

    public void uploadAvatar(File file) {
        isConnecting.setValue(true);
        avatarRepo.uploadAvatar(file, allResponseListener);
    }

    AllResponseListener allResponseListener = new AllResponseListener() {
        @Override
        public void onSuccessResponse(Object object) {
            avatarResponseMutableLiveData.postValue((AvatarResponse) object);
        }

        @Override
        public void onError(String error) {
            isFailed.postValue(error);
        }
    };
}
