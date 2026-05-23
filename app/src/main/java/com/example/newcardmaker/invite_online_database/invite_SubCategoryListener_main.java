package com.example.newcardmaker.invite_online_database;

import java.util.ArrayList;

public interface invite_SubCategoryListener_main {
    void onStart();
    void onEnd(String success, String verifyStatus, String message, ArrayList<invite_ItemSubCat_main> arrayListImageCat, ArrayList<invite_ItemSubCat_main> arrayListTextCat);
}