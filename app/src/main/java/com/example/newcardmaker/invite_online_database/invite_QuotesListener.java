package com.example.newcardmaker.invite_online_database;



import java.util.ArrayList;

public interface invite_QuotesListener {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<invite_ItemQuotes> arrayListQuotes, int total_records);
}