package com.example.mirarai.Audio.callback;

public interface AllResponseListener<T> {

    void onSuccessResponse(T object);
    void onError(String error);
}