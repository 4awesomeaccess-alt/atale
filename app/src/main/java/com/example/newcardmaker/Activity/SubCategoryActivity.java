package com.example.newcardmaker.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newcardmaker.R;
import com.example.newcardmaker.invite_online_database.invite_AppConstants;
import com.example.newcardmaker.invite_online_database.invite_ItemSubCat_main;
import com.example.newcardmaker.invite_online_database.invite_Methods;

import java.util.ArrayList;

public class SubCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private invite_Methods methods;
    private ArrayList<invite_ItemSubCat_main> subCatList = new ArrayList<>();
    private String cid, categoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        cid          = getIntent().getStringExtra("cid");
        categoryName = getIntent().getStringExtra("name");

        recyclerView = findViewById(R.id.recyclerView);
        progressBar  = findViewById(R.id.progressBar);
        tvEmpty      = findViewById(R.id.tv_empty);

        TextView tvTitle = findViewById(R.id.tv_title);
        if (categoryName != null) tvTitle.setText(categoryName);

        TextView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> finish());

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        methods = new invite_Methods(this);
        loadSubCategories();
    }

    private void loadSubCategories() {
        if (!methods.isConnectingToInternet()) {
            tvEmpty.setVisibility(View.VISIBLE);
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        android.os.AsyncTask<Void, Void, ArrayList<invite_ItemSubCat_main>> task =
                new android.os.AsyncTask<Void, Void, ArrayList<invite_ItemSubCat_main>>() {
            @Override
            protected ArrayList<invite_ItemSubCat_main> doInBackground(Void... v) {
                ArrayList<invite_ItemSubCat_main> list = new ArrayList<>();
                try {
                    okhttp3.RequestBody reqBody = methods.getAPIRequest(
                            invite_AppConstants.METHOD_CAT_PHOTOWALL1,
                            0, "", "", "", cid, "", "", "", "", "", "", "", "", "", "", "", null);
                    String json = com.example.newcardmaker.invite_online_database.invite_JSONParser
                            .okhttpPost(invite_AppConstants.SERVER_URL, reqBody);
                    org.json.JSONObject root = new org.json.JSONObject(json);
                    org.json.JSONObject diary = root.getJSONObject("QUOTES_DIARY");
                    org.json.JSONArray arr = diary.getJSONArray("image_quotes_cat");
                    for (int i = 0; i < arr.length(); i++) {
                        org.json.JSONObject c = arr.getJSONObject(i);
                        list.add(new invite_ItemSubCat_main(
                                c.optString("cid", ""),
                                c.optString("category_name", ""),
                                c.optString("category_image", ""),
                                c.optString("detail", ""),
                                c.optString("detail1", "")));
                    }
                } catch (Exception e) {
                    android.util.Log.e("#SubCat_err", e.getMessage() + "");
                }
                return list;
            }

            @Override
            protected void onPostExecute(ArrayList<invite_ItemSubCat_main> result) {
                progressBar.setVisibility(View.GONE);
                if (result.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    subCatList.addAll(result);
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.setAdapter(new SubCatAdapter());
                }
            }
        };
        task.execute();
    }

    private class SubCatAdapter extends RecyclerView.Adapter<SubCatAdapter.SCHolder> {

        @Override
        public SCHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            android.widget.FrameLayout card = new android.widget.FrameLayout(SubCategoryActivity.this);
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

            ImageView imageView = new ImageView(SubCategoryActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            android.widget.FrameLayout.LayoutParams imgLp =
                    new android.widget.FrameLayout.LayoutParams(
                            android.widget.FrameLayout.LayoutParams.MATCH_PARENT, 350);
            imageView.setLayoutParams(imgLp);
            card.addView(imageView);

            TextView tvName = new TextView(SubCategoryActivity.this);
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

            return new SCHolder(card, imageView, tvName);
        }

        @Override
        public void onBindViewHolder(SCHolder holder, int position) {
            invite_ItemSubCat_main item = subCatList.get(position);

            Glide.with(SubCategoryActivity.this)
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

                Intent intent = new Intent(SubCategoryActivity.this, SingleListActivity.class);
                intent.putExtra("cid", item.getId());
                intent.putExtra("name", item.getName());
                startActivity(intent);
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
}
