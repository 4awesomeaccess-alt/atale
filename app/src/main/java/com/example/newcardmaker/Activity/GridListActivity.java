package com.example.newcardmaker.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.newcardmaker.R;
import com.example.newcardmaker.invite_online_database.invite_AppConstants;
import com.example.newcardmaker.invite_online_database.invite_EndlessRecyclerViewScrollListener1;
import com.example.newcardmaker.invite_online_database.invite_Methods;
import com.example.newcardmaker.invite_photo_frame;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GridListActivity extends AppCompatActivity {

    public static final String EXTRA_CELLS_JSON = "grid_cells_json";
    public static final String EXTRA_ROWS = "grid_rows";
    public static final String EXTRA_COLS = "grid_cols";
    public static final String EXTRA_SHAPE = "grid_shape";
    public static final String EXTRA_CELL_SIZE = "grid_cell_size";
    public static final String EXTRA_SHOW_NAME = "show_name";
    public static final String EXTRA_SHOW_INFO = "show_info";
    public static final String EXTRA_GRID_INDEX = "grid_index";
    public static final String RESULT_CELLS_JSON = "updated_cells_json";
    public static final String RESULT_GRID_INDEX = "grid_index";
    public static final String RESULT_ACTION = "action";

    private static final int REQUEST_CELL_PHOTO = 901;
    private static final int REQUEST_IMPORT_CSV = 902;

    private final List<JSONObject> cellDataList = new ArrayList<>();
    private final List<JSONObject> displayList = new ArrayList<>();
    private String shape = "ROUNDED";
    private int cellSizePx = 200;
    private int rows = 3;
    private int cols = 4;
    private boolean showName = true;
    private boolean showInfo = true;
    private int gridIndex = 0;
    private boolean dataChanged = false;
    private int pendingCellIdx = -1;

    // Multi-select
    private boolean isMultiSelectMode = false;
    private final Set<Integer> selectedIndices = new HashSet<>();

    // Filter
    private String currentFilter = "ALL";

    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    private RecyclerView recyclerView;
    private TextView tvStats;
    private EditText etSearch;
    private TextView[] sortBtns;
    private TextView tvMultiCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parseIntent();
        buildUI();
    }

    private void parseIntent() {
        Intent i = getIntent();
        rows = i.getIntExtra(EXTRA_ROWS, 3);
        cols = i.getIntExtra(EXTRA_COLS, 4);
        shape = i.getStringExtra(EXTRA_SHAPE);
        if (shape == null) shape = "ROUNDED";
        cellSizePx = i.getIntExtra(EXTRA_CELL_SIZE, 200);
        showName = i.getBooleanExtra(EXTRA_SHOW_NAME, true);
        showInfo = i.getBooleanExtra(EXTRA_SHOW_INFO, true);
        gridIndex = i.getIntExtra(EXTRA_GRID_INDEX, 0);
        String js = i.getStringExtra(EXTRA_CELLS_JSON);
        if (js != null && !js.isEmpty()) {
            try {
                JSONArray arr = new JSONArray(js);
                for (int x = 0; x < arr.length(); x++)
                    cellDataList.add(arr.getJSONObject(x));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        displayList.addAll(cellDataList);
    }

    // ── BUILD UI ─────────────────────────────────────────────
    private void buildUI() {
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.WHITE);
        setContentView(root);

        LinearLayout header = makeHeader();
        LinearLayout filterBar = makeFilterBar();
        LinearLayout statsBar = makeStatsBar();
        recyclerView = new RecyclerView(this);
        recyclerView.setBackgroundColor(Color.parseColor("#F5F5F5"));
        recyclerView.setPadding(dp(8), dp(8), dp(8), dp(8));
        LinearLayout bottomBar = makeBottomBar();

        RelativeLayout.LayoutParams hLp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        hLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(header, hLp);

        RelativeLayout.LayoutParams bbLp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bbLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        root.addView(bottomBar, bbLp);

        root.addView(filterBar);
        root.addView(statsBar);
        root.addView(recyclerView);

        root.post(() -> {
            int hH = header.getHeight();
            int fH = filterBar.getHeight();
            int sH = statsBar.getHeight();
            int bH = bottomBar.getHeight();

            RelativeLayout.LayoutParams fLp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            fLp.topMargin = hH;
            filterBar.setLayoutParams(fLp);

            RelativeLayout.LayoutParams sLp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            sLp.topMargin = hH + fH;
            statsBar.setLayoutParams(sLp);

            RelativeLayout.LayoutParams rvLp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            rvLp.topMargin = hH + fH + sH;
            rvLp.bottomMargin = bH;
            recyclerView.setLayoutParams(rvLp);
        });
        setupAdapter();
    }

    // ── HEADER ───────────────────────────────────────────────
    private LinearLayout makeHeader() {
        LinearLayout h = new LinearLayout(this);
        h.setOrientation(LinearLayout.HORIZONTAL);
        h.setBackgroundColor(Color.parseColor("#1565C0"));
        h.setPadding(dp(14), dp(44), dp(14), dp(14));
        h.setGravity(Gravity.CENTER_VERTICAL);

        TextView title = new TextView(this);
        title.setText("Grid List (" + rows + "x" + cols + ")");
        title.setTextColor(Color.WHITE);
        title.setTextSize(14);
        title.setTypeface(null, Typeface.BOLD);
        title.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        h.addView(title);

        etSearch = new EditText(this);
        etSearch.setHint("Search...");
        etSearch.setTextSize(12);
        etSearch.setBackgroundColor(Color.WHITE);
        etSearch.setPadding(dp(8), dp(4), dp(8), dp(4));
        LinearLayout.LayoutParams sLp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        sLp.setMargins(dp(6), 0, 0, 0);
        etSearch.setLayoutParams(sLp);
        etSearch.addTextChangedListener(new android.text.TextWatcher() {
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {
            }

            public void onTextChanged(CharSequence s, int a, int b, int c) {
                applyFilterAndSearch();
            }

            public void afterTextChanged(android.text.Editable s) {
            }
        });
        h.addView(etSearch);

        TextView btnX = new TextView(this);
        btnX.setText("X");
        btnX.setTextColor(Color.WHITE);
        btnX.setTextSize(18);
        btnX.setPadding(dp(14), 0, dp(6), 0);
        btnX.setOnClickListener(v -> finishWithResult("NO_CHANGE"));
        h.addView(btnX);
        return h;
    }

    // ── FILTER BAR ────────────────────────────────────────────
    private LinearLayout makeFilterBar() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setBackgroundColor(Color.parseColor("#E8EAF6"));
        bar.setPadding(dp(6), dp(5), dp(6), dp(5));

        String[] labels = {"All", "Photo", "No Photo", "Mask", "No Mask"};
        String[] keys = {"ALL", "HAS_PHOTO", "NO_PHOTO", "HAS_MASK", "NO_MASK"};

        for (int i = 0; i < labels.length; i++) {
            final String key = keys[i];
            TextView btn = new TextView(this);
            btn.setText(labels[i]);
            btn.setTextSize(10);
            btn.setGravity(Gravity.CENTER);
            btn.setPadding(dp(8), dp(4), dp(8), dp(4));
            GradientDrawable gd = new GradientDrawable();
            gd.setCornerRadius(dp(12));
            gd.setColor(i == 0 ? Color.parseColor("#1565C0") : Color.parseColor("#C5CAE9"));
            btn.setBackground(gd);
            btn.setTextColor(i == 0 ? Color.WHITE : Color.parseColor("#1A237E"));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(dp(3), 0, dp(3), 0);
            btn.setLayoutParams(lp);
            btn.setOnClickListener(v -> {
                currentFilter = key;
                LinearLayout parent = (LinearLayout) v.getParent();
                for (int j = 0; j < parent.getChildCount(); j++) {
                    View child = parent.getChildAt(j);
                    GradientDrawable bg = new GradientDrawable();
                    bg.setCornerRadius(dp(12));
                    bg.setColor(Color.parseColor("#C5CAE9"));
                    child.setBackground(bg);
                    if (child instanceof TextView)
                        ((TextView) child).setTextColor(Color.parseColor("#1A237E"));
                }
                GradientDrawable activeBg = new GradientDrawable();
                activeBg.setCornerRadius(dp(12));
                activeBg.setColor(Color.parseColor("#1565C0"));
                btn.setBackground(activeBg);
                btn.setTextColor(Color.WHITE);
                applyFilterAndSearch();
            });
            bar.addView(btn);
        }
        return bar;
    }

    // ── STATS BAR ─────────────────────────────────────────────
    private LinearLayout makeStatsBar() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setBackgroundColor(Color.parseColor("#E3F2FD"));
        bar.setPadding(dp(14), dp(5), dp(14), dp(5));
        bar.setGravity(Gravity.CENTER_VERTICAL);

        tvStats = new TextView(this);
        tvStats.setTextSize(11);
        tvStats.setTextColor(Color.parseColor("#1565C0"));
        tvStats.setTypeface(null, Typeface.BOLD);
        tvStats.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        bar.addView(tvStats);

        tvMultiCount = new TextView(this);
        tvMultiCount.setTextSize(11);
        tvMultiCount.setTextColor(Color.parseColor("#C62828"));
        tvMultiCount.setTypeface(null, Typeface.BOLD);
        tvMultiCount.setVisibility(View.GONE);
        bar.addView(tvMultiCount);

        updateStats();
        return bar;
    }

    // ── BOTTOM BAR ────────────────────────────────────────────
    private LinearLayout makeBottomBar() {
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.VERTICAL);
        bar.setBackgroundColor(Color.WHITE);
        bar.setPadding(dp(6), dp(5), dp(6), dp(30));

        // Sort row
        LinearLayout sortRow = new LinearLayout(this);
        sortRow.setOrientation(LinearLayout.HORIZONTAL);
        sortRow.setGravity(Gravity.CENTER_VERTICAL);
        sortRow.setPadding(0, dp(2), 0, dp(5));
        TextView lblSort = new TextView(this);
        lblSort.setText("Sort: ");
        lblSort.setTextSize(11);
        lblSort.setTextColor(Color.DKGRAY);
        sortRow.addView(lblSort);

        String[] sl = {"No.", "Name+", "Name-", "%+", "%-"};
        sortBtns = new TextView[sl.length];
        for (int i = 0; i < sl.length; i++) {
            final int si = i;
            TextView sb = new TextView(this);
            sb.setText(sl[i]);
            sb.setTextSize(10);
            sb.setGravity(Gravity.CENTER);
            sb.setPadding(dp(8), dp(4), dp(8), dp(4));
            GradientDrawable g = new GradientDrawable();
            g.setCornerRadius(dp(12));
            g.setColor(i == 0 ? Color.parseColor("#1565C0") : Color.parseColor("#E3F2FD"));
            sb.setBackground(g);
            sb.setTextColor(i == 0 ? Color.WHITE : Color.parseColor("#1565C0"));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(dp(2), 0, dp(2), 0);
            sb.setLayoutParams(lp);
            sortBtns[i] = sb;
            sb.setOnClickListener(v -> applySort(si));
            sortRow.addView(sb);
        }
        bar.addView(sortRow);

        // Row 1
        String[] r1 = {"CSV", "Import", "Bulk", "Add", "MaskAll", "Reorder"};
        int[] c1 = {0xFF1565C0, 0xFF00838F, 0xFF2E7D32, 0xFFE65100, 0xFF6A1B9A, 0xFF37474F};
        LinearLayout actRow1 = new LinearLayout(this);
        actRow1.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < r1.length; i++) {
            final int ai = i;
            Button ab = mkBtn(r1[i], c1[i]);
            ab.setOnClickListener(v -> handleRow1(ai));
            actRow1.addView(ab, mkBtnLp());
        }
        bar.addView(actRow1);

        // Row 2
        String[] r2 = {"AutoNum", "MultiSel", "Clear", "Save"};
        int[] c2 = {0xFFFF8F00, 0xFF1565C0, 0xFFC62828, 0xFF4527A0};
        LinearLayout actRow2 = new LinearLayout(this);
        actRow2.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams r2lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        r2lp.setMargins(0, dp(3), 0, 0);
        actRow2.setLayoutParams(r2lp);
        for (int i = 0; i < r2.length; i++) {
            final int ai = i;
            Button ab = mkBtn(r2[i], c2[i]);
            ab.setOnClickListener(v -> handleRow2(ai));
            actRow2.addView(ab, mkBtnLp());
        }
        bar.addView(actRow2);
        return bar;
    }

    private Button mkBtn(String text, int color) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(9);
        b.setBackgroundColor(color);
        return b;
    }

    private LinearLayout.LayoutParams mkBtnLp() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(dp(2), 0, dp(2), 0);
        return lp;
    }

    private void handleRow1(int idx) {
        switch (idx) {
            case 0:
                exportCsv();
                break;
            case 1:
                importCsv();
                break;
            case 2:
                showBulkEdit();
                break;
            case 3:
                showAddCell();
                break;
            case 4:
                showMaskPickerForAll();
                break;
            case 5:
                showReorderDialog();
                break;
        }
    }

    private void handleRow2(int idx) {
        switch (idx) {
            case 0:
                showAutoNumberDialog();
                break;
            case 1:
                toggleMultiSelectMode();
                break;
            case 2:
                confirmClearAll();
                break;
            case 3:
                finishWithResult("UPDATE");
                break;
        }
    }

    // ── ADAPTER ──────────────────────────────────────────────
    private void setupAdapter() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
                LinearLayout card = new LinearLayout(GridListActivity.this);
                card.setOrientation(LinearLayout.VERTICAL);
                card.setPadding(dp(10), dp(10), dp(10), dp(10));
                GradientDrawable bg = new GradientDrawable();
                bg.setColor(Color.WHITE);
                bg.setStroke(1, Color.parseColor("#E0E0E0"));
                bg.setCornerRadius(dp(12));
                card.setBackground(bg);
                RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
                lp.setMargins(dp(4), dp(4), dp(4), dp(4));
                card.setLayoutParams(lp);
                return new RecyclerView.ViewHolder(card) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
                bindCard((LinearLayout) h.itemView, pos);
            }

            @Override
            public int getItemCount() {
                return displayList.size();
            }
        };
        recyclerView.setAdapter(adapter);
    }

    private int getNameColor(JSONObject obj) {
        try {
            if (!obj.has("nameColor")) return Color.BLACK;
            Object val = obj.get("nameColor");
            if (val instanceof Integer) return (Integer) val;
            if (val instanceof Long)    return ((Long) val).intValue();
            if (val instanceof String) {
                String s = ((String) val).trim();
                if (s.isEmpty()) return Color.BLACK;
                return Integer.parseInt(s); // negative int string parse
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Color.BLACK;
    }

    // ── BIND CARD ─────────────────────────────────────────────
    private void bindCard(LinearLayout card, int position) {
        card.removeAllViews();

        JSONObject obj = displayList.get(position);
        int cellIdx = cellDataList.indexOf(obj);
        String photoUri = obj.optString("photoUri", "");
        String photoB64 = obj.optString("photoBitmap", "");
        String name = obj.optString("name", "");
        String info = obj.optString("info", "");
        String notes = obj.optString("notes", "");
        String maskUrl = obj.optString("frameMask", "");
        String topUrl = obj.optString("frameTop", "");
        boolean hasMask = !maskUrl.isEmpty() || !topUrl.isEmpty();
        boolean hasPhoto = !photoB64.isEmpty() || !photoUri.isEmpty();
        int nameColor = getNameColor(obj);
        boolean selected = selectedIndices.contains(cellIdx);

        // Card background — multi-select highlight
        GradientDrawable cardBg = new GradientDrawable();
        cardBg.setColor(selected ? Color.parseColor("#E3F2FD") : Color.WHITE);
        cardBg.setStroke(selected ? 3 : 1, selected ? Color.parseColor("#1565C0") : Color.parseColor("#E0E0E0"));
        cardBg.setCornerRadius(dp(12));
        card.setBackground(cardBg);

        if (isMultiSelectMode) {
            card.setOnClickListener(v -> {
                if (selectedIndices.contains(cellIdx)) selectedIndices.remove(cellIdx);
                else selectedIndices.add(cellIdx);
                adapter.notifyDataSetChanged();
                updateMultiCount();
            });
        } else {
            card.setOnClickListener(null);
        }

        // ── Top row
        LinearLayout topRow = new LinearLayout(this);
        topRow.setOrientation(LinearLayout.HORIZONTAL);
        topRow.setGravity(Gravity.CENTER_VERTICAL);

        if (isMultiSelectMode) {
            CheckBox cb = new CheckBox(this);
            cb.setChecked(selected);
            cb.setClickable(false);
            LinearLayout.LayoutParams cbLp = new LinearLayout.LayoutParams(dp(28), dp(28));
            cbLp.setMargins(0, 0, dp(6), 0);
            cb.setLayoutParams(cbLp);
            topRow.addView(cb);
        }

        TextView tvNo = new TextView(this);
        tvNo.setText(String.valueOf(position + 1));
        tvNo.setTextSize(11);
        tvNo.setTextColor(Color.WHITE);
        tvNo.setGravity(Gravity.CENTER);
        tvNo.setTypeface(null, Typeface.BOLD);
        GradientDrawable noGd = new GradientDrawable();
        noGd.setShape(GradientDrawable.OVAL);
        noGd.setColor(Color.parseColor("#1565C0"));
        tvNo.setBackground(noGd);
        LinearLayout.LayoutParams noLp = new LinearLayout.LayoutParams(dp(28), dp(28));
        noLp.setMargins(0, 0, dp(8), 0);
        tvNo.setLayoutParams(noLp);
        topRow.addView(tvNo);

        ImageView thumb = new ImageView(this);
        int ts = dp(62);
        LinearLayout.LayoutParams tLp = new LinearLayout.LayoutParams(ts, ts);
        tLp.setMargins(0, 0, dp(10), 0);
        thumb.setLayoutParams(tLp);
        thumb.setScaleType(ImageView.ScaleType.CENTER_CROP);
        applyThumbShape(thumb, ts);
        loadThumbPhoto(thumb, photoB64, photoUri);
        thumb.setOnClickListener(v -> showFullPhotoDialog(obj, name, info));
        topRow.addView(thumb);

        LinearLayout infoCol = new LinearLayout(this);
        infoCol.setOrientation(LinearLayout.VERTICAL);
        infoCol.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        if (hasMask) {
            TextView badge = new TextView(this);
            badge.setText("Mask");
            badge.setTextSize(9);
            badge.setTextColor(Color.WHITE);
            badge.setPadding(dp(5), dp(1), dp(5), dp(1));
            GradientDrawable bd = new GradientDrawable();
            bd.setColor(Color.parseColor("#6A1B9A"));
            bd.setCornerRadius(dp(6));
            badge.setBackground(bd);
            LinearLayout.LayoutParams bdLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            bdLp.setMargins(0, 0, 0, dp(2));
            badge.setLayoutParams(bdLp);
            infoCol.addView(badge);
        }

        TextView tvName = new TextView(this);
        tvName.setText(name.isEmpty() ? "(No Name)" : name);
        tvName.setTextSize(14);
        tvName.setTextColor(name.isEmpty() ? Color.LTGRAY : nameColor);
        tvName.setTypeface(null, Typeface.BOLD);
        tvName.setMaxLines(1);
        tvName.setEllipsize(TextUtils.TruncateAt.END);
        infoCol.addView(tvName);

        TextView tvInfo = new TextView(this);
        tvInfo.setText(info.isEmpty() ? "-" : info);
        tvInfo.setTextSize(12);
        tvInfo.setTypeface(null, Typeface.BOLD);
        tvInfo.setTextColor(infoColor(info));
        infoCol.addView(tvInfo);

        // Progress bar
        try {
            float pct = Float.parseFloat(info.replace("%", "").replace(",", ".").trim());
            if (pct >= 0) {
                LinearLayout pRow = new LinearLayout(this);
                pRow.setOrientation(LinearLayout.HORIZONTAL);
                pRow.setGravity(Gravity.CENTER_VERTICAL);
                android.widget.ProgressBar pbar = new android.widget.ProgressBar(
                        this, null, android.R.attr.progressBarStyleHorizontal);
                pbar.setMax(100);
                pbar.setProgress(Math.min(100, (int) pct));
                LinearLayout.LayoutParams pbarLp = new LinearLayout.LayoutParams(0, dp(8), 1f);
                pbarLp.setMargins(0, dp(2), dp(4), 0);
                pbar.setLayoutParams(pbarLp);
                int pbarColor = pct >= 75f ? Color.parseColor("#2E7D32") : (pct >= 50f ? Color.parseColor("#1565C0") : (pct >= 25f ? Color.parseColor("#FF9800") : Color.parseColor("#C62828")));
                pbar.getProgressDrawable().setColorFilter(pbarColor, android.graphics.PorterDuff.Mode.SRC_IN);
                pRow.addView(pbar);
                TextView pPct = new TextView(this);
                pPct.setText((int) pct + "%");
                pPct.setTextSize(9);
                pPct.setTextColor(pbarColor);
                pRow.addView(pPct);
                infoCol.addView(pRow);
            }
        } catch (Exception ignored) {
        }

        if (!notes.isEmpty()) {
            TextView tvNotes = new TextView(this);
            tvNotes.setText("Note: " + notes);
            tvNotes.setTextSize(10);
            tvNotes.setTextColor(Color.parseColor("#757575"));
            tvNotes.setMaxLines(1);
            tvNotes.setEllipsize(TextUtils.TruncateAt.END);
            infoCol.addView(tvNotes);
        }

        TextView tvStat = new TextView(this);
        tvStat.setText(hasPhoto ? "Photo OK" : "No Photo");
        tvStat.setTextSize(10);
        tvStat.setTextColor(hasPhoto ? Color.parseColor("#2E7D32") : Color.parseColor("#999999"));
        infoCol.addView(tvStat);

        topRow.addView(infoCol);
        card.addView(topRow);

        // ── Button Row 1: Name | Info | Photo | Mask | Dup | Note
        String[][] btn1 = {
                {"Name", "#1565C0"},
                {"Info", "#E65100"},
                {hasPhoto ? "Replace" : "Photo", hasPhoto ? "#00838F" : "#2E7D32"},
                {"Mask", hasMask ? "#4A148C" : "#6A1B9A"},
                {"Dup", "#37474F"},
                {"Note", "#FF6F00"},
        };
        LinearLayout br1 = new LinearLayout(this);
        br1.setOrientation(LinearLayout.HORIZONTAL);
        br1.setPadding(0, dp(8), 0, dp(2));
        for (int b = 0; b < btn1.length; b++) {
            final int bi = b;
            TextView btn = mkCellBtn(btn1[b][0], btn1[b][1]);
            btn.setOnClickListener(v -> handleCellBtn1(bi, obj, cellIdx, tvName, tvInfo, thumb));
            br1.addView(btn, mkCellBtnLp());
        }
        card.addView(br1);

        // ── Button Row 2: NameColor | DelPhoto | Up | Down | Share | Del
        String[][] btn2 = {
                {"Color", "#C62828"},
                {"DelPic", "#D84315"},
                {"Up", "#546E7A"},
                {"Down", "#546E7A"},
                {"Share", "#1B5E20"},
                {"Del", "#B71C1C"},
        };
        LinearLayout br2 = new LinearLayout(this);
        br2.setOrientation(LinearLayout.HORIZONTAL);
        for (int b = 0; b < btn2.length; b++) {
            final int bi = b;
            TextView btn = mkCellBtn(btn2[b][0], btn2[b][1]);
            btn.setOnClickListener(v -> handleCellBtn2(bi, obj, cellIdx, tvName, tvInfo, thumb));
            br2.addView(btn, mkCellBtnLp());
        }
        card.addView(br2);
    }

    private TextView mkCellBtn(String text, String colorHex) {
        TextView btn = new TextView(this);
        btn.setText(text);
        btn.setTextSize(9);
        btn.setTextColor(Color.WHITE);
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(dp(2), dp(5), dp(2), dp(5));
        GradientDrawable bgd = new GradientDrawable();
        bgd.setColor(Color.parseColor(colorHex));
        bgd.setCornerRadius(dp(6));
        btn.setBackground(bgd);
        return btn;
    }

    private LinearLayout.LayoutParams mkCellBtnLp() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(dp(2), 0, dp(2), 0);
        return lp;
    }

    private void handleCellBtn1(int bi, JSONObject obj, int cellIdx, TextView tvName, TextView tvInfo, ImageView thumb) {
        switch (bi) {
            case 0:
                editName(obj, cellIdx, tvName);
                break;
            case 1:
                editInfo(obj, cellIdx, tvInfo);
                break;
            case 2:
                pickPhoto(cellIdx);
                break;
            case 3:
                showMaskPickerForCell(obj, cellIdx, thumb);
                break;
            case 4:
                duplicateCell(cellIdx);
                break;
            case 5:
                editNotes(obj, cellIdx);
                break;
        }
    }

    private void handleCellBtn2(int bi, JSONObject obj, int cellIdx, TextView tvName, TextView tvInfo, ImageView thumb) {
        switch (bi) {
            case 0:
                showNameColorPicker(obj, cellIdx, tvName);
                break;
            case 1:
                removePhoto(obj, cellIdx, thumb);
                break;
            case 2:
                moveCellUp(cellIdx);
                break;
            case 3:
                moveCellDown(cellIdx);
                break;
            case 4:
                shareCell(obj);
                break;
            case 5:
                confirmDelete(obj, cellIdx);
                break;
        }
    }

    // ════════════════════════════════════════════════════════
    // DUPLICATE CELL
    // ════════════════════════════════════════════════════════
    private void duplicateCell(int cellIdx) {
        if (cellIdx < 0 || cellIdx >= cellDataList.size()) return;
        try {
            JSONObject copy = new JSONObject(cellDataList.get(cellIdx).toString());
            copy.put("name", cellDataList.get(cellIdx).optString("name", "") + " (Copy)");
            cellDataList.add(cellIdx + 1, copy);
            dataChanged = true;
            applyFilterAndSearch();
            updateStats();
            recyclerView.scrollToPosition(cellIdx + 1);
            Toast.makeText(this, "Cell duplicate!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ════════════════════════════════════════════════════════
    // NAME COLOR PICKER
    // ════════════════════════════════════════════════════════
    private void showNameColorPicker(JSONObject obj, int cellIdx, TextView tvName) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Name Color — Cell " + (cellIdx + 1));

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(16), dp(20), dp(16));

        TextView preview = new TextView(this);
        preview.setText(obj.optString("name", "Preview"));
        preview.setTextSize(18);
        preview.setGravity(Gravity.CENTER);
        preview.setTypeface(null, Typeface.BOLD);
        preview.setTextColor(obj.optInt("nameColor", Color.BLACK));

        preview.setPadding(0, dp(8), 0, dp(12));
        root.addView(preview);

        int[] colors = {
                Color.BLACK, Color.WHITE,
                Color.parseColor("#C62828"), Color.parseColor("#1565C0"),
                Color.parseColor("#2E7D32"), Color.parseColor("#FF9800"),
                Color.parseColor("#6A1B9A"), Color.parseColor("#00838F"),
                Color.parseColor("#E91E63"), Color.parseColor("#FF5722"),
                Color.parseColor("#795548"), Color.parseColor("#607D8B"),
                Color.parseColor("#F9A825"), Color.parseColor("#00BCD4"),
                Color.parseColor("#4CAF50"), Color.parseColor("#9C27B0"),
        };

        LinearLayout row1 = new LinearLayout(this);
        row1.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout row2 = new LinearLayout(this);
        row2.setOrientation(LinearLayout.HORIZONTAL);

        for (int i = 0; i < colors.length; i++) {
            final int c = colors[i];
            View dot = new View(this);
            GradientDrawable dotBg = new GradientDrawable();
            dotBg.setShape(GradientDrawable.OVAL);
            dotBg.setColor(c);
            dotBg.setStroke(2, Color.parseColor("#BBBBBB"));
            dot.setBackground(dotBg);
            LinearLayout.LayoutParams dotLp = new LinearLayout.LayoutParams(dp(34), dp(34));
            dotLp.setMargins(dp(4), dp(4), dp(4), dp(4));
            dot.setLayoutParams(dotLp);
            dot.setOnClickListener(v -> {
                preview.setTextColor(c);
                try {
                    obj.put("nameColor", String.valueOf(c));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                tvName.setTextColor(c);
                dataChanged = true;
            });
            if (i < 8) row1.addView(dot);
            else row2.addView(dot);
        }
        root.addView(row1);
        root.addView(row2);

        b.setView(root);
        b.setPositiveButton("Apply", (d, w) -> adapter.notifyDataSetChanged());
        b.setNegativeButton("Cancel", null).show();
    }

    // ════════════════════════════════════════════════════════
    // REMOVE PHOTO
    // ════════════════════════════════════════════════════════
    private void removePhoto(JSONObject obj, int cellIdx, ImageView thumb) {
        new AlertDialog.Builder(this)
                .setTitle("Remove Photo")
                .setMessage("Cell " + (cellIdx + 1) + " photo remove?")
                .setPositiveButton("Remove", (d, w) -> {
                    try {
                        obj.put("photoUri", "");
                        obj.put("photoBitmap", "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    applyThumbShape(thumb, dp(62));
                    thumb.setImageResource(android.R.drawable.ic_menu_gallery);
                    dataChanged = true;
                    adapter.notifyDataSetChanged();
                    updateStats();
                    Toast.makeText(this, "Photo removed!", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null).show();
    }

    // ════════════════════════════════════════════════════════
    // REORDER
    // ════════════════════════════════════════════════════════
    private void moveCellUp(int cellIdx) {
        if (cellIdx <= 0) {
            Toast.makeText(this, "Already top!", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject cur = cellDataList.get(cellIdx);
        JSONObject prev = cellDataList.get(cellIdx - 1);
        cellDataList.set(cellIdx - 1, cur);
        cellDataList.set(cellIdx, prev);
        dataChanged = true;
        applyFilterAndSearch();
        recyclerView.scrollToPosition(Math.max(0, cellIdx - 1));
    }

    private void moveCellDown(int cellIdx) {
        if (cellIdx >= cellDataList.size() - 1) {
            Toast.makeText(this, "Already bottom!", Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject cur = cellDataList.get(cellIdx);
        JSONObject next = cellDataList.get(cellIdx + 1);
        cellDataList.set(cellIdx + 1, cur);
        cellDataList.set(cellIdx, next);
        dataChanged = true;
        applyFilterAndSearch();
        recyclerView.scrollToPosition(Math.min(displayList.size() - 1, cellIdx + 1));
    }

    private void showReorderDialog() {
        if (cellDataList.isEmpty()) {
            Toast.makeText(this, "No cells!", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setTitle("Reorder Cells");
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(12), dp(8), dp(12), dp(8));
        ScrollView sv = new ScrollView(this);
        final LinearLayout listC = new LinearLayout(this);
        listC.setOrientation(LinearLayout.VERTICAL);
        sv.addView(listC);
        Runnable[] buildRef = {null};
        buildRef[0] = () -> {
            listC.removeAllViews();
            for (int i = 0; i < cellDataList.size(); i++) {
                final int idx = i;
                JSONObject obj = cellDataList.get(i);
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.setPadding(dp(10), dp(8), dp(10), dp(8));
                row.setBackgroundColor(i % 2 == 0 ? Color.WHITE : Color.parseColor("#F5F5F5"));
                LinearLayout.LayoutParams rLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                rLp.setMargins(0, dp(2), 0, dp(2));
                row.setLayoutParams(rLp);
                TextView tvNo = new TextView(this);
                tvNo.setText((idx + 1) + ".");
                tvNo.setTextSize(13);
                tvNo.setTypeface(null, Typeface.BOLD);
                tvNo.setTextColor(Color.parseColor("#1565C0"));
                tvNo.setMinWidth(dp(30));
                row.addView(tvNo);
                LinearLayout ic = new LinearLayout(this);
                ic.setOrientation(LinearLayout.VERTICAL);
                ic.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                TextView tvN = new TextView(this);
                tvN.setText(obj.optString("name", "(No Name)"));
                tvN.setTextSize(13);
                tvN.setTypeface(null, Typeface.BOLD);
                tvN.setMaxLines(1);
                ic.addView(tvN);
                String info = obj.optString("info", "");
                if (!info.isEmpty()) {
                    TextView tvI = new TextView(this);
                    tvI.setText(info);
                    tvI.setTextSize(11);
                    tvI.setTextColor(infoColor(info));
                    ic.addView(tvI);
                }
                row.addView(ic);
                TextView up = new TextView(this);
                up.setText("^");
                up.setTextSize(18);
                up.setPadding(dp(10), dp(4), dp(4), dp(4));
                up.setTextColor(idx == 0 ? Color.LTGRAY : Color.parseColor("#1565C0"));
                row.addView(up);
                TextView dn = new TextView(this);
                dn.setText("v");
                dn.setTextSize(18);
                dn.setPadding(dp(4), dp(4), dp(10), dp(4));
                dn.setTextColor(idx == cellDataList.size() - 1 ? Color.LTGRAY : Color.parseColor("#1565C0"));
                row.addView(dn);
                final Runnable rebuild = buildRef[0];
                up.setOnClickListener(v -> {
                    if (idx <= 0) return;
                    JSONObject c = cellDataList.get(idx);
                    JSONObject p = cellDataList.get(idx - 1);
                    cellDataList.set(idx - 1, c);
                    cellDataList.set(idx, p);
                    dataChanged = true;
                    if (rebuild != null) rebuild.run();
                });
                dn.setOnClickListener(v -> {
                    if (idx >= cellDataList.size() - 1) return;
                    JSONObject c = cellDataList.get(idx);
                    JSONObject n = cellDataList.get(idx + 1);
                    cellDataList.set(idx + 1, c);
                    cellDataList.set(idx, n);
                    dataChanged = true;
                    if (rebuild != null) rebuild.run();
                });
                listC.addView(row);
            }
        };
        buildRef[0].run();
        root.addView(sv);
        bld.setView(root);
        bld.setPositiveButton("Apply", (d, w) -> {
            applyFilterAndSearch();
            updateStats();
        });
        bld.setNegativeButton("Cancel", null).show();
    }

    // ════════════════════════════════════════════════════════
    // SHARE CELL
    // ════════════════════════════════════════════════════════
    private void shareCell(JSONObject obj) {
        String name  = obj.optString("name",  "");
        String info  = obj.optString("info",  "");
        String notes = obj.optString("notes", "");

        StringBuilder sb = new StringBuilder("Cell Info\n--------\n");
        if (!name.isEmpty())  sb.append("Name: ").append(name).append("\n");
        if (!info.isEmpty())  sb.append("Info: ").append(info).append("\n");
        if (!notes.isEmpty()) sb.append("Notes: ").append(notes).append("\n");
        String shareText = sb.toString();

        // ── Photo bitmap (Base64 or URI) ─────────────────────
        String photoB64 = obj.optString("photoBitmap", "");
        String photoUri = obj.optString("photoUri",    "");

        Bitmap photoBmp = null;
        if (!photoB64.isEmpty()) {
            try {
                byte[] by = android.util.Base64.decode(photoB64, android.util.Base64.DEFAULT);
                photoBmp = BitmapFactory.decodeByteArray(by, 0, by.length);
            } catch (Exception ignored) { }
        }
        if (photoBmp == null && !photoUri.isEmpty()) {
            try {
                photoBmp = android.provider.MediaStore.Images.Media
                        .getBitmap(getContentResolver(), Uri.parse(photoUri));
            } catch (Exception ignored) { }
        }

        // ── Dialog: WhatsApp / WhatsApp Business / Generic ───
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("Share — " + (name.isEmpty() ? "Cell" : name));

        final Bitmap finalBmp = photoBmp;

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(16), dp(20), dp(16));

        // WhatsApp button
        Button btnWA = makeDlgBtn("WhatsApp", Color.parseColor("#25D366"));
        btnWA.setOnClickListener(v -> {
            shareCellViaWhatsApp("com.whatsapp", shareText, finalBmp);
        });
        root.addView(btnWA);

        // WhatsApp Business button
        LinearLayout.LayoutParams bLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bLp.setMargins(0, dp(8), 0, 0);

        Button btnWAB = makeDlgBtn("WhatsApp Business", Color.parseColor("#128C7E"));
        btnWAB.setLayoutParams(bLp);
        btnWAB.setOnClickListener(v -> {
            shareCellViaWhatsApp("com.whatsapp.w4b", shareText, finalBmp);
        });
        root.addView(btnWAB);

        // Generic Share button
        Button btnGen = makeDlgBtn("Other Apps", Color.parseColor("#1565C0"));
        LinearLayout.LayoutParams bLp2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        bLp2.setMargins(0, dp(8), 0, 0);
        btnGen.setLayoutParams(bLp2);
        btnGen.setOnClickListener(v -> {
            Intent si = new Intent(Intent.ACTION_SEND);
            if (finalBmp != null) {
                Uri imgUri = saveBitmapForShare(finalBmp);
                if (imgUri != null) {
                    si.setType("image/*");
                    si.putExtra(Intent.EXTRA_STREAM, imgUri);
                    si.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                } else {
                    si.setType("text/plain");
                }
            } else {
                si.setType("text/plain");
            }
            si.putExtra(Intent.EXTRA_TEXT, shareText);
            startActivity(Intent.createChooser(si, "Share Cell Info"));
        });
        root.addView(btnGen);

        dlg.setView(root);
        dlg.setNegativeButton("Cancel", null);
        dlg.show();
    }

    /** WhatsApp/WhatsApp Business direct share (text + optional image) */
    private void shareCellViaWhatsApp(String pkg, String text, Bitmap bmp) {
        // Check if app is installed
        try {
            getPackageManager().getPackageInfo(pkg, 0);
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            String appName = pkg.contains("w4b") ? "WhatsApp Business" : "WhatsApp";
            Toast.makeText(this, appName + " installed nathi!", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent wa = new Intent(Intent.ACTION_SEND);
        wa.setPackage(pkg);

        if (bmp != null) {
            Uri imgUri = saveBitmapForShare(bmp);
            if (imgUri != null) {
                wa.setType("image/*");
                wa.putExtra(Intent.EXTRA_STREAM, imgUri);
                wa.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                wa.setType("text/plain");
            }
        } else {
            wa.setType("text/plain");
        }

        wa.putExtra(Intent.EXTRA_TEXT, text);
        try {
            startActivity(wa);
        } catch (Exception ex) {
            Toast.makeText(this, "Share karva ma error aavyo.", Toast.LENGTH_SHORT).show();
        }
    }

    /** Save bitmap to cache dir and return FileProvider URI for sharing */
    private Uri saveBitmapForShare(Bitmap bmp) {
        try {
            java.io.File cacheDir = new java.io.File(getCacheDir(), "share_imgs");
            if (!cacheDir.exists()) cacheDir.mkdirs();
            java.io.File imgFile = new java.io.File(cacheDir, "cell_share_" + System.currentTimeMillis() + ".png");
            java.io.FileOutputStream fos = new java.io.FileOutputStream(imgFile);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            return androidx.core.content.FileProvider.getUriForFile(
                    this, getPackageName() + ".provider", imgFile);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // ════════════════════════════════════════════════════════
    // NOTES EDIT
    // ════════════════════════════════════════════════════════
    private void editNotes(JSONObject obj, int cellIdx) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Notes — Cell " + (cellIdx + 1));
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(16), dp(20), dp(16));
        EditText et = new EditText(this);
        et.setText(obj.optString("notes", ""));
        et.setHint("Notes...");
        et.setMinLines(3);
        et.setMaxLines(6);
        et.setGravity(Gravity.TOP);
        root.addView(et);
        b.setView(root);
        b.setPositiveButton("Apply", (d, w) -> {
            try {
                obj.put("notes", et.getText().toString().trim());
            } catch (Exception e) {
                e.printStackTrace();
            }
            dataChanged = true;
            adapter.notifyDataSetChanged();
        });
        b.setNeutralButton("Clear", (d, w) -> {
            try {
                obj.put("notes", "");
            } catch (Exception e) {
                e.printStackTrace();
            }
            dataChanged = true;
            adapter.notifyDataSetChanged();
        });
        b.setNegativeButton("Cancel", null).show();
    }

    // ════════════════════════════════════════════════════════
    // AUTO NUMBER
    // ════════════════════════════════════════════════════════
    private void showAutoNumberDialog() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Auto Number Names");
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(16), dp(20), dp(16));
        TextView l1 = new TextView(this);
        l1.setText("Prefix:");
        root.addView(l1);
        EditText etP = new EditText(this);
        etP.setText("Name ");
        root.addView(etP);
        TextView l2 = new TextView(this);
        l2.setText("Start number:");
        l2.setPadding(0, dp(10), 0, dp(4));
        root.addView(l2);
        EditText etS = new EditText(this);
        etS.setText("1");
        etS.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        root.addView(etS);
        b.setView(root);
        b.setPositiveButton("Apply to All", (d, w) -> {
            String prefix = etP.getText().toString();
            int start;
            try {
                start = Integer.parseInt(etS.getText().toString());
            } catch (Exception e) {
                start = 1;
            }
            for (int i = 0; i < cellDataList.size(); i++) {
                try {
                    cellDataList.get(i).put("name", prefix + (start + i));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            dataChanged = true;
            applyFilterAndSearch();
            updateStats();
            Toast.makeText(this, "Auto number applied!", Toast.LENGTH_SHORT).show();
        });
        b.setNegativeButton("Cancel", null).show();
    }

    // ════════════════════════════════════════════════════════
    // MULTI SELECT
    // ════════════════════════════════════════════════════════
    private void toggleMultiSelectMode() {
        isMultiSelectMode = !isMultiSelectMode;
        selectedIndices.clear();
        if (isMultiSelectMode) {
            new AlertDialog.Builder(this)
                    .setTitle("Multi-Select Mode ON")
                    .setMessage("Cells tap = select")
                    .setPositiveButton("Delete Selected", (d, w) -> {
                        if (selectedIndices.isEmpty()) {
                            Toast.makeText(this, "Select cells first", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        new AlertDialog.Builder(this).setTitle("Delete " + selectedIndices.size() + " cells?")
                                .setPositiveButton("Delete", (d2, w2) -> {
                                    List<JSONObject> rem = new ArrayList<>();
                                    for (int idx : selectedIndices) {
                                        if (idx >= 0 && idx < cellDataList.size())
                                            rem.add(cellDataList.get(idx));
                                    }
                                    cellDataList.removeAll(rem);
                                    displayList.removeAll(rem);
                                    selectedIndices.clear();
                                    isMultiSelectMode = false;
                                    if (tvMultiCount != null) tvMultiCount.setVisibility(View.GONE);
                                    dataChanged = true;
                                    applyFilterAndSearch();
                                    updateStats();
                                    Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show();
                                }).setNegativeButton("Cancel", null).show();
                    })
                    .setNeutralButton("Select All", (d, w) -> {
                        for (int i = 0; i < cellDataList.size(); i++) selectedIndices.add(i);
                        adapter.notifyDataSetChanged();
                        updateMultiCount();
                    })
                    .setNegativeButton("Exit", (d, w) -> {
                        isMultiSelectMode = false;
                        selectedIndices.clear();
                        if (tvMultiCount != null) tvMultiCount.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    })
                    .show();
        } else {
            if (tvMultiCount != null) tvMultiCount.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
        updateMultiCount();
    }

    private void updateMultiCount() {
        if (tvMultiCount == null) return;
        if (isMultiSelectMode) {
            tvMultiCount.setVisibility(View.VISIBLE);
            tvMultiCount.setText("Selected: " + selectedIndices.size());
        } else {
            tvMultiCount.setVisibility(View.GONE);
        }
    }

    // ════════════════════════════════════════════════════════
    // IMPORT CSV
    // ════════════════════════════════════════════════════════
    private void importCsv() {
        new AlertDialog.Builder(this)
                .setTitle("Import CSV")
                .setMessage("Format: Name,Info(%),Notes\nExample: Ram,75%,Good")
                .setPositiveButton("Select File", (d, w) -> {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("*/*");
                    startActivityForResult(Intent.createChooser(intent, "Select CSV"), REQUEST_IMPORT_CSV);
                })
                .setNegativeButton("Cancel", null).show();
    }

    private void processImportedCsv(Uri uri) {
        new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(getContentResolver().openInputStream(uri)));
                List<JSONObject> imported = new ArrayList<>();
                String line;
                boolean first = true;
                while ((line = reader.readLine()) != null) {
                    if (first) {
                        first = false;
                        if (line.toLowerCase().startsWith("name") || line.toLowerCase().startsWith("no,"))
                            continue;
                    }
                    String[] parts = line.split(",", -1);
                    if (parts.length == 0) continue;
                    JSONObject cell = new JSONObject();
                    cell.put("name", parts.length > 0 ? parts[0].trim() : "");
                    cell.put("info", parts.length > 1 ? parts[1].trim() : "");
                    cell.put("notes", parts.length > 2 ? parts[2].trim() : "");
                    cell.put("photoUri", "");
                    cell.put("photoBitmap", "");
                    cell.put("frameMask", "");
                    cell.put("frameTop", "");
                    cell.put("nameColor", Color.BLACK);
                    imported.add(cell);
                }
                reader.close();
                final int count = imported.size();
                runOnUiThread(() -> {
                    if (count == 0) {
                        Toast.makeText(this, "CSV empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    new AlertDialog.Builder(this).setTitle(count + " rows found").setMessage("Import mode:")
                            .setPositiveButton("Append", (d, w) -> {
                                cellDataList.addAll(imported);
                                dataChanged = true;
                                applyFilterAndSearch();
                                updateStats();
                                Toast.makeText(this, count + " cells added!", Toast.LENGTH_SHORT).show();
                            })
                            .setNeutralButton("Replace All", (d, w) -> {
                                cellDataList.clear();
                                cellDataList.addAll(imported);
                                dataChanged = true;
                                applyFilterAndSearch();
                                updateStats();
                                Toast.makeText(this, count + " cells replaced!", Toast.LENGTH_SHORT).show();
                            })
                            .setNegativeButton("Cancel", null).show();
                });
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "CSV error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    // ════════════════════════════════════════════════════════
    // onActivityResult
    // ════════════════════════════════════════════════════════
    @Override
    protected void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);
        if (req == REQUEST_CELL_PHOTO && res == RESULT_OK && data != null && pendingCellIdx >= 0 && pendingCellIdx < cellDataList.size()) {
            Uri uri = data.getData();
            if (uri == null) return;
            final int idx = pendingCellIdx;
            pendingCellIdx = -1;
            try {
                cellDataList.get(idx).put("photoUri", uri.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Glide.with(this).asBitmap().load(uri).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap bmp, @Nullable Transition<? super Bitmap> t) {
                    runOnUiThread(() -> showFingerAdjustDialog(bmp, uri, idx));
                }

                @Override
                public void onLoadCleared(@Nullable Drawable p) {
                }
            });
        }
        if (req == REQUEST_IMPORT_CSV && res == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) processImportedCsv(uri);
        }
    }

    // ════════════════════════════════════════════════════════
    // FILTER + SEARCH
    // ════════════════════════════════════════════════════════
    private void applyFilterAndSearch() {
        String q = etSearch != null ? etSearch.getText().toString().toLowerCase().trim() : "";
        displayList.clear();
        for (JSONObject o : cellDataList) {
            String n = o.optString("name", "").toLowerCase();
            String inf = o.optString("info", "").toLowerCase();
            String nt = o.optString("notes", "").toLowerCase();
            boolean sm = q.isEmpty() || n.contains(q) || inf.contains(q) || nt.contains(q);
            if (!sm) continue;
            boolean hp = !o.optString("photoUri", "").isEmpty() || !o.optString("photoBitmap", "").isEmpty();
            boolean hm = !o.optString("frameMask", "").isEmpty() || !o.optString("frameTop", "").isEmpty();
            boolean fm;
            switch (currentFilter) {
                case "HAS_PHOTO":
                    fm = hp;
                    break;
                case "NO_PHOTO":
                    fm = !hp;
                    break;
                case "HAS_MASK":
                    fm = hm;
                    break;
                case "NO_MASK":
                    fm = !hm;
                    break;
                default:
                    fm = true;
                    break;
            }
            if (!fm) continue;
            displayList.add(o);
        }
        adapter.notifyDataSetChanged();
    }

    // ════════════════════════════════════════════════════════
    // PHOTO PICK
    // ════════════════════════════════════════════════════════
    private void pickPhoto(int cellIdx) {
        pendingCellIdx = cellIdx;
        JSONObject obj = cellDataList.get(cellIdx);
        boolean hp = !obj.optString("photoBitmap", "").isEmpty() || !obj.optString("photoUri", "").isEmpty();
        if (hp) {
            new AlertDialog.Builder(this).setTitle("Photo Replace?")
                    .setPositiveButton("New Photo", (d, w) -> openGallery())
                    .setNeutralButton("Adjust Existing", (d, w) -> {
                        pendingCellIdx = -1;
                        reopenAdjustForCell(obj, cellIdx);
                    })
                    .setNegativeButton("Cancel", (d, w) -> pendingCellIdx = -1).show();
        } else {
            openGallery();
        }
    }

    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Photo"), REQUEST_CELL_PHOTO);
    }

    // ════════════════════════════════════════════════════════
    // FINGER ADJUST DIALOG
    // ════════════════════════════════════════════════════════
    private void showFingerAdjustDialog(final Bitmap orig, final Uri photoUri, final int cellIdx) {
        JSONObject obj = cellDataList.get(cellIdx);
        String maskUrl = obj.optString("frameMask", "");
        String topUrl = obj.optString("frameTop", "");
        boolean hasMask = !maskUrl.isEmpty() || !topUrl.isEmpty();
        android.app.Dialog dialog = new android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.parseColor("#CC000000"));
        dialog.setContentView(root);
        TextView title = new TextView(this);
        title.setText("Pinch Zoom + Drag");
        title.setTextColor(Color.WHITE);
        title.setTextSize(13);
        title.setGravity(Gravity.CENTER);
        title.setBackgroundColor(Color.parseColor("#AA000000"));
        title.setPadding(dp(16), dp(44), dp(16), dp(14));
        RelativeLayout.LayoutParams tlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        tlp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(title, tlp);
        int ps = Math.min(getResources().getDisplayMetrics().widthPixels, getResources().getDisplayMetrics().heightPixels) - dp(120);
        final Bitmap[] mH = {null}, tH = {null};
        final Matrix matrix = new Matrix(), savedMatrix = new Matrix();
        final View pv = new View(this) {
            @Override
            protected void onDraw(Canvas c) {
                super.onDraw(c);
                int w = getWidth();
                int h = getHeight();
                if (w <= 0 || h <= 0) return;
                c.drawColor(Color.parseColor("#111111"));
                Bitmap pb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas pc = new Canvas(pb);
                Paint pp = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                pc.drawBitmap(orig, matrix, pp);
                Bitmap rb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                Canvas rc = new Canvas(rb);
                Paint rp = new Paint(Paint.ANTI_ALIAS_FLAG);
                if (hasMask && mH[0] != null) {
                    Bitmap sm = Bitmap.createScaledBitmap(mH[0], w, h, true);
                    boolean t = hasTransparentPixels(sm);
                    if (t) {
                        rc.drawBitmap(pb, 0, 0, rp);
                        Paint di = new Paint(Paint.ANTI_ALIAS_FLAG);
                        di.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                        rc.drawBitmap(sm, 0, 0, di);
                        di.setXfermode(null);
                    } else {
                        rc.drawBitmap(sm, 0, 0, rp);
                        Paint si2 = new Paint(Paint.ANTI_ALIAS_FLAG);
                        si2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                        rc.drawBitmap(pb, 0, 0, si2);
                        si2.setXfermode(null);
                    }
                    sm.recycle();
                } else {
                    Paint sp = new Paint(Paint.ANTI_ALIAS_FLAG);
                    sp.setColor(Color.WHITE);
                    if ("CIRCLE".equals(shape))
                        rc.drawCircle(w / 2f, h / 2f, Math.min(w, h) / 2f, sp);
                    else if ("ROUNDED".equals(shape)) {
                        float r = w * 0.12f;
                        rc.drawRoundRect(0, 0, w, h, r, r, sp);
                    } else rc.drawRect(0, 0, w, h, sp);
                    Paint si2 = new Paint(Paint.ANTI_ALIAS_FLAG);
                    si2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                    rc.drawBitmap(pb, 0, 0, si2);
                    si2.setXfermode(null);
                }
                c.drawBitmap(rb, 0, 0, rp);
                if (hasMask && tH[0] != null) {
                    Bitmap st = Bitmap.createScaledBitmap(tH[0], w, h, true);
                    c.drawBitmap(st, 0, 0, rp);
                    st.recycle();
                }
                pb.recycle();
                rb.recycle();
            }
        };
        pv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        RelativeLayout.LayoutParams pvLp = new RelativeLayout.LayoutParams(ps, ps);
        pvLp.addRule(RelativeLayout.CENTER_IN_PARENT);
        root.addView(pv, pvLp);
        final ImageView tv = new ImageView(this);
        tv.setImageBitmap(orig);
        tv.setScaleType(ImageView.ScaleType.MATRIX);
        tv.setAlpha(0f);
        root.addView(tv, pvLp);
        tv.post(() -> {
            float iw = orig.getWidth();
            float ih = orig.getHeight();
            float sc = Math.max((float) ps / iw, (float) ps / ih);
            float dx = (ps - iw * sc) / 2f;
            float dy = (ps - ih * sc) / 2f;
            matrix.setScale(sc, sc);
            matrix.postTranslate(dx, dy);
            tv.setImageMatrix(matrix);
            savedMatrix.set(matrix);
            pv.invalidate();
        });
        if (hasMask && !maskUrl.isEmpty())
            Glide.with(this).asBitmap().load(maskUrl).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap b, @Nullable Transition<? super Bitmap> t) {
                    mH[0] = b;
                    pv.invalidate();
                }

                @Override
                public void onLoadCleared(@Nullable Drawable p) {
                }
            });
        if (hasMask && !topUrl.isEmpty())
            Glide.with(this).asBitmap().load(topUrl).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap b, @Nullable Transition<? super Bitmap> t) {
                    tH[0] = b;
                    pv.invalidate();
                }

                @Override
                public void onLoadCleared(@Nullable Drawable p) {
                }
            });
        final float[] lX = {0}, lY = {0}, mX = {0}, mY = {0}, sD = {0};
        final int[] tm = {0};
        tv.setOnTouchListener((v, event) -> {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    savedMatrix.set(matrix);
                    lX[0] = event.getX();
                    lY[0] = event.getY();
                    tm[0] = 1;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    if (event.getPointerCount() >= 2) {
                        sD[0] = fingerSpacing(event);
                        if (sD[0] > 10f) {
                            savedMatrix.set(matrix);
                            fingerMidPoint(mX, mY, event);
                            tm[0] = 2;
                        }
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (tm[0] == 1) {
                        float dx = event.getX() - lX[0];
                        float dy = event.getY() - lY[0];
                        matrix.set(savedMatrix);
                        matrix.postTranslate(dx, dy);
                        tv.setImageMatrix(matrix);
                        pv.invalidate();
                    } else if (tm[0] == 2 && event.getPointerCount() >= 2) {
                        float nd = fingerSpacing(event);
                        if (nd > 10f) {
                            float sf = nd / sD[0];
                            matrix.set(savedMatrix);
                            matrix.postScale(sf, sf, mX[0], mY[0]);
                            tv.setImageMatrix(matrix);
                            pv.invalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    tm[0] = 0;
                    break;
            }
            return true;
        });
        LinearLayout btnRow = new LinearLayout(this);
        btnRow.setOrientation(LinearLayout.HORIZONTAL);
        btnRow.setGravity(Gravity.CENTER);
        btnRow.setPadding(dp(8), dp(14), dp(8), dp(36));
        btnRow.setBackgroundColor(Color.parseColor("#AA000000"));
        RelativeLayout.LayoutParams brlp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        brlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        root.addView(btnRow, brlp);
        Button bReset = makeDlgBtn("Reset", Color.parseColor("#546E7A"));
        bReset.setOnClickListener(v -> {
            float iw = orig.getWidth();
            float ih = orig.getHeight();
            float sc = Math.max((float) ps / iw, (float) ps / ih);
            float dx = (ps - iw * sc) / 2f;
            float dy = (ps - ih * sc) / 2f;
            matrix.setScale(sc, sc);
            matrix.postTranslate(dx, dy);
            tv.setImageMatrix(matrix);
            savedMatrix.set(matrix);
            pv.invalidate();
        });
        btnRow.addView(bReset, makeBtnLp());
        Button bCancel = makeDlgBtn("Cancel", Color.parseColor("#C62828"));
        bCancel.setOnClickListener(v -> dialog.dismiss());
        btnRow.addView(bCancel, makeBtnLp());
        Button bApply = makeDlgBtn("Apply", Color.parseColor("#1565C0"));
        bApply.setOnClickListener(v -> {
            dialog.dismiss();
            applyAdjustedPhoto(orig, matrix, tv, mH[0], tH[0], cellIdx, photoUri, hasMask);
        });
        btnRow.addView(bApply, makeBtnLp());
        dialog.show();
    }

    private void applyAdjustedPhoto(Bitmap orig, Matrix matrix, ImageView tv, @Nullable Bitmap maskBmp, @Nullable Bitmap topBmp, int cellIdx, Uri photoUri, boolean hasMask) {
        Matrix inv = new Matrix();
        matrix.invert(inv);
        int vW = tv.getWidth();
        int vH = tv.getHeight();
        float[] c = {0, 0, vW, 0, vW, vH, 0, vH};
        inv.mapPoints(c);
        float minX = Math.min(Math.min(c[0], c[2]), Math.min(c[4], c[6]));
        float minY = Math.min(Math.min(c[1], c[3]), Math.min(c[5], c[7]));
        float maxX = Math.max(Math.max(c[0], c[2]), Math.max(c[4], c[6]));
        float maxY = Math.max(Math.max(c[1], c[3]), Math.max(c[5], c[7]));
        int bW = orig.getWidth();
        int bH = orig.getHeight();
        minX = Math.max(0, minX);
        minY = Math.max(0, minY);
        maxX = Math.min(bW, maxX);
        maxY = Math.min(bH, maxY);
        int cW = (int) (maxX - minX);
        int cH = (int) (maxY - minY);
        if (cW <= 0 || cH <= 0) {
            cW = bW;
            cH = bH;
            minX = 0;
            minY = 0;
        }
        Bitmap cropped = Bitmap.createBitmap(orig, (int) minX, (int) minY, cW, cH);
        int sz = cellSizePx > 0 ? cellSizePx : 200;

        if (hasMask && maskBmp != null) {
            Bitmap r = mergePhotoMaskTop(cropped, maskBmp, topBmp, sz, sz);
            saveAndRefreshCell(cellIdx, r, photoUri, cropped); // ✅ cropped original pass
        } else {
            Bitmap shaped = clipToShape(cropped, sz);
            saveAndRefreshCell(cellIdx, shaped, photoUri, cropped); // ✅ cropped original pass
            cropped.recycle();
        }
    }

    private void saveAndRefreshCell(int cellIdx, Bitmap result, Uri photoUri, @Nullable Bitmap originalBitmap) {
        if (cellIdx < 0 || cellIdx >= cellDataList.size()) return;
        JSONObject obj = cellDataList.get(cellIdx);
        try {
            obj.put("photoBitmap", bitmapToBase64(result));
            if (photoUri != null) obj.put("photoUri", photoUri.toString());

            // ✅ Original bitmap અલગ save કરો
            if (originalBitmap != null) {
                obj.put("originalBitmap", bitmapToBase64(originalBitmap));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        dataChanged = true;
        runOnUiThread(() -> {
            applyFilterAndSearch();
            updateStats();
            Toast.makeText(this, "Photo apply!", Toast.LENGTH_SHORT).show();
        });
    }

    private void reopenAdjustForCell(JSONObject obj, int cellIdx) {

        // ✅ પહેલા originalBitmap check કરો
        String origB64 = obj.optString("originalBitmap", "");
        if (!origB64.isEmpty()) {
            try {
                byte[] by = android.util.Base64.decode(origB64, android.util.Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(by, 0, by.length);
                if (bmp != null) {
                    showFingerAdjustDialog(bmp, null, cellIdx);
                    return;
                }
            } catch (Exception ignore) {}
        }

        // Fallback — photoUri થી load
        String uri = obj.optString("photoUri", "");
        if (!uri.isEmpty()) {
            Uri u = Uri.parse(uri);
            Glide.with(this).asBitmap().load(u).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap bmp,
                                            @Nullable Transition<? super Bitmap> t) {
                    runOnUiThread(() -> showFingerAdjustDialog(bmp, u, cellIdx));
                }
                @Override
                public void onLoadCleared(@Nullable Drawable p) {}
            });
            return;
        }

        // shaped bitmap fallback (last resort)
        String b64 = obj.optString("photoBitmap", "");
        if (!b64.isEmpty()) {
            try {
                byte[] by = android.util.Base64.decode(b64, android.util.Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(by, 0, by.length);
                if (bmp != null) {
                    showFingerAdjustDialog(bmp, null, cellIdx);
                    return;
                }
            } catch (Exception ignore) {}
        }

        Toast.makeText(this, "Photo select karo", Toast.LENGTH_SHORT).show();
        pickPhoto(cellIdx);
    }

    // ════════════════════════════════════════════════════════
    // MASK PICKERS
    // ════════════════════════════════════════════════════════
    private void showMaskPickerForCell(JSONObject obj, int cellIdx, ImageView thumb) {
        String curMask = obj.optString("frameMask", "");
        String curTop = obj.optString("frameTop", "");
        boolean has = !curMask.isEmpty() || !curTop.isEmpty();
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setTitle("Mask — Cell " + (cellIdx + 1));
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(14), dp(14), dp(14), dp(14));
        TextView lblSt = new TextView(this);
        lblSt.setText(has ? "Mask set" : "No Mask");
        lblSt.setTextColor(has ? Color.parseColor("#2E7D32") : Color.parseColor("#C62828"));
        root.addView(lblSt);
        ImageView prev = new ImageView(this);
        prev.setScaleType(ImageView.ScaleType.FIT_CENTER);
        prev.setBackgroundColor(Color.parseColor("#F5F5F5"));
        LinearLayout.LayoutParams pvLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(110));
        pvLp.setMargins(0, dp(6), 0, dp(6));
        prev.setLayoutParams(pvLp);
        if (has) Glide.with(this).load(!curTop.isEmpty() ? curTop : curMask).into(prev);
        else prev.setImageResource(android.R.drawable.ic_menu_gallery);
        root.addView(prev);
        Button btnRem = new Button(this);
        btnRem.setText("Remove Mask");
        btnRem.setTextColor(Color.WHITE);
        btnRem.setBackgroundColor(Color.parseColor("#C62828"));
        btnRem.setEnabled(has);
        btnRem.setAlpha(has ? 1f : 0.4f);
        LinearLayout.LayoutParams rLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        rLp.setMargins(0, 0, 0, dp(6));
        btnRem.setLayoutParams(rLp);
        root.addView(btnRem);
        ProgressBar pb = new ProgressBar(this);
        root.addView(pb);
        RecyclerView fRV = new RecyclerView(this);
        fRV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(360)));
        root.addView(fRV);
        ScrollView sv = new ScrollView(this);
        sv.addView(root);
        final AlertDialog[] dlgRef = {null};
        dlgRef[0] = bld.setView(sv).setNegativeButton("Cancel", null).create();
        loadFrameList(fRV, pb);
        fRV.addOnItemTouchListener(new invite_AppConstants.RecyclerTouchListener(this, fRV, new invite_AppConstants.RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int pos) {
                if (invite_photo_frame.frame_arrayList == null || invite_photo_frame.frame_arrayList.isEmpty())
                    return;
                String nM = invite_photo_frame.frame_arrayList.get(pos).getcard_background();
                String nT = invite_photo_frame.frame_arrayList.get(pos).getemail_icon();
                String fI = invite_photo_frame.frame_arrayList.get(pos).getImageBig();
                Glide.with(GridListActivity.this).load(fI).into(prev);
                lblSt.setText("Frame selected!");
                for (int i = root.getChildCount() - 1; i >= 0; i--) {
                    View ch = root.getChildAt(i);
                    if (ch instanceof Button && ((Button) ch).getText().toString().startsWith("Apply")) {
                        root.removeViewAt(i);
                        break;
                    }
                }
                Button bApp = new Button(GridListActivity.this);
                bApp.setText("Apply to Cell " + (cellIdx + 1));
                bApp.setTextColor(Color.WHITE);
                bApp.setBackgroundColor(Color.parseColor("#1565C0"));
                LinearLayout.LayoutParams aLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                aLp.setMargins(0, 0, 0, dp(6));
                bApp.setLayoutParams(aLp);
                int ri = root.indexOfChild(btnRem);
                root.addView(bApp, ri);
                bApp.setOnClickListener(av -> {
                    if (dlgRef[0] != null && dlgRef[0].isShowing()) dlgRef[0].dismiss();
                    applyMaskToCell(obj, cellIdx, nM, nT, thumb);
                });
            }

            @Override
            public void onLongClick(View v, int p) {
            }
        }));
        btnRem.setOnClickListener(v -> {
            if (dlgRef[0] != null && dlgRef[0].isShowing()) dlgRef[0].dismiss();
            removeMaskFromCell(obj, cellIdx, thumb);
        });
        dlgRef[0].show();
    }

    private void applyMaskToCell(JSONObject obj, int cellIdx,
                                 String maskUrl, String topUrl, ImageView thumb) {
        try {
            obj.put("frameMask", maskUrl);
            obj.put("frameTop", topUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataChanged = true;

        // ✅ original હોય તો adjust dialog ખોલો
        String hasOriginal = obj.optString("originalBitmap", "");
        String hasPhoto = obj.optString("photoBitmap", "");
        String hasUri = obj.optString("photoUri", "");

        if (!hasOriginal.isEmpty() || !hasPhoto.isEmpty() || !hasUri.isEmpty()) {
            reopenAdjustForCell(obj, cellIdx); // original bitmap વાપરશે
        } else {
            adapter.notifyDataSetChanged();
            Toast.makeText(this, "Mask set!", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeMaskFromCell(JSONObject obj, int cellIdx, ImageView thumb) {
        try {
            obj.put("frameMask", "");
            obj.put("frameTop", "");
        } catch (Exception e) {
            e.printStackTrace();
        }
        dataChanged = true;
        applyThumbShape(thumb, dp(62));
        loadThumbPhoto(thumb, obj.optString("photoBitmap", ""), obj.optString("photoUri", ""));
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Mask removed!", Toast.LENGTH_SHORT).show();
    }

    private void showMaskPickerForAll() {
        if (cellDataList.isEmpty()) {
            Toast.makeText(this, "No cells!", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setTitle("All Cells Mask");
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(14), dp(14), dp(14), dp(14));
        ImageView prev = new ImageView(this);
        prev.setScaleType(ImageView.ScaleType.FIT_CENTER);
        prev.setBackgroundColor(Color.parseColor("#F5F5F5"));
        LinearLayout.LayoutParams pvLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(110));
        pvLp.setMargins(0, 0, 0, dp(6));
        prev.setLayoutParams(pvLp);
        prev.setImageResource(android.R.drawable.ic_menu_gallery);
        root.addView(prev);
        Button btnRemAll = new Button(this);
        btnRemAll.setText("Remove All Masks");
        btnRemAll.setTextColor(Color.WHITE);
        btnRemAll.setBackgroundColor(Color.parseColor("#C62828"));
        LinearLayout.LayoutParams raLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        raLp.setMargins(0, 0, 0, dp(6));
        btnRemAll.setLayoutParams(raLp);
        root.addView(btnRemAll);
        ProgressBar pb = new ProgressBar(this);
        root.addView(pb);
        RecyclerView fRV = new RecyclerView(this);
        fRV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dp(360)));
        root.addView(fRV);
        ScrollView sv = new ScrollView(this);
        sv.addView(root);
        final AlertDialog[] dlgRef = {null};
        dlgRef[0] = bld.setView(sv).setNegativeButton("Cancel", null).create();
        loadFrameList(fRV, pb);
        fRV.addOnItemTouchListener(new invite_AppConstants.RecyclerTouchListener(this, fRV, new invite_AppConstants.RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int pos) {
                if (invite_photo_frame.frame_arrayList == null || invite_photo_frame.frame_arrayList.isEmpty())
                    return;
                String nM = invite_photo_frame.frame_arrayList.get(pos).getcard_background();
                String nT = invite_photo_frame.frame_arrayList.get(pos).getemail_icon();
                String fI = invite_photo_frame.frame_arrayList.get(pos).getImageBig();
                Glide.with(GridListActivity.this).load(fI).into(prev);
                for (int i = root.getChildCount() - 1; i >= 0; i--) {
                    View ch = root.getChildAt(i);
                    if (ch instanceof Button && ((Button) ch).getText().toString().startsWith("Apply")) {
                        root.removeViewAt(i);
                        break;
                    }
                }
                Button bApp = new Button(GridListActivity.this);
                bApp.setText("Apply to All " + cellDataList.size());
                bApp.setTextColor(Color.WHITE);
                bApp.setBackgroundColor(Color.parseColor("#1565C0"));
                LinearLayout.LayoutParams aLp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                aLp.setMargins(0, 0, 0, dp(6));
                bApp.setLayoutParams(aLp);
                int ri = root.indexOfChild(btnRemAll);
                root.addView(bApp, ri);
                bApp.setOnClickListener(av -> {
                    if (dlgRef[0] != null && dlgRef[0].isShowing()) dlgRef[0].dismiss();
                    for (JSONObject o : cellDataList) {
                        try {
                            o.put("frameMask", nM);
                            o.put("frameTop", nT);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    dataChanged = true;
                    adapter.notifyDataSetChanged();
                    updateStats();
                    Toast.makeText(GridListActivity.this, "Mask applied!", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onLongClick(View v, int p) {
            }
        }));
        btnRemAll.setOnClickListener(v -> {
            if (dlgRef[0] != null && dlgRef[0].isShowing()) dlgRef[0].dismiss();
            new AlertDialog.Builder(this).setTitle("Remove All Masks?").setPositiveButton("Remove", (d, w) -> {
                for (JSONObject o : cellDataList) {
                    try {
                        o.put("frameMask", "");
                        o.put("frameTop", "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                dataChanged = true;
                adapter.notifyDataSetChanged();
                updateStats();
                Toast.makeText(this, "All masks removed!", Toast.LENGTH_SHORT).show();
            }).setNegativeButton("Cancel", null).show();
        });
        dlgRef[0].show();
    }

    private void loadFrameList(RecyclerView rv, ProgressBar pb) {
        android.widget.LinearLayout dLl = new android.widget.LinearLayout(this);
        dLl.setVisibility(View.GONE);
        android.widget.TextView dTv = new android.widget.TextView(this);
        invite_photo_frame.frame_arrayList = new java.util.ArrayList<>();
        invite_photo_frame.frame_arrayListTemp = new java.util.ArrayList<>();
        invite_photo_frame.frame_isOver = false;
        invite_photo_frame.frame_isScroll = false;
        invite_photo_frame.frame_page = 1;
        invite_photo_frame.frame_cidddddd = "8";
        invite_photo_frame.frame_recyclerView = rv;
        invite_photo_frame.frame_progressBar = pb;
        invite_photo_frame.frame_ll_empty = dLl;
        invite_photo_frame.frame_tv_empty = dTv;
        invite_photo_frame.frame_button_empty = null;
        invite_photo_frame.frame_adapterImageQuotes = null;
        invite_photo_frame.frame_methods = new invite_Methods(this);
        StaggeredGridLayoutManager lm = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(lm);
        invite_photo_frame.frame_lLayout = lm;
        invite_photo_frame.frame_selectmethod = invite_AppConstants.METHOD_FRAME_ALL;
        invite_photo_frame.loadQuotesByCat_1(invite_AppConstants.METHOD_FRAME_ALL, this);
        invite_EndlessRecyclerViewScrollListener1 sl = new invite_EndlessRecyclerViewScrollListener1(lm) {
            @Override
            public void onLoadMore(int pg, int total, RecyclerView v) {
                if (!invite_photo_frame.frame_isOver && !invite_photo_frame.frame_isScroll) {
                    invite_photo_frame.frame_isScroll = true;
                    new Handler().postDelayed(() -> invite_photo_frame.loadQuotesByCat_1(invite_AppConstants.METHOD_FRAME_ALL, GridListActivity.this), 500);
                }
            }
        };
        rv.addOnScrollListener(sl);
        invite_photo_frame.frame_scrollListener = sl;
    }

    // ════════════════════════════════════════════════════════
    // EDIT NAME/INFO
    // ════════════════════════════════════════════════════════
    private void editName(JSONObject obj, int ci, TextView tvName) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Name — Cell " + (ci + 1));
        EditText et = new EditText(this);
        et.setText(obj.optString("name", ""));
        et.setSelection(et.getText().length());
        et.setPadding(dp(14), dp(10), dp(14), dp(10));
        b.setView(et);
        b.setPositiveButton("Apply", (d, w) -> {
            String v = et.getText().toString().trim();
            try {
                obj.put("name", v);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvName.setText(v.isEmpty() ? "(No Name)" : v);
            dataChanged = true;
            updateStats();
        });
        b.setNegativeButton("Cancel", null).show();
    }

    private void editInfo(JSONObject obj, int ci, TextView tvInfo) {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Info — Cell " + (ci + 1));
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(14), dp(18), dp(14));
        EditText et = new EditText(this);
        et.setText(obj.optString("info", ""));
        et.setSelection(et.getText().length());
        et.setHint("0.00%");
        et.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        root.addView(et);
        LinearLayout qr = new LinearLayout(this);
        qr.setOrientation(LinearLayout.HORIZONTAL);
        qr.setPadding(0, dp(8), 0, 0);
        String[] qv = {"25%", "50%", "75%", "100%"};
        int[] qc = {Color.parseColor("#C62828"), Color.parseColor("#FF9800"), Color.parseColor("#1565C0"), Color.parseColor("#2E7D32")};
        for (int i = 0; i < qv.length; i++) {
            final String val = qv[i];
            TextView tb = new TextView(this);
            tb.setText(val);
            tb.setTextColor(Color.WHITE);
            tb.setTextSize(12);
            tb.setGravity(Gravity.CENTER);
            tb.setPadding(dp(6), dp(6), dp(6), dp(6));
            GradientDrawable gd = new GradientDrawable();
            gd.setColor(qc[i]);
            gd.setCornerRadius(dp(8));
            tb.setBackground(gd);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            lp.setMargins(dp(3), 0, dp(3), 0);
            tb.setLayoutParams(lp);
            tb.setOnClickListener(v -> {
                et.setText(val);
                et.setSelection(et.getText().length());
            });
            qr.addView(tb);
        }
        root.addView(qr);
        b.setView(root);
        b.setPositiveButton("Apply", (d, w) -> {
            String v = et.getText().toString().trim();
            try {
                obj.put("info", v);
            } catch (Exception e) {
                e.printStackTrace();
            }
            tvInfo.setText(v.isEmpty() ? "—" : v);
            tvInfo.setTextColor(infoColor(v));
            dataChanged = true;
            updateStats();
        });
        b.setNegativeButton("Cancel", null).show();
    }

    private void confirmDelete(JSONObject obj, int ci) {
        new AlertDialog.Builder(this).setTitle("Delete Cell " + (ci + 1)).setMessage(obj.optString("name", ""))
                .setPositiveButton("Delete", (d, w) -> {
                    cellDataList.remove(obj);
                    displayList.remove(obj);
                    adapter.notifyDataSetChanged();
                    dataChanged = true;
                    updateStats();
                }).setNegativeButton("Cancel", null).show();
    }

    private void showAddCell() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("New Cell");
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(14), dp(18), dp(14));
        TextView l1 = new TextView(this);
        l1.setText("Name:");
        root.addView(l1);
        EditText etN = new EditText(this);
        etN.setHint("Name");
        root.addView(etN);
        TextView l2 = new TextView(this);
        l2.setText("Info:");
        l2.setPadding(0, dp(8), 0, dp(3));
        root.addView(l2);
        EditText etI = new EditText(this);
        etI.setHint("0.00%");
        etI.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        root.addView(etI);
        TextView l3 = new TextView(this);
        l3.setText("Notes:");
        l3.setPadding(0, dp(8), 0, dp(3));
        root.addView(l3);
        EditText etNt = new EditText(this);
        etNt.setHint("Notes...");
        root.addView(etNt);
        b.setView(root);
        b.setPositiveButton("Add", (d, w) -> {
            JSONObject nc = new JSONObject();
            try {
                nc.put("name", etN.getText().toString().trim());
                nc.put("info", etI.getText().toString().trim());
                nc.put("notes", etNt.getText().toString().trim());
                nc.put("photoUri", "");
                nc.put("photoBitmap", "");
                nc.put("frameMask", "");
                nc.put("frameTop", "");
                nc.put("nameColor", Color.BLACK);
            } catch (Exception e) {
                e.printStackTrace();
            }
            cellDataList.add(nc);
            displayList.add(nc);
            adapter.notifyDataSetChanged();
            dataChanged = true;
            updateStats();
            Toast.makeText(this, "Cell added!", Toast.LENGTH_SHORT).show();
        });
        b.setNegativeButton("Cancel", null).show();
    }

    private void showBulkEdit() {
        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Bulk Edit");
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(18), dp(14), dp(18), dp(14));
        TextView l1 = new TextView(this);
        l1.setText("Info suffix:");
        root.addView(l1);
        EditText etS = new EditText(this);
        etS.setText("%");
        root.addView(etS);
        TextView l2 = new TextView(this);
        l2.setText("Name prefix:");
        l2.setPadding(0, dp(8), 0, dp(3));
        root.addView(l2);
        EditText etP = new EditText(this);
        etP.setHint("e.g. Mr.");
        root.addView(etP);
        b.setView(root);
        b.setPositiveButton("Apply", (d, w) -> {
            String suf = etS.getText().toString();
            String pre = etP.getText().toString();
            for (JSONObject o : cellDataList) {
                try {
                    if (!suf.isEmpty()) {
                        String inf = o.optString("info", "");
                        if (!inf.isEmpty() && !inf.endsWith(suf)) o.put("info", inf + suf);
                    }
                    if (!pre.isEmpty()) {
                        String nm = o.optString("name", "");
                        if (!nm.isEmpty() && !nm.startsWith(pre)) o.put("name", pre + nm);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            dataChanged = true;
            applyFilterAndSearch();
            updateStats();
        });
        b.setNegativeButton("Cancel", null).show();
    }

    private void confirmClearAll() {
        new AlertDialog.Builder(this).setTitle("Clear All").setMessage("All cells clear?")
                .setPositiveButton("Clear", (d, w) -> {
                    for (JSONObject o : cellDataList) {
                        try {
                            o.put("photoUri", "");
                            o.put("photoBitmap", "");
                            o.put("name", "");
                            o.put("info", "");
                            o.put("notes", "");
                            o.put("frameMask", "");
                            o.put("frameTop", "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    dataChanged = true;
                    applyFilterAndSearch();
                    updateStats();
                }).setNegativeButton("Cancel", null).show();
    }

    private void showFullPhotoDialog(JSONObject obj, String name, String info) {
        android.app.Dialog dlg = new android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        RelativeLayout root = new RelativeLayout(this);
        root.setBackgroundColor(Color.BLACK);
        dlg.setContentView(root);
        ImageView big = new ImageView(this);
        big.setScaleType(ImageView.ScaleType.FIT_CENTER);
        root.addView(big, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        String b64 = obj.optString("photoBitmap", "");
        String uri = obj.optString("photoUri", "");
        if (!b64.isEmpty()) {
            try {
                byte[] by = android.util.Base64.decode(b64, android.util.Base64.DEFAULT);
                Bitmap bm = BitmapFactory.decodeByteArray(by, 0, by.length);
                if (bm != null) big.setImageBitmap(bm);
            } catch (Exception ignore) {
            }
        } else if (!uri.isEmpty()) Glide.with(this).load(Uri.parse(uri)).into(big);
        else big.setImageResource(android.R.drawable.ic_menu_gallery);
        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.VERTICAL);
        bar.setBackgroundColor(Color.parseColor("#AA000000"));
        bar.setPadding(dp(18), dp(12), dp(18), dp(34));
        RelativeLayout.LayoutParams bLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        bLp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        root.addView(bar, bLp);
        TextView tvN = new TextView(this);
        tvN.setText(name.isEmpty() ? "(No Name)" : name);
        tvN.setTextColor(Color.WHITE);
        tvN.setTextSize(20);
        tvN.setTypeface(null, Typeface.BOLD);
        bar.addView(tvN);
        TextView tvI = new TextView(this);
        tvI.setText(info.isEmpty() ? "—" : info);
        tvI.setTextColor(Color.parseColor("#FF9800"));
        tvI.setTextSize(16);
        tvI.setTypeface(null, Typeface.BOLD);
        bar.addView(tvI);
        String notes = obj.optString("notes", "");
        if (!notes.isEmpty()) {
            TextView tvNt = new TextView(this);
            tvNt.setText("Note: " + notes);
            tvNt.setTextColor(Color.parseColor("#CCCCCC"));
            tvNt.setTextSize(12);
            bar.addView(tvNt);
        }
        TextView x = new TextView(this);
        x.setText("X");
        x.setTextColor(Color.WHITE);
        x.setTextSize(22);
        x.setPadding(dp(14), dp(44), dp(14), dp(14));
        RelativeLayout.LayoutParams xLp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        xLp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        xLp.addRule(RelativeLayout.ALIGN_PARENT_END);
        root.addView(x, xLp);
        x.setOnClickListener(v -> dlg.dismiss());
        big.setOnClickListener(v -> dlg.dismiss());
        dlg.show();
    }

    private void exportCsv() {
        try {
            StringBuilder sb = new StringBuilder("No,Name,Percentage,Notes,Photo,Mask\n");
            for (int i = 0; i < cellDataList.size(); i++) {
                JSONObject o = cellDataList.get(i);
                boolean hp = !o.optString("photoUri", "").isEmpty() || !o.optString("photoBitmap", "").isEmpty();
                boolean hm = !o.optString("frameMask", "").isEmpty() || !o.optString("frameTop", "").isEmpty();
                sb.append(i + 1).append(",").append(o.optString("name", "")).append(",").append(o.optString("info", "")).append(",").append(o.optString("notes", "")).append(",").append(hp ? "Yes" : "No").append(",").append(hm ? "Yes" : "No").append("\n");
            }
            String fn = "grid_" + System.currentTimeMillis() + ".csv";
            java.io.File f = new java.io.File(getExternalFilesDir(null), fn);
            java.io.FileWriter fw = new java.io.FileWriter(f);
            fw.write(sb.toString());
            fw.close();
            Uri fu = androidx.core.content.FileProvider.getUriForFile(this, getPackageName() + ".provider", f);
            Intent si = new Intent(Intent.ACTION_SEND);
            si.setType("text/csv");
            si.putExtra(Intent.EXTRA_STREAM, fu);
            si.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(si, "CSV Share"));
        } catch (Exception e) {
            Toast.makeText(this, "CSV error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void applySort(int si) {
        for (int j = 0; j < sortBtns.length; j++) {
            GradientDrawable g = new GradientDrawable();
            g.setCornerRadius(dp(12));
            boolean sel = j == si;
            g.setColor(sel ? Color.parseColor("#1565C0") : Color.parseColor("#E3F2FD"));
            sortBtns[j].setBackground(g);
            sortBtns[j].setTextColor(sel ? Color.WHITE : Color.parseColor("#1565C0"));
        }
        switch (si) {
            case 0:
                displayList.clear();
                displayList.addAll(cellDataList);
                break;
            case 1:
                displayList.sort((a, b) -> a.optString("name", "").compareToIgnoreCase(b.optString("name", "")));
                break;
            case 2:
                displayList.sort((a, b) -> b.optString("name", "").compareToIgnoreCase(a.optString("name", "")));
                break;
            case 3:
                displayList.sort((a, b) -> Float.compare(pct(a), pct(b)));
                break;
            case 4:
                displayList.sort((a, b) -> Float.compare(pct(b), pct(a)));
                break;
        }
        adapter.notifyDataSetChanged();
    }

    private void updateStats() {
        int total = cellDataList.size(), filled = 0, masked = 0;
        float sum = 0f;
        for (JSONObject o : cellDataList) {
            if (!o.optString("photoUri", "").isEmpty() || !o.optString("photoBitmap", "").isEmpty())
                filled++;
            if (!o.optString("frameMask", "").isEmpty() || !o.optString("frameTop", "").isEmpty())
                masked++;
            sum += pct(o);
        }
        if (tvStats != null)
            tvStats.setText("Total:" + total + " Photo:" + filled + " Mask:" + masked + " Sum:" + String.format("%.1f", sum) + "%");
    }

    @Override
    public void onBackPressed() {
        if (dataChanged) {
            new AlertDialog.Builder(this).setTitle("Save?").setPositiveButton("Save", (d, w) -> finishWithResult("UPDATE")).setNegativeButton("Discard", (d, w) -> finishWithResult("NO_CHANGE")).setNeutralButton("Cancel", null).show();
        } else finishWithResult("NO_CHANGE");
    }

    private void    finishWithResult(String action) {
        Intent r = new Intent();
        r.putExtra(RESULT_GRID_INDEX, gridIndex);
        r.putExtra(RESULT_ACTION, action);
        if ("UPDATE".equals(action)) {
            int total = cellDataList.size();
            int nr = total == 0 ? rows : (int) Math.ceil((float) total / cols);

            JSONArray arr = new JSONArray();
            for (JSONObject o : cellDataList) {
                try {
                    // ✅ originalBitmap વગર copy બનાવો
                    JSONObject clean = new JSONObject();
                    clean.put("name",       o.optString("name", ""));
                    clean.put("info",       o.optString("info", ""));
                    clean.put("notes",      o.optString("notes", ""));
                    clean.put("photoUri",   o.optString("photoUri", ""));
                    clean.put("photoBitmap",o.optString("photoBitmap", ""));
                    clean.put("frameMask",  o.optString("frameMask", ""));
                    clean.put("frameTop",   o.optString("frameTop", ""));
                    clean.put("nameColor", String.valueOf(o.optInt("nameColor", Color.BLACK)));
                    // originalBitmap intentionally SKIP — memory only
                    arr.put(clean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            r.putExtra(RESULT_CELLS_JSON, arr.toString());
            r.putExtra(EXTRA_ROWS, nr);
            r.putExtra(EXTRA_COLS, cols);
            r.putExtra(EXTRA_SHAPE, shape);
            r.putExtra(EXTRA_CELL_SIZE, cellSizePx);
            r.putExtra(EXTRA_SHOW_NAME, showName);
            r.putExtra(EXTRA_SHOW_INFO, showInfo);
            setResult(RESULT_OK, r);
        } else {
            setResult(RESULT_CANCELED, r);
        }
        finish();
    }

    // ── BITMAP HELPERS ───────────────────────────────────────
    private Bitmap mergePhotoMaskTop(Bitmap photo, Bitmap mask, @Nullable Bitmap top, int w, int h) {
        if (w <= 0) w = mask.getWidth();
        if (h <= 0) h = mask.getHeight();
        Bitmap ps = Bitmap.createScaledBitmap(photo, w, h, true);
        Bitmap ms = Bitmap.createScaledBitmap(mask, w, h, true);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        boolean t = hasTransparentPixels(ms);
        Bitmap masked = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas mc = new Canvas(masked);
        if (t) {
            mc.drawBitmap(ps, 0, 0, p);
            Paint di = new Paint(Paint.ANTI_ALIAS_FLAG);
            di.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            mc.drawBitmap(ms, 0, 0, di);
            di.setXfermode(null);
        } else {
            mc.drawBitmap(ms, 0, 0, p);
            Paint si = new Paint(Paint.ANTI_ALIAS_FLAG);
            si.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            mc.drawBitmap(ps, 0, 0, si);
            si.setXfermode(null);
        }
        ps.recycle();
        ms.recycle();
        if (top == null) return masked;
        Bitmap ts2 = Bitmap.createScaledBitmap(top, w, h, true);
        Bitmap result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas rc = new Canvas(result);
        rc.drawBitmap(masked, 0, 0, p);
        rc.drawBitmap(ts2, 0, 0, p);
        masked.recycle();
        ts2.recycle();
        return result;
    }

    private Bitmap clipToShape(Bitmap src, int size) {
        Bitmap result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap scaled = Bitmap.createScaledBitmap(src, size, size, true);
        if ("CIRCLE".equals(shape)) {
            canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(scaled, 0, 0, paint);
        } else if ("ROUNDED".equals(shape)) {
            float r = size * 0.12f;
            canvas.drawRoundRect(0, 0, size, size, r, r, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(scaled, 0, 0, paint);
        } else canvas.drawBitmap(scaled, 0, 0, paint);
        paint.setXfermode(null);
        scaled.recycle();
        return result;
    }

    private boolean hasTransparentPixels(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();
        int[] cx = {0, w - 1, w / 2, w / 4, 3 * w / 4};
        int[] cy = {0, h - 1, h / 2, h / 4, 3 * h / 4};
        for (int i = 0; i < cx.length; i++) {
            if (Color.alpha(bmp.getPixel(cx[i], cy[i])) < 255) return true;
        }
        return false;
    }

    private void applyThumbShape(ImageView iv, int size) {
        GradientDrawable bg = new GradientDrawable();
        bg.setColor(Color.parseColor("#EEEEEE"));
        if ("CIRCLE".equals(shape)) {
            bg.setShape(GradientDrawable.OVAL);
            iv.setClipToOutline(true);
            iv.setOutlineProvider(new android.view.ViewOutlineProvider() {
                @Override
                public void getOutline(View v, android.graphics.Outline o) {
                    o.setOval(0, 0, v.getWidth(), v.getHeight());
                }
            });
        } else if ("ROUNDED".equals(shape)) {
            bg.setCornerRadius(size * 0.15f);
            iv.setClipToOutline(true);
            iv.setOutlineProvider(new android.view.ViewOutlineProvider() {
                @Override
                public void getOutline(View v, android.graphics.Outline o) {
                    o.setRoundRect(0, 0, v.getWidth(), v.getHeight(), size * 0.15f);
                }
            });
        }
        iv.setBackground(bg);
    }

    private void loadThumbPhoto(ImageView iv, String b64, String uri) {
        if (!b64.isEmpty()) {
            try {
                byte[] by = android.util.Base64.decode(b64, android.util.Base64.DEFAULT);
                Bitmap bm = BitmapFactory.decodeByteArray(by, 0, by.length);
                if (bm != null) {
                    iv.setImageBitmap(bm);
                    iv.setBackground(null);
                    return;
                }
            } catch (Exception ignore) {
            }
        }
        if (!uri.isEmpty()) {
            Glide.with(this).load(Uri.parse(uri)).centerCrop().into(iv);
            iv.setBackground(null);
            return;
        }
        iv.setImageResource(android.R.drawable.ic_menu_gallery);
    }

    private Button makeDlgBtn(String text, int bgColor) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(13);
        b.setBackgroundColor(bgColor);
        return b;
    }

    private LinearLayout.LayoutParams makeBtnLp() {
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        lp.setMargins(dp(5), 0, dp(5), 0);
        return lp;
    }

    private float fingerSpacing(MotionEvent e) {
        float x = e.getX(0) - e.getX(1);
        float y = e.getY(0) - e.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void fingerMidPoint(float[] mX, float[] mY, MotionEvent e) {
        mX[0] = (e.getX(0) + e.getX(1)) / 2f;
        mY[0] = (e.getY(0) + e.getY(1)) / 2f;
    }

    private int infoColor(String s) {
        try {
            float p = Float.parseFloat(s.replace("%", "").replace(",", ".").trim());
            if (p >= 75f) return Color.parseColor("#2E7D32");
            if (p >= 50f) return Color.parseColor("#1565C0");
            if (p >= 25f) return Color.parseColor("#FF9800");
            return Color.parseColor("#C62828");
        } catch (Exception e) {
            return Color.parseColor("#FF9800");
        }
    }

    private float pct(JSONObject o) {
        try {
            return Float.parseFloat(o.optString("info", "0").replace("%", "").replace(",", ".").trim());
        } catch (Exception e) {
            return 0f;
        }
    }

    private String bitmapToBase64(Bitmap bmp) {
        try {
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return android.util.Base64.encodeToString(baos.toByteArray(), android.util.Base64.DEFAULT);
        } catch (Exception e) {
            return "";
        }
    }

    private int dp(int val) {
        return (int) (val * getResources().getDisplayMetrics().density + 0.5f);
    }
}