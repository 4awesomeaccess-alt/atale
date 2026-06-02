package com.example.newcardmaker.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.example.newcardmaker.R;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.newcardmaker.invite_online_database.invite_AppConstants;
import com.example.newcardmaker.invite_online_database.invite_EndlessRecyclerViewScrollListener1;
import com.example.newcardmaker.invite_online_database.invite_Item_OneImages;
import com.example.newcardmaker.invite_online_database.invite_ItemSubCat_main;
import com.example.newcardmaker.invite_online_database.invite_Load_OneImages;
import com.example.newcardmaker.invite_online_database.invite_LoadSubCat_main;
import com.example.newcardmaker.invite_online_database.invite_Methods;
import com.example.newcardmaker.invite_online_database.invite_OneImagesListener;
import com.example.newcardmaker.invite_online_database.invite_SubCategoryListener_main;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class ShapeActivity extends AppCompatActivity {

    // ── Sub-category mode
    RecyclerView subCatRecycler;
    ArrayList<invite_ItemSubCat_main> subCatList = new ArrayList<>();

    // ── Image mode
    RecyclerView home_recyclerView;
    StaggeredGridLayoutManager home_lLayout;
    Boolean home_isOver = false, home_isScroll = false;
    int home_page = 1;
    ArrayList<invite_Item_OneImages> home_arrayList = new ArrayList<>();
    ArrayList<invite_Item_OneImages> home_arrayListTemp = new ArrayList<>();
    ImageAdapter home_adapterImageQuotes;
    invite_Load_OneImages loadQuotes;
    RequestBody requestBody;

    // ── Common
    invite_Methods home_methods;
    LinearLayout home_ll_empty;
    ProgressBar home_progressBar;
    TextView home_tv_empty;
    Bundle bb;
    String cid, categoryName;
    String currentSubCatId = null; // null = show sub-cats, non-null = show images

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cid = getIntent().getStringExtra("cid");
        categoryName = getIntent().getStringExtra("name");

        setContentView(R.layout.activity_shape);

        // ── Views from XML
        LinearLayout toolbar = findViewById(R.id.home_toolbar);
        TextView tvTitle = findViewById(R.id.home_tv_title);
        home_progressBar = findViewById(R.id.home_progressBar);
        home_ll_empty = findViewById(R.id.home_ll_empty);
        subCatRecycler = findViewById(R.id.subCatRecycler);
        home_recyclerView = findViewById(R.id.home_recyclerView);

        // Subcategory grid
        subCatRecycler.setLayoutManager(new GridLayoutManager(this, 2));
        subCatRecycler.setPadding(8, 8, 8, 8);

        // Image grid
        home_lLayout = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        home_recyclerView.setLayoutManager(home_lLayout);
        home_recyclerView.setPadding(8, 8, 8, 8);

        // Title
        if (categoryName != null) tvTitle.setText(categoryName);

        // Back button
        android.view.View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        home_methods = new invite_Methods(this);

        // Load all images directly
        loadImages("");
    }

    // ════════════════════════════════
    // Load Sub-Categories
    // ════════════════════════════════
    private void loadSubCategories() {
        if (!home_methods.isConnectingToInternet()) {
            setEmpty("Internet connection નથી");
            return;
        }

        home_progressBar.setVisibility(View.VISIBLE);
        subCatRecycler.setVisibility(View.GONE);

        okhttp3.RequestBody reqBody = home_methods.getAPIRequest(
                invite_AppConstants.METHOD_CAT_PHOTOWALL1,
                0, "", "", "", cid, "", "", "", "", "", "", "", "", "", "", "", null);

        new android.os.AsyncTask<Void, Void, ArrayList<invite_ItemSubCat_main>>() {
            @Override
            protected ArrayList<invite_ItemSubCat_main> doInBackground(Void... v) {
                ArrayList<invite_ItemSubCat_main> list = new ArrayList<>();
                try {
                    String json = com.example.newcardmaker.invite_online_database.invite_JSONParser
                            .okhttpPost(invite_AppConstants.SERVER_URL, reqBody);
                    android.util.Log.e("#SubCat_raw", json + "");
                    org.json.JSONObject root = new org.json.JSONObject(json);
                    org.json.JSONObject diary = root.getJSONObject("QUOTES_DIARY");
                    org.json.JSONArray arr = diary.getJSONArray("image_quotes_cat");
                    for (int i = 0; i < arr.length(); i++) {
                        org.json.JSONObject c = arr.getJSONObject(i);
                        String id = c.optString("cid", "");
                        String name = c.optString("category_name", "");
                        String image = c.optString("category_image", "").replace(" ", "%20");
                        String detail = c.optString("detail", "");
                        String detail1 = c.optString("detail1", "");
                        list.add(new invite_ItemSubCat_main(id, name, image, detail, detail1));
                    }
                } catch (Exception e) {
                    android.util.Log.e("#SubCat_err", e.getMessage() + "");
                }
                return list;
            }

            @Override
            protected void onPostExecute(ArrayList<invite_ItemSubCat_main> result) {
                home_progressBar.setVisibility(View.GONE);
                android.util.Log.e("#SubCat_count", "count=" + result.size());
                if (result.isEmpty()) {
                    setEmpty("કોઈ sub-category નથી");
                } else {
                    subCatList.clear();
                    subCatList.addAll(result);
                    home_ll_empty.setVisibility(View.GONE);
                    subCatRecycler.setVisibility(View.VISIBLE);
                    subCatRecycler.setAdapter(new SubCatAdapter());
                }
            }
        }.execute();
    }

    // ════════════════════════════════
    // Load Images by Sub-Category
    // ════════════════════════════════
    private void loadImages(String subCatId) {
        currentSubCatId = subCatId;
        home_isOver = false;
        home_isScroll = false;
        home_page = 1;
        home_arrayList.clear();
        home_arrayListTemp.clear();

        subCatRecycler.setVisibility(View.GONE);
        home_recyclerView.setVisibility(View.VISIBLE);
        home_progressBar.setVisibility(View.VISIBLE);

        fetchImages(subCatId);

        // Endless scroll
        home_recyclerView.addOnScrollListener(
                new invite_EndlessRecyclerViewScrollListener1(home_lLayout) {
                    @Override
                    public void onLoadMore(int p, int totalItemsCount, RecyclerView view) {
                        if (!home_isOver) {
                            new Handler().postDelayed(() -> {
                                home_isScroll = true;
                                fetchImages(subCatId);
                            }, 500);
                        }
                    }
                });

        // Item click
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

    private void fetchImages(String subCatId) {
        if (!home_methods.isConnectingToInternet()) {
            setEmpty("Internet connection નથી");
            return;
        }

        requestBody = home_methods.getAPIRequest(
                invite_AppConstants.METHOD_IMAGE_All_PHOTOGREETING11,
                home_page, "", "", "", "", "", "", "", "", "", "", "", "",
                "", invite_AppConstants.itemUser.getId(), "", null);
        android.util.Log.e("#fetchImages_cid", "loading all images page=" + home_page);

        loadQuotes = new invite_Load_OneImages(new invite_OneImagesListener() {
            @Override
            public void onStart() {
                if (home_arrayList.isEmpty()) {
                    home_recyclerView.setVisibility(View.GONE);
                    home_ll_empty.setVisibility(View.GONE);
                }
            }

            @Override
            public void onEnd(String success, String verifyStatus, String message,
                              ArrayList<invite_Item_OneImages> arrayListQuotes, int total_records) {
                home_progressBar.setVisibility(View.GONE);
                if ("1".equals(success) && !"-1".equals(verifyStatus) && !"-2".equals(verifyStatus)) {
                    if (arrayListQuotes.isEmpty()) {
                        home_isOver = true;
                        if (home_arrayList.isEmpty()) setEmpty("No cards found");
                    } else {
                        for (invite_Item_OneImages imgItem : arrayListQuotes) {
                            android.util.Log.e("#imgCatId", "imgCatId=" + imgItem.getCatId() + " subCatId=" + subCatId);
                            home_arrayList.add(imgItem);
                            home_arrayListTemp.add(imgItem);
                        }
                        home_page++;
                        setImageAdapter();
                    }
                } else {
                    setEmpty("Error loading cards");
                }
            }
        }, requestBody);
        loadQuotes.execute();
    }

    private void setImageAdapter() {
        if (!home_isScroll) {
            home_adapterImageQuotes = new ImageAdapter();
            home_recyclerView.setAdapter(home_adapterImageQuotes);
        } else {
            if (home_adapterImageQuotes != null)
                home_adapterImageQuotes.notifyDataSetChanged();
        }
        home_ll_empty.setVisibility(View.GONE);
        home_recyclerView.setVisibility(View.VISIBLE);
    }

    private void setEmpty(String msg) {
        home_tv_empty.setText(msg);
        home_ll_empty.setVisibility(View.VISIBLE);
        subCatRecycler.setVisibility(View.GONE);
        home_recyclerView.setVisibility(View.GONE);
    }

    // ════════════════════════════════
    // Open Card
    // ════════════════════════════════
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

        // JSON download karo and external files dir ma save karo
        String baseUrl = invite_AppConstants.SERVER_URL.replace("api.php", "");
        String jsonUrl = baseUrl + "images/" + item.getquote_imagejson();
        String imageUrl = item.getImageBig();
        android.util.Log.e("#JSON_URL", "url=" + jsonUrl);
        new android.os.AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... urls) {
                try {
                    java.net.URL url = new java.net.URL(urls[0]);
                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(10000);
                    conn.connect();
                    if (conn.getResponseCode() != java.net.HttpURLConnection.HTTP_OK) return null;

                    java.io.InputStream is = new java.io.BufferedInputStream(conn.getInputStream());
                    java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    reader.close(); is.close(); conn.disconnect();

                    java.io.File dir = getExternalFilesDir(null);
                    if (dir != null && !dir.exists()) dir.mkdirs();
                    String uniqueName = "design_" + System.currentTimeMillis();

                    // JSON save
                    java.io.File outFile = new java.io.File(dir, uniqueName + ".json");
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(outFile);
                    fos.write(sb.toString().getBytes());
                    fos.close();

                    // Image URL .img file ma save
                    java.io.File imgFile = new java.io.File(dir, uniqueName + ".img");
                    java.io.FileOutputStream imgFos = new java.io.FileOutputStream(imgFile);
                    imgFos.write(imageUrl.getBytes());
                    imgFos.close();

                    return outFile.getAbsolutePath();
                } catch (Exception e) {
                    android.util.Log.e("#JSON_err", e.getMessage() + "");
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String savedPath) {
                if (savedPath != null) {
                    android.util.Log.e("#JSON_saved", "path=" + savedPath);
                    Intent intent = new Intent(ShapeActivity.this, MainActivity.class);
                    intent.putExtra("FILE_PATH", savedPath);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else {
                    android.widget.Toast.makeText(ShapeActivity.this,
                            "Download failed", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(jsonUrl);
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

    // ════════════════════════════════
    // Sub-Category Adapter
    // ════════════════════════════════
    private class SubCatAdapter extends RecyclerView.Adapter<SubCatAdapter.SCHolder> {

        @Override
        public SCHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FrameLayout card = new FrameLayout(ShapeActivity.this);
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

            ImageView imageView = new ImageView(ShapeActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            FrameLayout.LayoutParams imgLp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, 350);
            imageView.setLayoutParams(imgLp);
            card.addView(imageView);

            TextView tvName = new TextView(ShapeActivity.this);
            tvName.setTextSize(13);
            tvName.setTextColor(Color.WHITE);
            tvName.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            tvName.setBackgroundColor(Color.parseColor("#AA000000"));
            tvName.setPadding(12, 8, 12, 10);
            tvName.setGravity(Gravity.CENTER);
            FrameLayout.LayoutParams nameLp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            nameLp.gravity = Gravity.BOTTOM;
            nameLp.setMargins(0, 350, 0, 0);
            tvName.setLayoutParams(nameLp);
            card.addView(tvName);

            return new SCHolder(card, imageView, tvName);
        }

        @Override
        public void onBindViewHolder(SCHolder holder, int position) {
            invite_ItemSubCat_main item = subCatList.get(position);

            Glide.with(ShapeActivity.this)
                    .load(item.getImageBig())
                    .fitCenter()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.imageView);

            holder.tvName.setText(item.getName());

            holder.itemView.setOnClickListener(v -> {
                holder.itemView.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80)
                        .withEndAction(() ->
                                holder.itemView.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                        ).start();
                // Use sub-category cid directly
                android.util.Log.e("#loadImages", "cid=" + item.getId() + " detail=" + item.getdetail());
                loadImages(item.getId());
            });
        }

        @Override
        public int getItemCount() { return subCatList.size(); }

        class SCHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvName;
            SCHolder(View itemView, ImageView imageView, TextView tvName) {
                super(itemView);
                this.imageView = imageView;
                this.tvName = tvName;
            }
        }
    }

    // ════════════════════════════════
    // Image Adapter
    // ════════════════════════════════
    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImgHolder> {

        @Override
        public ImgHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            FrameLayout card = new FrameLayout(ShapeActivity.this);
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

            ImageView imageView = new ImageView(ShapeActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            FrameLayout.LayoutParams imgLp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, 380);
            imageView.setLayoutParams(imgLp);
            card.addView(imageView);

            return new ImgHolder(card, imageView);
        }

        @Override
        public void onBindViewHolder(ImgHolder holder, int position) {
            invite_Item_OneImages item = home_arrayList.get(position);

            Glide.with(ShapeActivity.this)
                    .load(item.getImageBig())
                    .fitCenter()
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

}
