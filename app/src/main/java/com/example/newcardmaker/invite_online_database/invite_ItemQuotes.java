package com.example.newcardmaker.invite_online_database;

import java.io.Serializable;

public class invite_ItemQuotes implements Serializable {

    private String id, catId, catName, imageBig, imageSmall,imageBig1, imageSmall1, quote, likes, views, downloads, bgColor, font, fontColor;
    private Boolean isLiked, isFav, isApproved;

    public invite_ItemQuotes(String id, String catId, String catName, String imageBig, String imageSmall, String quote, String likes, Boolean isLiked, Boolean isFav, String views, String downloads) {
        this.id = id;
        this.catId = catId;
        this.catName = catName;
        this.imageBig = imageBig;
        this.imageSmall = imageSmall;
        this.quote = quote;
        this.likes = likes;
        this.isLiked = isLiked;
        this.isFav = isFav;
        this.views = views;
        this.downloads = downloads;
    }

    public invite_ItemQuotes(String id, String catId, String catName, String imageBig, String imageBig1, String imageSmall, String imageSmall1, String quote, String likes, Boolean isLiked, Boolean isFav, String views, String downloads) {
        this.id = id;
        this.catId = catId;
        this.catName = catName;
        this.imageBig = imageBig;
        this.imageSmall = imageSmall;
        this.imageBig1 = imageBig1;
        this.imageSmall1 = imageSmall1;
        this.quote = quote;
        this.likes = likes;
        this.isLiked = isLiked;
        this.isFav = isFav;
        this.views = views;
        this.downloads = downloads;
    }

    public invite_ItemQuotes(String id, String catId, String catName, String imageBig, String imageBig1, String imageSmall, String imageSmall1, String quote, String likes, Boolean isLiked, Boolean isFav, String views, String downloads, String bgColor, String font, String fontColor) {
        this.id = id;
        this.catId = catId;
        this.catName = catName;
        this.imageBig = imageBig;
        this.imageSmall = imageSmall;
        this.imageBig1 = imageBig1;
        this.imageSmall1 = imageSmall1;
        this.quote = quote;
        this.likes = likes;
        this.isLiked = isLiked;
        this.isFav = isFav;
        this.views = views;
        this.downloads = downloads;
        this.bgColor = bgColor;
        this.font = font;
        this.fontColor = fontColor;
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

    public String getImageSmall() {
        return imageSmall;
    }

    public String getImageBig1() {
        return imageBig1;
    }

    public String getImageSmall1() {
        return imageSmall1;
    }

    public String getLikes() {
        return likes;
    }

    public Boolean getLiked() {
        return isLiked;
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

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
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