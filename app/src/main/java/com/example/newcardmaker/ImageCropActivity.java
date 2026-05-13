package com.example.newcardmaker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ImageCropActivity extends AppCompatActivity {

    private ImageView       ivCropImage;
    private CropOverlayView cropOverlay;
    private Button          btnApply;
    private Button          btnCancel;
    private Button          btnReset;
    private Button          btnRotateLeft;
    private Button          btnRotateRight;
    private Button          btnFlipH;
    private Button          btnFlipV;

    private Bitmap originalBitmap;  // never modify
    private Bitmap workingBitmap;   // rotate/flip apply
    private String  imagePath;
    private String  imageUri;

    // ── Aspect ratio
    private float lockedAspectW = 0f;
    private float lockedAspectH = 0f;
    private boolean isFreeMode  = true;

    // ── Current rotation + flip
    private int   rotateDeg = 0;
    private boolean flipH   = false;
    private boolean flipV   = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_crop);

        imagePath = getIntent().getStringExtra("imagePath");
        imageUri  = getIntent().getStringExtra("imageUri");

        // ── Bind
        ivCropImage  = findViewById(R.id.iv_crop_image);
        cropOverlay  = findViewById(R.id.crop_overlay);
        btnApply     = findViewById(R.id.btn_crop_apply);
        btnCancel    = findViewById(R.id.btn_crop_cancel);
        btnReset     = findViewById(R.id.btn_crop_reset);
        btnRotateLeft  = findViewById(R.id.btn_rotate_left);
        btnRotateRight = findViewById(R.id.btn_rotate_right);
        btnFlipH     = findViewById(R.id.btn_flip_h);
        btnFlipV     = findViewById(R.id.btn_flip_v);

        // ── Aspect ratio buttons
        setupAspectRatioButtons();

        // ── Load bitmap
        loadBitmap();

        // ── Rotate Left
        btnRotateLeft.setOnClickListener(v -> {
            rotateDeg = (rotateDeg - 90 + 360) % 360;
            applyTransform();
        });

        // ── Rotate Right
        btnRotateRight.setOnClickListener(v -> {
            rotateDeg = (rotateDeg + 90) % 360;
            applyTransform();
        });

        // ── Flip Horizontal
        btnFlipH.setOnClickListener(v -> {
            flipH = !flipH;
            btnFlipH.setAlpha(flipH ? 1f : 0.5f);
            applyTransform();
        });

        // ── Flip Vertical
        btnFlipV.setOnClickListener(v -> {
            flipV = !flipV;
            btnFlipV.setAlpha(flipV ? 1f : 0.5f);
            applyTransform();
        });

        // ── Reset
        btnReset.setOnClickListener(v -> {
            rotateDeg = 0;
            flipH     = false;
            flipV     = false;
            btnFlipH.setAlpha(0.5f);
            btnFlipV.setAlpha(0.5f);
            workingBitmap = originalBitmap.copy(
                    Bitmap.Config.ARGB_8888, false);
            ivCropImage.setImageBitmap(workingBitmap);
            resetCropOverlay();
        });

        // ── Apply
        btnApply.setOnClickListener(v -> applyCrop());

        // ── Cancel
        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }

    // ════════════════════════════════
    // Bitmap Load
    // ════════════════════════════════

    private void loadBitmap() {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;

            if (imagePath != null) {
                originalBitmap = BitmapFactory.decodeFile(imagePath, opts);
            } else if (imageUri != null) {
                InputStream is = getContentResolver()
                        .openInputStream(Uri.parse(imageUri));
                originalBitmap = BitmapFactory.decodeStream(
                        is, null, opts);
                if (is != null) is.close();
            }

            if (originalBitmap == null) {
                Toast.makeText(this, "Image load error",
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            workingBitmap = originalBitmap.copy(
                    Bitmap.Config.ARGB_8888, false);
            ivCropImage.setImageBitmap(workingBitmap);

            ivCropImage.post(() -> resetCropOverlay());

        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    // ════════════════════════════════
    // Aspect Ratio Buttons
    // ════════════════════════════════

    private void setupAspectRatioButtons() {
        HorizontalScrollView scroll = findViewById(R.id.scroll_aspect);
        LinearLayout container = scroll.findViewById(R.id.ll_aspect_buttons);

        String[][] ratios = {
                {"Free",   "0:0"},
                {"1:1",    "1:1"},
                {"4:3",    "4:3"},
                {"3:4",    "3:4"},
                {"16:9",   "16:9"},
                {"9:16",   "9:16"},
                {"3:2",    "3:2"},
                {"2:3",    "2:3"},
                {"5:4",    "5:4"},
                {"Original","O:O"},
        };

        for (String[] ratio : ratios) {
            final String label = ratio[0];
            final String value = ratio[1];

            TextView btn = new TextView(this);
            btn.setText(label);
            btn.setTextSize(12);
            btn.setTextColor(0xFF1565C0);
            btn.setPadding(20, 10, 20, 10);
            btn.setGravity(android.view.Gravity.CENTER);

            android.graphics.drawable.GradientDrawable gd =
                    new android.graphics.drawable.GradientDrawable();
            gd.setColor(0xFFE3F2FD);
            gd.setStroke(2, 0xFF1565C0);
            gd.setCornerRadius(20f);
            btn.setBackground(gd);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(6, 6, 6, 6);
            btn.setLayoutParams(lp);

            btn.setOnClickListener(v -> {
                // ── Highlight selected
                for (int i = 0; i < container.getChildCount(); i++) {
                    View child = container.getChildAt(i);
                    if (child instanceof TextView) {
                        android.graphics.drawable.GradientDrawable bgd =
                                new android.graphics.drawable.GradientDrawable();
                        bgd.setColor(child == btn
                                ? 0xFF1565C0 : 0xFFE3F2FD);
                        bgd.setStroke(2, 0xFF1565C0);
                        bgd.setCornerRadius(20f);
                        child.setBackground(bgd);
                        ((TextView) child).setTextColor(
                                child == btn
                                        ? 0xFFFFFFFF : 0xFF1565C0);
                    }
                }

                // ── Ratio apply
                applyAspectRatio(value);
            });

            container.addView(btn);
        }
    }

    private void applyAspectRatio(String value) {
        if (value.equals("0:0")) {
            // ── Free mode
            isFreeMode    = true;
            lockedAspectW = 0f;
            lockedAspectH = 0f;
            cropOverlay.setAspectRatioLock(0f, 0f);

        } else if (value.equals("O:O")) {
            // ── Original ratio
            isFreeMode    = false;
            lockedAspectW = workingBitmap.getWidth();
            lockedAspectH = workingBitmap.getHeight();
            cropOverlay.setAspectRatioLock(lockedAspectW, lockedAspectH);

        } else {
            // ── Fixed ratio
            String[] parts = value.split(":");
            isFreeMode    = false;
            lockedAspectW = Float.parseFloat(parts[0]);
            lockedAspectH = Float.parseFloat(parts[1]);
            cropOverlay.setAspectRatioLock(lockedAspectW, lockedAspectH);
        }

        // ── Crop rect reset with new ratio
        resetCropOverlay();
    }

    // ════════════════════════════════
    // Transform (Rotate + Flip)
    // ════════════════════════════════

    private void applyTransform() {
        if (originalBitmap == null) return;

        Matrix matrix = new Matrix();

        // Rotate
        matrix.postRotate(rotateDeg);

        // Flip
        if (flipH) matrix.postScale(-1f, 1f);
        if (flipV) matrix.postScale(1f, -1f);

        workingBitmap = Bitmap.createBitmap(
                originalBitmap,
                0, 0,
                originalBitmap.getWidth(),
                originalBitmap.getHeight(),
                matrix,
                true);

        ivCropImage.setImageBitmap(workingBitmap);

        // ── Crop overlay reset
        ivCropImage.post(() -> resetCropOverlay());
    }

    // ════════════════════════════════
    // Crop Overlay Reset
    // ════════════════════════════════

    private void resetCropOverlay() {
        if (workingBitmap == null) return;
        RectF bounds = getImageBounds(ivCropImage, workingBitmap);
        cropOverlay.setImageBounds(bounds);

        // Ratio lock re-apply
        cropOverlay.setAspectRatioLock(lockedAspectW, lockedAspectH);
    }

    // ── ImageView actual image bounds
    private RectF getImageBounds(ImageView iv, Bitmap bmp) {
        float vW = iv.getWidth();
        float vH = iv.getHeight();
        float bW = bmp.getWidth();
        float bH = bmp.getHeight();

        float scale = Math.min(vW / bW, vH / bH);
        float imgW  = bW * scale;
        float imgH  = bH * scale;
        float left  = (vW - imgW) / 2f;
        float top   = (vH - imgH) / 2f;

        return new RectF(left, top, left + imgW, top + imgH);
    }

    // ════════════════════════════════
    // Apply Crop
    // ════════════════════════════════

    private void applyCrop() {
        try {
            if (workingBitmap == null) return;

            RectF imageBounds = getImageBounds(
                    ivCropImage, workingBitmap);
            Rect cropRect = cropOverlay.getCropRectInBitmap(
                    workingBitmap, imageBounds);

            if (cropRect == null) {
                Toast.makeText(this, "Invalid crop",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            int w = cropRect.right  - cropRect.left;
            int h = cropRect.bottom - cropRect.top;

            if (w < 10 || h < 10) {
                Toast.makeText(this, "Crop area too small",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // ── Crop
            Bitmap cropped = Bitmap.createBitmap(
                    workingBitmap,
                    cropRect.left, cropRect.top,
                    w, h);

            // ── PNG check
            boolean isPng = originalBitmap.hasAlpha() ||
                    (imagePath != null &&
                            imagePath.toLowerCase().endsWith(".png"));

            // ── Save
            File outDir = new File(getCacheDir(), "cropped");
            if (!outDir.exists()) outDir.mkdirs();

            String ext    = isPng ? ".png" : ".jpg";
            File outFile  = new File(outDir,
                    "crop_" + System.currentTimeMillis() + ext);

            FileOutputStream fos = new FileOutputStream(outFile);
            if (isPng) {
                cropped.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } else {
                cropped.compress(Bitmap.CompressFormat.JPEG, 95, fos);
            }
            fos.flush();
            fos.close();
            cropped.recycle();

            Intent result = new Intent();
            result.putExtra("croppedPath", outFile.getAbsolutePath());
            result.putExtra("isPng", isPng);
            setResult(RESULT_OK, result);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Crop error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}