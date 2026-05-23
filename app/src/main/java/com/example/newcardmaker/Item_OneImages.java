package com.example.newcardmaker;

import java.io.Serializable;

public class Item_OneImages implements Serializable {

    private String id, catId, catName, imageBig, quote, likes, views, downloads, bgColor, font, fontColor, ad_on_off;
    private Boolean isLiked, isFav, isApproved;
    private String image;

    // ✅ ADD THIS (NO-ARG CONSTRUCTOR)
    public Item_OneImages() {
        this.id = "";
        this.catId = "";
        this.catName = "";
        this.imageBig = "";
        this.quote = "";
        this.likes = "0";
        this.isLiked = false;
        this.isFav = false;
        this.views = "0";
        this.downloads = "0";
        this.bgColor = "";
        this.font = "";
        this.fontColor = "";
        this.ad_on_off = "0";
        this.isApproved = true;
        this.image = "";
    }

    public Item_OneImages(String id, String catId, String catName, String imageBig, String quote, String likes, Boolean isLiked,
                          Boolean isFav, String views, String downloads, String bgColor, String font, String fontColor, String ad_on_off) {
        this.id = id;
        this.catId = catId;
        this.catName = catName;
        this.imageBig = imageBig;
        this.quote = quote;
        this.likes = likes;
        this.isLiked = isLiked;
        this.isFav = isFav;
        this.views = views;
        this.downloads = downloads;
        this.bgColor = bgColor;
        this.font = font;
        this.fontColor = fontColor;
        this.ad_on_off = ad_on_off;
    }

    public void setImageBig(String imageBig) {
        this.imageBig = imageBig;
    }


    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public String getAd_on_off() {
        return ad_on_off;
    }

    public String getId() {
        return id;
    }

    public String getCatId() {
        return catId;
    }

    public String getCatName() {
        return catName;
    }

    public String getQuote() {
        return quote;
    }

    public String getImageBig() {
        return imageBig;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public Boolean getFav() {
        return isFav;
    }

    public void setFav(Boolean fav) {
        isFav = fav;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getDownloads() {
        return downloads;
    }

    public void setDownloads(String downloads) {
        this.downloads = downloads;
    }

    public String getBgColor() {
        return bgColor;
    }

    public String getFont() {
        return font;
    }

    public String getFontColor() {
        return fontColor;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean isApproved) {
        this.isApproved = isApproved;
    }
}
