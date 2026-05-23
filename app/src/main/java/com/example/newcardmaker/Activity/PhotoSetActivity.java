package com.example.newcardmaker.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.example.newcardmaker.RotationGestureDetector;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class PhotoSetActivity extends AppCompatActivity {

    private static final int REQUEST_GALLERY = 100;
    private static final int REQUEST_UCROP = 101;

    // Views
    private FrameLayout flEdit;
    private FrameLayout flCanvas;
    private ImageView ivEditPreview;
    private TextView tvTabEdit;

    private Button btnChangePhoto;
    private Button btnDone;
    private Button btnCancel;
    private Button btnZoomIn;
    private Button btnZoomOut;
    private Button btnRotateLeft;
    private Button btnRotateRight;
    private Button btnReset;
    private SeekBar seekRotation;
    private TextView tvRotationVal;

    // Intent data
    private String maskUrl = "";
    private String topUrl = "";
    private int color = Color.TRANSPARENT;
    private float aspectW = 1f;
    private float aspectH = 1f;

    // Transform
    private final Matrix photoMatrix = new Matrix();
    private float currentScale = 1f;
    private float currentRotation = 0f;
    private float translateX = 0f;
    private float translateY = 0f;

    // Gesture
    private ScaleGestureDetector scaleDetector;
    private RotationGestureDetector rotationDetector;

    // Touch
    private float lastTouchX = 0f;
    private float lastTouchY = 0f;
    private boolean isDragging = false;

    // Bitmaps
    private Bitmap currentPhotoBitmap = null;
    private Bitmap maskBitmap = null;
    private Bitmap topBitmap = null;

    // Render control
    private boolean isRenderingPreview = false;
    private final Handler previewHandler = new Handler(Looper.getMainLooper());
    private Runnable previewRunnable;
    private long lastRenderTime = 0L;
    private static final long PREVIEW_RENDER_DELAY = 35L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_set);

        maskUrl = getIntent().getStringExtra("maskUrl") != null
                ? getIntent().getStringExtra("maskUrl") : "";
        topUrl = getIntent().getStringExtra("topUrl") != null
                ? getIntent().getStringExtra("topUrl") : "";
        color = getIntent().getIntExtra("color", Color.TRANSPARENT);
        aspectW = getIntent().getFloatExtra("aspectW", 1f);
        aspectH = getIntent().getFloatExtra("aspectH", 1f);

        flEdit = findViewById(R.id.fl_edit);
        flCanvas = findViewById(R.id.fl_canvas);
        ivEditPreview = findViewById(R.id.iv_edit_preview);
        tvTabEdit = findViewById(R.id.tv_tab_edit);

        btnChangePhoto = findViewById(R.id.btn_change_photo);
        btnDone = findViewById(R.id.btn_done);
        btnCancel = findViewById(R.id.btn_cancel_photo);
        btnZoomIn = findViewById(R.id.btn_zoom_in);
        btnZoomOut = findViewById(R.id.btn_zoom_out);
        btnRotateLeft = findViewById(R.id.btn_rotate_left);
        btnRotateRight = findViewById(R.id.btn_rotate_right);
        btnReset = findViewById(R.id.btn_reset_transform);
        seekRotation = findViewById(R.id.seek_rotation);
        tvRotationVal = findViewById(R.id.tv_rotation_val);

        btnDone.setEnabled(false);
        btnDone.setAlpha(0.5f);

        showEditTab();
        loadFrameBitmaps();

        addPressEffect(btnZoomIn);
        addPressEffect(btnZoomOut);
        addPressEffect(btnRotateLeft);
        addPressEffect(btnRotateRight);
        addPressEffect(btnReset);
        addPressEffect(btnDone);
        addPressEffect(btnChangePhoto);
        addPressEffect(btnCancel);

        scaleDetector = new ScaleGestureDetector(this,
                new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                    @Override
                    public boolean onScale(ScaleGestureDetector detector) {
                        currentScale *= detector.getScaleFactor();
                        currentScale = Math.max(0.3f, Math.min(currentScale, 5f));
                        applyTransform();
                        return true;
                    }
                });

        rotationDetector = new RotationGestureDetector(delta -> {
            currentRotation = (currentRotation + delta) % 360f;
            if (currentRotation < 0) currentRotation += 360f;
            updateSeekFromRotation();
            applyTransform();
        });

        flCanvas.setOnTouchListener((v, event) -> {
            scaleDetector.onTouchEvent(event);
            rotationDetector.onTouchEvent(event);

            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_DOWN:
                    setCanvasTouchEffect(true);
                    lastTouchX = event.getRawX();
                    lastTouchY = event.getRawY();
                    isDragging = true;
                    break;

                case MotionEvent.ACTION_POINTER_DOWN:
                    setCanvasTouchEffect(true);
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (event.getPointerCount() == 1) {
                        if (isDragging && !scaleDetector.isInProgress()) {
                            translateX += event.getRawX() - lastTouchX;
                            translateY += event.getRawY() - lastTouchY;
                            lastTouchX = event.getRawX();
                            lastTouchY = event.getRawY();
                            applyTransform();
                        }
                    }
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                case MotionEvent.ACTION_CANCEL:
                    isDragging = false;
                    setCanvasTouchEffect(false);
                    schedulePreviewRender();
                    break;
            }
            return true;
        });

        btnZoomIn.setOnClickListener(v -> {
            currentScale = Math.min(currentScale + 0.1f, 5f);
            applyTransform();
        });

        btnZoomIn.setOnLongClickListener(v -> {
            currentScale = Math.min(currentScale + 0.3f, 5f);
            applyTransform();
            return true;
        });

        btnZoomOut.setOnClickListener(v -> {
            currentScale = Math.max(currentScale - 0.1f, 0.3f);
            applyTransform();
        });

        btnZoomOut.setOnLongClickListener(v -> {
            currentScale = Math.max(currentScale - 0.3f, 0.3f);
            applyTransform();
            return true;
        });

        btnRotateLeft.setOnClickListener(v -> {
            currentRotation = (currentRotation - 15f + 360f) % 360f;
            updateSeekFromRotation();
            applyTransform();
        });

        btnRotateLeft.setOnLongClickListener(v -> {
            currentRotation = (currentRotation - 45f + 360f) % 360f;
            updateSeekFromRotation();
            applyTransform();
            return true;
        });

        btnRotateRight.setOnClickListener(v -> {
            currentRotation = (currentRotation + 15f) % 360f;
            updateSeekFromRotation();
            applyTransform();
        });

        btnRotateRight.setOnLongClickListener(v -> {
            currentRotation = (currentRotation + 45f) % 360f;
            updateSeekFromRotation();
            applyTransform();
            return true;
        });

        btnReset.setOnClickListener(v -> resetTransform());

        seekRotation.setMax(359);
        seekRotation.setProgress(0);
        seekRotation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    currentRotation = progress;
                    tvRotationVal.setText(progress + "°");
                    applyTransform();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        btnChangePhoto.setOnClickListener(v -> openGallery());

        btnDone.setOnClickListener(v -> {
            if (currentPhotoBitmap == null) {
                Toast.makeText(this, "પહેલા Photo પસંદ કરો", Toast.LENGTH_SHORT).show();
                return;
            }
            processFinalAndReturn();
        });

        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });

        openGallery();
    }

    private void showEditTab() {
        flEdit.setVisibility(View.VISIBLE);
        tvTabEdit.setTextColor(Color.WHITE);
        tvTabEdit.setBackgroundColor(Color.parseColor("#1565C0"));
    }

    private void loadFrameBitmaps() {
        if (!topUrl.isEmpty()) {
            Glide.with(this)
                    .asBitmap()
                    .load(topUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource,
                                                    @Nullable Transition<? super Bitmap> transition) {
                            topBitmap = resource;
                            if (currentPhotoBitmap != null) {
                                schedulePreviewRender();
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) { }
                    });
        }

        if (!maskUrl.isEmpty()) {
            Glide.with(this)
                    .asBitmap()
                    .load(maskUrl)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource,
                                                    @Nullable Transition<? super Bitmap> transition) {
                            maskBitmap = resource;
                            if (currentPhotoBitmap != null) {
                                schedulePreviewRender();
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) { }
                    });
        }
    }

    private void applyTransform() {
        if (currentPhotoBitmap == null) return;

        float viewW = flCanvas.getWidth();
        float viewH = flCanvas.getHeight();
        if (viewW <= 0 || viewH <= 0) return;

        float bmpW = currentPhotoBitmap.getWidth();
        float bmpH = currentPhotoBitmap.getHeight();

        photoMatrix.reset();
        photoMatrix.postTranslate(-bmpW / 2f, -bmpH / 2f);
        photoMatrix.postScale(currentScale, currentScale);
        photoMatrix.postRotate(currentRotation);
        photoMatrix.postTranslate(viewW / 2f + translateX, viewH / 2f + translateY);

        schedulePreviewRender();
    }

    private void schedulePreviewRender() {
        previewHandler.removeCallbacksAndMessages(null);

        previewRunnable = () -> {
            if (currentPhotoBitmap == null || isRenderingPreview) return;

            long now = System.currentTimeMillis();
            long diff = now - lastRenderTime;

            if (diff < PREVIEW_RENDER_DELAY) {
                previewHandler.postDelayed(() -> generateMaskedEditPreview(), PREVIEW_RENDER_DELAY - diff);
            } else {
                generateMaskedEditPreview();
            }
        };

        previewHandler.postDelayed(previewRunnable, PREVIEW_RENDER_DELAY);
    }

    private void resetTransform() {
        currentScale = 1f;
        currentRotation = 0f;
        translateX = 0f;
        translateY = 0f;
        seekRotation.setProgress(0);
        tvRotationVal.setText("0°");
        fitPhotoToView();
    }

    private void fitPhotoToView() {
        if (currentPhotoBitmap == null) return;

        flCanvas.post(() -> {
            float viewW = flCanvas.getWidth();
            float viewH = flCanvas.getHeight();
            float bmpW = currentPhotoBitmap.getWidth();
            float bmpH = currentPhotoBitmap.getHeight();

            if (viewW <= 0 || viewH <= 0 || bmpW <= 0 || bmpH <= 0) return;

            currentScale = Math.min(viewW / bmpW, viewH / bmpH);
            translateX = 0f;
            translateY = 0f;
            applyTransform();
        });
    }

    private void updateSeekFromRotation() {
        int progress = ((int) currentRotation + 360) % 360;
        seekRotation.setProgress(progress);
        tvRotationVal.setText(progress + "°");
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Photo પસંદ કરો"), REQUEST_GALLERY);
    }

    private void launchUCrop(Uri sourceUri) {
        File outDir = new File(getCacheDir(), "ucrop");
        if (!outDir.exists()) outDir.mkdirs();

        Uri destUri = Uri.fromFile(new File(outDir, "ps_" + System.currentTimeMillis() + ".png"));

        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(100);
        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        options.setToolbarTitle("Photo Crop કરો");
        options.setToolbarColor(Color.parseColor("#1565C0"));
        options.setActiveControlsWidgetColor(Color.parseColor("#1565C0"));

        UCrop.of(sourceUri, destUri)
                .withOptions(options)
                .start(this, REQUEST_UCROP);
    }

    private void generateMaskedEditPreview() {
        if (currentPhotoBitmap == null || isRenderingPreview) return;

        isRenderingPreview = true;

        new Thread(() -> {
            Bitmap finalPreview = null;
            try {
                finalPreview = buildPreviewBitmap(false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Bitmap result = finalPreview;
            runOnUiThread(() -> {
                isRenderingPreview = false;
                lastRenderTime = System.currentTimeMillis();
                if (result != null) {
                    ivEditPreview.setImageBitmap(result);
                }
            });
        }).start();
    }

    // Fast preview bitmap
    private Bitmap buildPreviewBitmap(boolean onlyMaskedPhoto) {
        float viewW = flCanvas.getWidth();
        float viewH = flCanvas.getHeight();

        if (viewW <= 0 || viewH <= 0) {
            viewW = 800;
            viewH = 800;
        }

        int outW = (int) viewW;
        int outH = (int) viewH;

        Bitmap transformed = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888);
        Canvas tc = new Canvas(transformed);
        Paint tp = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        tc.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        tc.drawBitmap(currentPhotoBitmap, photoMatrix, tp);

        Bitmap scaledMask = null;
        if (maskBitmap != null) {
            scaledMask = Bitmap.createScaledBitmap(maskBitmap, outW, outH, true);
        }

        Bitmap userMasked = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888);
        Canvas mc = new Canvas(userMasked);
        Paint mp = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (scaledMask != null) {
            boolean hasAlpha = hasTransparentPixels(scaledMask);

            if (hasAlpha) {
                mc.drawBitmap(scaledMask, 0, 0, mp);
                Paint srcIn = new Paint(Paint.ANTI_ALIAS_FLAG);
                srcIn.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                mc.drawBitmap(transformed, 0, 0, srcIn);
                srcIn.setXfermode(null);
            } else {
                mc.drawBitmap(transformed, 0, 0, mp);
                Paint dstIn = new Paint(Paint.ANTI_ALIAS_FLAG);
                dstIn.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                mc.drawBitmap(scaledMask, 0, 0, dstIn);
                dstIn.setXfermode(null);
            }
            scaledMask.recycle();
        } else {
            mc.drawBitmap(transformed, 0, 0, mp);
        }

        transformed.recycle();

        if (onlyMaskedPhoto) {
            return userMasked;
        }

        Bitmap finalBmp = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888);
        Canvas fc = new Canvas(finalBmp);
        Paint fp = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        fc.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        fc.drawBitmap(userMasked, 0, 0, fp);
        userMasked.recycle();

        if (topBitmap != null) {
            Bitmap topScaled = Bitmap.createScaledBitmap(topBitmap, outW, outH, true);
            if (color != Color.TRANSPARENT) {
                Bitmap tinted = applyColorTint(topScaled, color);
                fc.drawBitmap(tinted, 0, 0, fp);
                tinted.recycle();
            } else {
                fc.drawBitmap(topScaled, 0, 0, fp);
            }
            topScaled.recycle();
        }

        return finalBmp;
    }

    // Original size final bitmap
    private Bitmap buildOriginalSizeFinalBitmap(boolean onlyMaskedPhoto) {
        if (currentPhotoBitmap == null) return null;

        int outW = currentPhotoBitmap.getWidth();
        int outH = currentPhotoBitmap.getHeight();

        Bitmap photoBase = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888);
        Canvas photoCanvas = new Canvas(photoBase);
        Paint photoPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        Matrix saveMatrix = new Matrix(photoMatrix);

        float previewW = flCanvas.getWidth();
        float previewH = flCanvas.getHeight();

        if (previewW <= 0 || previewH <= 0) {
            previewW = outW;
            previewH = outH;
        }

        float scaleX = (float) outW / previewW;
        float scaleY = (float) outH / previewH;
        saveMatrix.postScale(scaleX, scaleY);

        photoCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        photoCanvas.drawBitmap(currentPhotoBitmap, saveMatrix, photoPaint);

        Bitmap scaledMask = null;
        if (maskBitmap != null) {
            scaledMask = Bitmap.createScaledBitmap(maskBitmap, outW, outH, true);
        }

        Bitmap userMasked = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888);
        Canvas mc = new Canvas(userMasked);
        Paint mp = new Paint(Paint.ANTI_ALIAS_FLAG);

        if (scaledMask != null) {
            boolean hasAlpha = hasTransparentPixels(scaledMask);

            if (hasAlpha) {
                mc.drawBitmap(scaledMask, 0, 0, mp);
                Paint srcIn = new Paint(Paint.ANTI_ALIAS_FLAG);
                srcIn.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
                mc.drawBitmap(photoBase, 0, 0, srcIn);
                srcIn.setXfermode(null);
            } else {
                mc.drawBitmap(photoBase, 0, 0, mp);
                Paint dstIn = new Paint(Paint.ANTI_ALIAS_FLAG);
                dstIn.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
                mc.drawBitmap(scaledMask, 0, 0, dstIn);
                dstIn.setXfermode(null);
            }

            scaledMask.recycle();
        } else {
            mc.drawBitmap(photoBase, 0, 0, mp);
        }

        photoBase.recycle();

        if (onlyMaskedPhoto) {
            return userMasked;
        }

        Bitmap finalBmp = Bitmap.createBitmap(outW, outH, Bitmap.Config.ARGB_8888);
        Canvas fc = new Canvas(finalBmp);
        Paint fp = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        fc.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        fc.drawBitmap(userMasked, 0, 0, fp);
        userMasked.recycle();

        if (topBitmap != null) {
            Bitmap topScaled = Bitmap.createScaledBitmap(topBitmap, outW, outH, true);

            if (color != Color.TRANSPARENT) {
                Bitmap tinted = applyColorTint(topScaled, color);
                fc.drawBitmap(tinted, 0, 0, fp);
                tinted.recycle();
            } else {
                fc.drawBitmap(topScaled, 0, 0, fp);
            }

            topScaled.recycle();
        }

        return finalBmp;
    }

    private void processFinalAndReturn() {
        Toast.makeText(this, "Processing...", Toast.LENGTH_SHORT).show();
        btnDone.setEnabled(false);

        new Thread(() -> {
            try {
                Bitmap finalBmp = buildOriginalSizeFinalBitmap(false);
                Bitmap maskedBmp = buildOriginalSizeFinalBitmap(true);

                File outDir = new File(getCacheDir(), "photoset");
                if (!outDir.exists()) outDir.mkdirs();

                File finalFile = new File(outDir, "final_" + System.currentTimeMillis() + ".png");
                FileOutputStream fos1 = new FileOutputStream(finalFile);
                finalBmp.compress(Bitmap.CompressFormat.PNG, 100, fos1);
                fos1.flush();
                fos1.close();
                finalBmp.recycle();

                File maskedFile = new File(outDir, "masked_" + System.currentTimeMillis() + ".png");
                FileOutputStream fos2 = new FileOutputStream(maskedFile);
                maskedBmp.compress(Bitmap.CompressFormat.PNG, 100, fos2);
                fos2.flush();
                fos2.close();
                maskedBmp.recycle();

                runOnUiThread(() -> {
                    Intent result = new Intent();
                    result.putExtra("finalPath", finalFile.getAbsolutePath());
                    result.putExtra("userMaskedPath", maskedFile.getAbsolutePath());
                    setResult(RESULT_OK, result);
                    finish();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    btnDone.setEnabled(true);
                    btnDone.setAlpha(1f);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private boolean hasTransparentPixels(Bitmap bmp) {
        if (!bmp.hasAlpha()) return false;

        int w = bmp.getWidth();
        int h = bmp.getHeight();
        if (w <= 0 || h <= 0) return false;

        int[] xs = {0, w - 1, w / 2, w / 4, (3 * w) / 4};
        int[] ys = {0, h - 1, h / 2, h / 4, (3 * h) / 4};

        for (int i = 0; i < xs.length; i++) {
            int pixel = bmp.getPixel(
                    Math.min(xs[i], w - 1),
                    Math.min(ys[i], h - 1)
            );
            if (Color.alpha(pixel) < 255) return true;
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

    private void addPressEffect(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.92f).scaleY(0.92f).alpha(0.75f).setDuration(80).start();
                    break;

                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(80).start();
                    break;
            }
            return false;
        });
    }

    private void setCanvasTouchEffect(boolean pressed) {
        if (pressed) {
            flCanvas.animate().alpha(0.96f).setDuration(50).start();
        } else {
            flCanvas.animate().alpha(1f).setDuration(80).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        previewHandler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                launchUCrop(uri);
            }
        }

        if (requestCode == REQUEST_UCROP && resultCode == RESULT_OK && data != null) {
            Uri cropped = UCrop.getOutput(data);
            if (cropped != null) {
                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(cropped);
                    currentPhotoBitmap = BitmapFactory.decodeStream(inputStream);

                    if (currentPhotoBitmap != null) {
                        resetTransform();
                        fitPhotoToView();

                        btnDone.setEnabled(true);
                        btnDone.setAlpha(1f);
                        btnChangePhoto.setText("📷 Photo બદલો");

                        Toast.makeText(this,
                                "✅ Photo ready! Adjust કરો → Done",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (inputStream != null) inputStream.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        }

        if (requestCode == REQUEST_UCROP && resultCode == UCrop.RESULT_ERROR) {
            Toast.makeText(this, "Crop error", Toast.LENGTH_SHORT).show();
        }

        if (requestCode == REQUEST_GALLERY && resultCode == RESULT_CANCELED) {
            if (currentPhotoBitmap == null) {
                setResult(RESULT_CANCELED);
                finish();
            }
        }
    }
}