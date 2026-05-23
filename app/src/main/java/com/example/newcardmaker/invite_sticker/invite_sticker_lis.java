package com.example.newcardmaker.invite_sticker;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/*import com.CrytonixCodings.ColorLightSplashEffectPhotoEditor.R;
import com.CrytonixCodings.ColorLightSplashEffectPhotoEditor.invite_adapter.invite_Adapter_Image_Greeting_dialog_special;
import com.CrytonixCodings.ColorLightSplashEffectPhotoEditor.invite_online_database.invite_AppConstants;
import com.CrytonixCodings.ColorLightSplashEffectPhotoEditor.invite_online_database.invite_EndlessRecyclerViewScrollListener1;
import com.CrytonixCodings.ColorLightSplashEffectPhotoEditor.invite_online_database.invite_ItemSubCat;
import com.CrytonixCodings.ColorLightSplashEffectPhotoEditor.invite_online_database.invite_Item_OneImages_dialog;
import com.CrytonixCodings.ColorLightSplashEffectPhotoEditor.invite_online_database.invite_Load_OneImages_dialog;
import com.CrytonixCodings.ColorLightSplashEffectPhotoEditor.invite_online_database.invite_Methods;
import com.CrytonixCodings.ColorLightSplashEffectPhotoEditor.invite_online_database.invite_OneImagesListener_dialog;*/

import com.example.newcardmaker.R;
import com.example.newcardmaker.invite_Adapter_Image_Greeting_dialog_special;
import com.example.newcardmaker.invite_online_database.invite_AppConstants;
import com.example.newcardmaker.invite_online_database.invite_EndlessRecyclerViewScrollListener1;
import com.example.newcardmaker.invite_online_database.invite_ItemSubCat;
import com.example.newcardmaker.invite_online_database.invite_Item_OneImages_dialog;
import com.example.newcardmaker.invite_online_database.invite_Load_OneImages_dialog;
import com.example.newcardmaker.invite_online_database.invite_Methods;
import com.example.newcardmaker.invite_online_database.invite_OneImagesListener_dialog;

import java.util.ArrayList;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;
import okhttp3.RequestBody;

public class invite_sticker_lis extends Fragment {

    private View viewccc;
    private RecyclerView sticker_recyclerView;
    private invite_Adapter_Image_Greeting_dialog_special sticker_adapterImageQuotes;
    private invite_Methods sticker_methods;
    private CircularProgressBar sticker_progressBar;
    private LinearLayout sticker_ll_empty;
    private TextView sticker_tv_empty;
    private AppCompatButton sticker_button_empty;

    private ArrayList<invite_Item_OneImages_dialog> sticker_arrayList;
    private ArrayList<invite_Item_OneImages_dialog> sticker_arrayListTemp;

    private boolean sticker_isOver = false;
    private boolean sticker_isScroll = false;
    private int sticker_page = 1;

    private StaggeredGridLayoutManager sticker_lLayout;
    private String cidddddd = "";

    private invite_Load_OneImages_dialog loadQuotes;
    private RequestBody requestBody;

    public static String sticker_selectmethod;

    public invite_sticker_lis() {
    }

