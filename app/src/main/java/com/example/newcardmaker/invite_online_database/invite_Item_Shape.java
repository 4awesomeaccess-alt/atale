package com.example.newcardmaker.invite_online_database;

public class invite_Item_Shape {
    private String id, imageUrl;

    public invite_Item_Shape(String id, String imageUrl) {
        this.id = id;
        this.imageUrl = imageUrl;
    }

    public String getId()       { return id; }
    public String getImageUrl() { return imageUrl; }
}
