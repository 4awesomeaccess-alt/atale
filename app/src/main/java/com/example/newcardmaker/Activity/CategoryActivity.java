package com.example.newcardmaker.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newcardmaker.invite_online_database.invite_AppConstants;
import com.example.newcardmaker.invite_online_database.invite_ItemSubCat_main;
import com.example.newcardmaker.invite_online_database.invite_LoadSubCat_main;
import com.example.newcardmaker.invite_online_database.invite_Methods;
import com.example.newcardmaker.invite_online_database.invite_SubCategoryListener_main;
import com.example.newcardmaker.invite_sticker.invite_sticker_main_category;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private invite_Methods methods;
    private ArrayList<invite_ItemSubCat_main> categoryList = new ArrayList<>();
    private CategoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ── Root Layout
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
        tvTitle.setText("Categories");
        tvTitle.setTextSize(18);
        tvTitle.setTextColor(Color.WHITE);
        android.graphics.Typeface bold = android.graphics.Typeface.DEFAULT_BOLD;
        tvTitle.setTypeface(bold);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvTitle.setLayoutParams(titleLp);

        toolbar.addView(btnBack);
        toolbar.addView(tvTitle);
        root.addView(toolbar);

        // ── ProgressBar
        progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);
        LinearLayout.LayoutParams pbLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        pbLp.gravity = Gravity.CENTER_HORIZONTAL;
        pbLp.setMargins(0, 40, 0, 0);
        progressBar.setLayoutParams(pbLp);
        root.addView(progressBar);

        // ── Empty text
        tvEmpty = new TextView(this);
        tvEmpty.setText("કોઈ category મળી નહીં");
        tvEmpty.setTextSize(16);
        tvEmpty.setTextColor(Color.GRAY);
        tvEmpty.setGravity(Gravity.CENTER);
        tvEmpty.setPadding(40, 80, 40, 40);
        tvEmpty.setVisibility(View.GONE);
        root.addView(tvEmpty);

        // ── RecyclerView
        recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setPadding(8, 8, 8, 8);
        LinearLayout.LayoutParams rvLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        recyclerView.setLayoutParams(rvLp);
        root.addView(recyclerView);

        setContentView(root);

        // ── Load categories
        methods = new invite_Methods(this);
        loadCategories();
    }

    private void loadCategories() {
        if (!methods.isConnectingToInternet()) {
            tvEmpty.setVisibility(View.VISIBLE);
            tvEmpty.setText("Internet connection નથી");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        invite_LoadSubCat_main loader = new invite_LoadSubCat_main(
                new invite_SubCategoryListener_main() {
                    @Override
                    public void onStart() {
                        categoryList.clear();
                    }

                    @Override
                    public void onEnd(String success, String verifyStatus, String message,
                                      ArrayList<invite_ItemSubCat_main> imageCat,
                                      ArrayList<invite_ItemSubCat_main> textCat) {
                        progressBar.setVisibility(View.GONE);

                        if ("1".equals(success) && !"-1".equals(verifyStatus)) {
                            categoryList.addAll(imageCat);
                            if (categoryList.isEmpty()) {
                                tvEmpty.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            } else {
                                tvEmpty.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                adapter = new CategoryAdapter();
                                recyclerView.setAdapter(adapter);
                            }
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                            Toast.makeText(CategoryActivity.this,
                                    "Categories load નહીં થઈ", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                methods.getAPIRequest(invite_AppConstants.METHOD_CAT_PHOTOWALL,
                        0, "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", null)
        );
        loader.execute();
    }

    // ════════════════════════════════
    // Adapter
    // ════════════════════════════════
    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CatHolder> {

        @Override
        public CatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Card
            android.widget.FrameLayout card = new android.widget.FrameLayout(CategoryActivity.this);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT);
            lp.setMargins(8, 8, 8, 8);
            card.setLayoutParams(lp);

            android.graphics.drawable.GradientDrawable bg =
                    new android.graphics.drawable.GradientDrawable();
            bg.setColor(Color.WHITE);
            bg.setCornerRadius(12f);
            bg.setStroke(1, Color.parseColor("#E0E0E0"));
            card.setBackground(bg);
            card.setElevation(4f);

            // Image
            ImageView imageView = new ImageView(CategoryActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            android.widget.FrameLayout.LayoutParams imgLp =
                    new android.widget.FrameLayout.LayoutParams(
                            android.widget.FrameLayout.LayoutParams.MATCH_PARENT, 350);
            imageView.setLayoutParams(imgLp);
            card.addView(imageView);

            // Name overlay
            TextView tvName = new TextView(CategoryActivity.this);
            tvName.setTextSize(13);
            tvName.setTextColor(Color.WHITE);
            tvName.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            tvName.setBackgroundColor(Color.parseColor("#AA000000"));
            tvName.setPadding(12, 8, 12, 10);
            tvName.setGravity(Gravity.CENTER);
            android.widget.FrameLayout.LayoutParams nameLp =
                    new android.widget.FrameLayout.LayoutParams(
                            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT);
            nameLp.gravity = Gravity.BOTTOM;
            nameLp.setMargins(0, 350, 0, 0);
            tvName.setLayoutParams(nameLp);
            card.addView(tvName);

            return new CatHolder(card, imageView, tvName);
        }

        @Override
        public void onBindViewHolder(CatHolder holder, int position) {
            invite_ItemSubCat_main item = categoryList.get(position);

            // Load image
            Glide.with(CategoryActivity.this)
                    .load(item.getImageBig())
                    .fitCenter()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.imageView);

            holder.tvName.setText(item.getName());

            // Click — open invite_sticker_main_category
            holder.itemView.setOnClickListener(v -> {
                // Press animation
                holder.itemView.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80)
                        .withEndAction(() ->
                                holder.itemView.animate().scaleX(1f).scaleY(1f).setDuration(80).start()
                        ).start();

                android.util.Log.e("#CategoryClick", "id=" + item.getId() + " name=" + item.getName());

                Intent intent = new Intent(CategoryActivity.this,
                        SingleListActivity.class);
                intent.putExtra("cid", item.getId());
                intent.putExtra("name", item.getName());
                intent.putExtra("sticker_typo", item.getdetail());
                intent.putExtra("sticker_vector", item.getdetail1());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() { return categoryList.size(); }

        class CatHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView tvName;

            CatHolder(View itemView, ImageView imageView, TextView tvName) {
                super(itemView);
                this.imageView = imageView;
                this.tvName = tvName;
            }
        }
    }
}
