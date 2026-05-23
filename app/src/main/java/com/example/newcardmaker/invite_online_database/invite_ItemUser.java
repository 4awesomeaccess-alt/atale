package com.example.newcardmaker.invite_online_database;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class invite_ItemUser implements Serializable {

    @SerializedName("user_id")
    private String id;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("phone")
    private String mobile;

    @SerializedName("user_profile")
    private String image;

    private String authID;

    private String loginType;

    public invite_ItemUser(String id, String name, String email, String mobile, String image, String authID, String loginType) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.mobile = mobile;
        this.image = image;
        this.authID = authID;
        this.loginType = loginType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthID() {
        return authID;
    }

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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getLoginType() {
        return loginType;
    }
}