package com.example.newcardmaker.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newcardmaker.PdfFileManager;
import com.example.newcardmaker.PdfViewerActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PdfListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView     tvEmpty;
    private List<PdfFileManager.PdfItem> pdfList;
    private PdfAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ── Root layout (programmatic — no XML needed)
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#F5F5F5"));

        // ── Toolbar
        LinearLayout toolbar = new LinearLayout(this);
        toolbar.setOrientation(LinearLayout.HORIZONTAL);
        toolbar.setBackgroundColor(Color.parseColor("#1565C0"));
        toolbar.setPadding(16, 48, 16, 16);
        toolbar.setGravity(android.view.Gravity.CENTER_VERTICAL);

        // Back button
        TextView btnBack = new TextView(this);
        btnBack.setText("←");
        btnBack.setTextSize(22);
        btnBack.setTextColor(Color.WHITE);
        btnBack.setPadding(0, 0, 24, 0);
        btnBack.setOnClickListener(v -> finish());

        // Title
        TextView tvTitle = new TextView(this);
        tvTitle.setText("Saved PDFs");
        tvTitle.setTextSize(18);
        tvTitle.setTextColor(Color.WHITE);
        tvTitle.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);

        toolbar.addView(btnBack);
        toolbar.addView(tvTitle);
        root.addView(toolbar);

        // ── Empty state
        tvEmpty = new TextView(this);
        tvEmpty.setText("📄 કોઈ PDF save નથી\nPDF download કરો — અહીં દેખાશે");
        tvEmpty.setTextSize(16);
        tvEmpty.setTextColor(Color.GRAY);
        tvEmpty.setGravity(android.view.Gravity.CENTER);
        tvEmpty.setPadding(40, 80, 40, 40);
        tvEmpty.setVisibility(View.GONE);
        root.addView(tvEmpty);

        // ── RecyclerView
        recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setPadding(0, 8, 0, 8);
        LinearLayout.LayoutParams rvParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f);
        recyclerView.setLayoutParams(rvParams);
        root.addView(recyclerView);

        setContentView(root);

        loadPdfList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPdfList(); // Refresh when coming back
    }

    private void loadPdfList() {
        pdfList = PdfFileManager.getAll(this);

        if (pdfList.isEmpty()) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            adapter = new PdfAdapter();
            recyclerView.setAdapter(adapter);
        }
    }

    // ── Adapter
    private class PdfAdapter extends
            RecyclerView.Adapter<PdfAdapter.PdfViewHolder> {

        @Override
        public PdfViewHolder onCreateViewHolder(
                ViewGroup parent, int viewType) {

            // ── Card layout (programmatic)
            LinearLayout card = new LinearLayout(PdfListActivity.this);
            card.setOrientation(LinearLayout.HORIZONTAL);
            card.setBackgroundColor(Color.WHITE);
            card.setPadding(16, 16, 16, 16);
            card.setGravity(android.view.Gravity.CENTER_VERTICAL);

            // ── Margin between cards
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT);
            lp.setMargins(16, 8, 16, 0);
            card.setLayoutParams(lp);

            // Corner radius via background
            android.graphics.drawable.GradientDrawable cardBg =
                    new android.graphics.drawable.GradientDrawable();
            cardBg.setColor(Color.WHITE);
            cardBg.setCornerRadius(12f);
            cardBg.setStroke(1, Color.parseColor("#E0E0E0"));
            card.setBackground(cardBg);
            card.setElevation(4f);

            // ── PDF Icon
            TextView tvIcon = new TextView(PdfListActivity.this);
            tvIcon.setText("📄");
            tvIcon.setTextSize(28);
            tvIcon.setPadding(0, 0, 16, 0);
            card.addView(tvIcon);

            // ── Info column
            LinearLayout infoCol = new LinearLayout(PdfListActivity.this);
            infoCol.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams infoLp = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            infoCol.setLayoutParams(infoLp);

            TextView tvName = new TextView(PdfListActivity.this);
            tvName.setTextSize(15);
            tvName.setTextColor(Color.parseColor("#212121"));
            tvName.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            tvName.setMaxLines(1);
            tvName.setEllipsize(android.text.TextUtils.TruncateAt.END);

            TextView tvDate = new TextView(PdfListActivity.this);
            tvDate.setTextSize(12);
            tvDate.setTextColor(Color.GRAY);

            TextView tvSize = new TextView(PdfListActivity.this);
            tvSize.setTextSize(11);
            tvSize.setTextColor(Color.parseColor("#1565C0"));

            infoCol.addView(tvName);
            infoCol.addView(tvDate);
            infoCol.addView(tvSize);
            card.addView(infoCol);

            // ── Delete button
            TextView btnDelete = new TextView(PdfListActivity.this);
            btnDelete.setText("🗑");
            btnDelete.setTextSize(20);
            btnDelete.setPadding(12, 0, 0, 0);
            card.addView(btnDelete);

            return new PdfViewHolder(card, tvName, tvDate, tvSize, btnDelete);
        }

        @Override
        public void onBindViewHolder(PdfViewHolder holder, int position) {
            PdfFileManager.PdfItem item = pdfList.get(position);

            // ── Title (file name)
            holder.tvName.setText(item.title);

            // ── Date
            String date = new SimpleDateFormat(
                    "dd MMM yyyy, hh:mm a", Locale.getDefault())
                    .format(new Date(item.savedAt));
            holder.tvDate.setText(date);

            // ── File size
            File f = new File(item.filePath);
            if (f.exists()) {
                long sizeKb = f.length() / 1024;
                holder.tvSize.setText(sizeKb > 1024
                        ? String.format("%.1f MB", sizeKb / 1024f)
                        : sizeKb + " KB");
            } else {
                holder.tvSize.setText("File not found");
                holder.tvSize.setTextColor(Color.RED);
            }

            // ── Open PDF on click
            holder.itemView.setOnClickListener(v -> {
                File pdfFile = new File(item.filePath);
                if (!pdfFile.exists()) {
                    Toast.makeText(PdfListActivity.this,
                            "File મળ્યો નહીં", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(
                        PdfListActivity.this, PdfViewerActivity.class);
                intent.putExtra("PDF_PATH", item.filePath);
                startActivity(intent);
            });

            // ── Delete
            holder.btnDelete.setOnClickListener(v -> {
                new AlertDialog.Builder(PdfListActivity.this)
                        .setTitle("PDF Delete કરો?")
                        .setMessage("\"" + item.title + "\" delete થઈ જશે.")
                        .setPositiveButton("Delete", (d, w) -> {
                            PdfFileManager.delete(
                                    PdfListActivity.this, item.filePath);
                            pdfList.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position, pdfList.size());
                            if (pdfList.isEmpty()) {
                                tvEmpty.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                            }
                            Toast.makeText(PdfListActivity.this,
                                    "✅ Delete થઈ ગયો",
                                    Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            });
        }

        @Override
        public int getItemCount() { return pdfList.size(); }

        class PdfViewHolder extends RecyclerView.ViewHolder {
            TextView tvName, tvDate, tvSize, btnDelete;

            PdfViewHolder(View itemView, TextView tvName,
                          TextView tvDate, TextView tvSize,
                          TextView btnDelete) {
                super(itemView);
                this.tvName    = tvName;
                this.tvDate    = tvDate;
                this.tvSize    = tvSize;
                this.btnDelete = btnDelete;
            }
        }
    }
}
