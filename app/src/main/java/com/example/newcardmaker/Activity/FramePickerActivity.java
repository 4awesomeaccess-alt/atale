package com.example.newcardmaker.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.bumptech.glide.Glide;
import com.example.newcardmaker.R;
import com.example.newcardmaker.invite_online_database.invite_AppConstants;
import com.example.newcardmaker.invite_online_database.invite_EndlessRecyclerViewScrollListener1;
import com.example.newcardmaker.invite_online_database.invite_Methods;
import com.example.newcardmaker.invite_photo_frame;

import yuku.ambilwarna.AmbilWarnaDialog;

public class FramePickerActivity extends AppCompatActivity {

    // ══════════════════════════════════════════════
    // INTENT KEYS — MainActivity ને return
    // ══════════════════════════════════════════════
    public static final String EXTRA_FRAME_URL           = "frame_url";
    public static final String EXTRA_MASK_URL            = "mask_url";
    public static final String EXTRA_IMAGE_TOP_URL       = "image_top_url";
    public static final String EXTRA_OVERLAY_COLOR       = "overlay_color";
    public static final String EXTRA_USER_PHOTO_URI      = "user_photo_uri";
    public static final String EXTRA_BORDER_COLOR        = "border_color";
    public static final String EXTRA_BORDER_WIDTH        = "border_width";
    public static final String EXTRA_HAS_PHOTO           = "has_photo";
    public static final String EXTRA_ADJUSTED_MERGED     = "adjustedMergedPath";
    public static final String EXTRA_ADJUSTED_MASKED     = "adjustedUserMaskedPath";

    // ══════════════════════════════════════════════
    // VIEWS
    // ══════════════════════════════════════════════
    private ImageView    selectedFramePreview;
    private ImageView    selectedPhotoPreview;
    private TextView     lblStep;
    private Button       btnAddFrame;
    private View         colorPreviewBox;
    private TextView     lblColor;
    private View         borderColorBox;
    private TextView     lblBorderColor;
    private TextView     lblBorderWidth;
    private LinearLayout colorBtnRow;
    private LinearLayout borderColorBtnRow;
    private Button       btnAdjustPhoto;

    // ══════════════════════════════════════════════
    // DATA
    // ══════════════════════════════════════════════
    private String selectedFrameUrl     = "";
    private String selectedMaskUrl      = "";
    private String selectedImageTopUrl  = "";
    private int    selectedOverlayColor = Color.TRANSPARENT;
    private Uri    selectedPhotoUri     = null;
    private int    selectedBorderColor  = Color.TRANSPARENT;
    private int    selectedBorderWidth  = 0;

    // PhotoAdjustActivity result paths
    private String adjustedMergedPath     = "";
    private String adjustedUserMaskedPath = "";

    // ══════════════════════════════════════════════
    // LAUNCHERS
    // ══════════════════════════════════════════════
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> photoAdjustLauncher;

