package com.example.mirarai.Audio.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AudioResponse {
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("fileURL")
    @Expose
    private String fileURL;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFileURL() {
        return fileURL;
    }

    public void setFileURL(String fileURL) {
        this.fileURL = fileURL;
    }

}
