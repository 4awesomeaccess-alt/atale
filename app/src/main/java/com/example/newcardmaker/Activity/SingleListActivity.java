package com.example.newcardmaker.Activity;

import static android.util.Log.ASSERT;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.newcardmaker.invite_online_database.invite_AppConstants;
import com.example.newcardmaker.invite_online_database.invite_EndlessRecyclerViewScrollListener1;
import com.example.newcardmaker.invite_online_database.invite_Item_OneImages;
import com.example.newcardmaker.invite_online_database.invite_Load_OneImages;
import com.example.newcardmaker.invite_online_database.invite_Methods;
import com.example.newcardmaker.invite_online_database.invite_OneImagesListener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

import okhttp3.RequestBody;

public class SingleListActivity extends AppCompatActivity {

    RecyclerView home_recyclerView;
    invite_Methods home_methods;
    String home_selectmethod;
    StaggeredGridLayoutManager home_lLayout;
    Boolean home_isOver = false, home_isScroll = false;
    int home_page = 1;
    ArrayList<invite_Item_OneImages> home_arrayList, home_arrayListTemp;
    Bundle bb;
    LinearLayout home_ll_empty;
    String cid, categoryName;
    ImageAdapter home_adapterImageQuotes;
    ProgressBar home_progressBar;
    TextView home_tv_empty;
    RequestBody requestBody;
    invite_Load_OneImages loadQuotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cid = getIntent().getStringExtra("cid");
        categoryName = getIntent().getStringExtra("name");
        android.util.Log.e("#SingleList_CID", "cid=" + cid + " name=" + categoryName);

        // ── Root
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#F5F5F5"));

        // ── Toolbar
        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setBackgroundColor(Color.parseColor("#1565C0"));
        toolbar.setPadding(16, 48, 16, 16);
        toolbar.setGravity(Gravity.CENTER_VERTICAL);

        TextView btnBack = new TextView(this);
        btnBack.setText("←");
        btnBack.setTextSize(22);
        btnBack.setTextColor(Color.WHITE);
        btnBack.setPadding(0, 0, 20, 0);
        btnBack.setOnClickListener(v -> finish());

        TextView tvTitle = new TextView(this);
        tvTitle.setText(categoryName != null ? categoryName : "Cards");
        tvTitle.setTextSize(18);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvTitle.setLayoutParams(titleLp);

        toolbar.addView(btnBack);
        toolbar.addView(tvTitle);
        root.addView(toolbar);

        // ── ProgressBar
        home_progressBar = new ProgressBar(this);
        home_progressBar.setVisibility(View.GONE);
        LinearLayout.LayoutParams pbLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        pbLp.gravity = Gravity.CENTER_HORIZONTAL;
        pbLp.setMargins(0, 40, 0, 0);
        home_progressBar.setLayoutParams(pbLp);
        root.addView(home_progressBar);

        // ── Empty layout
        home_ll_empty = new LinearLayout(this);
        home_ll_empty.setGravity(Gravity.CENTER);
        home_ll_empty.setVisibility(View.GONE);
        home_tv_empty = new TextView(this);
        home_tv_empty.setTextSize(15);
        home_tv_empty.setTextColor(Color.GRAY);
        home_tv_empty.setPadding(40, 80, 40, 40);
        home_ll_empty.addView(home_tv_empty);
        root.addView(home_ll_empty);

        // ── RecyclerView
        home_recyclerView = new RecyclerView(this);
        home_lLayout = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        home_recyclerView.setLayoutManager(home_lLayout);
        home_recyclerView.setPadding(8, 8, 8, 8);
        LinearLayout.LayoutParams rvLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        home_recyclerView.setLayoutParams(rvLp);
        root.addView(home_recyclerView);

        setContentView(root);

        home_page = 1;
        home_arrayList = new ArrayList<>();
        home_arrayListTemp = new ArrayList<>();
        home_methods = new invite_Methods(this);
        home_selectmethod = invite_AppConstants.METHOD_IMAGE_PHOTOGREETING;

        android.util.Log.e("#SingleList_method", "method=" + home_selectmethod + " cid=" + cid);

        loadQuotesByCat(home_selectmethod);

