package com.example.newcardmaker;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PageModel {
    private List<View> textViewList; // Aa page na badha TextViews

    public PageModel() {
        this.textViewList = new ArrayList<>();
    }

    public List<View> getTextViewList() { return textViewList; }
}