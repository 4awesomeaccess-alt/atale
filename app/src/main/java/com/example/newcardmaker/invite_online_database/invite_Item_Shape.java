package com.example.newcardmaker.invite_online_database;

public class invite_Item_Shape {
    private String id, catId, imageUrl, imageUrl1;

    public invite_Item_Shape(String id, String catId, String imageUrl, String imageUrl1) {
        this.id = id;
        this.catId = catId;
        this.imageUrl = imageUrl;
        this.imageUrl1 = imageUrl1;
    }

    public String getId()        { return id; }
    public String getCatId()     { return catId; }
    public String getImageUrl()  { return imageUrl; }
    public String getImageUrl1() { return imageUrl1; }
}
