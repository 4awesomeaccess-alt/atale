package com.example.newcardmaker.invite_sticker;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.newcardmaker.R;
import com.example.newcardmaker.invite_Adapter_Category_sub;
import com.example.newcardmaker.invite_online_database.invite_AppConstants;
import com.example.newcardmaker.invite_online_database.invite_ItemSubCat;
import com.example.newcardmaker.invite_online_database.invite_LoadSubCat;
import com.example.newcardmaker.invite_online_database.invite_Methods;
import com.example.newcardmaker.invite_online_database.invite_SubCategoryListener;

import java.util.ArrayList;
import java.util.Collections;

public class invite_sticker_fragment extends Fragment {

    private invite_Methods sticker_methods1;
    private ArrayList<invite_ItemSubCat> sticker_arrayList_image_cat, sticker_arrayList_text_cat;
    private ProgressBar sticker_progressBar1;
    private LinearLayout sticker_ll_empty1;
    private TextView sticker_tv_empty1;
    private RecyclerView sticker_recyclerView_category;
    private invite_Adapter_Category_sub sticker_adapterCat;

    private String sticker_errr_msg;
    private String cid = "";
    private View viewcc;

    private int selectedPosition = 0;
    private boolean isDataLoaded = false;
    private boolean isFirstLoadDone = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        viewcc = inflater.inflate(R.layout.invite_fragment_sticker_fragment, container, false);

        if (getActivity() != null && getActivity().getIntent() != null) {
            cid = "24";
            if (cid == null) {
                cid = "";
            }
        }

        initview();

        GestureDetector gestureDetector = new GestureDetector(getActivity(), new SwipeGestureListener());

