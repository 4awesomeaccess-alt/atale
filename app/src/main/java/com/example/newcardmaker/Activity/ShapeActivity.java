package com.example.newcardmaker.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.newcardmaker.R;
import com.example.newcardmaker.invite_online_database.invite_AppConstants;
import com.example.newcardmaker.invite_online_database.invite_EndlessRecyclerViewScrollListener1;
import com.example.newcardmaker.invite_online_database.invite_Item_OneImages;
import com.example.newcardmaker.invite_online_database.invite_Load_OneImages;
import com.example.newcardmaker.invite_online_database.invite_Methods;
import com.example.newcardmaker.invite_online_database.invite_OneImagesListener;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class ShapeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    StaggeredGridLayoutManager layoutManager;
    ArrayList<invite_Item_OneImages> list = new ArrayList<>();
    ImageAdapter adapter;
    invite_Load_OneImages loadQuotes;
    RequestBody requestBody;
    invite_Methods methods;
    ProgressBar progressBar;
    LinearLayout llEmpty;
    TextView tvEmpty;
    boolean isOver = false, isScroll = false;
    int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape);

        progressBar  = findViewById(R.id.home_progressBar);
        llEmpty      = findViewById(R.id.home_ll_empty);
        tvEmpty      = findViewById(R.id.home_tv_empty);
        recyclerView = findViewById(R.id.home_recyclerView);

        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        TextView tvTitle = findViewById(R.id.home_tv_title);
        if (tvTitle != null) tvTitle.setText("Shape Templates");

        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setPadding(8, 8, 8, 8);

        methods = new invite_Methods(this);
        loadImages();
    }

    private void loadImages() {
        isOver = false; isScroll = false; page = 1;
        list.clear();
        recyclerView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        fetchImages();

        recyclerView.addOnScrollListener(new invite_EndlessRecyclerViewScrollListener1(layoutManager) {
            @Override
            public void onLoadMore(int p, int total, RecyclerView view) {
                if (!isOver) {
                    new android.os.Handler().postDelayed(() -> {
                        isScroll = true;
                        fetchImages();
                    }, 500);
                }
            }
        });

        recyclerView.addOnItemTouchListener(new invite_AppConstants.RecyclerTouchListener(this, recyclerView,
                new invite_AppConstants.RecyclerTouchListener.ClickListener() {
                    @Override public void onClick(View view, int position) {
                        if (position >= 0 && position < list.size()) openCard(position);
                    }
                    @Override public void onLongClick(View view, int position) {}
                }));
    }

    private void fetchImages() {
        if (!methods.isConnectingToInternet()) {
            setEmpty("Internet connection નથી");
            return;
        }
        requestBody = methods.getAPIRequest(
                invite_AppConstants.METHOD_ALL_SQUARE_FRAME,
                page, "", "", "", "", "", "", "", "", "", "", "", "",
                "", invite_AppConstants.itemUser.getId(), "", null);

        loadQuotes = new invite_Load_OneImages(new invite_OneImagesListener() {
            @Override public void onStart() {}
            @Override public void onEnd(String success, String verifyStatus, String message,
                                        ArrayList<invite_Item_OneImages> result, int total) {
                progressBar.setVisibility(View.GONE);
                if ("1".equals(success) && !"-1".equals(verifyStatus)) {
                    if (result.isEmpty()) {
                        isOver = true;
                        if (list.isEmpty()) setEmpty("કોઈ template નથી");
                    } else {
                        list.addAll(result);
                        page++;
                        if (!isScroll) {
                            adapter = new ImageAdapter();
                            recyclerView.setAdapter(adapter);
                        } else if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        llEmpty.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                } else { setEmpty("Error loading"); }
            }
        }, requestBody);
        loadQuotes.execute();
    }

    private void setEmpty(String msg) {
        if (tvEmpty != null) tvEmpty.setText(msg);
        llEmpty.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void openCard(int position) {
        invite_Item_OneImages item = list.get(position);
        String baseUrl = invite_AppConstants.SERVER_URL.replace("api.php", "");
        String jsonUrl = baseUrl + "images/" + item.getquote_imagejson();
        String imageUrl = item.getImageBig();

        new android.os.AsyncTask<String, Void, String>() {
            @Override protected String doInBackground(String... urls) {
                try {
                    java.net.URL url = new java.net.URL(urls[0]);
                    java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10000); conn.setReadTimeout(10000); conn.connect();
                    if (conn.getResponseCode() != 200) return null;
                    java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) sb.append(line);
                    reader.close(); conn.disconnect();

                    java.io.File dir = getExternalFilesDir(null);
                    if (dir != null && !dir.exists()) dir.mkdirs();
                    String name = "shape_" + System.currentTimeMillis();
                    java.io.File outFile = new java.io.File(dir, name + ".json");
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(outFile);
                    fos.write(sb.toString().getBytes()); fos.close();
                    return outFile.getAbsolutePath();
                } catch (Exception e) { return null; }
            }
            @Override protected void onPostExecute(String path) {
                if (path != null) {
                    Intent intent = new Intent(ShapeActivity.this, MainActivity.class);
                    intent.putExtra("FILE_PATH", path);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else {
                    android.widget.Toast.makeText(ShapeActivity.this,
                            "Download failed", android.widget.Toast.LENGTH_SHORT).show();
                }
            }
        }.execute(jsonUrl);
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.Holder> {
        @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            FrameLayout card = new FrameLayout(ShapeActivity.this);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            lp.setMargins(6, 6, 6, 6);
            card.setLayoutParams(lp);
            android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
            bg.setColor(android.graphics.Color.WHITE); bg.setCornerRadius(10f);
            card.setBackground(bg); card.setElevation(3f);
            ImageView iv = new ImageView(ShapeActivity.this);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            iv.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, 380));
            card.addView(iv);
            return new Holder(card, iv);
        }
        @Override public void onBindViewHolder(Holder h, int pos) {
            Glide.with(ShapeActivity.this).load(list.get(pos).getImageBig())
                    .fitCenter().placeholder(android.R.drawable.ic_menu_gallery).into(h.iv);
            h.itemView.setOnClickListener(v -> openCard(h.getAdapterPosition()));
        }
        @Override public int getItemCount() { return list.size(); }
        class Holder extends RecyclerView.ViewHolder {
            ImageView iv;
            Holder(View v, ImageView iv) { super(v); this.iv = iv; }
        }
    }
}
