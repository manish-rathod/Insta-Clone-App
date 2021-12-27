package com.example.instaclone.Models;

public class LoginResponseModel {

    private String _id;



    private String userId;

    private String userName;

    private String token;

    private int expiresIn;

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getToken() {
        return token;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public String get_id() {
        return _id;
    }
}
