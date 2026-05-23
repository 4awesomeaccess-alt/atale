package com.example.newcardmaker.invite_online_database;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;


import com.example.newcardmaker.BuildConfig;
import com.example.newcardmaker.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.yakivmospan.scytale.Store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;

import javax.crypto.SecretKey;
import javax.net.ssl.HttpsURLConnection;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class invite_Methods {

    private Context context;
    private boolean isClicked = false;
    private SecretKey key;

    public invite_Methods(Context context) {
        this.context = context;
        try {

            Store store = new Store(context);
            if (!store.hasKey(
                    BuildConfig.ENC_KEY)) {
                key = store.generateSymmetricKey(BuildConfig.ENC_KEY, null);
            } else {
                key = store.getSymmetricKey(BuildConfig.ENC_KEY, null);
            }


        } catch (Exception e) {

        }
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            InputStream input;
            if (src.contains("https://")) {
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            } else {
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                input = connection.getInputStream();
            }

            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();

        point.x = display.getWidth();
        point.y = display.getHeight();

        columnWidth = point.x;
        return columnWidth;
    }


    public void getVerifyDialog(String title, String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton(context.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();
    }


    public String format(Number number) {
        char[] suffix = {' ', 'k', 'M', 'B', 'T', 'P', 'E'};
        long numValue = number.longValue();
        int value = (int) Math.floor(Math.log10(numValue));
        int base = value / 3;
        if (value >= 3 && base < suffix.length) {
            return new DecimalFormat("#0.0").format(numValue / Math.pow(10, base * 3)) + suffix[base];
        } else {
            return new DecimalFormat("#,##0").format(numValue);
        }
    }

    public RequestBody getAPIRequest(String method, int page, String quotesID, String searchText,
                                     String like, String catID, String type, String tags, String colorText,
                                     String font, String colorBG, String email, String password,
                                     String name, String phone, String userID, String reportMessage, File file) {

        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new invite_API());
        jsObj.addProperty("method_name", method);
        jsObj.addProperty("package_name", context.getPackageName());

        switch (method) {
            case invite_AppConstants.METHOD_HOME:
                jsObj.addProperty("user_id", userID);
                break;
            case invite_AppConstants.METHOD_SEARCH_IMAGE:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("search_image_quotes", searchText);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_SEARCH_TEXT:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("search_text_quotes", searchText);
                jsObj.addProperty("page", page);
                break;

            /*................Photo Editor.............*/

            case invite_AppConstants.METHOD_CAT_PHOTOEDTIOR:
                jsObj.addProperty("image_quotes_cat_id", catID);
                break;

            case invite_AppConstants.METHOD_IMAGE_PHOTOEDTIOR:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_quotes_cat_id", catID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_EditorVIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_EditorDOWNLOAD:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_EDITOR_VIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_single_quotes_id", quotesID);
                break;
            case invite_AppConstants.METHOD_EDITOR_DOWNLOAD:
                jsObj.addProperty("quotes_download_id", quotesID);
                break;

            case invite_AppConstants.METHOD_EDITOR_ALL:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;

            case invite_AppConstants.METHOD_Editor_AdOFF:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_Editor_AdON:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;

            /*................Photo Frame.............*/
            case invite_AppConstants.METHOD_CAT_PHOTOFRAME:
                jsObj.addProperty("image_quotes_cat_id", catID);
                break;

            case invite_AppConstants.METHOD_IMAGE_PHOTOFRAME:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_quotes_cat_id", catID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_FRAMEVIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_FRAMEDOWNLOAD:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_FRAME_VIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_single_quotes_id", quotesID);
                break;
            case invite_AppConstants.METHOD_FRAME_DOWNLOAD:
                jsObj.addProperty("quotes_download_id", quotesID);
                break;

            case invite_AppConstants.METHOD_FRAME_ALL:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;

            case invite_AppConstants.METHOD_FRAME_AdOFF:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_FRAME_AdON:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            /*................Dual Photo Frame.............*/
            case invite_AppConstants.METHOD_CAT_PHOTODUAL:
                jsObj.addProperty("image_quotes_cat_id", catID);
                break;

            case invite_AppConstants.METHOD_IMAGE_PHOTODUAL:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_quotes_cat_id", catID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_DUALVIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_DUALDOWNLOAD:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_DUAL_VIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_single_quotes_id", quotesID);
                break;
            case invite_AppConstants.METHOD_DUAL_DOWNLOAD:
                jsObj.addProperty("quotes_download_id", quotesID);
                break;

            case invite_AppConstants.METHOD_DUAL_ALL:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_DUAL_AdOFF:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_DUAL_AdON:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            /*................Nyon Photo Effect.............*/
            case invite_AppConstants.METHOD_CAT_PHOTONYON:
                jsObj.addProperty("image_quotes_cat_id", catID);
                break;

            case invite_AppConstants.METHOD_IMAGE_PHOTONYON:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_quotes_cat_id", catID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_NYONVIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_NYONDOWNLOAD:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_NYON_VIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_single_quotes_id", quotesID);
                break;
            case invite_AppConstants.METHOD_NYON_DOWNLOAD:
                jsObj.addProperty("quotes_download_id", quotesID);
                break;

            case invite_AppConstants.METHOD_NYON_ALL:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_NYON_AdOFF:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_NYON_AdON:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            /*................Sticker.............*/
            case invite_AppConstants.METHOD_CAT_PHOTOSTICKER:
                jsObj.addProperty("image_quotes_cat_id", catID);
                break;
            case invite_AppConstants.METHOD_CAT_PHOTOSTICKER_All:
                jsObj.addProperty("image_quotes_cat_id", catID);
                break;

            case invite_AppConstants.METHOD_IMAGE_PHOTOSTICKER:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_quotes_cat_id", catID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_STICKERVIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_STICKERDOWNLOAD:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_STICKER_VIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_single_quotes_id", quotesID);
                break;
            case invite_AppConstants.METHOD_STICKER_DOWNLOAD:
                jsObj.addProperty("quotes_download_id", quotesID);
                break;

            case invite_AppConstants.METHOD_STICKER_ALL:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_STICKER_AdOFF:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_STICKER_AdON:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            /*................Greeting Card.............*/
            case invite_AppConstants.METHOD_CAT_PHOTOGREETING:
                jsObj.addProperty("image_quotes_cat_id", catID);
                break;

            case invite_AppConstants.METHOD_IMAGE_PHOTOGREETING:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_quotes_cat_id", catID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_IMAGE_PHOTOGREETING10:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_quotes_cat_id", catID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_IMAGE_PHOTOGREETING1:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;

            case invite_AppConstants.METHOD_IMAGE_All_PHOTOGREETING11:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_IMAGE_PHOTOGREETING11:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_GREETINGVIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_GREETINGDOWNLOAD:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_GREETING_VIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_single_quotes_id", quotesID);
                break;
            case invite_AppConstants.METHOD_GREETING_DOWNLOAD:
                jsObj.addProperty("quotes_download_id", quotesID);
                break;
            case invite_AppConstants.METHOD_GREETING_DOWNLOAD_LIKE:
                jsObj.addProperty("quotes_download_id", quotesID);
                break;

            case invite_AppConstants.METHOD_GREETING_ALL:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_GREETING_AdOFF:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_GREETING_AdON:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            /*................Greeting Card.............*/
            case invite_AppConstants.METHOD_CAT_PHOTOIMAGE:
                jsObj.addProperty("image_quotes_cat_id", catID);
                break;

            case invite_AppConstants.METHOD_IMAGE_PHOTOIMAGE:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_quotes_cat_id", catID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_IMAGEVIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_IMAGEDOWNLOAD:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_IMAGE_VIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_single_quotes_id", quotesID);
                break;
            case invite_AppConstants.METHOD_IMAGE_DOWNLOAD:
                jsObj.addProperty("quotes_download_id", quotesID);
                break;

            case invite_AppConstants.METHOD_IMAGE_ALL:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_IMAGE_AdOFF:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_IMAGE_AdON:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            /*................Wallpaper.............*/
            case invite_AppConstants.METHOD_CAT_PHOTOWALL:
                jsObj.addProperty("image_quotes_cat_id", catID);
                break;

          /*  case invite_AppConstants.METHOD_CAT_PHOTOWALL_dialog:
                jsObj.addProperty("image_quotes_cat_id", catID);
                break;
*/
            /*................Wallpaper.............*/
            case invite_AppConstants.METHOD_CAT_PHOTOWALL1:
                jsObj.addProperty("image_quotes_cat_id", catID);
                break;

            case invite_AppConstants.METHOD_IMAGE_PHOTOWALL:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_quotes_cat_id", catID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_WALLVIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_WALLDOWNLOAD:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_WALL_VIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_single_quotes_id", quotesID);
                break;
            case invite_AppConstants.METHOD_WALL_DOWNLOAD:
                jsObj.addProperty("quotes_download_id", quotesID);
                break;

            case invite_AppConstants.METHOD_WALL_ALL:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_WALL_AdOFF:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_WALL_AdON:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            /*................GIF.............*/
            case invite_AppConstants.METHOD_CAT_PHOTOGIF:
                jsObj.addProperty("image_quotes_cat_id", catID);
                break;

            case invite_AppConstants.METHOD_IMAGE_PHOTOGIF:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_quotes_cat_id", catID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_GIFVIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_GIFDOWNLOAD:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_GIF_VIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_single_quotes_id", quotesID);
                break;
            case invite_AppConstants.METHOD_GIF_DOWNLOAD:
                jsObj.addProperty("quotes_download_id", quotesID);
                break;

            case invite_AppConstants.METHOD_GIF_ALL:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_GIF_AdOFF:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_GIF_AdON:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            /*................Quotes Image............*/
            case invite_AppConstants.METHOD_CAT_PHOTOQUOTEIMG:
                jsObj.addProperty("image_quotes_cat_id", catID);
                break;

            case invite_AppConstants.METHOD_IMAGE_PHOTOQUOTEIMG:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_quotes_cat_id", catID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_QUOTEIMGVIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_TOP_QUOTEIMGDOWNLOAD:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_QUOTEIMG_VIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_single_quotes_id", quotesID);
                break;
            case invite_AppConstants.METHOD_QUOTEIMG_DOWNLOAD:
                jsObj.addProperty("quotes_download_id", quotesID);
                break;

            case invite_AppConstants.METHOD_QUOTEIMG_ALL:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_QUOTEIMG_AdOFF:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_QUOTEIMG_AdON:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            /*................Quotes Text............*/
            case invite_AppConstants.METHOD_CAT_PHOTOQUOTETXT:
                jsObj.addProperty("image_quotes_cat_id", catID);
                break;

            case invite_AppConstants.METHOD_QUOTES_CAT_TEXT:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("text_quotes_cat_id", catID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_QUOTES_POPULAR_TEXT:
                jsObj.addProperty("page", page);
                jsObj.addProperty("user_id", userID);
                break;
            case invite_AppConstants.METHOD_TOP_QUOTETXTDOWNLOAD:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_QUOTETXT_VIEW:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_single_quotes_id", quotesID);
                break;
            case invite_AppConstants.METHOD_SINGLE_IMAGE:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("image_single_quotes_id", quotesID);
                break;
            case invite_AppConstants.METHOD_SINGLE_TEXT:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("text_single_quotes_id", quotesID);
                break;
            case invite_AppConstants.METHOD_QUOTES_LATEST_IMAGE:
                jsObj.addProperty("page", page);
                jsObj.addProperty("user_id", userID);
                break;
            case invite_AppConstants.METHOD_QUOTES_LATEST_TEXT:
                jsObj.addProperty("page", page);
                jsObj.addProperty("user_id", userID);
                break;
            case invite_AppConstants.METHOD_QUOTES_TOP_LIKE_TEXT:
                jsObj.addProperty("page", page);
                jsObj.addProperty("user_id", userID);
                break;
            case invite_AppConstants.METHOD_QUOTES_FAVOURITE:
                jsObj.addProperty("type", type);
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("page", page);
                break;
            case invite_AppConstants.METHOD_FAV_QUOTE:
                jsObj.addProperty("type", type);
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("post_id", quotesID);
                break;
            case invite_AppConstants.METHOD_USER_QUOTES_TEXT:
                jsObj.addProperty("page", page);
                jsObj.addProperty("user_id", userID);
                break;
            case invite_AppConstants.METHOD_USER_QUOTES_IMAGES:
                jsObj.addProperty("page", page);
                jsObj.addProperty("user_id", userID);
                break;
            case invite_AppConstants.METHOD_USER_QUOTES_DELETE:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("quote_id", quotesID);
                jsObj.addProperty("type", type);
                break;
            case invite_AppConstants.METHOD_LIKE_IMAGE:
                jsObj.addProperty("like", like);
                jsObj.addProperty("quote_id", quotesID);
                jsObj.addProperty("user_id", userID);
                break;
            case invite_AppConstants.METHOD_LIKE_TEXT:
                jsObj.addProperty("like", like);
                jsObj.addProperty("quote_id", quotesID);
                jsObj.addProperty("user_id", userID);
                break;
            case invite_AppConstants.METHOD_PROFILE:
                jsObj.addProperty("id", userID);
                break;
            case invite_AppConstants.METHOD_PROFILE_UPDATE:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                break;
            case invite_AppConstants.METHOD_LOGIN:
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("auth_id", userID);
                jsObj.addProperty("type", type);
                break;
            case invite_AppConstants.METHOD_REGISTER:
                jsObj.addProperty("name", name);
                jsObj.addProperty("email", email);
                jsObj.addProperty("password", password);
                jsObj.addProperty("phone", phone);
                jsObj.addProperty("type", type);
                jsObj.addProperty("auth_id", userID);
                break;
            case invite_AppConstants.METHOD_FORGOT_PASS:
                jsObj.addProperty("user_email", email);
                break;
            case invite_AppConstants.METHOD_UPLOAD_IMAGE:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("cat_id", catID);
                jsObj.addProperty("quote_tags", tags);
                break;
            case invite_AppConstants.METHOD_UPLOAD_TEXT:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("cat_id", catID);
                jsObj.addProperty("quote", searchText);
                jsObj.addProperty("quote_tags", tags);
                jsObj.addProperty("quote_font", font);
                jsObj.addProperty("font_color", colorText);
                jsObj.addProperty("bg_color", colorBG);
                break;
            case invite_AppConstants.METHOD_REPORT:
                jsObj.addProperty("user_id", userID);
                jsObj.addProperty("post_id", quotesID);
                jsObj.addProperty("type", type);
                jsObj.addProperty("report", reportMessage);
                break;
        }

        if (file != null) {
            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");

            if (method.equals(invite_AppConstants.METHOD_REGISTER) || method.equals(invite_AppConstants.METHOD_PROFILE_UPDATE)) {
                return new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("user_profile", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file))
                        .addFormDataPart("data", invite_API.toBase64(jsObj.toString()))
                        .build();
            } else {
                return new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("quote_image", file.getName(), RequestBody.create(MEDIA_TYPE_PNG, file))
                        .addFormDataPart("data", invite_API.toBase64(jsObj.toString()))
                        .build();
            }
        } else {
            return new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("data", invite_API.toBase64(jsObj.toString()))
                    .build();
        }
    }

    public void getInvalidUserDialog(String message) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        alertDialog.setTitle(context.getString(R.string.invalid_user));
        alertDialog.setMessage(message);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

}