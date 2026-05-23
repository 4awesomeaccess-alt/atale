package com.example.newcardmaker.invite_online_database;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.example.newcardmaker.BuildConfig;


public class invite_AppConstants {

    public static final String SERVER_URL = BuildConfig.SERVER_URL + "api.php";

    public static final String METHOD_HOME = "get_home";

    /*................Photo Editor.............*/

    public static final String METHOD_CAT_PHOTOEDTIOR = "get_sub_photo_editor";
    public static final String METHOD_IMAGE_PHOTOEDTIOR = "get_image_photo_editor";

    /*................Photo Editor Top VIEW - DOWNLOAD.............*/

    public static final String METHOD_TOP_EditorVIEW = "image_quotes_popular";
    public static final String METHOD_TOP_EditorDOWNLOAD = "image_quotes_top_download";
    public static final String METHOD_EDITOR_VIEW = "get_image_photo_editor_View";
    public static final String METHOD_EDITOR_DOWNLOAD = "get_image_photo_editor_download";
    public static final String METHOD_EDITOR_ALL = "get_all_image_photo_editor";
    public static final String METHOD_Editor_AdOFF = "get_image_photo_editor_off";
    public static final String METHOD_Editor_AdON = "get_image_photo_editor_on";

    /*..............Photo Frame................*/

    public static final String METHOD_CAT_PHOTOFRAME = "get_sub_photo_frame";
    public static final String METHOD_RELESECAT_PHOTOFRAME = "get_sub_photo_frame_relese";
    public static final String METHOD_IMAGE_PHOTOFRAME = "get_image_photo_frame";

    /*................Photo Frame Top VIEW - DOWNLOAD.............*/

    public static final String METHOD_TOP_FRAMEVIEW = "image_quotes_popular_frame";
    public static final String METHOD_TOP_FRAMEDOWNLOAD = "image_quotes_top_download_frame";

    public static final String METHOD_FRAME_VIEW = "get_image_photo_frame_View";
    public static final String METHOD_FRAME_DOWNLOAD = "get_image_photo_frame_download";
    public static final String METHOD_FRAME_ALL = "get_all_image_landscape_photo_frame";
    public static final String METHOD_FRAME_AdOFF = "get_image_photo_frame_off";
    public static final String METHOD_FRAME_AdON = "get_image_photo_frame_on";

    /*..............Dual Photo Frame................*/

    public static final String METHOD_CAT_PHOTODUAL = "get_sub_dual_photo_frame";
    public static final String METHOD_RELESECAT_PHOTODUAL = "get_sub_dual_photo_frame_relese";
    public static final String METHOD_IMAGE_PHOTODUAL = "get_image_dual_photo_frame";

    /*................Dual Photo Frame Top VIEW - DOWNLOAD.............*/

    public static final String METHOD_TOP_DUALVIEW = "image_quotes_popular_dual";
    public static final String METHOD_TOP_DUALDOWNLOAD = "image_quotes_top_download_dual";

    public static final String METHOD_DUAL_VIEW = "get_image_dual_photo_frame_View";
    public static final String METHOD_DUAL_DOWNLOAD = "get_image_dual_photo_frame_download";
    public static final String METHOD_DUAL_ALL = "get_all_image_dual_photo_frame";
    public static final String METHOD_DUAL_AdOFF = "get_image_photo_dual_off";
    public static final String METHOD_DUAL_AdON = "get_image_photo_dual_on";

    /*..............Nyon Photo Frame................*/

    public static final String METHOD_CAT_PHOTONYON = "get_sub_nyon_effect_frame";
    public static final String METHOD_RELESECAT_PHOTONYON = "get_sub_nyon_effect_relese";
    public static final String METHOD_IMAGE_PHOTONYON = "get_image_nyon_effect_frame";

    /*................Nyon Top VIEW - DOWNLOAD.............*/

    public static final String METHOD_TOP_NYONVIEW = "image_quotes_popular_nyon";
    public static final String METHOD_TOP_NYONDOWNLOAD = "image_quotes_top_download_nyon";

