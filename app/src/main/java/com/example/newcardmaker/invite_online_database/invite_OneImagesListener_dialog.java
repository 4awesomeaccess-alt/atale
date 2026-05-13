package com.example.newcardmaker.invite_online_database;

import java.util.ArrayList;

public interface invite_OneImagesListener_dialog {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<invite_Item_OneImages_dialog> arrayListQuotes, int total_records);
}