        // ── Endless scroll
        home_recyclerView.addOnScrollListener(
                new invite_EndlessRecyclerViewScrollListener1(home_lLayout) {
                    @Override
                    public void onLoadMore(int p, int totalItemsCount, RecyclerView view) {
                        if (!home_isOver) {
                            new Handler().postDelayed(() -> {
                                home_isScroll = true;
                                loadQuotesByCat(home_selectmethod);
                            }, 500);
                        }
                    }
                });

        // ── Item click
        home_recyclerView.addOnItemTouchListener(
                new invite_AppConstants.RecyclerTouchListener(this, home_recyclerView,
                        new invite_AppConstants.RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                if (position < 0 || position >= home_arrayList.size()) return;
                                openCard(position);
                            }

                            @Override
                            public void onLongClick(View view, int position) {}
                        }));
    }

    private void openCard(int position) {
        invite_Item_OneImages item = home_arrayList.get(position);
        bb = new Bundle();
        bb.putString("bg_", item.getImageBig());
        bb.putString("bg2_", item.getcard_background());
        bb.putString("bg3_", item.getemail_icon());
        bb.putString("bg4_", item.getcontact_icon());
        bb.putString("flower_", item.getemail_icon());
        bb.putString("candle_", item.getcontact_icon());
        bb.putString("font_", item.getAd_on_off());
        bb.putString("design", item.getdetail_type());
        bb.putString("catid", "");
        bb.putString("cidddddd", cid);
        bb.putString("candle_pos", "3");
        bb.putString("flower_pos", "3");

        ArrayList<String> data = new ArrayList<>();
        addIfImage(data, item.getcard_background(), null);
        addIfImage(data, item.getemail_icon(), data.isEmpty() ? null : data.get(0));
        addIfImage(data, item.getcontact_icon(), data.size() < 2 ? null : data.get(1));
        addIfImage(data, item.getlocation_icon(), data.size() < 3 ? null : data.get(2));
        addIfImage(data, item.getwebsite_icon(), data.size() < 4 ? null : data.get(3));
        addIfImage(data, item.getquote_image6(), data.size() < 5 ? null : data.get(4));
        addIfImage(data, item.getquote_image7(), data.size() < 6 ? null : data.get(5));
        addIfImage(data, item.getquote_image8(), data.size() < 7 ? null : data.get(6));
        addIfImage(data, item.getquote_image9(), data.size() < 8 ? null : data.get(7));
        addIfImage(data, item.getquote_image10(), data.size() < 9 ? null : data.get(8));
        addIfImage(data, item.getquote_image11(), data.size() < 10 ? null : data.get(9));
        addIfImage(data, item.getquote_image12(), data.size() < 11 ? null : data.get(10));
        addIfImage(data, item.getquote_image13(), data.size() < 12 ? null : data.get(11));

        bb.putString("image_array", TextUtils.join("!--!", data));
        bb.putString("duration_array", item.getFont2());

        // Download JSON and open MainActivity
        String jsonUrl = "https://crytonixinvitesan.gardenphoto.in/images/" + item.getquote_imagejson();
        new DownloadJsonTask(bb).execute(jsonUrl);
    }

    private void addIfImage(ArrayList<String> data, String url, String fallback) {
        if (url != null && (url.toLowerCase().endsWith(".jpeg") ||
                url.toLowerCase().endsWith(".jpg") ||
                url.toLowerCase().endsWith(".png"))) {
            data.add(url);
        } else if (fallback != null) {
            data.add(fallback);
        }
    }

    private void loadQuotesByCat(String method) {
        if (!home_methods.isConnectingToInternet()) {
            setEmpty(false, "Internet connection નથી");
            home_progressBar.setVisibility(View.GONE);
            return;
        }

        requestBody = home_methods.getAPIRequest(method, home_page, "", "", "",
                cid, "", "", "", "", "", "", "", "",
                "", invite_AppConstants.itemUser.getId(), "", null);

        loadQuotes = new invite_Load_OneImages(new invite_OneImagesListener() {
            @Override
            public void onStart() {
                if (home_arrayList.isEmpty()) {
                    home_recyclerView.setVisibility(View.GONE);
                    home_ll_empty.setVisibility(View.GONE);
                    home_progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message,
                              ArrayList<invite_Item_OneImages> arrayListQuotes, int total_records) {
                home_progressBar.setVisibility(View.GONE);
                if ("1".equals(success) && !"-1".equals(verifyStatus) && !"-2".equals(verifyStatus)) {
                    if (arrayListQuotes.isEmpty()) {
                        home_isOver = true;
                        setEmpty(true, "Not Found");
                    } else {
                        home_arrayList.addAll(arrayListQuotes);
                        home_arrayListTemp.addAll(arrayListQuotes);
                        home_page++;
                        setAdapter();
                    }
                } else {
                    setEmpty(false, "Error loading cards");
                }
            }
        }, requestBody);
        loadQuotes.execute();
    }

    private void setAdapter() {
        if (!home_isScroll) {
            home_adapterImageQuotes = new ImageAdapter();
            home_recyclerView.setAdapter(home_adapterImageQuotes);
        } else {
            if (home_adapterImageQuotes != null)
                home_adapterImageQuotes.notifyDataSetChanged();
        }
        setEmpty(true, "Not Found");
    }

    private void setEmpty(boolean isSuccess, String msg) {
        if (isSuccess && !home_arrayList.isEmpty()) {
            home_ll_empty.setVisibility(View.GONE);
            home_recyclerView.setVisibility(View.VISIBLE);
        } else {
            home_tv_empty.setText(msg);
            home_ll_empty.setVisibility(View.VISIBLE);
            home_recyclerView.setVisibility(View.GONE);
        }
    }

    // ════════════════════════════════
    // Image Adapter
    // ════════════════════════════════
    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImgHolder> {

        @Override
        public ImgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FrameLayout card = new FrameLayout(SingleListActivity.this);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT);
            lp.setMargins(6, 6, 6, 6);
            card.setLayoutParams(lp);

            android.graphics.drawable.GradientDrawable bg =
                    new android.graphics.drawable.GradientDrawable();
            bg.setColor(Color.WHITE);
            bg.setCornerRadius(10f);
            card.setBackground(bg);
            card.setElevation(3f);

            ImageView imageView = new ImageView(SingleListActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            FrameLayout.LayoutParams imgLp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, 380);
            imageView.setLayoutParams(imgLp);
            card.addView(imageView);

            return new ImgHolder(card, imageView);
        }

        @Override
        public void onBindViewHolder(ImgHolder holder, int position) {
            invite_Item_OneImages item = home_arrayList.get(position);

            Glide.with(SingleListActivity.this)
                    .load(item.getImageBig())
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.imageView);

            holder.itemView.setOnClickListener(v -> {
                holder.itemView.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80)
                        .withEndAction(() ->
                                holder.itemView.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                        ).start();
                openCard(position);
            });
        }

        @Override
        public int getItemCount() { return home_arrayList.size(); }

        class ImgHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            ImgHolder(View itemView, ImageView imageView) {
                super(itemView);
                this.imageView = imageView;
            }
        }
    }

    // ════════════════════════════════
    // Download JSON & Open MainActivity
    // ════════════════════════════════
    private class DownloadJsonTask extends android.os.AsyncTask<String, Void, Boolean> {
        private Bundle bundle;
        private String savedPath;

        DownloadJsonTask(Bundle bundle) {
            this.bundle = bundle;
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                File dir = new File(getFilesDir() + "/invites_amantran_bundle");
                if (!dir.exists()) dir.mkdir();

                Random random = new Random();
                savedPath = getFilesDir() + "/invites_amantran_bundle/bundle_data" + random.nextInt(1000000) + ".json";

                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.connect();

                if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) return false;

                InputStream is = new BufferedInputStream(conn.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) sb.append(line);
                reader.close();
                is.close();
                conn.disconnect();

                FileOutputStream fos = new FileOutputStream(savedPath);
                fos.write(sb.toString().getBytes());
                fos.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success && savedPath != null) {
                bundle.putString("file", savedPath);
                bundle.putString("all_data_bg_array", "");
                bundle.putString("dlet_arraylist", "");
                bundle.putString("save_page_delet_array", "");
                bundle.putString("save_page_bg_delet_array", "");
                bundle.putString("save_page_delet_iamge_array", "");

                Intent intent = new Intent(SingleListActivity.this, MainActivity.class);
                intent.putExtra("online_data", bundle);
                intent.putExtra("main_cid", cid != null ? cid : "23");
                intent.putExtra("main_cid_vector", "8");
                startActivityForResult(intent, 1234567);
            }
        }
    }
}
