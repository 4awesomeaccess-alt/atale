package com.example.newcardmaker;


import java.util.ArrayList;

public interface OneImagesListener {
    void onStart();

    void onEnd(String success, String verifyStatus, String message, ArrayList<Item_OneImages> arrayListQuotes, int total_records);
}