    // ══════════════════════════════════════════════
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_picker);

        // ── Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Photo Frame પસંદ કરો");
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // ── Views bind
        lblStep              = findViewById(R.id.lblStep);
        selectedFramePreview = findViewById(R.id.selectedFramePreview);
        selectedPhotoPreview = findViewById(R.id.selectedPhotoPreview);
        colorPreviewBox      = findViewById(R.id.colorPreviewBox);
        lblColor             = findViewById(R.id.lblColor);
        colorBtnRow          = findViewById(R.id.colorBtnRow);
        borderColorBox       = findViewById(R.id.borderColorBox);
        lblBorderColor       = findViewById(R.id.lblBorderColor);
        lblBorderWidth       = findViewById(R.id.lblBorderWidth);
        borderColorBtnRow    = findViewById(R.id.borderColorBtnRow);
        btnAddFrame          = findViewById(R.id.btnAddFrame);
        btnAdjustPhoto       = findViewById(R.id.btnAdjustPhoto);

        ProgressBar progressBar = findViewById(R.id.progressBar);
        RecyclerView frameRV    = findViewById(R.id.frameRV);

        // ── Replace mode check (from showStickerToolbar)
        boolean isReplaceMode = getIntent().getBooleanExtra("isReplacePhotoMode", false);
        if (isReplaceMode) {
            lblStep.setText("📷 New Photo select કરો — Frame optional");
            btnAddFrame.setText("✅ Photo Set કરો");
            btnAddFrame.setEnabled(true);
            btnAddFrame.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(
                            Color.parseColor("#1565C0")));
        }

        // ══════════════════════════════════════════
        // REGISTER LAUNCHERS
        // ══════════════════════════════════════════

        // ── PhotoAdjust launcher
        photoAdjustLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null) {

                        adjustedMergedPath = result.getData()
                                .getStringExtra(PhotoAdjustActivity.RESULT_MERGED_PATH);
                        adjustedUserMaskedPath = result.getData()
                                .getStringExtra(PhotoAdjustActivity.RESULT_USER_MASKED_PATH);

                        if (adjustedMergedPath != null
                                && !adjustedMergedPath.isEmpty()) {

                            // ── Show merged preview in frame preview
                            Bitmap bmp = BitmapFactory.decodeFile(adjustedMergedPath);
                            if (bmp != null) {
                                selectedFramePreview.setImageBitmap(bmp);
                            }

                            // ── Show user photo thumbnail
                            Bitmap userBmp = BitmapFactory
                                    .decodeFile(adjustedUserMaskedPath);
                            if (userBmp != null
                                    && selectedPhotoPreview != null) {
                                selectedPhotoPreview.setImageBitmap(userBmp);
                                selectedPhotoPreview.setVisibility(View.VISIBLE);
                            }

                            Toast.makeText(this,
                                    "✅ Photo adjust apply!",
                                    Toast.LENGTH_SHORT).show();

                            returnResult();

                        }
                    }
                });

        // ── Gallery launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK
                            && result.getData() != null) {

                        selectedPhotoUri = result.getData().getData();
                        if (selectedPhotoUri == null) return;

                        // ── Reset old adjust
                        adjustedMergedPath     = "";
                        adjustedUserMaskedPath = "";

                        // ── Thumbnail show
                        Glide.with(this)
                                .load(selectedPhotoUri)
                                .centerCrop()
                                .into(selectedPhotoPreview);
                        selectedPhotoPreview.setVisibility(View.VISIBLE);

                        updateStepLabel();
                        updateAddButton();
                        updateAdjustButton();

                        // ── Frame already selected → auto open adjust
                        if (!selectedFrameUrl.isEmpty()
                                && !selectedMaskUrl.isEmpty()) {
                            Toast.makeText(this,
                                    "Photo select! Adjust ખૂલે છે...",
                                    Toast.LENGTH_SHORT).show();
                            selectedPhotoPreview.postDelayed(
                                    this::openPhotoAdjust, 400);
                        } else {
                            Toast.makeText(this,
                                    "Photo select! Frame select કરો",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // ══════════════════════════════════════════
        // BUTTON CLICKS
        // ══════════════════════════════════════════

        // ── Photo Select
        Button btnSelectPhoto = findViewById(R.id.btnSelectPhoto);
        if (btnSelectPhoto != null) {
            btnSelectPhoto.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                galleryLauncher.launch(
                        Intent.createChooser(intent, "Photo Select"));
            });
        }

        // ── Remove Photo
        Button btnRemovePhoto = findViewById(R.id.btnRemovePhoto);
        if (btnRemovePhoto != null) {
            btnRemovePhoto.setOnClickListener(v -> {
                selectedPhotoUri       = null;
                adjustedMergedPath     = "";
                adjustedUserMaskedPath = "";
                selectedPhotoPreview.setImageResource(
                        android.R.drawable.ic_menu_gallery);
                selectedPhotoPreview.setVisibility(View.GONE);
                // Frame preview restore
                if (!selectedFrameUrl.isEmpty()) {
                    Glide.with(this).load(selectedFrameUrl)
                            .into(selectedFramePreview);
                }
                updateStepLabel();
                updateAddButton();
                updateAdjustButton();
                Toast.makeText(this,
                        "Photo remove થઈ ગઈ", Toast.LENGTH_SHORT).show();
            });
        }

        // ── Adjust Photo Button
        if (btnAdjustPhoto != null) {
            btnAdjustPhoto.setOnClickListener(v -> {
                if (selectedPhotoUri == null) {
                    Toast.makeText(this,
                            "પહેલા Photo select કરો",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                if (selectedFrameUrl.isEmpty()) {
                    Toast.makeText(this,
                            "પહેલા Frame select કરો",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                openPhotoAdjust();
            });
            btnAdjustPhoto.setVisibility(View.GONE); // default hidden
        }

        // ── Add Frame Button
        btnAddFrame.setOnClickListener(v -> returnResult());

        // ── Build color rows
        buildColorRow(colorBtnRow, false);
        buildColorRow(borderColorBtnRow, true);

        // ── Border Width SeekBar
        SeekBar seekBorderWidth = findViewById(R.id.seekBorderWidth);
        if (seekBorderWidth != null) {
            seekBorderWidth.setMax(30);
            seekBorderWidth.setProgress(0);
            seekBorderWidth.setOnSeekBarChangeListener(
                    new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar s,
                                                      int p, boolean f) {
                            selectedBorderWidth = p;
                            if (lblBorderWidth != null)
                                lblBorderWidth.setText(
                                        "Border Width: " + p + "px");
                        }
                        @Override public void onStartTrackingTouch(SeekBar s) {}
                        @Override public void onStopTrackingTouch(SeekBar s) {}
                    });
        }

        // ── Load Frames
        loadFramesDirectly(frameRV, progressBar);

        // ── Frame Click
        frameRV.addOnItemTouchListener(new invite_AppConstants.RecyclerTouchListener(
                this, frameRV,
                new invite_AppConstants.RecyclerTouchListener.ClickListener() {

                    @Override
                    public void onClick(View view, int position) {
                        if (invite_photo_frame.frame_arrayList == null
                                || invite_photo_frame.frame_arrayList.isEmpty())
                            return;

                        selectedFrameUrl    = invite_photo_frame.frame_arrayList
                                .get(position).getImageBig();
                        selectedMaskUrl     = invite_photo_frame.frame_arrayList
                                .get(position).getcard_background();
                        selectedImageTopUrl = invite_photo_frame.frame_arrayList
                                .get(position).getemail_icon();

                        // ── Frame preview (photo adjust ન થઈ હોય ત્યાં સુધી)
                        if (adjustedMergedPath.isEmpty()) {
                            Glide.with(FramePickerActivity.this)
                                    .load(selectedFrameUrl)
                                    .placeholder(android.R.drawable.ic_menu_gallery)
                                    .into(selectedFramePreview);
                        }

                        updateStepLabel();
                        updateAddButton();
                        updateAdjustButton();

                        // ── Photo already selected → auto adjust
                        if (selectedPhotoUri != null
                                && !selectedMaskUrl.isEmpty()) {
                            Toast.makeText(FramePickerActivity.this,
                                    "Frame select! Adjust ખૂલે છે...",
                                    Toast.LENGTH_SHORT).show();
                            selectedFramePreview.postDelayed(
                                    () -> openPhotoAdjust(), 400);
                        } else {
                            Toast.makeText(FramePickerActivity.this,
                                    "✅ Frame select! Photo select કરો અથવા Add કરો",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onLongClick(View view, int position) {}
                }));
    }

    // ══════════════════════════════════════════════
    // OPEN PHOTO ADJUST ACTIVITY
    // ══════════════════════════════════════════════
    private void openPhotoAdjust() {
        if (selectedPhotoUri == null) {
            Toast.makeText(this,
                    "Photo select નથી", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedMaskUrl.isEmpty()) {
            Toast.makeText(this,
                    "Frame select નથી", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, PhotoAdjustActivity.class);
        intent.putExtra(PhotoAdjustActivity.EXTRA_PHOTO_URI,
                selectedPhotoUri.toString());
        intent.putExtra(PhotoAdjustActivity.EXTRA_MASK_URL,
                selectedMaskUrl);
        intent.putExtra(PhotoAdjustActivity.EXTRA_TOP_URL,
                selectedImageTopUrl);
        intent.putExtra(PhotoAdjustActivity.EXTRA_OVERLAY_COLOR,
                selectedOverlayColor);
        photoAdjustLauncher.launch(intent);
    }

    // ══════════════════════════════════════════════
    // UI HELPERS
    // ══════════════════════════════════════════════
    private void updateStepLabel() {
        boolean hasFrame   = !selectedFrameUrl.isEmpty();
        boolean hasPhoto   = selectedPhotoUri != null;
        boolean hasAdjust  = !adjustedMergedPath.isEmpty();

        if (hasAdjust) {
            lblStep.setText("✅ Photo adjust ready! Add Frame કરો");
        } else if (hasFrame && hasPhoto) {
            lblStep.setText("✅ Frame + Photo ready! Adjust કરો અથવા Add");
        } else if (hasFrame) {
            lblStep.setText("✅ Frame selected! Photo optional — Add કરો");
        } else if (hasPhoto) {
            lblStep.setText("Photo ready! Frame select કરો");
        } else {
            lblStep.setText("Step 1: Frame પસંદ કરો (Photo optional)");
        }
    }

    private void updateAddButton() {
        boolean canAdd = !selectedFrameUrl.isEmpty();
        btnAddFrame.setEnabled(canAdd);
        btnAddFrame.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        canAdd
                                ? Color.parseColor("#1565C0")
                                : Color.GRAY));
    }

    private void updateAdjustButton() {
        if (btnAdjustPhoto == null) return;
        boolean show = selectedPhotoUri != null && !selectedFrameUrl.isEmpty();
        btnAdjustPhoto.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    // ══════════════════════════════════════════════
    // COLOR ROW BUILDER
    // isBorder=false → frame overlay color
    // isBorder=true  → photo border color
    // ══════════════════════════════════════════════
    private void buildColorRow(LinearLayout row, boolean isBorder) {
        if (row == null) return;
        row.removeAllViews();

        String[] colorNames = {
                "None","Red","Green","Blue","Yellow",
                "Orange","Pink","Purple","White","Black"
        };
        int[] colors = {
                Color.TRANSPARENT,
                Color.RED,
                Color.GREEN,
                Color.BLUE,
                Color.YELLOW,
                Color.rgb(255, 165, 0),
                Color.rgb(255, 105, 180),
                Color.rgb(128, 0, 128),
                Color.WHITE,
                Color.BLACK
        };

        for (int i = 0; i < colors.length; i++) {
            final int    c = colors[i];
            final String n = colorNames[i];

            TextView btn = new TextView(this);
            btn.setGravity(Gravity.CENTER);
            btn.setTextSize(9);
            btn.setPadding(4, 4, 4, 4);

            GradientDrawable gd = new GradientDrawable();
            if (c == Color.TRANSPARENT) {
                gd.setColor(Color.WHITE);
                btn.setText("✕");
                btn.setTextColor(Color.RED);
            } else {
                gd.setColor(c);
                btn.setText(n);
                btn.setTextColor(
                        (c == Color.WHITE || c == Color.YELLOW)
                                ? Color.BLACK : Color.WHITE);
            }
            gd.setStroke(2, Color.GRAY);
            gd.setCornerRadius(6f);
            btn.setBackground(gd);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(70, 70);
            lp.setMargins(4, 0, 4, 0);
            btn.setLayoutParams(lp);

            btn.setOnClickListener(v -> {
                if (isBorder) {
                    selectedBorderColor = c;
                    if (borderColorBox != null)
                        borderColorBox.setBackgroundColor(
                                c == Color.TRANSPARENT
                                        ? Color.parseColor("#F5F5F5") : c);
                    if (lblBorderColor != null)
                        lblBorderColor.setText("Border Color: " + n);
                } else {
                    selectedOverlayColor = c;
                    if (colorPreviewBox != null)
                        colorPreviewBox.setBackgroundColor(
                                c == Color.TRANSPARENT
                                        ? Color.parseColor("#F5F5F5") : c);
                    if (lblColor != null)
                        lblColor.setText("Frame Color: " + n);
                }
            });

            row.addView(btn);
        }

        // ── 🎨 Custom Color
        TextView btnCustom = new TextView(this);
        btnCustom.setText("🎨");
        btnCustom.setTextSize(18);
        btnCustom.setGravity(Gravity.CENTER);

        GradientDrawable cGd = new GradientDrawable();
        cGd.setColor(Color.parseColor("#E8EAF6"));
        cGd.setStroke(2, Color.parseColor("#3F51B5"));
        cGd.setCornerRadius(6f);
        btnCustom.setBackground(cGd);

        LinearLayout.LayoutParams cP = new LinearLayout.LayoutParams(70, 70);
        cP.setMargins(4, 0, 4, 0);
        btnCustom.setLayoutParams(cP);

        btnCustom.setOnClickListener(v -> {
            int initColor = isBorder
                    ? (selectedBorderColor == Color.TRANSPARENT
                    ? Color.BLACK : selectedBorderColor)
                    : (selectedOverlayColor == Color.TRANSPARENT
                    ? Color.RED : selectedOverlayColor);

            new AmbilWarnaDialog(this, initColor,
                    new AmbilWarnaDialog.OnAmbilWarnaListener() {
                        @Override public void onCancel(AmbilWarnaDialog d) {}
                        @Override public void onOk(AmbilWarnaDialog d, int color) {
                            if (isBorder) {
                                selectedBorderColor = color;
                                if (borderColorBox != null)
                                    borderColorBox.setBackgroundColor(color);
                                if (lblBorderColor != null)
                                    lblBorderColor.setText("Border Color: Custom");
                            } else {
                                selectedOverlayColor = color;
                                if (colorPreviewBox != null)
                                    colorPreviewBox.setBackgroundColor(color);
                                if (lblColor != null)
                                    lblColor.setText("Frame Color: Custom");
                            }
                        }
                    }).show();
        });

        row.addView(btnCustom);
    }

    // ══════════════════════════════════════════════
    // LOAD FRAMES — MainActivity ની same copy
    // ══════════════════════════════════════════════
    private void loadFramesDirectly(RecyclerView frameRV, ProgressBar progressBar) {

        LinearLayout dummyLl = new LinearLayout(this);
        dummyLl.setVisibility(View.GONE);
        TextView dummyTv = new TextView(this);

        invite_photo_frame.frame_arrayList          = new java.util.ArrayList<>();
        invite_photo_frame.frame_arrayListTemp      = new java.util.ArrayList<>();
        invite_photo_frame.frame_isOver             = false;
        invite_photo_frame.frame_isScroll           = false;
        invite_photo_frame.frame_page               = 1;
        invite_photo_frame.frame_cidddddd           = "8";
        invite_photo_frame.frame_recyclerView       = frameRV;
        invite_photo_frame.frame_progressBar        = progressBar;
        invite_photo_frame.frame_ll_empty           = dummyLl;
        invite_photo_frame.frame_tv_empty           = dummyTv;
        invite_photo_frame.frame_button_empty       = null;
        invite_photo_frame.frame_adapterImageQuotes = null;
        invite_photo_frame.frame_methods            = new invite_Methods(this);

        StaggeredGridLayoutManager lLayout =
                new StaggeredGridLayoutManager(3,
                        StaggeredGridLayoutManager.VERTICAL);
        frameRV.setLayoutManager(lLayout);
        invite_photo_frame.frame_lLayout = lLayout;

        invite_photo_frame.frame_selectmethod =
                invite_AppConstants.METHOD_FRAME_ALL;
        invite_photo_frame.loadQuotesByCat_1(
                invite_AppConstants.METHOD_FRAME_ALL, this);

        invite_EndlessRecyclerViewScrollListener1 scrollListener =
                new invite_EndlessRecyclerViewScrollListener1(lLayout) {
                    @Override
                    public void onLoadMore(int p, int total, RecyclerView view) {
                        if (!invite_photo_frame.frame_isOver
                                && !invite_photo_frame.frame_isScroll) {
                            invite_photo_frame.frame_isScroll = true;
                            new Handler().postDelayed(() ->
                                    invite_photo_frame.loadQuotesByCat_1(
                                            invite_AppConstants.METHOD_FRAME_ALL,
                                            FramePickerActivity.this), 500);
                        }
                    }
                };
        frameRV.addOnScrollListener(scrollListener);
        invite_photo_frame.frame_scrollListener = scrollListener;
    }

    // ══════════════════════════════════════════════
    // RETURN RESULT TO MAIN ACTIVITY
    // ══════════════════════════════════════════════
    private void returnResult() {
        if (selectedFrameUrl.isEmpty()) {
            Toast.makeText(this,
                    "પહેલા Frame select કરો", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent result = new Intent();

        // ── Frame data
        result.putExtra(EXTRA_FRAME_URL,     selectedFrameUrl);
        result.putExtra(EXTRA_MASK_URL,      selectedMaskUrl);
        result.putExtra(EXTRA_IMAGE_TOP_URL, selectedImageTopUrl);
        result.putExtra(EXTRA_OVERLAY_COLOR, selectedOverlayColor);

        // ── Border data
        result.putExtra(EXTRA_BORDER_COLOR, selectedBorderColor);
        result.putExtra(EXTRA_BORDER_WIDTH, selectedBorderWidth);

        // ── Replace mode flag
        boolean isReplaceMode =
                getIntent().getBooleanExtra("isReplacePhotoMode", false);
        result.putExtra("isReplacePhotoMode", isReplaceMode);

        // ── Photo data
        boolean hasAdjustedPhoto = !adjustedMergedPath.isEmpty();
        boolean hasRawPhoto      = selectedPhotoUri != null;

        result.putExtra(EXTRA_HAS_PHOTO, hasAdjustedPhoto || hasRawPhoto);

        if (hasAdjustedPhoto) {
            // ── Adjusted (finger adjust) path
            result.putExtra(EXTRA_ADJUSTED_MERGED, adjustedMergedPath);
            result.putExtra(EXTRA_ADJUSTED_MASKED, adjustedUserMaskedPath);
        } else if (hasRawPhoto) {
            // ── Raw photo URI
            result.putExtra(EXTRA_USER_PHOTO_URI, selectedPhotoUri.toString());
        }

        setResult(RESULT_OK, result);
        finish();
    }
}