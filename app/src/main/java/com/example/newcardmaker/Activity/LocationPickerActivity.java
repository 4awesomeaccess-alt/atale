package com.example.newcardmaker.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.newcardmaker.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class LocationPickerActivity extends AppCompatActivity {

    // ── Result keys
    public static final String RESULT_MAP_URL     = "result_map_url";
    public static final String RESULT_LOC_NAME    = "result_loc_name";
    public static final String RESULT_CUSTOM_IMG  = "result_custom_img";
    public static final String RESULT_ICON_RES_ID = "result_icon_res_id";

    private static final int REQ_GALLERY = 701;

    // ── location_1.png to location_16.png
    private static final int[] ICON_RES_IDS = {
            R.drawable.location_1,  R.drawable.location_2,
            R.drawable.location_3,  R.drawable.location_4,
            R.drawable.location_5,  R.drawable.location_6,
            R.drawable.location_7,  R.drawable.location_8,
            R.drawable.location_9,  R.drawable.location_10,
            R.drawable.location_11, R.drawable.location_12,
            R.drawable.location_13, R.drawable.location_14,
            R.drawable.location_15, R.drawable.location_16,
    };

    private static final String[] ICON_NAMES = {
            "Style 1",  "Style 2",  "Style 3",  "Style 4",
            "Style 5",  "Style 6",  "Style 7",  "Style 8",
            "Style 9",  "Style 10", "Style 11", "Style 12",
            "Style 13", "Style 14", "Style 15", "Style 16",
    };

    // ── Views
    private View         panelUrl, panelIcon, panelGallery;
    private TextView     tabUrl, tabIcon, tabGallery;
    private EditText     etUrl;
    private TextView     tvUrlPreview;
    private RecyclerView rvIcons;
    private ImageView    ivGalleryPreview;
    private LinearLayout overlayPickHint;
    private TextView     tvGalleryHint, btnGalleryAdd;

    // ── State
    private int    selectedIconPos  = -1;
    private String galleryImagePath = "";
    private IconAdapter iconAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);

        bindViews();

        // ── Back
        findViewById(R.id.btn_loc_back)
                .setOnClickListener(v -> finish());

        // ── Tab clicks
        tabUrl.setOnClickListener(v     -> switchTab(0));
        tabIcon.setOnClickListener(v    -> switchTab(1));
        tabGallery.setOnClickListener(v -> switchTab(2));

        setupUrlTab();
        setupIconTab();
        setupGalleryTab();

        // ── Default tab = Icons (tab index 1)
        switchTab(1);
    }

    // ────────────────────────────────────────
    private void bindViews() {
        panelUrl         = findViewById(R.id.panel_url);
        panelIcon        = findViewById(R.id.panel_icon);
        panelGallery     = findViewById(R.id.panel_gallery);
        tabUrl           = findViewById(R.id.tab_url);
        tabIcon          = findViewById(R.id.tab_icon);
        tabGallery       = findViewById(R.id.tab_gallery);
        etUrl            = findViewById(R.id.et_location_url);
        tvUrlPreview     = findViewById(R.id.tv_url_preview);
        rvIcons          = findViewById(R.id.rv_icons);
        ivGalleryPreview = findViewById(R.id.iv_gallery_preview);
        overlayPickHint  = findViewById(R.id.overlay_pick_hint);
        tvGalleryHint    = findViewById(R.id.tv_gallery_hint);
        btnGalleryAdd    = findViewById(R.id.btn_gallery_add);
    }

    // ────────────────────────────────────────
    // TAB SWITCH
    // ────────────────────────────────────────
    private void switchTab(int idx) {
        int active       = Color.parseColor("#E3F2FD");
        int inactive     = Color.WHITE;
        int activeText   = Color.parseColor("#1565C0");
        int inactiveText = Color.parseColor("#888888");

        // Tab backgrounds + text
        tabUrl.setBackgroundColor(
                idx == 0 ? active : inactive);
        tabUrl.setTextColor(
                idx == 0 ? activeText : inactiveText);
        tabUrl.setTypeface(idx == 0
                ? android.graphics.Typeface.DEFAULT_BOLD
                : android.graphics.Typeface.DEFAULT);

        tabIcon.setBackgroundColor(
                idx == 1 ? active : inactive);
        tabIcon.setTextColor(
                idx == 1 ? activeText : inactiveText);
        tabIcon.setTypeface(idx == 1
                ? android.graphics.Typeface.DEFAULT_BOLD
                : android.graphics.Typeface.DEFAULT);

        tabGallery.setBackgroundColor(
                idx == 2 ? active : inactive);
        tabGallery.setTextColor(
                idx == 2 ? activeText : inactiveText);
        tabGallery.setTypeface(idx == 2
                ? android.graphics.Typeface.DEFAULT_BOLD
                : android.graphics.Typeface.DEFAULT);

        // Panel visibility
        panelUrl.setVisibility(
                idx == 0 ? View.VISIBLE : View.GONE);
        panelIcon.setVisibility(
                idx == 1 ? View.VISIBLE : View.GONE);
        panelGallery.setVisibility(
                idx == 2 ? View.VISIBLE : View.GONE);
    }

    // ────────────────────────────────────────
    // URL TAB
    // ────────────────────────────────────────
    private void setupUrlTab() {

        // ADD button
        findViewById(R.id.btn_loc_add_url)
                .setOnClickListener(v -> {
                    String url = etUrl.getText()
                            .toString().trim();
                    if (url.isEmpty()) {
                        Toast.makeText(this,
                                "Maps link paste કરો",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!url.startsWith("http")) {
                        Toast.makeText(this,
                                "Valid URL (https://...) paste કરો",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    returnUrlResult(url);
                });

        // URL live preview
        etUrl.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,
                                          int st, int c, int a) {}
            public void onTextChanged(CharSequence s,
                                      int st, int b, int c) {
                String url = s.toString().trim();
                if (url.startsWith("http")) {
                    tvUrlPreview.setVisibility(View.VISIBLE);
                    tvUrlPreview.setText("✅ " + url);
                } else {
                    tvUrlPreview.setVisibility(View.GONE);
                }
            }
            public void afterTextChanged(Editable s) {}
        });

        // Open Google Maps button
        findViewById(R.id.btn_open_gmaps)
                .setOnClickListener(v -> {
                    try {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(
                                        "https://maps.google.com")));
                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Maps app નથી",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // ────────────────────────────────────────
    // ICON TAB — location_1 to location_16
    // ────────────────────────────────────────
    private void setupIconTab() {
        // 4 columns grid
        rvIcons.setLayoutManager(
                new GridLayoutManager(this, 4));
        iconAdapter = new IconAdapter();
        rvIcons.setAdapter(iconAdapter);
    }

    // ────────────────────────────────────────
    // GALLERY TAB
    // ────────────────────────────────────────
    private void setupGalleryTab() {

        // Preview box tap = open gallery
        ivGalleryPreview.setOnClickListener(
                v -> openGallery());
        overlayPickHint.setOnClickListener(
                v -> openGallery());

        // Pick button
        findViewById(R.id.btn_pick_gallery)
                .setOnClickListener(v -> openGallery());

        // Add button
        btnGalleryAdd.setOnClickListener(v -> {
            if (galleryImagePath.isEmpty()) {
                Toast.makeText(this,
                        "પહેલા image pick કરો",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            returnGalleryResult(galleryImagePath);
        });
    }

    // ── Open gallery picker
    private void openGallery() {
        Intent intent = new Intent(
                Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(
                Intent.createChooser(intent,
                        "Location Image Select"),
                REQ_GALLERY);
    }

    @Override
    protected void onActivityResult(int req, int res,
                                    @Nullable Intent data) {
        super.onActivityResult(req, res, data);

        if (req == REQ_GALLERY
                && res == RESULT_OK
                && data != null) {

            Uri uri = data.getData();
            if (uri == null) return;

            try {
                // ── Copy to cache
                InputStream is = getContentResolver()
                        .openInputStream(uri);
                File f = new File(getCacheDir(),
                        "loc_img_" +
                                System.currentTimeMillis()
                                + ".jpg");
                FileOutputStream fos =
                        new FileOutputStream(f);
                byte[] buf = new byte[4096];
                int len;
                while ((len = is.read(buf)) != -1)
                    fos.write(buf, 0, len);
                fos.close();
                is.close();

                galleryImagePath = f.getAbsolutePath();

                // ── Show preview
                overlayPickHint.setVisibility(View.GONE);
                Glide.with(this)
                        .load(f)
                        .centerCrop()
                        .into(ivGalleryPreview);

                tvGalleryHint.setText(
                        "✅ Image selected!");
                tvGalleryHint.setTextColor(
                        Color.parseColor("#2E7D32"));

                // ── Show Add button
                btnGalleryAdd.setVisibility(View.VISIBLE);

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this,
                        "Image load error",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ────────────────────────────────────────
    // RETURN RESULTS
    // ────────────────────────────────────────
    private void returnUrlResult(String url) {
        Intent r = new Intent();
        r.putExtra(RESULT_MAP_URL,  url);
        r.putExtra(RESULT_LOC_NAME, "Location");
        setResult(RESULT_OK, r);
        finish();
    }

    private void returnIconResult(int resId, String name) {
        Intent r = new Intent();
        r.putExtra(RESULT_ICON_RES_ID, resId);
        r.putExtra(RESULT_LOC_NAME,    name);
        setResult(RESULT_OK, r);
        finish();
    }

    private void returnGalleryResult(String path) {
        Intent r = new Intent();
        r.putExtra(RESULT_CUSTOM_IMG, path);
        r.putExtra(RESULT_LOC_NAME,   "Custom Icon");
        setResult(RESULT_OK, r);
        finish();
    }

    // ════════════════════════════════════════
    // ICON ADAPTER — location_1 to location_16
    // ════════════════════════════════════════
    private class IconAdapter extends
            RecyclerView.Adapter<IconAdapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(
                @NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(
                            parent.getContext())
                    .inflate(R.layout.item_location_icon,
                            parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(
                @NonNull VH holder, int position) {

            int    resId = ICON_RES_IDS[position];
            String name  = ICON_NAMES[position];

            // ── Load drawable image
            holder.ivIcon.setImageResource(resId);
            holder.tvName.setText(name);

            // ── Selected highlight border
            GradientDrawable bg = new GradientDrawable();
            bg.setCornerRadius(14f);
            if (selectedIconPos == position) {
                // ── Selected = blue border
                bg.setColor(Color.parseColor("#E3F2FD"));
                bg.setStroke(3,
                        Color.parseColor("#1565C0"));
                holder.tvName.setTextColor(
                        Color.parseColor("#1565C0"));
            } else {
                // ── Normal = light gray
                bg.setColor(Color.WHITE);
                bg.setStroke(1,
                        Color.parseColor("#E0E0E0"));
                holder.tvName.setTextColor(
                        Color.parseColor("#555555"));
            }
            holder.itemView.setBackground(bg);

            // ── Click = highlight + add
            holder.itemView.setOnClickListener(v -> {
                selectedIconPos = position;
                notifyDataSetChanged(); // refresh borders
                returnIconResult(resId, name);
            });
        }

        @Override
        public int getItemCount() {
            // ── Always 16
            return ICON_RES_IDS.length;
        }

        class VH extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView  tvName;

            VH(@NonNull View v) {
                super(v);
                ivIcon = v.findViewById(R.id.iv_icon);
                tvName = v.findViewById(R.id.tv_icon_name);
            }
        }
    }
}