        FrameLayout ff = viewcc.findViewById(R.id.frame_recycler);
        ff.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });

        return viewcc;
    }

    public void refreshData() {
        if (!isDataLoaded) {
            selectedPosition = 0;
            loadCategories(invite_AppConstants.METHOD_CAT_PHOTOSTICKER);
        } else {
            showSelectedItem();
        }
    }

    private void initview() {
        sticker_progressBar1 = viewcc.findViewById(R.id.pb_cat);
        sticker_ll_empty1 = viewcc.findViewById(R.id.ll_empty);
        sticker_tv_empty1 = viewcc.findViewById(R.id.tv_empty);
        sticker_recyclerView_category = viewcc.findViewById(R.id.rv_category);

        sticker_methods1 = new invite_Methods(getActivity());
        sticker_arrayList_image_cat = new ArrayList<>();
        sticker_arrayList_text_cat = new ArrayList<>();

        sticker_recyclerView_category.addOnItemTouchListener(
                new invite_AppConstants.RecyclerTouchListener(
                        getActivity(),
                        sticker_recyclerView_category,
                        new invite_AppConstants.RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                selectedPosition = position;
                                openSelectedFragment(position);
                            }

                            @Override
                            public void onLongClick(View view, int position) {
                            }
                        }
                )
        );
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
            if (sticker_arrayList_image_cat == null || sticker_arrayList_image_cat.size() == 0) {
                return false;
            }

            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX < 0) {
                        if (selectedPosition < sticker_arrayList_image_cat.size() - 1) {
                            selectedPosition++;
                            openSelectedFragment(selectedPosition);
                        }
                        return true;
                    } else {
                        if (selectedPosition > 0) {
                            selectedPosition--;
                            openSelectedFragment(selectedPosition);
                        }
                        return true;
                    }
                }
            }
            return false;
        }
    }

    private void loadCategories(String methodCat) {
        if (sticker_methods1 != null && sticker_methods1.isConnectingToInternet()) {

            invite_LoadSubCat loadCat = new invite_LoadSubCat(new invite_SubCategoryListener() {
                @Override
                public void onStart() {
                    sticker_ll_empty1.setVisibility(View.GONE);
                    sticker_recyclerView_category.setVisibility(View.GONE);
                    sticker_progressBar1.setVisibility(View.VISIBLE);
                    sticker_arrayList_image_cat.clear();
                    sticker_arrayList_text_cat.clear();
                }

                @Override
                public void onEnd(String success, String verifyStatus, String message,
                                  ArrayList<invite_ItemSubCat> arrayListImageCat,
                                  ArrayList<invite_ItemSubCat> arrayListTextCat) {

                    if (!isAdded()) {
                        return;
                    }

                    try {
                        if (success.equals("1")) {
                            if (!verifyStatus.equals("-1")) {
                                sticker_errr_msg = getString(R.string.err_no_cat_found);

                                sticker_arrayList_image_cat.addAll(arrayListImageCat);
                                sticker_arrayList_text_cat.addAll(arrayListTextCat);

                                isDataLoaded = true;
                                sticker_progressBar1.setVisibility(View.GONE);
                                setEmpty(true);
                            } else {
                                sticker_progressBar1.setVisibility(View.GONE);
                                sticker_methods1.getVerifyDialog(getString(R.string.error_unauth_access), message);
                            }
                        } else {
                            sticker_progressBar1.setVisibility(View.GONE);
                            sticker_errr_msg = getString(R.string.err_server);
                            setEmpty(false);
                            sticker_methods1.showToast(getString(R.string.err_server));
                        }
                    } catch (Exception e) {
                        sticker_progressBar1.setVisibility(View.GONE);
                        sticker_errr_msg = getString(R.string.err_server);
                        setEmpty(false);
                    }
                }
            }, sticker_methods1.getAPIRequest(methodCat, 0, "", "", "", cid,
                    "", "", "", "", "", "", "", "", "", "", "", null));

            loadCat.execute();
        } else {
            sticker_errr_msg = getString(R.string.err_internet_not_conn);
            setEmpty(false);
        }
    }

    private void setEmpty(Boolean isSuccess) {
        if (!isAdded()) {
            return;
        }

        if (isSuccess && sticker_arrayList_image_cat.size() > 0) {
            sticker_ll_empty1.setVisibility(View.GONE);
            sticker_recyclerView_category.setVisibility(View.VISIBLE);

            Collections.reverse(sticker_arrayList_image_cat);

            sticker_adapterCat = new invite_Adapter_Category_sub(getActivity(), sticker_arrayList_image_cat);
            LinearLayoutManager lLayout1 = new LinearLayoutManager(getActivity(), RecyclerView.HORIZONTAL, false);
            sticker_recyclerView_category.setLayoutManager(lLayout1);
            sticker_recyclerView_category.setAdapter(sticker_adapterCat);

            if (!isFirstLoadDone) {
                selectedPosition = 0;
                isFirstLoadDone = true;
            } else if (selectedPosition < 0 || selectedPosition >= sticker_arrayList_image_cat.size()) {
                selectedPosition = 0;
            }

            showSelectedItem();
        } else {
            sticker_tv_empty1.setText(sticker_errr_msg);
            sticker_ll_empty1.setVisibility(View.VISIBLE);
            sticker_recyclerView_category.setVisibility(View.GONE);
        }
    }

    private String currentLoadedCategoryId = "";
    private long lastClickTime = 0;

    private void showSelectedItem() {
        if (!isAdded() || sticker_arrayList_image_cat == null || sticker_arrayList_image_cat.isEmpty()) {
            return;
        }

        if (selectedPosition < 0 || selectedPosition >= sticker_arrayList_image_cat.size()) {
            selectedPosition = 0;
        }

        String newCategoryId = sticker_arrayList_image_cat.get(selectedPosition).getId();

        if (sticker_adapterCat != null) {
            sticker_adapterCat.setSelectedPosition(selectedPosition);
        }

        if (sticker_recyclerView_category != null) {
            sticker_recyclerView_category.scrollToPosition(selectedPosition);
        }

        long now = System.currentTimeMillis();
        boolean isSameCategory = newCategoryId.equals(currentLoadedCategoryId);

        if (isSameCategory && (now - lastClickTime) < 300) {
            return;
        }

        lastClickTime = now;
        currentLoadedCategoryId = newCategoryId;

        Fragment fragment = new invite_sticker_lis_common(newCategoryId);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.executePendingTransactions();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_recycler, fragment);
        transaction.commitAllowingStateLoss();
    }


    private void openSelectedFragment(int position) {
        selectedPosition = position;
        showSelectedItem();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public int getCategoryCount() {
        return sticker_arrayList_image_cat == null ? 0 : sticker_arrayList_image_cat.size();
    }

    public ArrayList<invite_ItemSubCat> getCategoryList() {
        return sticker_arrayList_image_cat;
    }

    public void moveToPosition(int position) {
        selectedPosition = position;
        showSelectedItem();
    }
}