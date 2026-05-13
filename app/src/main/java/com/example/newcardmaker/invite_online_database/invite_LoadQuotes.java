package com.example.newcardmaker.invite_online_database;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class invite_LoadQuotes extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private invite_QuotesListener quotesListener;
    private ArrayList<invite_ItemQuotes> arrayList;
    private String verifyStatus = "0", message = "0";
    private int total_records = -1;

    public invite_LoadQuotes(invite_QuotesListener quotesListener, RequestBody requestBody) {
        this.quotesListener = quotesListener;
        this.requestBody = requestBody;
        arrayList = new ArrayList<>();
    }
    public static Boolean isFav;
    @Override
    protected void onPreExecute() {
        quotesListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String json = invite_JSONParser.okhttpPost(invite_AppConstants.SERVER_URL, requestBody);
        try {
            JSONObject jOb = new JSONObject(json);
            JSONArray jsonArray = jOb.getJSONArray(invite_AppConstants.TAG_ROOT);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);
                if (!objJson.has(invite_AppConstants.TAG_SUCCESS)) {
                    if (objJson.has("num")) {
                        total_records = objJson.getInt("num");
                    }

                    String id = objJson.getString(invite_AppConstants.TAG_QUOTES_ID);
                    String cid = objJson.getString(invite_AppConstants.TAG_QUOTES_CAT_ID);
                    String cat_name = "", img = "", img_thumb = "", img1 = "", img_thumb1 = "", quote = "", bg = "", font = "", font_color = "";
                    if (objJson.has(invite_AppConstants.TAG_QUOTES_CAT_NAME)) {
                        cat_name = objJson.getString(invite_AppConstants.TAG_QUOTES_CAT_NAME);
                    }
                    if (objJson.has(invite_AppConstants.TAG_QUOTES_IMAGE_BIG)) {
                        img = objJson.getString(invite_AppConstants.TAG_QUOTES_IMAGE_BIG).replace(" ", "%20");
                    }
                    if (objJson.has(invite_AppConstants.TAG_QUOTES_TEXT)) {
                        quote = objJson.getString(invite_AppConstants.TAG_QUOTES_TEXT);
                        bg = objJson.getString(invite_AppConstants.TAG_QUOTES_BG);
                    }

                    Boolean isliked = Boolean.parseBoolean(objJson.getString(invite_AppConstants.TAG_QUOTES_LIKED));
                    isFav = Boolean.parseBoolean(objJson.getString(invite_AppConstants.TAG_QUOTES_FAV));
                    String totalviews = objJson.getString(invite_AppConstants.TAG_QUOTES_TOTAL_VIEWS);
                    String totallikes = objJson.getString(invite_AppConstants.TAG_QUOTES_TOTAL_LIKES);
                    String totaldownload = objJson.getString(invite_AppConstants.TAG_QUOTES_TOTAL_DOWNLOADS);

                    invite_ItemQuotes itemQuotes = new invite_ItemQuotes(id, cid, cat_name, img, img1, img_thumb, img_thumb1, quote, totallikes, isliked, isFav, totalviews, totaldownload, bg, font, font_color);

                    if (objJson.has(invite_AppConstants.TAG_QUOTES_APPROVED)) {
                        itemQuotes.setIsApproved(objJson.getBoolean(invite_AppConstants.TAG_QUOTES_APPROVED));
                    }
                    arrayList.add(itemQuotes);
                } else {
                    verifyStatus = objJson.getString(invite_AppConstants.TAG_SUCCESS);
                    message = objJson.getString(invite_AppConstants.TAG_MSG);
                }
            }

            return "1";
        } catch (Exception ee) {
            ee.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        quotesListener.onEnd(s, verifyStatus, message, arrayList, total_records);
        super.onPostExecute(s);
    }
}