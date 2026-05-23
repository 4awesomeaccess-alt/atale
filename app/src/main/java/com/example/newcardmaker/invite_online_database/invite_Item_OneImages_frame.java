package com.example.newcardmaker.invite_online_database;

import java.io.Serializable;

public class invite_Item_OneImages_frame implements Serializable {

    private String id, catId, catName, imageBig, quote, likes, views, downloads, bgColor, font, font2, fontColor, ad_on_off, card_background, email_icon, contact_icon, quote_pv_video, quote_video, detail_type, quote_imagejson, location_icon, website_icon, quote_image6, quote_image7, quote_image8, quote_image9, quote_image10, quote_image11, quote_image12, quote_image13,detail_type1;
    private Boolean isLiked, isFav, isApproved;


    public invite_Item_OneImages_frame(String id, String cid, String catName, String img, String card_background, String email_icon) {
        this.id = id;
        this.catId = cid;
        this.catName = catName;
        this.imageBig = img;
        this.card_background = card_background;
        this.email_icon = email_icon;


    }

    public String getdetail_type1() {
        return detail_type1;
    }

    public String getlocation_icon() {
        return location_icon;
    }
    public String getwebsite_icon() {
        return website_icon;
    }
    public String getquote_image6() {
        return quote_image6;
    }

    public String getquote_image7() {
        return quote_image7;
    }

    public String getquote_image8() {
        return quote_image8;
    }

    public String getquote_image9() {
        return quote_image9;
    }

    public String getquote_image10() {
        return quote_image10;
    }

    public String getquote_image11() {
        return quote_image11;
    }

    public String getquote_image12() {
        return quote_image12;
    }

    public String getquote_image13() {
        return quote_image13;
    }


    public String getAd_on_off() {
        return ad_on_off;
    }

    public String getdetail_type() {
        return detail_type;
    }

    public String getId() {
        return id;
    }

    public String getcard_background() {
        return card_background;
    }

    public String getemail_icon() {
        return email_icon;
    }

    public String getcontact_icon() {
        return contact_icon;
    }

    public String getquote_video() {
        return quote_video;
    }

    public String getquote_pv_video() {
        return quote_pv_video;
    }




    public String getquote_imagejson() {
        return quote_imagejson;
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

    public String getFont2() {
        return font2;
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