package com.example.mirarai.avatarImage.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AvatarResponse {

    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("userId")
        @Expose
        private Integer userId;
        @SerializedName("avatarImageUrl")
        @Expose
        private String avatarImageUrl;
        @SerializedName("audioFileUrl")
        @Expose
        private String audioFileUrl;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public String getAvatarImageUrl() {
            return avatarImageUrl;
        }

        public void setAvatarImageUrl(String avatarImageUrl) {
            this.avatarImageUrl = avatarImageUrl;
        }

        public String getAudioFileUrl() {
            return audioFileUrl;
        }

        public void setAudioFileUrl(String audioFileUrl) {
            this.audioFileUrl = audioFileUrl;
        }

    }

}
