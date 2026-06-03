package com.example.newcardmaker.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.newcardmaker.R;
import com.example.newcardmaker.invite_online_database.invite_AppConstants;
import com.example.newcardmaker.invite_online_database.invite_EndlessRecyclerViewScrollListener1;
import com.example.newcardmaker.invite_online_database.invite_Item_Shape;
import com.example.newcardmaker.invite_online_database.invite_Load_OneImages_shape;
import com.example.newcardmaker.invite_online_database.invite_Methods;
import com.example.newcardmaker.invite_online_database.invite_ShapeListener;

import java.util.ArrayList;

import okhttp3.RequestBody;

public class ShapeActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    StaggeredGridLayoutManager layoutManager;
    ArrayList<invite_Item_Shape> list = new ArrayList<>();
    ImageAdapter adapter;
    invite_Load_OneImages_shape loader;
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
        if (tvTitle != null) tvTitle.setText("Shape");

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
            @Override public void onLoadMore(int p, int total, RecyclerView view) {
                if (!isOver) new Handler().postDelayed(() -> { isScroll = true; fetchImages(); }, 500);
            }
        });

        recyclerView.addOnItemTouchListener(new invite_AppConstants.RecyclerTouchListener(this, recyclerView,
            new invite_AppConstants.RecyclerTouchListener.ClickListener() {
                @Override public void onClick(View v, int pos) {
                    if (pos >= 0 && pos < list.size()) openCard(pos);
                }
                @Override public void onLongClick(View v, int pos) {}
            }));
    }

    private void fetchImages() {
        if (!methods.isConnectingToInternet()) { setEmpty("Internet connection નથી"); return; }

        requestBody = methods.getAPIRequest(
                invite_AppConstants.METHOD_ALL_SQUARE_FRAME,
                page, "", "", "", "", "", "", "", "", "", "", "", "",
                "", invite_AppConstants.itemUser.getId(), "", null);

        loader = new invite_Load_OneImages_shape(new invite_ShapeListener() {
            @Override public void onStart() {}
            @Override public void onEnd(String success, ArrayList<invite_Item_Shape> result) {
                progressBar.setVisibility(View.GONE);
                if ("1".equals(success) && !result.isEmpty()) {
                    list.addAll(result);
                    page++;
                    if (!isScroll) { adapter = new ImageAdapter(); recyclerView.setAdapter(adapter); }
                    else if (adapter != null) adapter.notifyDataSetChanged();
                    llEmpty.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    isOver = true;
                    if (list.isEmpty()) setEmpty("કોઈ shape નથી");
                }
            }
        }, requestBody);
        loader.execute();
    }

    private void setEmpty(String msg) {
        if (tvEmpty != null) tvEmpty.setText(msg);
        llEmpty.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    private void openCard(int position) {
        invite_Item_Shape item = list.get(position);
        String imageUrl = item.getImageUrl();
        Toast.makeText(this, "Selected: " + item.getId(), Toast.LENGTH_SHORT).show();
        // TODO: JSON URL masha avse tyare open karvu
    }

    private class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.Holder> {
        @Override public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            FrameLayout card = new FrameLayout(ShapeActivity.this);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
            lp.setMargins(6, 6, 6, 6); card.setLayoutParams(lp);
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
            Glide.with(ShapeActivity.this).load(list.get(pos).getImageUrl())
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
