package com.example.newcardmaker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FreehandCropActivity extends AppCompatActivity {

    private ImageView        ivImage;
    private FreehandCropView freehandView;
    private Button           btnApply;
    private Button           btnCancel;
    private Button           btnClear;
    private TextView         tabFreehand;
    private TextView         tabPolygon;
    private TextView         tvHint;
    private TextView         tvPolygonHint;

    private Bitmap originalBitmap;
    private String  imagePath;
    private String  imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_freehand_crop);

        imagePath = getIntent().getStringExtra("imagePath");
        imageUri  = getIntent().getStringExtra("imageUri");

        ivImage       = findViewById(R.id.iv_freehand_image);
        freehandView  = findViewById(R.id.freehand_crop_view);
        btnApply      = findViewById(R.id.btn_freehand_apply);
        btnCancel     = findViewById(R.id.btn_freehand_cancel);
        btnClear      = findViewById(R.id.btn_freehand_clear);
        tabFreehand   = findViewById(R.id.tab_freehand);
        tabPolygon    = findViewById(R.id.tab_polygon);
        tvHint        = findViewById(R.id.tv_hint);
        tvPolygonHint = findViewById(R.id.tv_polygon_hint);

        // ── Load
        loadBitmap();

        // ── Selection done
        freehandView.setOnSelectionDoneListener((path, bounds) -> {
            btnApply.setEnabled(true);
            btnApply.setAlpha(1f);
            tvHint.setText("✅ Selected! Apply tap કરો");
        });

        // ── Tabs
        tabFreehand.setOnClickListener(v -> switchMode(
                FreehandCropView.Mode.FREEHAND));
        tabPolygon.setOnClickListener(v -> switchMode(
                FreehandCropView.Mode.POLYGON));

        // ── Buttons
        btnApply.setEnabled(false);
        btnApply.setAlpha(0.5f);
        btnApply.setOnClickListener(v -> applyFreehandCrop());
        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
        btnClear.setOnClickListener(v -> {
            freehandView.clearSelection();
            btnApply.setEnabled(false);
            btnApply.setAlpha(0.5f);
            tvHint.setText(freehandView.getMode() ==
                    FreehandCropView.Mode.FREEHAND
                    ? "Draw to select area"
                    : "Tap to add points");
        });
    }

    private void switchMode(FreehandCropView.Mode mode) {
        freehandView.setMode(mode);
        btnApply.setEnabled(false);
        btnApply.setAlpha(0.5f);

        boolean isFree = mode == FreehandCropView.Mode.FREEHAND;

        tabFreehand.setTextColor(isFree
                ? Color.WHITE : Color.parseColor("#888888"));
        tabFreehand.setBackgroundColor(isFree
                ? Color.parseColor("#1565C0")
                : Color.parseColor("#1A1A1A"));

        tabPolygon.setTextColor(!isFree
                ? Color.WHITE : Color.parseColor("#888888"));
        tabPolygon.setBackgroundColor(!isFree
                ? Color.parseColor("#1565C0")
                : Color.parseColor("#1A1A1A"));

        tvHint.setText(isFree
                ? "Draw to select area"
                : "Tap to add points");
        tvPolygonHint.setVisibility(isFree
                ? View.GONE : View.VISIBLE);
    }

    private void loadBitmap() {
        try {
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inPreferredConfig = Bitmap.Config.ARGB_8888;

            if (imagePath != null) {
                originalBitmap = BitmapFactory
                        .decodeFile(imagePath, opts);
            } else if (imageUri != null) {
                InputStream is = getContentResolver()
                        .openInputStream(Uri.parse(imageUri));
                originalBitmap = BitmapFactory
                        .decodeStream(is, null, opts);
                if (is != null) is.close();
            }

            if (originalBitmap == null) {
                Toast.makeText(this, "Image load error",
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            ivImage.setImageBitmap(originalBitmap);

            ivImage.post(() -> {
                RectF bounds = getImageBounds();
                freehandView.setImageBounds(bounds);
            });

        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }
    }

    private RectF getImageBounds() {
        float vW = ivImage.getWidth();
        float vH = ivImage.getHeight();
        float bW = originalBitmap.getWidth();
        float bH = originalBitmap.getHeight();

        float scale = Math.min(vW / bW, vH / bH);
        float imgW  = bW * scale;
        float imgH  = bH * scale;
        float left  = (vW - imgW) / 2f;
        float top   = (vH - imgH) / 2f;

        return new RectF(left, top, left + imgW, top + imgH);
    }

    private void applyFreehandCrop() {
        if (!freehandView.hasSelection()) {
            Toast.makeText(this, "પહેલા area select કરો",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            RectF ivBounds = getImageBounds();

            // ── Path → Bitmap coordinates
            Path bmpPath = freehandView.getPathInBitmap(
                    originalBitmap, ivBounds);
            if (bmpPath == null) return;

            // ── Path bounds
            RectF pathBounds = new RectF();
            bmpPath.computeBounds(pathBounds, true);

            int cropX = Math.max(0, (int) pathBounds.left);
            int cropY = Math.max(0, (int) pathBounds.top);
            int cropW = Math.min(
                    (int) pathBounds.width(),
                    originalBitmap.getWidth() - cropX);
            int cropH = Math.min(
                    (int) pathBounds.height(),
                    originalBitmap.getHeight() - cropY);

            if (cropW < 10 || cropH < 10) {
                Toast.makeText(this, "Selection too small",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // ── Result bitmap — same size as bounds
            Bitmap result = Bitmap.createBitmap(
                    cropW, cropH, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(result);

            // ── Path translate to crop origin
            Matrix translateMatrix = new Matrix();
            translateMatrix.setTranslate(-cropX, -cropY);
            Path localPath = new Path(bmpPath);
            localPath.transform(translateMatrix);

            // ── Clip to path
            canvas.clipPath(localPath);

            // ── Draw original image (cropped region)
            canvas.drawBitmap(originalBitmap,
                    new Rect(cropX, cropY,
                            cropX + cropW,
                            cropY + cropH),
                    new Rect(0, 0, cropW, cropH),
                    null);

            // ── Save as PNG (transparency maintain)
            File outDir = new File(getCacheDir(), "freehand");
            if (!outDir.exists()) outDir.mkdirs();

            File outFile = new File(outDir,
                    "freehand_" + System.currentTimeMillis() + ".png");
            FileOutputStream fos = new FileOutputStream(outFile);
            result.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            result.recycle();

            // ── Return
            Intent intent = new Intent();
            intent.putExtra("croppedPath", outFile.getAbsolutePath());
            intent.putExtra("isPng", true);
            setResult(RESULT_OK, intent);
            finish();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this,
                    "Error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }
}