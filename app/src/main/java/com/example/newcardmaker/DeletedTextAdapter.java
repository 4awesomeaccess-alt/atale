package com.example.newcardmaker;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

public class DeletedTextAdapter extends
        RecyclerView.Adapter<DeletedTextAdapter.VH> {

    // ── ✅ New interface — restore + permanent delete
    public interface OnActionListener {
        void onRestore(int position);
        void onPermanentDelete(int position);
    }

    private final List<JSONObject>  list;
    private final OnActionListener  listener;

    // ── Constructor
    public DeletedTextAdapter(
            List<JSONObject> list,
            OnActionListener listener) {
        this.list     = list;
        this.listener = listener;
    }

    // ── Legacy constructor (backward compat)
    public interface OnRestoreListener {
        void onRestore(int position);
    }

    public DeletedTextAdapter(
            List<JSONObject> list,
            OnRestoreListener restoreListener) {
        this.list = list;
        this.listener = new OnActionListener() {
            @Override
            public void onRestore(int position) {
                restoreListener.onRestore(position);
            }
            @Override
            public void onPermanentDelete(int position) {
                // Legacy — no permanent delete
                list.remove(position);
                notifyItemRemoved(position);
            }
        };
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {

        // ── Build row programmatically
        android.widget.LinearLayout row =
                new android.widget.LinearLayout(
                        parent.getContext());
        row.setOrientation(
                android.widget.LinearLayout.HORIZONTAL);
        row.setPadding(16, 12, 16, 12);
        row.setBackgroundColor(Color.WHITE);

        RecyclerView.LayoutParams rowP =
                new RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT,
                        RecyclerView.LayoutParams.WRAP_CONTENT);
        rowP.setMargins(0, 0, 0, 2);
        row.setLayoutParams(rowP);

        // ── Text label
        TextView tvText = new TextView(
                parent.getContext());
        tvText.setTextSize(15);
        tvText.setTextColor(Color.parseColor("#212121"));
        tvText.setPadding(0, 0, 8, 0);
        android.widget.LinearLayout.LayoutParams tvP =
                new android.widget.LinearLayout.LayoutParams(
                        0,
                        android.widget.LinearLayout
                                .LayoutParams.WRAP_CONTENT,
                        1f);
        tvText.setLayoutParams(tvP);
        row.addView(tvText);

        // ── Restore button
        Button btnRestore = new Button(
                parent.getContext());
        btnRestore.setText("↩ Restore");
        btnRestore.setTextSize(11);
        btnRestore.setTextColor(Color.WHITE);
        btnRestore.setBackgroundColor(
                Color.parseColor("#1565C0"));
        btnRestore.setPadding(12, 8, 12, 8);
        android.widget.LinearLayout.LayoutParams btnRP =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout
                                .LayoutParams.WRAP_CONTENT,
                        android.widget.LinearLayout
                                .LayoutParams.WRAP_CONTENT);
        btnRP.setMargins(4, 0, 4, 0);
        btnRestore.setLayoutParams(btnRP);
        row.addView(btnRestore);

        // ── ✅ Permanent Delete button
        Button btnDelete = new Button(
                parent.getContext());
        btnDelete.setText("🗑");
        btnDelete.setTextSize(14);
        btnDelete.setTextColor(Color.WHITE);
        btnDelete.setBackgroundColor(
                Color.parseColor("#C62828"));
        btnDelete.setPadding(12, 8, 12, 8);
        android.widget.LinearLayout.LayoutParams btnDP =
                new android.widget.LinearLayout.LayoutParams(
                        android.widget.LinearLayout
                                .LayoutParams.WRAP_CONTENT,
                        android.widget.LinearLayout
                                .LayoutParams.WRAP_CONTENT);
        btnDP.setMargins(0, 0, 0, 0);
        btnDelete.setLayoutParams(btnDP);
        row.addView(btnDelete);

        return new VH(row, tvText, btnRestore, btnDelete);
    }

    @Override
    public void onBindViewHolder(
            @NonNull VH holder, int position) {

        JSONObject obj = list.get(position);
        String text = obj.optString(
                "text", "(Empty)");

        holder.tvText.setText(text);

        // ── Restore
        holder.btnRestore.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_ID &&
                    listener != null) {
                listener.onRestore(pos);
            }
        });

        // ── ✅ Permanent delete
        holder.btnDelete.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_ID &&
                    listener != null) {
                listener.onPermanentDelete(pos);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvText;
        Button   btnRestore;
        Button   btnDelete;

        VH(View v, TextView t, Button r, Button d) {
            super(v);
            tvText     = t;
            btnRestore = r;
            btnDelete  = d;
        }
    }
}
