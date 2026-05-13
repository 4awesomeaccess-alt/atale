package com.example.newcardmaker.invite_online_database;

import static android.util.Log.ASSERT;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class invite_LoadSubCat_main extends AsyncTask<String, String, String> {
    private RequestBody requestBody;
    private invite_SubCategoryListener_main subcategoryListener;
    private ArrayList<invite_ItemSubCat_main> arrayList_image_cat;
    private ArrayList<invite_ItemSubCat_main> arrayList_text_cat;
    private String message = "", verifyStatus = "0";

    public invite_LoadSubCat_main(invite_SubCategoryListener_main subcategoryListener, RequestBody requestBody) {
        this.subcategoryListener = subcategoryListener;
        this.requestBody = requestBody;
        arrayList_image_cat = new ArrayList<>();
        arrayList_text_cat = new ArrayList<>();
    }

    @Override
    protected void onPreExecute() {
        subcategoryListener.onStart();
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {
            String json = invite_JSONParser.okhttpPost(invite_AppConstants.SERVER_URL, requestBody);
            JSONObject jOb = null;

            Log.println(ASSERT,"json",json+"");
            try {
                jOb = new JSONObject(json);

                JSONObject jsonObj = jOb.getJSONObject(invite_AppConstants.TAG_ROOT);
                Log.println(ASSERT,"jsonObj",jsonObj+"");
                JSONArray jsonArray_image = jsonObj.getJSONArray("image_quotes_cat");
                for (int i = 0; i < jsonArray_image.length(); i++) {
                    JSONObject c = jsonArray_image.getJSONObject(i);

                    String id = c.getString(invite_AppConstants.TAG_CID);
                    String detail = c.getString(invite_AppConstants.detail);
                    String detail1 = c.getString(invite_AppConstants.detail1);
                    String name = c.getString(invite_AppConstants.TAG_CAT_NAME);
                    String image = c.getString(invite_AppConstants.TAG_CAT_IMAGE).replace(" ", "%20");

                    invite_ItemSubCat_main itemCat = new invite_ItemSubCat_main(id, name, image,detail,detail1);
                    arrayList_image_cat.add(itemCat);

                }
                return "1";
            } catch (JSONException e) {
                JSONArray jsonArray = null;
                try {
                    jsonArray = jOb.getJSONArray(invite_AppConstants.TAG_ROOT);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject c = jsonArray.getJSONObject(i);
                        if (c.has(invite_AppConstants.TAG_SUCCESS)) {
                            verifyStatus = c.getString(invite_AppConstants.TAG_SUCCESS);
                            message = c.getString(invite_AppConstants.TAG_MSG);
                        }
                    }
                    return "1";
                } catch (Exception e1) {
                    e1.printStackTrace();
                    return "0";
                }
            } catch (Exception e) {
                e.printStackTrace();
                return "0";
            }

        } catch (Exception e1) {
            e1.printStackTrace();
            return "0";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        subcategoryListener.onEnd(s, verifyStatus, message, arrayList_image_cat, arrayList_text_cat);
        super.onPostExecute(s);
    }
}