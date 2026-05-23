package com.example.newcardmaker;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfFileManager {

    private static final String PREF_NAME = "pdf_file_list";
    private static final String KEY_PDF_LIST = "saved_pdfs";

    // ── PDF Item model
    public static class PdfItem {
        public String title;
        public String filePath;
        public long   savedAt; // timestamp

        public PdfItem(String title, String filePath, long savedAt) {
            this.title    = title;
            this.filePath = filePath;
            this.savedAt  = savedAt;
        }
    }

    // ── PDF save — list માં ઉમેરો
    public static void savePdf(Context ctx, String title, String filePath) {
        try {
            List<PdfItem> list = getAll(ctx);

            // Duplicate check — same path already exist?
            for (PdfItem item : list) {
                if (item.filePath.equals(filePath)) return;
            }

            JSONArray array = getRawArray(ctx);
            JSONObject obj  = new JSONObject();
            obj.put("title",    title);
            obj.put("filePath", filePath);
            obj.put("savedAt",  System.currentTimeMillis());
            array.put(obj);

            SharedPreferences prefs =
                    ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_PDF_LIST, array.toString()).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Get all saved PDFs
    public static List<PdfItem> getAll(Context ctx) {
        List<PdfItem> list = new ArrayList<>();
        try {
            JSONArray array = getRawArray(ctx);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String path = obj.optString("filePath", "");

                // ── File exist check — deleted files skip
                if (!new File(path).exists()) continue;

                list.add(new PdfItem(
                        obj.optString("title", "PDF " + (i + 1)),
                        path,
                        obj.optLong("savedAt", 0)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ── Delete one PDF from list (file + record)
    public static void delete(Context ctx, String filePath) {
        try {
            // File delete
            File f = new File(filePath);
            if (f.exists()) f.delete();

            // Record remove
            JSONArray oldArray = getRawArray(ctx);
            JSONArray newArray = new JSONArray();
            for (int i = 0; i < oldArray.length(); i++) {
                JSONObject obj = oldArray.getJSONObject(i);
                if (!obj.optString("filePath").equals(filePath)) {
                    newArray.put(obj);
                }
            }

            SharedPreferences prefs =
                    ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString(KEY_PDF_LIST, newArray.toString()).apply();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Raw JSON array
    private static JSONArray getRawArray(Context ctx) {
        try {
            SharedPreferences prefs =
                    ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            String json = prefs.getString(KEY_PDF_LIST, "[]");
            return new JSONArray(json);
        } catch (Exception e) {
            return new JSONArray();
        }
    }
}
