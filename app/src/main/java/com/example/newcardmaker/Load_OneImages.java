package com.example.newcardmaker;

import static android.util.Log.ASSERT;

import android.os.AsyncTask;
import android.util.Log;

import com.example.newcardmaker.invite_online_database.invite_JSONParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class Load_OneImages extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private OneImagesListener quotesListener;
    private ArrayList<Item_OneImages> arrayList;
    private String verifyStatus = "0", message = "0";
    private int total_records = -1;

    public Load_OneImages(OneImagesListener quotesListener, RequestBody requestBody) {
        this.quotesListener = quotesListener;
        this.requestBody = requestBody;
        arrayList = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        quotesListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        String json = invite_JSONParser.okhttpPost(AppConstants.SERVER_URL, requestBody);
        Log.println(ASSERT,"#2json", json + "");
        try {
            JSONObject jOb = new JSONObject(json);
            JSONArray jsonArray = jOb.getJSONArray(AppConstants.TAG_ROOT);
            Log.println(ASSERT,"#2jsonArray", jsonArray.length() + "");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                if (!objJson.has(AppConstants.TAG_SUCCESS)) {

                    if (objJson.has("num")) {
                        total_records = objJson.getInt("num");
                    }

                    String id = objJson.getString(AppConstants.TAG_QUOTES_ID);
                    String cid = objJson.getString(AppConstants.TAG_QUOTES_CAT_ID);
                    String cat_name = "", img = "", quote = "", bg = "", font = "", font_color = "";
                    if (objJson.has(AppConstants.TAG_QUOTES_CAT_NAME)) {
                        cat_name = objJson.getString(AppConstants.TAG_QUOTES_CAT_NAME);
                    }
                    if (objJson.has(AppConstants.TAG_QUOTES_IMAGE_BIG)) {
                        img = objJson.getString(AppConstants.TAG_QUOTES_IMAGE_BIG).replace(" ", "%20");
                    }

                    if (objJson.has(AppConstants.TAG_QUOTES_TEXT)) {
                        quote = objJson.getString(AppConstants.TAG_QUOTES_TEXT);
                        bg = objJson.getString(AppConstants.TAG_QUOTES_BG);
                        font = objJson.getString(AppConstants.TAG_QUOTES_FONT);
                        font_color = objJson.getString(AppConstants.TAG_QUOTES_FONT_COLOR);
                    }

                    Boolean isliked = Boolean.parseBoolean(objJson.getString(AppConstants.TAG_QUOTES_LIKED));
                    Boolean isFav = Boolean.parseBoolean(objJson.getString(AppConstants.TAG_QUOTES_FAV));
                    String totalviews = objJson.getString(AppConstants.TAG_QUOTES_TOTAL_VIEWS);
                    String totallikes = objJson.getString(AppConstants.TAG_QUOTES_TOTAL_LIKES);
                    String totaldownload = objJson.getString(AppConstants.TAG_QUOTES_TOTAL_DOWNLOADS);
                    String ad_on_off = objJson.getString(AppConstants.TAG_AD_ONOFF);

                    Log.println(ASSERT,"#22 img", img + "");


                    Item_OneImages itemQuotes = new Item_OneImages(id, cid, cat_name, img, quote, totallikes, isliked, isFav, totalviews,
                            totaldownload, bg, font, font_color, ad_on_off);

                    if (objJson.has(AppConstants.TAG_QUOTES_APPROVED)) {
                        itemQuotes.setIsApproved(objJson.getBoolean(AppConstants.TAG_QUOTES_APPROVED));
                    }

                    arrayList.add(itemQuotes);

                } else {
                    verifyStatus = objJson.getString(AppConstants.TAG_SUCCESS);
                    message = objJson.getString(AppConstants.TAG_MSG);
                }
            }

            return "1";
        } catch (Exception ee) {
            Log.println(ASSERT,"#4nn error",ee+"");
            Log.e("#2eximage", ee.getMessage());
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