    public static final String METHOD_NYON_VIEW = "get_image_nyon_effect_frame_View";
    public static final String METHOD_NYON_DOWNLOAD = "get_image_nyon_effect_frame_download";
    public static final String METHOD_NYON_ALL = "get_all_image_nyon_effect_frame";
    public static final String METHOD_NYON_AdOFF = "get_image_photo_nyon_off";
    public static final String METHOD_NYON_AdON = "get_image_photo_nyon_on";

    /*..............Sticker................*/
/*search_image_quotes_sticker*/
    public static final String METHOD_CAT_PHOTOSTICKER = "get_sub_sticker_frame";
    public static final String METHOD_CAT_PHOTOSTICKER_All = "get_newsub_sticker_card";
    public static final String METHOD_CAT_PHOTOSTICKER_new = "get_sub_sticker_frame";
    public static final String METHOD_RELESECAT_PHOTOSTICKER = "get_sub_sticker_relese";
    public static final String METHOD_IMAGE_PHOTOSTICKER = "get_image_sticker_frame";

    /*................Sticker Top VIEW - DOWNLOAD.............*/

    public static final String METHOD_TOP_STICKERVIEW = "image_quotes_popular_sticker";
    public static final String METHOD_TOP_STICKERDOWNLOAD = "image_quotes_top_download_sticker";

    public static final String METHOD_STICKER_VIEW = "get_image_sticker_frame_View";
    public static final String METHOD_STICKER_DOWNLOAD = "get_image_sticker_frame_download";
    public static final String METHOD_STICKER_ALL = "get_all_image_sticker_frame";
    public static final String METHOD_STICKER_AdOFF = "get_image_photo_sticker_off";
    public static final String METHOD_STICKER_AdON = "get_image_photo_sticker_on";

    /*..............Greeting................*/

    public static final String METHOD_CAT_PHOTOGREETING = "get_sub_greeting_card_frame";
    public static final String METHOD_RELESECAT_PHOTOGREETING = "get_sub_greeting_card_relese";


    public static final String METHOD_IMAGE_PHOTOGREETING11 = "get_all_visiting_card_new11";
    public static final String METHOD_IMAGE_All_PHOTOGREETING11 = "get_image_visiting_card_all";
    public static final String METHOD_IMAGE_PHOTOGREETING1 = "get_all_visiting_card";
    public static final String METHOD_IMAGE_PHOTOGREETING = "get_image_visiting_card";
    public static final String METHOD_IMAGE_PHOTOGREETING10 = "get_image_greeting_card_frame";



    /*................Greeting Top VIEW - DOWNLOAD.............*/

    public static final String METHOD_TOP_GREETINGVIEW = "image_quotes_popular_greeting";
    public static final String METHOD_TOP_GREETINGDOWNLOAD = "image_quotes_top_download_greeting";

    public static final String METHOD_GREETING_VIEW = "get_visiting_card_view";
    public static final String METHOD_GREETING_DOWNLOAD = "get_visiting_card_download";
    public static final String METHOD_GREETING_DOWNLOAD_LIKE = "get_visiting_card_like";
    public static final String METHOD_GREETING_ALL = "get_all_image_greeting_card_frame";
    public static final String METHOD_GREETING_AdOFF = "get_image_photo_greeting_off";
    public static final String METHOD_GREETING_AdON = "get_image_photo_greeting_on";

    /*..............Image................*/

    public static final String METHOD_CAT_PHOTOIMAGE = "get_sub_image_frame";
    public static final String METHOD_RELESECAT_PHOTOIMAGE = "get_sub_image_relese";
    public static final String METHOD_IMAGE_PHOTOIMAGE = "get_image_image_frame";

    /*................Image Top VIEW - DOWNLOAD.............*/

    public static final String METHOD_TOP_IMAGEVIEW = "image_quotes_popular_image";
    public static final String METHOD_TOP_IMAGEDOWNLOAD = "image_quotes_top_download_image";

    public static final String METHOD_IMAGE_VIEW = "get_image_image_frame_View";
    public static final String METHOD_IMAGE_DOWNLOAD = "get_image_image_frame_download";
    public static final String METHOD_IMAGE_ALL = "get_all_image_image_frame";
    public static final String METHOD_IMAGE_AdOFF = "get_image_photo_image_off";
    public static final String METHOD_IMAGE_AdON = "get_image_photo_image_on";

