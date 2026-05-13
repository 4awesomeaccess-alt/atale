package com.example.newcardmaker.invite_online_database;

import android.util.Base64;


import com.example.newcardmaker.BuildConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class invite_API {
    @Expose
    @SerializedName("sign")
    private String sign;
    @Expose
    @SerializedName("salt")
    private String salt;

    public invite_API() {
        String apiKey = BuildConfig.API_KEY;
        salt = "" + getRandomSalt();
        sign = md5(apiKey + salt);
    }

    public static String md5(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            byte messageDigest[] = digest.digest();

            StringBuilder hexString = new StringBuilder();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(String.format("%02x", messageDigest[i]));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toBase64(String input) {
        byte[] encodeValue = Base64.encode(input.getBytes(), Base64.DEFAULT);
        return new String(encodeValue);
    }

    private int getRandomSalt() {
        Random random = new Random();
        return random.nextInt(900);
    }
}