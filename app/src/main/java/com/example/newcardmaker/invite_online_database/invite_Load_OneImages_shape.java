package com.example.newcardmaker.invite_online_database;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.RequestBody;

public class invite_Load_OneImages_shape {

    private final RequestBody requestBody;
    private final invite_ShapeListener listener;
    private final ArrayList<invite_Item_Shape> list = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public invite_Load_OneImages_shape(invite_ShapeListener listener, RequestBody requestBody) {
        this.listener = listener;
        this.requestBody = requestBody;
    }

    public void execute() {
        listener.onStart();
        executor.execute(() -> {
            final String result = doInBackground();
            mainHandler.post(() -> listener.onEnd(result, list));
        });
    }

    private String doInBackground() {
        try {
            String json = invite_JSONParser.okhttpPost(invite_AppConstants.SERVER_URL, requestBody);
            Log.e("#ShapeAPI", json + "");

            JSONObject root = new JSONObject(json);
            JSONObject diary = root.optJSONObject("QUOTES_DIARY");
            if (diary == null) return "0";

            // Try multiple array keys
            JSONArray arr = null;
            String[] keys = {"image_quotes", "data", "images", "items"};
            for (String key : keys) {
                arr = diary.optJSONArray(key);
                if (arr != null) break;
            }
            if (arr == null) return "0";

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String id       = obj.optString("id", "");
                String catId    = obj.optString("cat_id", "");
                String imgB     = obj.optString("quote_image_b", "").replace(" ", "%20");
                String imgB1    = obj.optString("quote_image_b1", "").replace(" ", "%20");
                list.add(new invite_Item_Shape(id, catId, imgB, imgB1));
            }
            return "1";
        } catch (Exception e) {
            Log.e("#ShapeAPI_err", e.getMessage() + "");
            return "0";
        }
    }
}
