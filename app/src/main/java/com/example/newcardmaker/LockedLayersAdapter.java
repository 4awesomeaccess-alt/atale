package com.example.newcardmaker;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class LockedLayersAdapter extends
        RecyclerView.Adapter<LockedLayersAdapter.ViewHolder> {

    // ── Listener Interface
    public interface OnLayerActionListener {
        void onLock(View view, int position);
        void onUnlock(View view, int position);
        void onSelect(View view, int position);
        void onRestore(View view, int position); // ✅ Deleted item restore
        void onMoveUp(View view, int position);
        void onMoveDown(View view, int position);
    }

    // ── Filter mode
    public enum FilterMode { ALL, LOCKED, UNLOCKED }

    // ── All data (unfiltered)
    private List<View>    allLayers   = new ArrayList<>();
    private List<String>  allLabels   = new ArrayList<>();
    private List<Boolean> lockStates  = new ArrayList<>();

    // ── Filtered list (shown in RecyclerView)
    private List<View>    showLayers  = new ArrayList<>();
    private List<String>  showLabels  = new ArrayList<>();
    private List<Boolean> showStates  = new ArrayList<>();

    private OnLayerActionListener listener;
    private FilterMode filterMode = FilterMode.ALL;

    // ── Deleted item tag prefix
    private static final String DELETED_TAG = "DELETED_TEXT_";

    public void setOnLayerActionListener(OnLayerActionListener l) {
        this.listener = l;
    }

    public void setFilterMode(FilterMode mode) {
        this.filterMode = mode;
        applyFilter();
    }

    // ── Data update
    public void updateLayers(List<View>    layers,
                             List<String>  labels,
                             List<Boolean> states) {
        allLayers.clear();
        allLabels.clear();
        lockStates.clear();

        allLayers.addAll(layers);
        allLabels.addAll(labels);
        lockStates.addAll(states);

        applyFilter();
    }

    // ── Filter apply
    private void applyFilter() {
        showLayers.clear();
        showLabels.clear();
        showStates.clear();

        for (int i = 0; i < allLayers.size(); i++) {
            View    v        = allLayers.get(i);
            boolean isLocked = lockStates.get(i);

            // ── Deleted items — always show in ALL tab, never in LOCKED/UNLOCKED
            boolean isDeleted = isDeletedItem(v);

            boolean show;
            if (isDeleted) {
                show = filterMode == FilterMode.ALL;
            } else {
                show = filterMode == FilterMode.ALL
                        || (filterMode == FilterMode.LOCKED   &&  isLocked)
                        || (filterMode == FilterMode.UNLOCKED && !isLocked);
            }

            if (show) {
                showLayers.add(v);
                showLabels.add(allLabels.get(i));
                showStates.add(isLocked);
            }
        }
        notifyDataSetChanged();
    }

    // ── Helper: check if item is a deleted dummy view
    private boolean isDeletedItem(View v) {
        if (v == null) return false;
        Object tag = v.getTag(R.id.btn_ms_select_all);
        return tag != null && tag.toString().startsWith(DELETED_TAG);
    }

    // ── Count helpers (exclude deleted items)
    public int getLockedCount() {
        int c = 0;
        for (int i = 0; i < allLayers.size(); i++) {
            if (!isDeletedItem(allLayers.get(i)) && lockStates.get(i)) c++;
        }
        return c;
    }

    public int getUnlockedCount() {
        int c = 0;
        for (int i = 0; i < allLayers.size(); i++) {
            if (!isDeletedItem(allLayers.get(i)) && !lockStates.get(i)) c++;
        }
        return c;
    }

    public int getTotalCount() {
        // Only active (non-deleted) layers
        int c = 0;
        for (View v : allLayers) {
            if (!isDeletedItem(v)) c++;
        }
        return c;
    }

    public int getDeletedCount() {
        int c = 0;
        for (View v : allLayers) {
            if (isDeletedItem(v)) c++;
        }
        return c;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_locked_layer, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        View    layerView = showLayers.get(position);
        String  label     = showLabels.get(position);
        boolean isLocked  = showStates.get(position);

        // ══════════════════════════════════════════════
        // ── DELETED ITEM
        // ══════════════════════════════════════════════
        if (isDeletedItem(layerView)) {

            if (holder.btnLayerUp != null)
                holder.btnLayerUp.setVisibility(View.GONE);
            if (holder.btnLayerDown != null)
                holder.btnLayerDown.setVisibility(View.GONE);

            // ── Row background — light red
            holder.itemView.setBackgroundColor(Color.parseColor("#FFEBEE"));

            // ── Type badge
            holder.tvType.setText("🗑");
            holder.tvType.setBackgroundColor(Color.parseColor("#FFCDD2"));
            holder.tvType.setTextColor(Color.parseColor("#B71C1C"));

            // ── Label
            holder.tvLabel.setText(label);
            holder.tvLabel.setTextColor(Color.parseColor("#B71C1C"));
            holder.tvLabel.setAlpha(0.85f);

            // ── Lock status — N/A for deleted
            holder.tvLockStatus.setText("🗑");
            holder.tvLockStatus.setBackgroundColor(Color.parseColor("#FFCDD2"));

            // ── Image preview — hide
            holder.ivPreview.setImageDrawable(null);
            holder.ivPreview.setBackgroundColor(Color.parseColor("#FFCDD2"));

            // ── Action button — hide
            holder.btnAction.setVisibility(View.GONE);

            // ── Restore button — show
            if (holder.btnRestore != null) {
                holder.btnRestore.setVisibility(View.VISIBLE);
                holder.btnRestore.setText("↩ Restore");
                holder.btnRestore.setBackgroundColor(Color.parseColor("#E8F5E9"));
                holder.btnRestore.setTextColor(Color.parseColor("#1B5E20"));
                holder.btnRestore.setOnClickListener(v -> {
                    if (listener != null)
                        listener.onRestore(layerView, holder.getAdapterPosition());
                });
            }

            // ── Row click — disabled for deleted
            holder.itemView.setOnClickListener(null);

            return;
        }

        // ══════════════════════════════════════════════
        // ── ACTIVE ITEM (normal layer)
        // ══════════════════════════════════════════════

        // ── Reset restore button
        if (holder.btnRestore != null) {
            holder.btnRestore.setVisibility(View.GONE);
        }


        // ── ACTIVE ITEM block ની end માં — Row click ની નીચે

// ✅ Up button
        if (holder.btnLayerUp != null) {
            holder.btnLayerUp.setVisibility(View.VISIBLE);
            holder.btnLayerUp.setOnClickListener(v -> {
                if (listener != null)
                    listener.onMoveUp(
                            layerView,
                            holder.getAdapterPosition());
            });
        }

// ✅ Down button
        if (holder.btnLayerDown != null) {
            holder.btnLayerDown.setVisibility(View.VISIBLE);
            holder.btnLayerDown.setOnClickListener(v -> {
                if (listener != null)
                    listener.onMoveDown(
                            layerView,
                            holder.getAdapterPosition());
            });
        }


        // ── Action button visible
        holder.btnAction.setVisibility(View.VISIBLE);

        // ── Label reset
        holder.tvLabel.setAlpha(1.0f);
        holder.tvLabel.setTextColor(Color.BLACK);

        // ── Row background
        holder.itemView.setBackgroundColor(
                isLocked ? Color.parseColor("#FFF9C4") : Color.WHITE);

        // ── Type badge + preview
        if (layerView instanceof StrokeTextView) {
            holder.tvType.setText("T");
            holder.tvType.setBackgroundColor(Color.parseColor("#E3F2FD"));
            holder.tvType.setTextColor(Color.parseColor("#1565C0"));

            String txt = ((StrokeTextView) layerView).getText().toString();
            holder.tvLabel.setText(txt.isEmpty() ? "(Empty Text)" : txt);

            holder.ivPreview.setImageDrawable(null);
            holder.ivPreview.setBackgroundColor(Color.parseColor("#E3F2FD"));

        } else if (layerView instanceof ImageView) {
            holder.tvType.setText("I");
            holder.tvType.setBackgroundColor(Color.parseColor("#F3E5F5"));
            holder.tvType.setTextColor(Color.parseColor("#7B1FA2"));
            holder.tvLabel.setText(label);

            // ── Image preview
            try {
                layerView.setDrawingCacheEnabled(true);
                layerView.buildDrawingCache();
                Bitmap bmp = Bitmap.createBitmap(
                        layerView.getDrawingCache());
                layerView.setDrawingCacheEnabled(false);
                if (bmp != null) {
                    holder.ivPreview.setImageBitmap(bmp);
                } else {
                    holder.ivPreview.setImageResource(
                            android.R.drawable.ic_menu_gallery);
                }
            } catch (Exception e) {
                holder.ivPreview.setImageResource(
                        android.R.drawable.ic_menu_gallery);
            }
        } else {
            holder.tvType.setText("?");
            holder.tvType.setBackgroundColor(Color.LTGRAY);
            holder.tvType.setTextColor(Color.DKGRAY);
            holder.tvLabel.setText(label);
        }

        // ── Lock status icon
        holder.tvLockStatus.setText(isLocked ? "🔒" : "🔓");
        holder.tvLockStatus.setBackgroundColor(isLocked
                ? Color.parseColor("#FFCDD2")
                : Color.parseColor("#E8F5E9"));

        // ── Action button (Lock / Unlock)
        if (isLocked) {
            holder.btnAction.setText("🔓 Unlock");
            holder.btnAction.setBackgroundColor(Color.parseColor("#43A047"));
            holder.btnAction.setTextColor(Color.WHITE);
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null)
                    listener.onUnlock(layerView, holder.getAdapterPosition());
            });
        } else {
            holder.btnAction.setText("🔒 Lock");
            holder.btnAction.setBackgroundColor(Color.parseColor("#E53935"));
            holder.btnAction.setTextColor(Color.WHITE);
            holder.btnAction.setOnClickListener(v -> {
                if (listener != null)
                    listener.onLock(layerView, holder.getAdapterPosition());
            });
        }

        // ── Row click = highlight/select
        holder.itemView.setOnClickListener(v -> {
            if (listener != null)
                listener.onSelect(layerView, holder.getAdapterPosition());
        });


    }

    @Override
    public int getItemCount() { return showLayers.size(); }

    // ── ViewHolder
    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPreview;
        TextView  tvLabel;
        TextView  tvType;
        TextView  tvLockStatus;
        TextView  btnAction;
        Button    btnRestore; // ✅ Restore button for deleted items
        TextView  btnLayerUp;
        TextView  btnLayerDown;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPreview    = itemView.findViewById(R.id.iv_layer_preview);
            tvLabel      = itemView.findViewById(R.id.tv_layer_label);
            tvType       = itemView.findViewById(R.id.tv_layer_type);
            tvLockStatus = itemView.findViewById(R.id.tv_lock_status);
            btnAction    = itemView.findViewById(R.id.btn_layer_action);
            btnRestore   = itemView.findViewById(R.id.btn_layer_restore); // ✅
            btnLayerUp   = itemView.findViewById(R.id.btn_layer_up);
            btnLayerDown = itemView.findViewById(R.id.btn_layer_down);
        }
    }
}