    /*..............GIF................*/

    public static final String METHOD_CAT_PHOTOGIF = "get_sub_gif_frame";
    public static final String METHOD_RELESECAT_PHOTOGIF = "get_sub_gif_relese";
    public static final String METHOD_IMAGE_PHOTOGIF = "get_image_gif_frame";

    /*................Image Top VIEW - DOWNLOAD.............*/

    public static final String METHOD_TOP_GIFVIEW = "image_quotes_popular_gif";
    public static final String METHOD_TOP_GIFDOWNLOAD = "image_quotes_top_download_gif";

    public static final String METHOD_GIF_VIEW = "get_image_gif_frame_View";
    public static final String METHOD_GIF_DOWNLOAD = "get_image_gif_frame_download";
    public static final String METHOD_GIF_ALL = "get_all_image_gif_frame";
    public static final String METHOD_GIF_AdOFF = "get_image_photo_gif_off";
    public static final String METHOD_GIF_AdON = "get_image_photo_gif_on";


    /*..............Wallpaper................*/
    public static final String METHOD_CAT_PHOTOWALL_dialog = "get_sub_wallpaper_frame";
    public static final String METHOD_CAT_PHOTOWALL_main = "get_visiting_card";
    public static final String METHOD_CAT_PHOTOWALL1 = "get_sub_visiting_card";
    //public static final String METHOD_CAT_PHOTOWALL = "get_visiting_card";
    public static final String METHOD_CAT_PHOTOWALL = "get_sub_wallpaper_frame";
    public static final String METHOD_RELESECAT_PHOTOWALL= "get_sub_wallpaper_relese";
    public static final String METHOD_IMAGE_PHOTOWALL = "get_image_wallpaper_frame";

    /*................Image Top VIEW - DOWNLOAD.............*/

    public static final String METHOD_TOP_WALLVIEW = "image_quotes_popular_wallpaper";
    public static final String METHOD_TOP_WALLDOWNLOAD = "image_quotes_top_download_wallpaper";

    public static final String METHOD_WALL_VIEW = "get_image_wallpaper_frame_View";
    public static final String METHOD_WALL_DOWNLOAD = "get_image_wallpaper_frame_download";
    public static final String METHOD_WALL_ALL = "get_all_image_wallpaper_frame";
    public static final String METHOD_WALL_AdOFF = "get_image_photo_wallpaper_off";
    public static final String METHOD_WALL_AdON = "get_image_photo_wallpaper_on";

    /*..............Quote Image................*/

    public static final String METHOD_CAT_PHOTOQUOTEIMG = "get_sub_quote_image_frame";
    public static final String METHOD_RELESECAT_PHOTOQUOTEIMG= "get_sub_quote_image_relese";
    public static final String METHOD_IMAGE_PHOTOQUOTEIMG = "get_image_quote_image_frame";

    /*................Image Top VIEW - DOWNLOAD.............*/

    public static final String METHOD_TOP_QUOTEIMGVIEW = "image_quotes_popular_quotes_img";
    public static final String METHOD_TOP_QUOTEIMGDOWNLOAD = "image_quotes_top_download_quotes_img";

    public static final String METHOD_QUOTEIMG_VIEW = "get_image_quote_image_frame_View";
    public static final String METHOD_QUOTEIMG_DOWNLOAD = "get_image_quote_image_frame_download";
    public static final String METHOD_QUOTEIMG_ALL = "get_all_image_quote_image_frame";
    public static final String METHOD_QUOTEIMG_AdOFF = "get_image_photo_quotesimg_off";
    public static final String METHOD_QUOTEIMG_AdON = "get_image_photo_quotesimg_on";
    /*..............Quote TEXT................*/

    public static final String METHOD_CAT_PHOTOQUOTETXT = "get_sub_quote_txt_frame";
    public static final String METHOD_RELESECAT_PHOTOQUOTETXT= "get_sub_quote_txt_relese";
    public static final String METHOD_QUOTES_CAT_TEXT= "get_text_quotes_cat_id";