    public invite_sticker_lis(String id) {
        this.cidddddd = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewccc = inflater.inflate(R.layout.invite_fragment_sticker_lis, container, false);
        initview1();
        return viewccc;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (loadQuotes != null) {
            loadQuotes.cancel(true);
            loadQuotes = null;
        }

        sticker_recyclerView = null;
        sticker_progressBar = null;
        sticker_ll_empty = null;
        sticker_tv_empty = null;
        sticker_button_empty = null;
        sticker_adapterImageQuotes = null;
        viewccc = null;
    }

    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) <= Math.abs(diffY)) {
                return false;
            }

            if (Math.abs(diffX) <= SWIPE_THRESHOLD || Math.abs(velocityX) <= SWIPE_VELOCITY_THRESHOLD) {
                return false;
            }

            Fragment parent = getParentFragment();
            if (!(parent instanceof invite_sticker_fragment)) {
                return false;
            }

            invite_sticker_fragment parentFragment = (invite_sticker_fragment) parent;

            int currentPosition = parentFragment.getSelectedPosition();
            int totalSize = parentFragment.getCategoryCount();

            if (diffX < 0) {
                if (currentPosition < totalSize - 1) {
                    parentFragment.moveToPosition(currentPosition + 1);
                }
                return true;
            } else {
                if (currentPosition > 0) {
                    parentFragment.moveToPosition(currentPosition - 1);
                }
                return true;
            }
        }
    }

    private void initview1() {
        try {
            sticker_isOver = false;
            sticker_isScroll = false;
            sticker_page = 1;

            requestBody = null;
            loadQuotes = null;

            sticker_arrayList = new ArrayList<>();
            sticker_arrayListTemp = new ArrayList<>();

            sticker_ll_empty = viewccc.findViewById(R.id.ll_empty);
            sticker_tv_empty = viewccc.findViewById(R.id.tv_empty);
            sticker_button_empty = viewccc.findViewById(R.id.btn_empty_try);
            sticker_progressBar = viewccc.findViewById(R.id.pb_quote_by_cat);
            sticker_recyclerView = viewccc.findViewById(R.id.recyclerview);

            sticker_methods = new invite_Methods(getActivity());

            sticker_lLayout = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
            sticker_recyclerView.setLayoutManager(sticker_lLayout);
            sticker_recyclerView.setHasFixedSize(true);

            sticker_selectmethod = invite_AppConstants.METHOD_IMAGE_PHOTOSTICKER;

            loadQuotesByCat(sticker_selectmethod);

            GestureDetector gestureDetector = new GestureDetector(getActivity(), new SwipeGestureListener());
            sticker_recyclerView.setOnTouchListener((v, event) -> {
                gestureDetector.onTouchEvent(event);
                return false;
            });

            sticker_recyclerView.addOnScrollListener(new invite_EndlessRecyclerViewScrollListener1(sticker_lLayout) {
                @Override
                public void onLoadMore(int p, int totalItemsCount, RecyclerView view) {
                    if (!sticker_isOver) {
                        new Handler().postDelayed(() -> {
                            sticker_isScroll = true;
                            loadQuotesByCat(sticker_selectmethod);
                        }, 200);
                    }
                }
            });

            sticker_recyclerView.addOnItemTouchListener(
                    new invite_AppConstants.RecyclerTouchListener(
                            getActivity(),
                            sticker_recyclerView,
                            new invite_AppConstants.RecyclerTouchListener.ClickListener() {
                                @Override
                                public void onClick(View view, int position) {
                                    if (!isAdded() || getActivity() == null) {
                                        return;
                                    }

                                    Intent returnIntent = new Intent();
                                    returnIntent.putExtra("image", sticker_arrayList.get(position).getImageBig());
                                    returnIntent.putExtra("catid", cidddddd);
                                    returnIntent.putExtra("getAd_on_off", sticker_arrayList.get(position).getAd_on_off());

                                    Fragment parent = getParentFragment();
                                    String categoryName = "";

                                    if (parent instanceof invite_sticker_fragment) {
                                        invite_sticker_fragment parentFragment = (invite_sticker_fragment) parent;
                                        int currentPosition = parentFragment.getSelectedPosition();
                                        ArrayList<invite_ItemSubCat> categoryList = parentFragment.getCategoryList();

                                        if (categoryList != null
                                                && currentPosition >= 0
                                                && currentPosition < categoryList.size()) {
                                            categoryName = categoryList.get(currentPosition).getName();
                                        }
                                    }

                                    returnIntent.putExtra("name", categoryName);
                                    getActivity().setResult(RESULT_OK, returnIntent);
                                    getActivity().finish();
                                }

                                @Override
                                public void onLongClick(View view, int position) {
                                }
                            }
                    )
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadQuotesByCat(String methodQuotesCatImage) {
        if (loadQuotes != null) {
            loadQuotes.cancel(true);
            loadQuotes = null;
        }

        if (sticker_methods == null) {
            return;
        }

        if (sticker_methods.isConnectingToInternet()) {
            requestBody = sticker_methods.getAPIRequest(
                    methodQuotesCatImage,
                    sticker_page,
                    "",
                    "",
                    "",
                    cidddddd,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    invite_AppConstants.itemUser.getId(),
                    "",
                    null
            );

            loadQuotes = new invite_Load_OneImages_dialog(new invite_OneImagesListener_dialog() {
                @Override
                public void onStart() {
                    if (!isAdded()) {
                        return;
                    }

                    if (sticker_arrayList.size() == 0) {
                        sticker_arrayList.clear();

                        if (sticker_recyclerView != null) {
                            sticker_recyclerView.setVisibility(View.GONE);
                        }
                        if (sticker_ll_empty != null) {
                            sticker_ll_empty.setVisibility(View.GONE);
                        }
                        if (sticker_progressBar != null) {
                            sticker_progressBar.setVisibility(View.VISIBLE);
                        }
                    }
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message,
                                  ArrayList<invite_Item_OneImages_dialog> arrayListQuotes, int total_records) {

                    if (!isAdded()) {
                        return;
                    }

                    if ("1".equals(success)) {
                        if (!"-1".equals(verifyStatus) && !"-2".equals(verifyStatus)) {
                            if (arrayListQuotes == null || arrayListQuotes.size() == 0) {
                                sticker_isOver = true;
                                setEmpty(true, getString(R.string.err_no_quotes_found));
                            } else {
                                sticker_arrayListTemp.addAll(arrayListQuotes);
                                sticker_arrayList.addAll(arrayListQuotes);
                                sticker_page = sticker_page + 1;
                                setAdapter();
                            }
                        } else if ("-2".equals(verifyStatus)) {
                            if (sticker_methods != null) {
                                sticker_methods.getInvalidUserDialog(message);
                            }
                        } else {
                            if (sticker_methods != null) {
                                sticker_methods.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            }
                        }
                    } else {
                        setEmpty(false, getString(R.string.err_server));
                    }

                    if (sticker_progressBar != null) {
                        sticker_progressBar.setVisibility(View.GONE);
                    }
                }
            }, requestBody);

            loadQuotes.execute();
        } else {
            if (isAdded()) {
                setEmpty(false, getString(R.string.err_internet_not_conn));
            }
            if (sticker_progressBar != null) {
                sticker_progressBar.setVisibility(View.GONE);
            }
        }
    }

    private void setAdapter() {
        if (!isAdded()) {
            return;
        }

        if (!sticker_isScroll) {
            sticker_adapterImageQuotes =
                    new invite_Adapter_Image_Greeting_dialog_special(getActivity(), sticker_arrayList);

            if (sticker_recyclerView != null) {
                sticker_recyclerView.setAdapter(sticker_adapterImageQuotes);
            }
        } else {
            if (sticker_adapterImageQuotes != null) {
                sticker_adapterImageQuotes.notifyDataSetChanged();
            }
        }

        setEmpty(true, getString(R.string.err_no_quotes_found));
    }

    private void setEmpty(Boolean isSuccess, String errorMsg) {
        if (!isAdded()) {
            return;
        }

        if (isSuccess && sticker_arrayList != null && sticker_arrayList.size() > 0) {
            if (sticker_ll_empty != null) {
                sticker_ll_empty.setVisibility(View.GONE);
            }
            if (sticker_recyclerView != null) {
                sticker_recyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            if (sticker_tv_empty != null) {
                sticker_tv_empty.setText(errorMsg);
            }
            if (sticker_ll_empty != null) {
                sticker_ll_empty.setVisibility(View.VISIBLE);
            }
            if (sticker_recyclerView != null) {
                sticker_recyclerView.setVisibility(View.GONE);
            }
        }
    }
}