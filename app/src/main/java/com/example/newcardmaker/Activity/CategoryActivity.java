package com.example.newcardmaker.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newcardmaker.R;
import com.example.newcardmaker.invite_online_database.invite_AppConstants;
import com.example.newcardmaker.invite_online_database.invite_ItemSubCat_main;
import com.example.newcardmaker.invite_online_database.invite_LoadSubCat_main;
import com.example.newcardmaker.invite_online_database.invite_Methods;
import com.example.newcardmaker.invite_online_database.invite_SubCategoryListener_main;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private invite_Methods methods;
    private ArrayList<invite_ItemSubCat_main> categoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar  = findViewById(R.id.progressBar);
        tvEmpty      = findViewById(R.id.tv_empty);

        TextView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

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
                    @Override public void onStart() { categoryList.clear(); }

                    @Override
                    public void onEnd(String success, String verifyStatus, String message,
                                      ArrayList<invite_ItemSubCat_main> imageCat,
                                      ArrayList<invite_ItemSubCat_main> textCat) {
                        progressBar.setVisibility(View.GONE);
                        if ("1".equals(success) && !"-1".equals(verifyStatus)) {
                            categoryList.addAll(imageCat);
                            if (categoryList.isEmpty()) {
                                tvEmpty.setVisibility(View.VISIBLE);
                            } else {
                                tvEmpty.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                recyclerView.setAdapter(new CategoryAdapter());
                            }
                        } else {
                            tvEmpty.setVisibility(View.VISIBLE);
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

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CatHolder> {

        @Override
        public CatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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

            ImageView imageView = new ImageView(CategoryActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            android.widget.FrameLayout.LayoutParams imgLp =
                    new android.widget.FrameLayout.LayoutParams(
                            android.widget.FrameLayout.LayoutParams.MATCH_PARENT, 350);
            imageView.setLayoutParams(imgLp);
            card.addView(imageView);

            TextView tvName = new TextView(CategoryActivity.this);
            tvName.setTextSize(13);
            tvName.setTextColor(Color.WHITE);
            tvName.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            tvName.setBackgroundColor(Color.parseColor("#AA000000"));
            tvName.setPadding(12, 8, 12, 10);
            tvName.setGravity(android.view.Gravity.CENTER);
            android.widget.FrameLayout.LayoutParams nameLp =
                    new android.widget.FrameLayout.LayoutParams(
                            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                            android.widget.FrameLayout.LayoutParams.WRAP_CONTENT);
            nameLp.gravity = android.view.Gravity.BOTTOM;
            tvName.setLayoutParams(nameLp);
            card.addView(tvName);

            return new CatHolder(card, imageView, tvName);
        }

        @Override
        public void onBindViewHolder(CatHolder holder, int position) {
            invite_ItemSubCat_main item = categoryList.get(position);

            Glide.with(CategoryActivity.this)
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

                android.util.Log.e("#CategoryClick", "id=" + item.getId() + " name=" + item.getName());

                // Directly open SingleListActivity
                Intent intent = new Intent(CategoryActivity.this, SingleListActivity.class);
                intent.putExtra("cid", item.getId());
                intent.putExtra("name", item.getName());
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
