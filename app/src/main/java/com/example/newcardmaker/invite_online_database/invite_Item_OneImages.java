package com.example.newcardmaker.invite_online_database;

import java.io.Serializable;

public class invite_Item_OneImages implements Serializable {

    private String id, catId, catName, imageBig, quote, likes, views, downloads, bgColor, font, font2, fontColor, ad_on_off, card_background, email_icon, contact_icon, quote_pv_video, quote_video, detail_type, quote_imagejson, location_icon, website_icon, quote_image6, quote_image7, quote_image8, quote_image9, quote_image10, quote_image11, quote_image12, quote_image13,detail_type1;
    private Boolean isLiked, isFav, isApproved;


    public invite_Item_OneImages(String id, String cid, String catName, String img, String quote, String totallikes, Boolean isliked,
                                 Boolean isFav, String totalviews, String totaldownload, String bg, String font, String font2, String fontColor, String cardBackground, String emailIcon, String contactIcon, String quotePvVideo, String quoteVideo, String detailType, String quote_imagejson
            , String location_icon, String website_icon, String quote_image6, String quote_image7, String quote_image8, String quote_image9, String quote_image10, String quote_image11, String quote_image12, String quote_image13, String detail_type1) {
        this.id = id;
        this.catId = cid;
        this.catName = catName;
        this.imageBig = img;
        this.quote = quote;
        this.likes = totallikes;
        this.isLiked = isliked;
        this.isFav = isFav;
        this.views = totalviews;
        this.downloads = totaldownload;
        this.bgColor = bg;
        this.font = font;
        this.font2 = font2;
        this.fontColor = fontColor;
        this.card_background = cardBackground;
        this.email_icon = emailIcon;
        this.contact_icon = contactIcon;
        this.quote_video = quotePvVideo;
        this.quote_pv_video = quoteVideo;
        this.detail_type = detailType;
        this.quote_imagejson = quote_imagejson;

        this.location_icon = location_icon;
        this.website_icon =  website_icon;
        this.quote_image6 =  quote_image6;
        this.quote_image7 =  quote_image7;
        this.quote_image8 =  quote_image8;
        this.quote_image9 =  quote_image9;
        this.quote_image10 = quote_image10;
        this.quote_image11 = quote_image11;
        this.quote_image12 = quote_image12;
        this.quote_image13 = quote_image13;

        this.detail_type1 = detail_type1;
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