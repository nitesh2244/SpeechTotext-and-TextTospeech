package com.example.mirarai.Audio.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mirarai.Audio.callback.AllResponseListener;
import com.example.mirarai.Audio.model.AudioResponse;
import com.example.mirarai.Audio.model.TextToAudioResponse;
import com.example.mirarai.Audio.repository.AudioRepository;

import java.io.File;
import java.util.HashMap;

public class AudioViewModel extends ViewModel {

    AudioRepository audioRepository;
    MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();
    MutableLiveData<String> isFailed = new MutableLiveData<>();

    private Context context;

    private MutableLiveData<AudioResponse> audioResponseMutableLiveData;
    private MutableLiveData<TextToAudioResponse> textToAudioResponseMutableLiveData;


    public LiveData<String> getIsFailed() {
        return isFailed;
    }

    public LiveData<Boolean> getIsConnecting() {
        return isConnecting;
    }

    public LiveData<AudioResponse> observeUploadAudioResponse() {

        if (audioResponseMutableLiveData ==
                null) {
            audioResponseMutableLiveData = new MutableLiveData<>();
        }
        return audioResponseMutableLiveData;
    }

    public LiveData<TextToAudioResponse> observeTextToAudioResponse() {

        if (textToAudioResponseMutableLiveData ==
                null) {
            textToAudioResponseMutableLiveData = new MutableLiveData<>();
        }
        return textToAudioResponseMutableLiveData;
    }

    public void init(Context context) {
        this.context = context;
        if (audioResponseMutableLiveData != null) {
            return;
        }
        audioRepository = AudioRepository.getInstance();
    }

    public void uploadAudio(File file) {
        isConnecting.setValue(true);
        audioRepository.uploadAudio(file, allResponseListener);
    }

    public void textToAudio(HashMap<String, String> map) {
        isConnecting.setValue(true);
        audioRepository.textToAudio(map, allResponseListener1);
    }

    AllResponseListener allResponseListener = new AllResponseListener() {
        @Override
        public void onSuccessResponse(Object object) {
            audioResponseMutableLiveData.postValue((AudioResponse) object);
        }

        @Override
        public void onError(String error) {
            isFailed.postValue(error);
        }
    };

    AllResponseListener allResponseListener1 = new AllResponseListener() {
        @Override
        public void onSuccessResponse(Object object) {
            textToAudioResponseMutableLiveData.postValue((TextToAudioResponse) object);
        }

        @Override
        public void onError(String error) {
            isFailed.postValue(error);
        }
    };

}
