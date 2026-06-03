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
            android.util.Log.e("#ShapeAPI_raw", json + "");

            JSONObject root = new JSONObject(json);

            // QUOTES_DIARY is a JSONArray directly
            JSONArray arr = root.optJSONArray("QUOTES_DIARY");
            if (arr == null) {
                // Try as object with nested array
                JSONObject diary = root.optJSONObject("QUOTES_DIARY");
                if (diary != null) {
                    String[] keys = {"image_quotes", "data", "images", "items", "result"};
                    for (String key : keys) {
                        arr = diary.optJSONArray(key);
                        if (arr != null) break;
                    }
                }
            }
            if (arr == null) {
                android.util.Log.e("#ShapeAPI_err", "No array found in response");
                return "0";
            }

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                if (obj.has("success")) continue; // skip status objects
                String id    = obj.optString("id", "");
                String catId = obj.optString("cat_id", "");
                String imgB  = obj.optString("quote_image_b", "").replace(" ", "%20");
                String imgB1 = obj.optString("quote_image_b1", "").replace(" ", "%20");
                if (!id.isEmpty()) {
                    list.add(new invite_Item_Shape(id, catId, imgB, imgB1));
                }
            }
            android.util.Log.e("#ShapeAPI_count", "count=" + list.size());
            return "1";
        } catch (Exception e) {
            android.util.Log.e("#ShapeAPI_err", e.getMessage() + "");
            return "0";
        }
    }
}
