package com.example.newcardmaker;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageFileManager {

    private static final String PREF_NAME    = "image_file_list";
    private static final String KEY_IMG_LIST = "saved_images";

    // ── Image Item model
    public static class ImageItem {
        public String title;
        public String filePath;
        public long   savedAt;

        public ImageItem(String title, String filePath, long savedAt) {
            this.title    = title;
            this.filePath = filePath;
            this.savedAt  = savedAt;
        }
    }

    // ── Save image record
    public static void saveImage(Context ctx, String title, String filePath) {
        try {
            JSONArray array = getRawArray(ctx);

            // Duplicate check
            for (int i = 0; i < array.length(); i++) {
                if (array.getJSONObject(i)
                        .optString("filePath").equals(filePath)) return;
            }

            JSONObject obj = new JSONObject();
            obj.put("title",    title);
            obj.put("filePath", filePath);
            obj.put("savedAt",  System.currentTimeMillis());
            array.put(obj);

            SharedPreferences prefs =
                    ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_IMG_LIST, array.toString()).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Get all saved images
    public static List<ImageItem> getAll(Context ctx) {
        List<ImageItem> list = new ArrayList<>();
        try {
            JSONArray array = getRawArray(ctx);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj  = array.getJSONObject(i);
                String     path = obj.optString("filePath", "");
                if (!new File(path).exists()) continue; // skip deleted files
                list.add(new ImageItem(
                        obj.optString("title", "Image " + (i + 1)),
                        path,
                        obj.optLong("savedAt", 0)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── Delete one image (file + record)
    public static void delete(Context ctx, String filePath) {
        try {
            File f = new File(filePath);
            if (f.exists()) f.delete();

            JSONArray oldArray = getRawArray(ctx);
            JSONArray newArray = new JSONArray();
            for (int i = 0; i < oldArray.length(); i++) {
                JSONObject obj = oldArray.getJSONObject(i);
                if (!obj.optString("filePath").equals(filePath)) {
                    newArray.put(obj);
                }
            }

            ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    .edit()
                    .putString(KEY_IMG_LIST, newArray.toString())
                    .apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Raw JSON array
    private static JSONArray getRawArray(Context ctx) {
        try {
            String json = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    .getString(KEY_IMG_LIST, "[]");
            return new JSONArray(json);
        } catch (Exception e) {
            return new JSONArray();
        }
    }
}
