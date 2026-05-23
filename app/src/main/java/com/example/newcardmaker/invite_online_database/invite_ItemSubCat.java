package com.example.newcardmaker.invite_online_database;

import java.io.Serializable;

public class invite_ItemSubCat implements Serializable {

    private String id, catid, name, imageBig, imageSmall, ad_on_off, date, detail;


    public invite_ItemSubCat(String id, String catid, String name, String imageBig, String ad_on_off, String date, String detail) {
        this.id = id;
        this.catid = catid;
        this.name = name;
        this.imageBig = imageBig;
        this.ad_on_off = ad_on_off;
        this.date = date;
        this.detail = detail;
    }

    public invite_ItemSubCat(String id, String catid, String name, String imageBig) {
        this.id = id;
        this.catid = catid;
        this.name = name;
        this.imageBig = imageBig;

    }

    public invite_ItemSubCat(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getcatId() {
        return catid;
    }

    public String getName() {
        return name;
    }

    public String getImageBig() {
        return imageBig;
    }


    public String getAd_on_off() {
        return ad_on_off;
    }

    public String getDate() {
        return date;
    }

    public String getDetail() {
        return detail;
    }
}