    /*................Image Top VIEW - DOWNLOAD.............*/

    public static final String METHOD_QUOTES_POPULAR_TEXT = "text_quotes_popular";
    public static final String METHOD_TOP_QUOTETXTDOWNLOAD = "text_quotes_top_download";

    public static final String METHOD_QUOTETXT_VIEW = "get_text_quotes_id_view";
    public static final String METHOD_QUOTETXT_DOWNLOAD = "get_image_quote_image_frame_download";
    public static final String METHOD_QUOTETXT_ALL = "get_text_quotes";
    public static final String METHOD_QUOTETXT_AdOFF = "get_image_photo_quotesimg_off";
    public static final String METHOD_QUOTETXT_AdON = "get_image_photo_quotesimg_on";

    /*..............................*/


    public static final String METHOD_CAT_TYPE = "get_category_users";

    public static final String METHOD_QUOTES_LATEST_IMAGE = "image_quotes_latest";
    public static final String METHOD_QUOTES_LATEST_TEXT = "text_quotes_latest";
    public static final String METHOD_QUOTES_TOP_LIKE_TEXT = "text_quotes_top_likes";
    public static final String METHOD_QUOTES_FAVOURITE = "get_favourite_list";
    public static final String METHOD_SEARCH_IMAGE = "search_image_quotes";

    public static final String METHOD_SEARCH_IMAGE_sticker = "search_image_quotes_sticker";
    public static final String METHOD_SEARCH_TEXT = "search_text_quotes";
    public static final String METHOD_REPORT = "quotes_report";

    public static final String METHOD_LIKE_IMAGE = "get_image_like";
    public static final String METHOD_LIKE_TEXT = "get_text_like";
    public static final String METHOD_FAV_QUOTE = "quotes_favourite";

    public static final String METHOD_UPLOAD_IMAGE = "upload_image_quote";
    public static final String METHOD_UPLOAD_TEXT = "upload_text_quote";

    public static final String METHOD_SINGLE_IMAGE = "get_image_single_quotes_id";
    public static final String METHOD_SINGLE_TEXT = "get_text_single_quotes_id";

    public static final String METHOD_DOWNLOAD_COUNT = "get_quotes_download";

    public static final String METHOD_LOGIN = "user_login";
    public static final String METHOD_REGISTER = "user_register";
    public static final String METHOD_PROFILE = "user_profile";
    public static final String METHOD_PROFILE_UPDATE = "user_profile_update";
    public static final String METHOD_FORGOT_PASS = "forgot_pass";
    public static final String METHOD_USER_QUOTES_TEXT = "get_text_quotes";
    public static final String METHOD_USER_QUOTES_IMAGES = "get_image_quotes";
    public static final String METHOD_USER_QUOTES_DELETE = "delete_quote";

    public static final String TAG_ROOT = "QUOTES_DIARY";
    public static final String TAG_MSG = "msg";
    public static final String TAG_SUCCESS = "success";

    public static final String TAG_USER_ID = "user_id";
    public static final String TAG_NAME = "name";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_PHONE = "phone";
    public static final String TAG_PROFILE_IMAGE = "user_profile";

    public static final String TAG_CID = "cid";
    public static final String detail = "detail";
    public static final String detail1 = "detail1";
    public static final String TAG_CID_CAT = "cat_id";
    public static final String TAG_CAT_NAME = "category_name";
    public static final String TAG_CAT_IMAGE = "category_image";
    public static final String TAG_CAT_IMAGE_THUMB = "category_image_thumb";
    public static final String TAG_AD_ONOFF = "ad_on_off";
    public static final String card_background = "card_background";
    public static final String card_preview = "card_preview";
    public static final String email_icon = "email_icon";
    public static final String contact_icon = "contact_icon";
    public static final String quote_video = "quote_video0";
    public static final String quote_pv_video = "quote_video1";
    public static final String color_code = "color_code";
    public static final String color_code1 = "color_code1";
    public static final String detail_type = "detail_type";
    public static final String quote_imagejson = "quote_imagejson";
    public static final String color_code2 = "color_code2";
    public static final String color_code3 = "color_code3";
    public static final String color_code4 = "color_code4";
    public static final String TAG_CAT_DATE = "date";
    public static final String location_icon="location_icon";
    public static final String website_icon="website_icon";
    public static final String quote_image6="quote_image6";
    public static final String quote_image7="quote_image7";
    public static final String quote_image8="quote_image8";
    public static final String quote_image9="quote_image9";
    public static final String quote_image10="quote_image10";
    public static final String quote_image11="quote_image11";
    public static final String quote_image12="quote_image12";
    public static final String quote_image13="quote_image13";
    public static final String detail_type1="detail_type1";

