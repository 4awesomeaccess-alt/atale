package com.example.newcardmaker.invite_online_database;

import java.util.ArrayList;

public interface invite_OneImagesListener_frame {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<invite_Item_OneImages_frame> arrayListQuotes, int total_records);
}