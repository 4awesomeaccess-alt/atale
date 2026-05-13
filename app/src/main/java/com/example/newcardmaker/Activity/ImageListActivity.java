package com.example.newcardmaker.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newcardmaker.ImageFileManager;
import com.example.newcardmaker.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImageListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView     tvEmpty;
    private List<ImageFileManager.ImageItem> imageList;
    private ImageAdapter adapter;

    // ── View mode toggle
    private boolean isGridMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        // Back
        TextView btnBack = new TextView(this);
        btnBack.setText("←");
        btnBack.setTextSize(22);
        btnBack.setTextColor(Color.WHITE);
        btnBack.setPadding(0, 0, 20, 0);
        btnBack.setOnClickListener(v -> finish());

        // Title
        TextView tvTitle = new TextView(this);
        tvTitle.setText("Saved Images");
        tvTitle.setTextSize(18);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvTitle.setLayoutParams(titleLp);

        // Grid / List toggle
        TextView btnToggle = new TextView(this);
        btnToggle.setText("⊞");
        btnToggle.setTextSize(20);
        btnToggle.setTextColor(Color.WHITE);
        btnToggle.setPadding(12, 0, 0, 0);
        btnToggle.setOnClickListener(v -> {
            isGridMode = !isGridMode;
            btnToggle.setText(isGridMode ? "⊞" : "☰");
            recyclerView.setLayoutManager(isGridMode
                    ? new GridLayoutManager(this, 2)
                    : new androidx.recyclerview.widget.LinearLayoutManager(this));
            if (adapter != null) adapter.notifyDataSetChanged();
        });

        toolbar.addView(btnBack);
        toolbar.addView(tvTitle);
        toolbar.addView(btnToggle);
        root.addView(toolbar);

        // ── Count bar
        TextView tvCount = new TextView(this);
        tvCount.setId(R.id.tv_lock_count); // reuse id
        tvCount.setPadding(24, 12, 24, 12);
        tvCount.setTextSize(13);
        tvCount.setTextColor(Color.parseColor("#555555"));
        tvCount.setBackgroundColor(Color.parseColor("#EEEEEE"));
        root.addView(tvCount);

        // ── Empty state
        tvEmpty = new TextView(this);
        tvEmpty.setText("🖼 કોઈ Image save નથી\nImage download કરો — અહીં દેખાશે");
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

        loadImages(tvCount);
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView tvCount = findViewById(R.id.tv_lock_count);
        loadImages(tvCount);
    }

    private void loadImages(TextView tvCount) {
        imageList = ImageFileManager.getAll(this);

        if (tvCount != null) {
            tvCount.setText("કુલ " + imageList.size() + " images saved");
        }

        if (imageList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new ImageAdapter();
            recyclerView.setAdapter(adapter);
        }
    }

    // ════════════════════════════════════════
    // ── Adapter
    // ════════════════════════════════════════
    private class ImageAdapter extends
            RecyclerView.Adapter<ImageAdapter.ImgHolder> {

        @Override
        public ImgHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            // ── Card container
            FrameLayout card = new FrameLayout(ImageListActivity.this);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT);
            lp.setMargins(8, 8, 8, 8);
            card.setLayoutParams(lp);

            android.graphics.drawable.GradientDrawable cardBg =
                    new android.graphics.drawable.GradientDrawable();
            cardBg.setColor(Color.WHITE);
            cardBg.setCornerRadius(14f);
            cardBg.setStroke(1, Color.parseColor("#E0E0E0"));
            card.setBackground(cardBg);
            card.setElevation(4f);

            // ── Image
            ImageView ivThumb = new ImageView(ImageListActivity.this);
            ivThumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
            FrameLayout.LayoutParams imgLp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, 320);
            ivThumb.setLayoutParams(imgLp);
            card.addView(ivThumb);

            // ── Bottom info overlay
            LinearLayout infoOverlay = new LinearLayout(ImageListActivity.this);
            infoOverlay.setOrientation(LinearLayout.VERTICAL);
            infoOverlay.setBackgroundColor(Color.parseColor("#CC000000"));
            infoOverlay.setPadding(12, 8, 12, 8);
            FrameLayout.LayoutParams overlayLp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            overlayLp.gravity = Gravity.BOTTOM;
            infoOverlay.setLayoutParams(overlayLp);

            TextView tvName = new TextView(ImageListActivity.this);
            tvName.setTextSize(12);
            tvName.setTextColor(Color.WHITE);
            tvName.setTypeface(Typeface.DEFAULT_BOLD);
            tvName.setMaxLines(1);
            tvName.setEllipsize(TextUtils.TruncateAt.END);

            TextView tvDate = new TextView(ImageListActivity.this);
            tvDate.setTextSize(10);
            tvDate.setTextColor(Color.parseColor("#CCCCCC"));

            infoOverlay.addView(tvName);
            infoOverlay.addView(tvDate);
            card.addView(infoOverlay);

            // ── Action buttons row (Share + Delete)
            LinearLayout btnRow = new LinearLayout(ImageListActivity.this);
            btnRow.setOrientation(LinearLayout.HORIZONTAL);
            btnRow.setBackgroundColor(Color.parseColor("#F5F5F5"));
            btnRow.setPadding(8, 8, 8, 8);
            FrameLayout.LayoutParams btnRowLp = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT);
            btnRowLp.gravity = Gravity.BOTTOM;
            btnRowLp.setMargins(0, 320, 0, 0); // push below image
            btnRow.setLayoutParams(btnRowLp);

            // Share button
            TextView btnShare = new TextView(ImageListActivity.this);
            btnShare.setText("📤 Share");
            btnShare.setTextSize(12);
            btnShare.setTextColor(Color.parseColor("#1565C0"));
            btnShare.setGravity(Gravity.CENTER);
            btnShare.setPadding(8, 10, 8, 10);
            LinearLayout.LayoutParams shareLp = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            btnShare.setLayoutParams(shareLp);

            // Divider
            View divider = new View(ImageListActivity.this);
            divider.setBackgroundColor(Color.parseColor("#E0E0E0"));
            divider.setLayoutParams(new LinearLayout.LayoutParams(1,
                    LinearLayout.LayoutParams.MATCH_PARENT));

            // Delete button
            TextView btnDelete = new TextView(ImageListActivity.this);
            btnDelete.setText("🗑 Delete");
            btnDelete.setTextSize(12);
            btnDelete.setTextColor(Color.parseColor("#E53935"));
            btnDelete.setGravity(Gravity.CENTER);
            btnDelete.setPadding(8, 10, 8, 10);
            LinearLayout.LayoutParams deleteLp = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            btnDelete.setLayoutParams(deleteLp);

            btnRow.addView(btnShare);
            btnRow.addView(divider);
            btnRow.addView(btnDelete);
            card.addView(btnRow);

            return new ImgHolder(card, ivThumb, tvName, tvDate,
                    btnShare, btnDelete);
        }

        @Override
        public void onBindViewHolder(ImgHolder holder, int position) {
            ImageFileManager.ImageItem item = imageList.get(position);

            // ── Thumbnail load via Glide
            Glide.with(ImageListActivity.this)
                    .load(new File(item.filePath))
                    .centerCrop()
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivThumb);

            // ── Name
            holder.tvName.setText(item.title);

            // ── Date
            String date = new SimpleDateFormat(
                    "dd MMM yyyy", Locale.getDefault())
                    .format(new Date(item.savedAt));
            holder.tvDate.setText(date);

            // ── Open full image on click
            holder.itemView.setOnClickListener(v ->
                    showFullImageDialog(item));

            // ── Share
            holder.btnShare.setOnClickListener(v ->
                    shareImage(item.filePath));

            // ── Delete
            holder.btnDelete.setOnClickListener(v ->
                    confirmDelete(item, position));
        }

        @Override
        public int getItemCount() { return imageList.size(); }

        class ImgHolder extends RecyclerView.ViewHolder {
            ImageView ivThumb;
            TextView  tvName, tvDate, btnShare, btnDelete;

            ImgHolder(View itemView, ImageView ivThumb,
                      TextView tvName, TextView tvDate,
                      TextView btnShare, TextView btnDelete) {
                super(itemView);
                this.ivThumb   = ivThumb;
                this.tvName    = tvName;
                this.tvDate    = tvDate;
                this.btnShare  = btnShare;
                this.btnDelete = btnDelete;
            }
        }
    }

    // ════════════════════════════════════════
    // ── Full Image Dialog
    // ════════════════════════════════════════
    private void showFullImageDialog(ImageFileManager.ImageItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        ImageView fullImg = new ImageView(this);
        fullImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        fullImg.setPadding(8, 8, 8, 8);

        Glide.with(this)
                .load(new File(item.filePath))
                .into(fullImg);

        builder.setView(fullImg);
        builder.setTitle(item.title);

        builder.setPositiveButton("📤 Share", (d, w) ->
                shareImage(item.filePath));

        builder.setNeutralButton("🗑 Delete", (d, w) ->
                confirmDelete(item,
                        imageList.indexOf(item)));

        builder.setNegativeButton("Close", null);
        builder.show();
    }

    // ════════════════════════════════════════
    // ── Share Image
    // ════════════════════════════════════════
    private void shareImage(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                Toast.makeText(this, "File મળ્યો નહીં",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // ── FileProvider URI (secure sharing)
            Uri uri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".provider", // AndroidManifest provider authority
                    file);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/jpeg");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    "Card Maker થી share કરેલ image 🎨");
            shareIntent.addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(
                    shareIntent, "Image Share કરો"));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Share error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ════════════════════════════════════════
    // ── Confirm Delete
    // ════════════════════════════════════════
    private void confirmDelete(ImageFileManager.ImageItem item, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Image Delete કરો?")
                .setMessage("\"" + item.title + "\" permanently delete થઈ જશે.")
                .setPositiveButton("Delete", (d, w) -> {
                    ImageFileManager.delete(this, item.filePath);
                    imageList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, imageList.size());

                    if (imageList.isEmpty()) {
                        tvEmpty.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    }

                    // Count update
                    TextView tvCount = findViewById(R.id.tv_lock_count);
                    if (tvCount != null) {
                        tvCount.setText("કુલ " + imageList.size() + " images saved");
                    }

                    Toast.makeText(this, "✅ Delete થઈ ગઈ",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
