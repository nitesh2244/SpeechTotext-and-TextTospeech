package com.example.mirarai.auth.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("user")
    @Expose
    private User user;
    @SerializedName("token")
    @Expose
    private String token;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public class User {

        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("email")
        @Expose
        private String email;
        @SerializedName("userId")
        @Expose
        private String userId;
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

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
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