    public static final String TAG_CAT_DETAIL = "detail";

    public static final String TAG_FEATURED_QUOTES = "featured_image_quotes";
    public static final String TAG_LATEST_IMAGE_QUOTES = "latest_image_quotes";
    public static final String TAG_POPULAR_QUOTES = "popular_image_quotes";
    public static final String TAG_TOP_LIKED_QUOTES = "top_liked_image_quotes";
    public static final String TAG_QUOTE_OF_THE_DAY = "today_quote";
    public static final String TAG_LATEST_TEXT_QUOTES = "latest_text_quotes";

    public static final String TAG_QUOTES_ID = "id";
    public static final String TAG_QUOTES_CAT_ID = "cat_id";
    public static final String TAG_QUOTES_CAT_NAME = "category_name";
    public static final String TAG_QUOTES_IMAGE_BIG = "card_preview";
    public static final String TAG_QUOTES_IMAGE_BIG1 = "quote_image_b";
    public static final String TAG_QUOTES_THUMBIMAGE = "Thumb_Image_b";
    public static final String TAG_QUOTES_BACKGROUNDIMAGE = "Background_Image_b";
    public static final String TAG_QUOTES_LAYERIMAGE = "Layer_Bottom_b";
    public static final String TAG_QUOTES_PHOTOIMAGE = "Photo_b";
    public static final String TAG_QUOTES_LAYERTOPIMAGE = "Layer_Top_b";
    public static final String TAG_QUOTES_IMAGE_SMALL = "quote_image_s";
    public static final String TAG_QUOTES_TEXT = "quote";
    public static final String TAG_QUOTES_TOTAL_LIKES = "quotes_likes";
    public static final String TAG_QUOTES_TOTAL_VIEWS = "total_views";
    public static final String TAG_QUOTES_TOTAL_DOWNLOADS = "total_download";
    public static final String TAG_QUOTES_LIKED = "already_like";
    public static final String TAG_QUOTES_BG = "quote_bg";
    public static final String TAG_QUOTES_FONT = "font_new_one1";
    public static final String TAG_QUOTES_FONT2 = "font_new_one2";
    public static final String TAG_QUOTES_FONT_COLOR = "font_new_one_n";
    public static final String TAG_QUOTES_FAV = "already_favourite";
    public static final String TAG_QUOTES_APPROVED = "quote_status";

    public static final String DARK_MODE_ON = "on";
    public static final String DARK_MODE_OFF = "off";
    public static final String DARK_MODE_SYSTEM = "system";

    public static final String LOGIN_TYPE_NORMAL = "normal";
    public static final String LOGIN_TYPE_GOOGLE = "google";
    public static final String LOGIN_TYPE_FB = "facebook";
    public static Boolean isUpdate = false, isLogged = false, isFromPush = false, isBannerAd = true, isInterstitialAd = true, isNativeAd = true;
    public static String publisherAdID = "", interstitialAdID = "", nativeAdID = "", bannerAdID = "", bannerAdType = "admob", interstitialAdType = "admob", natveAdType = "admob";
    public static int nativeAdShow = 8, interstitialAdShow = 5;
    public static invite_ItemUser itemUser = new invite_ItemUser("", "", "", "", "", "", invite_AppConstants.LOGIN_TYPE_NORMAL);
    public static int adCount = 0;
    private static String TAG_HOST_URL = BuildConfig.SERVER_URL;
    public static final String URL_ABOUT_US_LOGO = TAG_HOST_URL + "images/";

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {

                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }

        public interface ClickListener {

            void onClick(View view, int position);

            void onLongClick(View view, int position);
        }
    }
}