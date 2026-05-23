package com.example.newcardmaker.invite_online_database;

import java.io.Serializable;

public class invite_ItemSubCat_main implements Serializable {

    private String id,  name, imageBig,detail,detail1;


    public invite_ItemSubCat_main(String id, String name, String imageBig, String detail, String detail1) {
        this.id = id;
        this.name = name;
        this.imageBig = imageBig;
        this.detail = detail;
        this.detail1 = detail1;
    }

    public String getId() {
        return id;
    }


    public String getName() {
        return name;
    } public String getdetail1() {
        return detail1;
    } public String getdetail() {
        return detail;
    }

    public String getImageBig() {
        return imageBig;
    }

}

