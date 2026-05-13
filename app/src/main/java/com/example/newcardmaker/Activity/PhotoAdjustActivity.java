package com.example.newcardmaker.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.newcardmaker.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoAdjustActivity extends AppCompatActivity {

    // ── Intent Keys input
    public static final String EXTRA_PHOTO_URI = "photo_uri";
    public static final String EXTRA_MASK_URL = "mask_url";
    public static final String EXTRA_TOP_URL = "top_url";
    public static final String EXTRA_OVERLAY_COLOR = "overlay_color";

    // ── Result Keys output
    public static final String RESULT_MERGED_PATH = "merged_path";
    public static final String RESULT_USER_MASKED_PATH = "user_masked_path";

    // ── Views
    private View previewView;
    private ImageView adjustIv;
    private TextView tvTitle;

    private View panelBrightness;
    private View panelContrast;

    private SeekBar seekBrightness;
    private SeekBar seekContrast;

    // ── Bitmaps
    private Bitmap originalBitmap = null;
    private Bitmap maskBitmap = null;
    private Bitmap topBitmap = null;

    // ── Preview size. Mask original ratio maintain karva mate.
    private int previewW = 0;
    private int previewH = 0;

    // ── Matrix for pan zoom rotate
    private final Matrix matrix = new Matrix();
    private final Matrix savedMatrix = new Matrix();

    // ── Touch state
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int touchMode = NONE;

    private float lastX, lastY;
    private float midX, midY;
    private float startDist;

    // ── Adjustments
    private float brightnessValue = 0f;
    private float contrastValue = 1f;
    private float rotationAngle = 0f;
    private boolean flipHorizontal = false;
    private boolean flipVertical = false;

    // ── Crop handles
    private boolean cropMode = false;
    private RectF cropRect = null;
    private int cropHandle = -1;

    private static final int HANDLE_NONE = -1;
    private static final int HANDLE_TL = 0;
    private static final int HANDLE_TR = 1;
    private static final int HANDLE_BR = 2;
    private static final int HANDLE_BL = 3;
    private static final float HANDLE_RADIUS = 30f;

    private int overlayColor = Color.TRANSPARENT;
    private String maskUrl = "";
    private String topUrl = "";

    private String activeTab = "";

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Button btnApply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_adjust);

        String photoUriStr = getIntent().getStringExtra(EXTRA_PHOTO_URI);
        maskUrl = getIntent().getStringExtra(EXTRA_MASK_URL);
        topUrl = getIntent().getStringExtra(EXTRA_TOP_URL);
        overlayColor = getIntent().getIntExtra(EXTRA_OVERLAY_COLOR, Color.TRANSPARENT);

        if (photoUriStr == null || maskUrl == null) {
            Toast.makeText(this, "Data missing", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvTitle = findViewById(R.id.tv_adjust_title);
        previewView = findViewById(R.id.photo_adjust_preview);
        adjustIv = findViewById(R.id.photo_adjust_touch);

        btnApply = findViewById(R.id.btn_adjust_apply);
        Button btnCancel = findViewById(R.id.btn_adjust_cancel);
        Button btnReset = findViewById(R.id.btn_adjust_reset);

        View btnBrightness = findViewById(R.id.btn_tool_brightness);
        View btnContrast = findViewById(R.id.btn_tool_contrast);
        View btnRotateL = findViewById(R.id.btn_tool_rotate_left);
        View btnRotateR = findViewById(R.id.btn_tool_rotate_right);
        View btnFlipH = findViewById(R.id.btn_tool_flip_h);
        View btnFlipV = findViewById(R.id.btn_tool_flip_v);
        View btnCrop = findViewById(R.id.btn_tool_crop);

        panelBrightness = findViewById(R.id.panel_brightness);
        panelContrast = findViewById(R.id.panel_contrast);

        seekBrightness = findViewById(R.id.seek_brightness);
        seekContrast = findViewById(R.id.seek_contrast);

        seekBrightness.setMax(510);
        seekBrightness.setProgress(255);
        seekContrast.setMax(150);
        seekContrast.setProgress(50);

        previewView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        tvTitle.setText("📷 Adjust Your Photo");

        Glide.with(this).asBitmap().load(maskUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bmp, @Nullable Transition<? super Bitmap> t) {
                        maskBitmap = bmp;
                        checkAndInitPreview();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable p) {
                    }
                });

        if (topUrl != null && !topUrl.isEmpty()) {
            Glide.with(this).asBitmap().load(topUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap bmp, @Nullable Transition<? super Bitmap> t) {
                            topBitmap = bmp;
                            if (previewView != null) previewView.invalidate();
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable p) {
                        }
                    });
        }

        Uri photoUri = Uri.parse(photoUriStr);
        Glide.with(this).asBitmap().load(photoUri)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bmp, @Nullable Transition<? super Bitmap> t) {
                        originalBitmap = bmp;
                        checkAndInitPreview();
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable p) {
                    }
                });

        adjustIv.setOnTouchListener((v, event) -> {
            handleTouch(event);
            return true;
        });

        btnApply.setOnClickListener(v -> applyAndReturn());
        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        btnReset.setOnClickListener(v -> resetAll());

        btnBrightness.setOnClickListener(v -> togglePanel("brightness"));
        btnContrast.setOnClickListener(v -> togglePanel("contrast"));

        btnRotateL.setOnClickListener(v -> {
            rotationAngle -= 90f;
            previewView.invalidate();
        });

        btnRotateR.setOnClickListener(v -> {
            rotationAngle += 90f;
            previewView.invalidate();
        });

        btnFlipH.setOnClickListener(v -> {
            flipHorizontal = !flipHorizontal;
            previewView.invalidate();
        });

        btnFlipV.setOnClickListener(v -> {
            flipVertical = !flipVertical;
            previewView.invalidate();
        });

        btnCrop.setOnClickListener(v -> {
            cropMode = !cropMode;
            if (cropMode) {
                initCropRect();
                tvTitle.setText("✂️ Drag corners to crop");
            } else {
                applyCrop();
                tvTitle.setText("📷 Adjust Your Photo");
            }
            previewView.invalidate();
        });

        seekBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar s, int p, boolean user) {
                brightnessValue = p - 255f;
                previewView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar s) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar s) {
            }
        });

        seekContrast.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar s, int p, boolean user) {
                contrastValue = 0.5f + (p / 150f) * 1.5f;
                previewView.invalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar s) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar s) {
            }
        });
    }

    private void togglePanel(String tab) {
        if (activeTab.equals(tab)) {
            panelBrightness.setVisibility(View.GONE);
            panelContrast.setVisibility(View.GONE);
            activeTab = "";
        } else {
            activeTab = tab;
            panelBrightness.setVisibility(tab.equals("brightness") ? View.VISIBLE : View.GONE);
            panelContrast.setVisibility(tab.equals("contrast") ? View.VISIBLE : View.GONE);
        }
    }

    private void resetAll() {
        brightnessValue = 0f;
        contrastValue = 1f;
        rotationAngle = 0f;
        flipHorizontal = false;
        flipVertical = false;
        cropMode = false;
        cropRect = null;
        seekBrightness.setProgress(255);
        seekContrast.setProgress(50);
        initMatrix();
        previewView.invalidate();
        tvTitle.setText("📷 Adjust Your Photo");
    }

    private void checkAndInitPreview() {
        if (originalBitmap == null || maskBitmap == null) return;
        setupPreviewView();
    }

    // Mask original ratio maintain. Screen karta moto hoy to fit, nano hoy to original size.
    private void calculatePreviewSize() {
        if (maskBitmap == null) return;

        int maskW = maskBitmap.getWidth();
        int maskH = maskBitmap.getHeight();

        int availableW = getResources().getDisplayMetrics().widthPixels;

        int titleH = dpToPx(36);
        int topH = dpToPx(44);
        int bottomH = dpToPx(64);
        int padding = dpToPx(16);

        int availableH = getResources().getDisplayMetrics().heightPixels
                - titleH - topH - bottomH - padding;

        float scale = Math.min(
                availableW / (float) maskW,
                availableH / (float) maskH
        );

        // image original કરતાં મોટી ના થાય
        scale = Math.min(scale, 1f);

        previewW = Math.round(maskW * scale);
        previewH = Math.round(maskH * scale);
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void setupPreviewView() {
        if (maskBitmap == null) return;

        calculatePreviewSize();

        RelativeLayout container = findViewById(R.id.photo_adjust_container);

        View customPreview = new View(this) {
            @Override
            protected void onSizeChanged(int w, int h, int oldW, int oldH) {
                super.onSizeChanged(w, h, oldW, oldH);
                if (w > 0 && h > 0 && originalBitmap != null) {
                    initMatrixForSize(w, h);
                }
            }

            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                drawPreview(canvas, getWidth(), getHeight());
            }
        };

        customPreview.setId(R.id.photo_adjust_preview);
        customPreview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(previewW, previewH);
        lp.addRule(RelativeLayout.BELOW, R.id.top_buttons);
        container.removeView(previewView);
        container.addView(customPreview, lp);

        previewView = customPreview;
        setupTouchView();
    }

    private void drawPreview(Canvas canvas, int w, int h) {
        if (originalBitmap == null || maskBitmap == null || w <= 0 || h <= 0) return;

        canvas.drawColor(Color.parseColor("#111111"));

        Bitmap photoBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas photoCanvas = new Canvas(photoBmp);

        Paint basePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        basePaint.setColorFilter(buildColorFilter());

        Matrix finalMatrix = buildFinalMatrix(w, h);
        photoCanvas.drawBitmap(originalBitmap, finalMatrix, basePaint);

        Bitmap resultBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas resultCanvas = new Canvas(resultBmp);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        Bitmap scaledMask = Bitmap.createScaledBitmap(maskBitmap, w, h, true);
        boolean maskTransparent = hasTransparentPixels(scaledMask);

        if (maskTransparent) {
            resultCanvas.drawBitmap(photoBmp, 0, 0, p);
            Paint dstIn = new Paint(Paint.ANTI_ALIAS_FLAG);
            dstIn.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            resultCanvas.drawBitmap(scaledMask, 0, 0, dstIn);
            dstIn.setXfermode(null);
        } else {
            resultCanvas.drawBitmap(scaledMask, 0, 0, p);
            Paint srcIn = new Paint(Paint.ANTI_ALIAS_FLAG);
            srcIn.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            resultCanvas.drawBitmap(photoBmp, 0, 0, srcIn);
            srcIn.setXfermode(null);
        }

        canvas.drawBitmap(resultBmp, 0, 0, p);

        if (topBitmap != null) {
            Bitmap scaledTop = Bitmap.createScaledBitmap(topBitmap, w, h, true);
            if (overlayColor != Color.TRANSPARENT && overlayColor != 0) {
                Bitmap tinted = applyColorTint(scaledTop, overlayColor);
                canvas.drawBitmap(tinted, 0, 0, p);
                tinted.recycle();
            } else {
                canvas.drawBitmap(scaledTop, 0, 0, p);
            }
            scaledTop.recycle();
        }

        if (cropMode && cropRect != null) {
            drawCropOverlay(canvas, w, h);
        }

        photoBmp.recycle();
        scaledMask.recycle();
        resultBmp.recycle();
    }

    private Matrix buildFinalMatrix(int w, int h) {
        Matrix m = new Matrix(matrix);

        if (rotationAngle != 0f) {
            m.postRotate(rotationAngle, w / 2f, h / 2f);
        }

        if (flipHorizontal) {
            m.postScale(-1f, 1f, w / 2f, h / 2f);
        }

        if (flipVertical) {
            m.postScale(1f, -1f, w / 2f, h / 2f);
        }

        return m;
    }

    private ColorMatrixColorFilter buildColorFilter() {
        float b = brightnessValue;
        float c = contrastValue;
        float t = (1f - c) / 2f * 255f;

        ColorMatrix cm = new ColorMatrix(new float[]{
                c, 0, 0, 0, b + t,
                0, c, 0, 0, b + t,
                0, 0, c, 0, b + t,
                0, 0, 0, 1, 0
        });
        return new ColorMatrixColorFilter(cm);
    }

    private void initCropRect() {
        if (previewView == null) return;
        int w = previewView.getWidth();
        int h = previewView.getHeight();
        float inset = Math.min(w, h) * 0.15f;
        cropRect = new RectF(inset, inset, w - inset, h - inset);
    }

    private void drawCropOverlay(Canvas canvas, int w, int h) {
        Paint dimPaint = new Paint();
        dimPaint.setColor(Color.argb(140, 0, 0, 0));

        canvas.drawRect(0, 0, w, cropRect.top, dimPaint);
        canvas.drawRect(0, cropRect.bottom, w, h, dimPaint);
        canvas.drawRect(0, cropRect.top, cropRect.left, cropRect.bottom, dimPaint);
        canvas.drawRect(cropRect.right, cropRect.top, w, cropRect.bottom, dimPaint);

        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2f);
        canvas.drawRect(cropRect, borderPaint);

        float thirdW = cropRect.width() / 3f;
        float thirdH = cropRect.height() / 3f;
        Paint gridPaint = new Paint();
        gridPaint.setColor(Color.argb(100, 255, 255, 255));
        gridPaint.setStrokeWidth(1f);

        for (int i = 1; i < 3; i++) {
            canvas.drawLine(cropRect.left + thirdW * i, cropRect.top,
                    cropRect.left + thirdW * i, cropRect.bottom, gridPaint);
            canvas.drawLine(cropRect.left, cropRect.top + thirdH * i,
                    cropRect.right, cropRect.top + thirdH * i, gridPaint);
        }

        Paint handlePaint = new Paint();
        handlePaint.setColor(Color.WHITE);
        handlePaint.setStyle(Paint.Style.FILL);

        float hr = HANDLE_RADIUS;
        canvas.drawCircle(cropRect.left, cropRect.top, hr, handlePaint);
        canvas.drawCircle(cropRect.right, cropRect.top, hr, handlePaint);
        canvas.drawCircle(cropRect.right, cropRect.bottom, hr, handlePaint);
        canvas.drawCircle(cropRect.left, cropRect.bottom, hr, handlePaint);
    }

    private void applyCrop() {
        if (cropRect == null || originalBitmap == null) return;

        int vW = previewView.getWidth();
        int vH = previewView.getHeight();

        Matrix invertMatrix = new Matrix();
        buildFinalMatrix(vW, vH).invert(invertMatrix);

        float[] pts = {
                cropRect.left, cropRect.top,
                cropRect.right, cropRect.bottom
        };
        invertMatrix.mapPoints(pts);

        int bW = originalBitmap.getWidth();
        int bH = originalBitmap.getHeight();

        int x = (int) Math.max(0, Math.min(pts[0], pts[2]));
        int y = (int) Math.max(0, Math.min(pts[1], pts[3]));
        int cx = (int) Math.min(bW, Math.max(pts[0], pts[2]));
        int cy = (int) Math.min(bH, Math.max(pts[1], pts[3]));

        int cw = cx - x;
        int ch = cy - y;

        if (cw > 0 && ch > 0) {
            originalBitmap = Bitmap.createBitmap(originalBitmap, x, y, cw, ch);
        }

        cropRect = null;
        cropMode = false;
        initMatrix();
        previewView.invalidate();
    }

    private void initMatrixForSize(int vW, int vH) {
        if (originalBitmap == null || maskBitmap == null) return;

        float imgW = originalBitmap.getWidth();
        float imgH = originalBitmap.getHeight();

        float scale = Math.max(vW / imgW, vH / imgH);

        float dx = (vW - imgW * scale) / 2f;
        float dy = (vH - imgH * scale) / 2f;

        matrix.setScale(scale, scale);
        matrix.postTranslate(dx, dy);
        savedMatrix.set(matrix);

        if (adjustIv != null) {
            adjustIv.setImageMatrix(matrix);
        }

        if (previewView != null) previewView.invalidate();
    }

    private void initMatrix() {
        if (originalBitmap == null || previewView == null) return;

        int vW = previewView.getWidth();
        int vH = previewView.getHeight();

        if (vW > 0 && vH > 0) {
            initMatrixForSize(vW, vH);
        } else {
            previewView.post(() -> {
                int w = previewView.getWidth();
                int h = previewView.getHeight();
                if (w > 0 && h > 0) initMatrixForSize(w, h);
            });
        }
    }

    private void setupTouchView() {
        adjustIv = findViewById(R.id.photo_adjust_touch);
        if (adjustIv == null || previewW <= 0 || previewH <= 0) return;

        adjustIv.post(() -> {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(previewW, previewH);
            lp.addRule(RelativeLayout.BELOW, R.id.top_buttons);

            adjustIv.setLayoutParams(lp);
            adjustIv.setImageBitmap(originalBitmap);
            adjustIv.setScaleType(ImageView.ScaleType.MATRIX);
            adjustIv.setImageMatrix(matrix);
            adjustIv.setAlpha(0f);
            adjustIv.bringToFront();
        });
    }

    private void handleTouch(MotionEvent event) {
        if (cropMode && cropRect != null) {
            handleCropTouch(event);
            return;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                savedMatrix.set(matrix);
                lastX = event.getX();
                lastY = event.getY();
                touchMode = DRAG;
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                if (event.getPointerCount() >= 2) {
                    startDist = fingerSpacing(event);
                    if (startDist > 10f) {
                        savedMatrix.set(matrix);
                        fingerMidPoint(event);
                        touchMode = ZOOM;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (touchMode == DRAG) {
                    float dx = event.getX() - lastX;
                    float dy = event.getY() - lastY;
                    matrix.set(savedMatrix);
                    matrix.postTranslate(dx, dy);
                    clampMatrix();
                    if (adjustIv != null) adjustIv.setImageMatrix(matrix);
                    previewView.invalidate();
                } else if (touchMode == ZOOM && event.getPointerCount() >= 2) {
                    float newDist = fingerSpacing(event);
                    if (newDist > 10f) {
                        float sf = newDist / startDist;
                        matrix.set(savedMatrix);
                        matrix.postScale(sf, sf, midX, midY);
                        clampMatrix();
                        if (adjustIv != null) adjustIv.setImageMatrix(matrix);
                        previewView.invalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                touchMode = NONE;
                break;
        }
    }

    private void handleCropTouch(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                cropHandle = getNearestHandle(x, y);
                lastX = x;
                lastY = y;
                break;

            case MotionEvent.ACTION_MOVE:
                if (cropHandle == HANDLE_NONE) break;

                float dx = x - lastX;
                float dy = y - lastY;
                float minSize = HANDLE_RADIUS * 4;

                switch (cropHandle) {
                    case HANDLE_TL:
                        if (cropRect.right - (cropRect.left + dx) > minSize) cropRect.left += dx;
                        if (cropRect.bottom - (cropRect.top + dy) > minSize) cropRect.top += dy;
                        break;

                    case HANDLE_TR:
                        if ((cropRect.right + dx) - cropRect.left > minSize) cropRect.right += dx;
                        if (cropRect.bottom - (cropRect.top + dy) > minSize) cropRect.top += dy;
                        break;

                    case HANDLE_BR:
                        if ((cropRect.right + dx) - cropRect.left > minSize) cropRect.right += dx;
                        if ((cropRect.bottom + dy) - cropRect.top > minSize) cropRect.bottom += dy;
                        break;

                    case HANDLE_BL:
                        if (cropRect.right - (cropRect.left + dx) > minSize) cropRect.left += dx;
                        if ((cropRect.bottom + dy) - cropRect.top > minSize) cropRect.bottom += dy;
                        break;
                }

                lastX = x;
                lastY = y;
                previewView.invalidate();
                break;

            case MotionEvent.ACTION_UP:
                cropHandle = HANDLE_NONE;
                break;
        }
    }

    private int getNearestHandle(float x, float y) {
        float threshold = HANDLE_RADIUS * 2.5f;
        float[][] handles = {
                {cropRect.left, cropRect.top},
                {cropRect.right, cropRect.top},
                {cropRect.right, cropRect.bottom},
                {cropRect.left, cropRect.bottom}
        };

        for (int i = 0; i < handles.length; i++) {
            float dist = (float) Math.sqrt(
                    Math.pow(x - handles[i][0], 2) + Math.pow(y - handles[i][1], 2));
            if (dist < threshold) return i;
        }
        return HANDLE_NONE;
    }

    private void applyAndReturn() {
        if (originalBitmap == null || maskBitmap == null) {
            Toast.makeText(this, "Image load નથી", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cropMode) {
            applyCrop();
        }

        btnApply.setEnabled(false);
        Toast.makeText(this, "Apply થઈ રહ્યું છે...", Toast.LENGTH_SHORT).show();

        executorService.execute(() -> {
            try {
                Intent result = createFinalImages();

                runOnUiThread(() -> {
                    btnApply.setEnabled(true);
                    setResult(RESULT_OK, result);
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();

                runOnUiThread(() -> {
                    btnApply.setEnabled(true);
                    Toast.makeText(
                            PhotoAdjustActivity.this,
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                });
            }
        });
    }

    private Intent createFinalImages() throws Exception {
        int outW = maskBitmap.getWidth();
        int outH = maskBitmap.getHeight();

        int vW = previewView.getWidth();
        int vH = previewView.getHeight();

        Bitmap photoBmp = Bitmap.createBitmap(vW, vH, Bitmap.Config.ARGB_8888);
        Canvas photoCanvas = new Canvas(photoBmp);

        Paint basePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        basePaint.setColorFilter(buildColorFilter());

        photoCanvas.drawBitmap(originalBitmap, buildFinalMatrix(vW, vH), basePaint);

        Bitmap scaledPhoto = Bitmap.createScaledBitmap(photoBmp, outW, outH, true);
        photoBmp.recycle();

        Bitmap maskScaled = Bitmap.createScaledBitmap(maskBitmap, outW, outH, true);
        boolean maskTransparent = hasTransparentPixels(maskScaled);

        Bitmap userMasked = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888);
        Canvas mc = new Canvas(userMasked);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        if (maskTransparent) {
            mc.drawBitmap(maskScaled, 0, 0, paint);

            Paint srcIn = new Paint(Paint.ANTI_ALIAS_FLAG);
            srcIn.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            mc.drawBitmap(scaledPhoto, 0, 0, srcIn);
            srcIn.setXfermode(null);
        } else {
            mc.drawBitmap(scaledPhoto, 0, 0, paint);

            Paint dstIn = new Paint(Paint.ANTI_ALIAS_FLAG);
            dstIn.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            mc.drawBitmap(maskScaled, 0, 0, dstIn);
            dstIn.setXfermode(null);
        }

        Bitmap finalBmp = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888);
        Canvas fc = new Canvas(finalBmp);
        fc.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        fc.drawBitmap(userMasked, 0, 0, paint);

        if (topBitmap != null) {
            Bitmap topScaled = Bitmap.createScaledBitmap(topBitmap, outW, outH, true);

            if (overlayColor != Color.TRANSPARENT && overlayColor != 0) {
                Bitmap tinted = applyColorTint(topScaled, overlayColor);
                fc.drawBitmap(tinted, 0, 0, paint);
                tinted.recycle();
            } else {
                fc.drawBitmap(topScaled, 0, 0, paint);
            }

            topScaled.recycle();
        }

        File mergedFile = new File(getCacheDir(), "merged_" + System.currentTimeMillis() + ".png");
        FileOutputStream fos1 = new FileOutputStream(mergedFile);
        finalBmp.compress(Bitmap.CompressFormat.PNG, 100, fos1);
        fos1.flush();
        fos1.close();

        File maskedFile = new File(getCacheDir(), "masked_" + System.currentTimeMillis() + ".png");
        FileOutputStream fos2 = new FileOutputStream(maskedFile);
        userMasked.compress(Bitmap.CompressFormat.PNG, 100, fos2);
        fos2.flush();
        fos2.close();

        scaledPhoto.recycle();
        maskScaled.recycle();
        userMasked.recycle();
        finalBmp.recycle();

        Intent result = new Intent();
        result.putExtra(RESULT_MERGED_PATH, mergedFile.getAbsolutePath());
        result.putExtra(RESULT_USER_MASKED_PATH, maskedFile.getAbsolutePath());

        return result;
    }


    private boolean hasTransparentPixels(Bitmap bmp) {
        int w = bmp.getWidth();
        int h = bmp.getHeight();

        int[] checkX = {0, w - 1, w / 2, w / 4, 3 * w / 4};
        int[] checkY = {0, h - 1, h / 2, h / 4, 3 * h / 4};

        for (int i = 0; i < checkX.length; i++) {
            if (Color.alpha(bmp.getPixel(checkX[i], checkY[i])) < 255) return true;
        }
        return false;
    }

    private Bitmap applyColorTint(Bitmap src, int tintColor) {
        Bitmap tinted = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tinted);
        canvas.drawBitmap(src, 0, 0, null);

        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(tintColor);
        p.setAlpha(150);
        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawRect(0, 0, src.getWidth(), src.getHeight(), p);
        p.setXfermode(null);

        return tinted;
    }

    private void clampMatrix() {
        if (originalBitmap == null || previewView == null) return;

        int vW = previewView.getWidth();
        int vH = previewView.getHeight();
        if (vW <= 0 || vH <= 0) return;

        float[] values = new float[9];
        matrix.getValues(values);

        float scaleX = values[Matrix.MSCALE_X];
        float scaleY = values[Matrix.MSCALE_Y];
        float transX = values[Matrix.MTRANS_X];
        float transY = values[Matrix.MTRANS_Y];

        float minScale = Math.max(vW / (float) originalBitmap.getWidth(),
                vH / (float) originalBitmap.getHeight());

        if (scaleX < minScale || scaleY < minScale) {
            matrix.setScale(minScale, minScale);
            float cx = (vW - originalBitmap.getWidth() * minScale) / 2f;
            float cy = (vH - originalBitmap.getHeight() * minScale) / 2f;
            matrix.postTranslate(cx, cy);
            return;
        }

        float imgW = originalBitmap.getWidth() * scaleX;
        float imgH = originalBitmap.getHeight() * scaleY;

        float clampedX;
        if (imgW >= vW) {
            clampedX = Math.min(transX, 0);
            clampedX = Math.max(clampedX, vW - imgW);
        } else {
            clampedX = (vW - imgW) / 2f;
        }

        float clampedY;
        if (imgH >= vH) {
            clampedY = Math.min(transY, 0);
            clampedY = Math.max(clampedY, vH - imgH);
        } else {
            clampedY = (vH - imgH) / 2f;
        }

        values[Matrix.MTRANS_X] = clampedX;
        values[Matrix.MTRANS_Y] = clampedY;
        matrix.setValues(values);
    }

    private float fingerSpacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void fingerMidPoint(MotionEvent event) {
        midX = (event.getX(0) + event.getX(1)) / 2f;
        midY = (event.getY(0) + event.getY(1)) / 2f;
    }
}
