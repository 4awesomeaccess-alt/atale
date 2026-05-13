package com.example.newcardmaker;

import static android.util.Log.ASSERT;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;


import com.example.newcardmaker.invite_online_database.invite_AppConstants;
import com.example.newcardmaker.invite_online_database.invite_EndlessRecyclerViewScrollListener1;
import com.example.newcardmaker.invite_online_database.invite_Item_OneImages_frame;
import com.example.newcardmaker.invite_online_database.invite_Load_OneImages_frame;
import com.example.newcardmaker.invite_online_database.invite_Methods;
import com.example.newcardmaker.invite_online_database.invite_OneImagesListener_frame;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class invite_photo_frame {

    public static RecyclerView frame_recyclerView;
    public static invite_Adapter_Image_Greeting_frame frame_adapterImageQuotes;
    public static invite_Methods frame_methods;
    public static ProgressBar frame_progressBar;
    public static LinearLayout frame_ll_empty;
    public static TextView frame_tv_empty;
    public static AppCompatButton frame_button_empty;
    public static ArrayList<invite_Item_OneImages_frame> frame_arrayList, frame_arrayListTemp;
    public static Boolean frame_isOver = false, frame_isScroll = false;
    public static int frame_position, frame_page = 1;
    public static StaggeredGridLayoutManager frame_lLayout;
    public static String frame_selectmethod;
    public static String frame_downloadID;
    public static String frame_cidddddd = "8";
    public static RequestBody frame_requestBody;
    public static invite_Load_OneImages_frame frame_loadQuotes;
    public static invite_EndlessRecyclerViewScrollListener1 frame_scrollListener;



    public static void initview_1(Activity activity, View dialog, RecyclerView rv_category_1, String cat_id, ProgressBar p) {
        try {
            frame_recyclerView = rv_category_1;
            frame_adapterImageQuotes = null;
            frame_methods = null;
            frame_progressBar = p;
            frame_ll_empty = null;
            frame_tv_empty = null;
            frame_button_empty = null;
            frame_arrayList = null;
            frame_arrayListTemp = null;
            frame_isOver = false;
            frame_isScroll = false;
            frame_position = 0;
            frame_page = 1;
            frame_lLayout = null;
            frame_selectmethod = null;
            frame_downloadID = null;
            frame_cidddddd = cat_id;
            frame_requestBody = null;
            frame_loadQuotes = null;
            frame_scrollListener = null;

            frame_arrayList = new ArrayList<>();
            frame_arrayListTemp = new ArrayList<>();

            frame_ll_empty = dialog.findViewById(R.id.ll_empty);
            frame_tv_empty = dialog.findViewById(R.id.tv_empty);
            frame_button_empty = dialog.findViewById(R.id.btn_empty_try);

            frame_methods = new invite_Methods(activity);

            frame_lLayout = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            frame_recyclerView.setLayoutManager(frame_lLayout);

            frame_adapterImageQuotes = new invite_Adapter_Image_Greeting_frame(activity, frame_arrayList);
            frame_recyclerView.setAdapter(frame_adapterImageQuotes);

            frame_selectmethod = invite_AppConstants.METHOD_FRAME_ALL;

            loadQuotesByCat_1(frame_selectmethod, activity);

            frame_scrollListener = new invite_EndlessRecyclerViewScrollListener1(frame_lLayout) {
                @Override
                public void onLoadMore(int p, int totalItemsCount, RecyclerView view) {
                    if (!frame_isOver && !frame_isScroll) {
                        frame_isScroll = true;

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadQuotesByCat_1(frame_selectmethod, activity);
                            }
                        }, 500);
                    }
                }
            };

            frame_recyclerView.addOnScrollListener(frame_scrollListener);

            if (frame_button_empty != null) {
                frame_button_empty.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!frame_isScroll) {
                            frame_page = 1;
                            frame_isOver = false;
                            frame_arrayList.clear();
                            frame_arrayListTemp.clear();
                            if (frame_adapterImageQuotes != null) {
                                frame_adapterImageQuotes.notifyDataSetChanged();
                            }
                            loadQuotesByCat_1(frame_selectmethod, activity);
                        }
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadQuotesByCat_1(String methodQuotesCatImage, Activity activity) {
        if (frame_methods != null && frame_methods.isConnectingToInternet()) {

            frame_requestBody = frame_methods.getAPIRequest(
                    methodQuotesCatImage,
                    frame_page,
                    frame_cidddddd == null ? "8" : frame_cidddddd,
                    "", "",
                    "", "", "", "", "", "", "", "", "",
                    "", invite_AppConstants.itemUser.getId(), "", null
            );

            frame_loadQuotes = new invite_Load_OneImages_frame(new invite_OneImagesListener_frame() {
                @Override
                public void onStart() {
                    /*if (frame_arrayList != null && frame_arrayList.size() == 0) {
                        frame_recyclerView.setVisibility(View.GONE);
                        frame_ll_empty.setVisibility(View.GONE);
                        frame_progressBar.setVisibility(View.VISIBLE);
                    }*/
                    if (frame_arrayList != null && frame_arrayList.size() == 0) {
                        if (frame_recyclerView != null)
                            frame_recyclerView.setVisibility(View.GONE);
                        if (frame_ll_empty != null)          // ✅ null check
                            frame_ll_empty.setVisibility(View.GONE);
                        if (frame_progressBar != null)       // ✅ null check
                            frame_progressBar.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message,
                                  ArrayList<invite_Item_OneImages_frame> arrayListQuotes,
                                  int total_records) {
                    try {


                        if (frame_progressBar != null)
                            frame_progressBar.setVisibility(View.GONE);

                        frame_isScroll = false;

                        if ("1".equals(success)) {
                            if (!"-1".equals(verifyStatus) && !"-2".equals(verifyStatus)) {
                                if (arrayListQuotes != null && arrayListQuotes.size() > 0) {

                                    int startPos = frame_arrayList.size();
                                    frame_arrayListTemp.addAll(arrayListQuotes);
                                    frame_arrayList.addAll(arrayListQuotes);

                                    if (frame_adapterImageQuotes == null) {
                                        frame_adapterImageQuotes =
                                                new invite_Adapter_Image_Greeting_frame(
                                                        activity, frame_arrayList);
                                        frame_recyclerView.setAdapter(frame_adapterImageQuotes);
                                    } else {
                                        frame_adapterImageQuotes
                                                .notifyItemRangeInserted(startPos,
                                                        arrayListQuotes.size());
                                    }

                                    if (frame_recyclerView != null)
                                        frame_recyclerView.setVisibility(View.VISIBLE);
                                    if (frame_ll_empty != null)      // ✅ null check
                                        frame_ll_empty.setVisibility(View.GONE);

                                    frame_page++;

                                    if (total_records > 0 &&
                                            frame_arrayList.size() >= total_records) {
                                        frame_isOver = true;
                                    }

                                } else {
                                    frame_isOver = true;
                                    if (frame_arrayList.size() == 0) {
                                        setEmpty_1(false,
                                                activity.getString(R.string.err_no_quotes_found));
                                    }
                                }
                            } else if ("-2".equals(verifyStatus)) {
                                if (frame_methods != null)
                                    frame_methods.getInvalidUserDialog(message);
                            } else {
                                if (frame_methods != null)
                                    frame_methods.getVerifyDialog(
                                            activity.getString(R.string.error_unauth_access),
                                            message);
                            }
                        } else {
                            if (frame_arrayList.size() == 0) {
                                setEmpty_1(false, activity.getString(R.string.err_server));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }, frame_requestBody);

            frame_loadQuotes.execute();

        } else {

            frame_isScroll = false;
            if (frame_progressBar != null) {
                frame_progressBar.setVisibility(View.GONE);
            }

            if (frame_arrayList != null && frame_arrayList.size() == 0) {
                setEmpty_1(false, activity.getString(R.string.err_internet_not_conn));
            }
        }
    }

    public static void setAdapter_1(Activity activity) {
        try {
            if (frame_adapterImageQuotes == null) {
                frame_adapterImageQuotes = new invite_Adapter_Image_Greeting_frame(activity, frame_arrayList);
                frame_recyclerView.setAdapter(frame_adapterImageQuotes);
            } else {
                frame_adapterImageQuotes.notifyDataSetChanged();
            }

            setEmpty_1(true, activity.getString(R.string.err_no_quotes_found));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setEmpty_1(Boolean isSuccess, String errorMsg) {
        try {
            if (isSuccess && frame_arrayList != null && frame_arrayList.size() > 0) {
                if (frame_ll_empty != null)
                    frame_ll_empty.setVisibility(View.GONE);
                if (frame_recyclerView != null)
                    frame_recyclerView.setVisibility(View.VISIBLE);
            } else {
                if (frame_tv_empty != null)      // ✅ null check
                    frame_tv_empty.setText(errorMsg);
                if (frame_ll_empty != null)      // ✅ null check
                    frame_ll_empty.setVisibility(View.VISIBLE);
                if (frame_recyclerView != null)
                    frame_recyclerView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}