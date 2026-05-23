package com.example.newcardmaker.invite_online_database;

import static android.util.Log.ASSERT;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.RequestBody;

public class invite_Load_OneImages {

    private final RequestBody requestBody;
    private final invite_OneImagesListener quotesListener;
    private final ArrayList<invite_Item_OneImages> arrayList = new ArrayList<>();

    private String verifyStatus = "0";
    private String message = "0";
    private int total_records = -1;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public invite_Load_OneImages(invite_OneImagesListener quotesListener, RequestBody requestBody) {
        this.quotesListener = quotesListener;
        this.requestBody = requestBody;
    }

    public void execute() {
        quotesListener.onStart();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                final String result = doInBackground();

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        quotesListener.onEnd(result, verifyStatus, message, arrayList, total_records);
                    }
                });
            }
        });
    }

    private String doInBackground() {
        Log.println(ASSERT, "#requestBody", String.valueOf(requestBody));

        String json = invite_JSONParser.okhttpPost(invite_AppConstants.SERVER_URL, requestBody);
        Log.println(ASSERT, "#4innewjson", String.valueOf(json));

        try {
            JSONObject jOb = new JSONObject(json);
            JSONArray jsonArray = jOb.getJSONArray(invite_AppConstants.TAG_ROOT);
            Log.println(ASSERT, "#jsonArray", String.valueOf(jsonArray));

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject objJson = jsonArray.getJSONObject(i);

                if (!objJson.has(invite_AppConstants.TAG_SUCCESS)) {

                    if (objJson.has("num")) {
                        total_records = objJson.getInt("num");
                    }

                    String id = objJson.optString(invite_AppConstants.TAG_QUOTES_ID, "");
                    String cid = objJson.optString(invite_AppConstants.TAG_QUOTES_CAT_ID, "");
                    String cat_name = objJson.optString(invite_AppConstants.TAG_QUOTES_CAT_NAME, "");
                    String img = objJson.optString(invite_AppConstants.TAG_QUOTES_IMAGE_BIG, "").replace(" ", "%20");
                    String quote = objJson.optString(invite_AppConstants.TAG_QUOTES_TEXT, "");
                    String bg = objJson.optString(invite_AppConstants.TAG_QUOTES_BG, "");
                    String font = objJson.optString(invite_AppConstants.TAG_QUOTES_FONT, "");
                    String font2 = objJson.optString(invite_AppConstants.TAG_QUOTES_FONT2, "");
                    String font_color = objJson.optString(invite_AppConstants.TAG_QUOTES_FONT_COLOR, "");

                    boolean isliked = Boolean.parseBoolean(objJson.optString(invite_AppConstants.TAG_QUOTES_LIKED, "false"));
                    boolean isFav = Boolean.parseBoolean(objJson.optString(invite_AppConstants.TAG_QUOTES_FAV, "false"));

                    String totalviews = objJson.optString(invite_AppConstants.TAG_QUOTES_TOTAL_VIEWS, "");
                    String totallikes = objJson.optString(invite_AppConstants.TAG_QUOTES_TOTAL_LIKES, "");
                    String totaldownload = objJson.optString(invite_AppConstants.TAG_QUOTES_TOTAL_DOWNLOADS, "");
                    String card_background = objJson.optString(invite_AppConstants.card_background, "");
                    String email_icon = objJson.optString(invite_AppConstants.email_icon, "");
                    String contact_icon = objJson.optString(invite_AppConstants.contact_icon, "");
                    String quote_video = objJson.optString(invite_AppConstants.quote_video, "");
                    String quote_pv_video = objJson.optString(invite_AppConstants.quote_pv_video, "");
                    String detail_type = objJson.optString(invite_AppConstants.detail_type, "");
                    String quote_imagejson = objJson.optString(invite_AppConstants.quote_imagejson, "");
                    String location_icon = objJson.optString(invite_AppConstants.location_icon, "");
                    String website_icon = objJson.optString(invite_AppConstants.website_icon, "");
                    String quote_image6 = objJson.optString(invite_AppConstants.quote_image6, "");
                    String quote_image7 = objJson.optString(invite_AppConstants.quote_image7, "");
                    String quote_image8 = objJson.optString(invite_AppConstants.quote_image8, "");
                    String quote_image9 = objJson.optString(invite_AppConstants.quote_image9, "");
                    String quote_image10 = objJson.optString(invite_AppConstants.quote_image10, "");
                    String quote_image11 = objJson.optString(invite_AppConstants.quote_image11, "");
                    String quote_image12 = objJson.optString(invite_AppConstants.quote_image12, "");
                    String quote_image13 = objJson.optString(invite_AppConstants.quote_image13, "");
                    String detail_type1 = objJson.optString(invite_AppConstants.detail_type1, "");

                    invite_Item_OneImages itemQuotes = new invite_Item_OneImages(
                            id, cid, cat_name, img, quote, totallikes, isliked, isFav, totalviews,
                            totaldownload, bg, font, font2, font_color, card_background, email_icon,
                            contact_icon, quote_pv_video, quote_video, detail_type, quote_imagejson,
                            location_icon, website_icon, quote_image6, quote_image7, quote_image8,
                            quote_image9, quote_image10, quote_image11, quote_image12, quote_image13,
                            detail_type1
                    );

                    if (objJson.has(invite_AppConstants.TAG_QUOTES_APPROVED)) {
                        itemQuotes.setIsApproved(objJson.optBoolean(invite_AppConstants.TAG_QUOTES_APPROVED, false));
                    }

                    arrayList.add(itemQuotes);
                } else {
                    verifyStatus = objJson.optString(invite_AppConstants.TAG_SUCCESS, "0");
                    message = objJson.optString(invite_AppConstants.TAG_MSG, "0");
                }
            }

            return "1";
        } catch (Exception e) {
            Log.println(ASSERT, "#error", String.valueOf(e.getMessage()));
            e.printStackTrace();
            return "0";
        }
    }
}