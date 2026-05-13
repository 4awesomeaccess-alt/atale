package com.example.newcardmaker.invite_online_database;

import static android.util.Log.ASSERT;

import android.os.AsyncTask;
import android.util.Log;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class invite_Load_OneImages_frame extends AsyncTask<String, String, String> {

    private RequestBody requestBody;
    private invite_OneImagesListener_frame quotesListener;
    private ArrayList<invite_Item_OneImages_frame> arrayList;
    private String verifyStatus = "0", message = "0";
    private int total_records = -1;

    public invite_Load_OneImages_frame(invite_OneImagesListener_frame quotesListener, RequestBody requestBody) {
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
        String json = invite_JSONParser.okhttpPost(invite_AppConstants.SERVER_URL, requestBody);
        try {
            JSONObject jOb = new JSONObject(json);
            JSONArray jsonArray = jOb.getJSONArray(invite_AppConstants.TAG_ROOT);

            Log.println(ASSERT,"jsonArray",""+jsonArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                if (!objJson.has(invite_AppConstants.TAG_SUCCESS)) {

                    if (objJson.has("num")) {
                        total_records = objJson.getInt("num");
                    }

                    String id = objJson.getString(invite_AppConstants.TAG_QUOTES_ID);
                    String cid = objJson.getString(invite_AppConstants.TAG_QUOTES_CAT_ID);
                    String cat_name = "", img = "", quote = "", bg = "", font = "", font2 = "", font_color = "";
                    if (objJson.has(invite_AppConstants.TAG_QUOTES_CAT_NAME)) {
                        cat_name = objJson.getString(invite_AppConstants.TAG_QUOTES_CAT_NAME);
                    }
                        img = objJson.getString(invite_AppConstants.TAG_QUOTES_IMAGE_BIG1);

                    if (objJson.has(invite_AppConstants.TAG_QUOTES_TEXT)) {
                        quote = objJson.getString(invite_AppConstants.TAG_QUOTES_TEXT);
                        bg = objJson.getString(invite_AppConstants.TAG_QUOTES_BG);
                        font = objJson.getString(invite_AppConstants.TAG_QUOTES_FONT);
                        font2 = objJson.getString(invite_AppConstants.TAG_QUOTES_FONT2);
                        font_color = objJson.getString(invite_AppConstants.TAG_QUOTES_FONT_COLOR);
                    }


                    String card_background = objJson.getString(invite_AppConstants.card_preview);
                    String email_icon = objJson.getString(invite_AppConstants.email_icon);

                    invite_Item_OneImages_frame itemQuotes = new invite_Item_OneImages_frame(id, cid, cat_name, img,card_background,email_icon);

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