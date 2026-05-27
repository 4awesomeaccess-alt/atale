package com.example.newcardmaker.Activity;

import static android.util.Log.ASSERT;

import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newcardmaker.R;
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class EditImageActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_URI = "image_uri";
    public static final String RESULT_EDITED_PATH = "edited_path";
    public static final int REQUEST_CODE = 2001;

    private static final String BG_REMOVE_URL = "http://168.144.87.18/remove-bg";

    private ImageView ivPreview;
    private Bitmap originalBitmap, editedBitmap;
    private float brightness = 0f, contrast = 1f, saturation = 1f;
    private int rotation = 0;
    private boolean flipH = false, flipV = false;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private android.app.ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_image);

        ivPreview = findViewById(R.id.iv_edit_preview);
        String uriStr = getIntent().getStringExtra(EXTRA_IMAGE_URI);
        if (uriStr == null) { finish(); return; }

        try {
            InputStream is = getContentResolver().openInputStream(Uri.parse(uriStr));
            originalBitmap = BitmapFactory.decodeStream(is);
            editedBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            ivPreview.setImageBitmap(editedBitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Image load error!", Toast.LENGTH_SHORT).show();
            finish(); return;
        }
        setupControls();
    }

    private void setupControls() {
        setupSeekBar(R.id.sb_brightness, R.id.tv_brightness_val, 100, p -> {
            brightness = p - 100; return String.valueOf((int)brightness);
        });
        setupSeekBar(R.id.sb_contrast, R.id.tv_contrast_val, 100, p -> {
            contrast = p / 100f; return String.format("%.1f", contrast);
        });
        setupSeekBar(R.id.sb_saturation, R.id.tv_saturation_val, 100, p -> {
            saturation = p / 100f; return String.format("%.1f", saturation);
        });

        findViewById(R.id.btn_rotate_left).setOnClickListener(v -> { rotation=(rotation-90+360)%360; applyEdits(); });
        findViewById(R.id.btn_rotate_right).setOnClickListener(v -> { rotation=(rotation+90)%360; applyEdits(); });
        findViewById(R.id.btn_flip_h).setOnClickListener(v -> { flipH=!flipH; applyEdits(); });
        findViewById(R.id.btn_flip_v).setOnClickListener(v -> { flipV=!flipV; applyEdits(); });

        // ── Remove BG via API
        findViewById(R.id.btn_remove_bg).setOnClickListener(v -> {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Background Remove")
                .setMessage("શું background remove કરવું છે?")
                .setPositiveButton("હા, Remove કરો", (d, w) -> removeBackgroundAPI())
                .setNegativeButton("ના", null)
                .show();
        });

        findViewById(R.id.btn_reset).setOnClickListener(v -> {
            brightness=0; contrast=1; saturation=1; rotation=0; flipH=false; flipV=false;
            ((SeekBar)findViewById(R.id.sb_brightness)).setProgress(100);
            ((SeekBar)findViewById(R.id.sb_contrast)).setProgress(100);
            ((SeekBar)findViewById(R.id.sb_saturation)).setProgress(100);
            editedBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            ivPreview.setImageBitmap(editedBitmap);
        });

        findViewById(R.id.btn_save_edit).setOnClickListener(v -> saveAndReturn());
        findViewById(R.id.btn_cancel_edit).setOnClickListener(v -> finish());
    }

    interface SeekCallback { String onChange(int p); }

    private void setupSeekBar(int seekId, int tvId, int defaultProgress, SeekCallback cb) {
        SeekBar sb = findViewById(seekId);
        TextView tv = findViewById(tvId);
        sb.setMax(200); sb.setProgress(defaultProgress);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar s, int p, boolean f) {
                if (tv != null) tv.setText(cb.onChange(p));
                applyEdits();
            }
            @Override public void onStartTrackingTouch(SeekBar s) {}
            @Override public void onStopTrackingTouch(SeekBar s) {}
        });
    }

    private void applyEdits() {
        if (originalBitmap == null) return;
        Matrix matrix = new Matrix();
        if (flipH) matrix.preScale(-1, 1, originalBitmap.getWidth()/2f, originalBitmap.getHeight()/2f);
        if (flipV) matrix.preScale(1, -1, originalBitmap.getWidth()/2f, originalBitmap.getHeight()/2f);
        matrix.postRotate(rotation);
        Bitmap rotated = Bitmap.createBitmap(originalBitmap, 0, 0,
            originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, true);
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(saturation);
        float c = contrast, b = brightness;
        float[] arr = { c,0,0,0,b, 0,c,0,0,b, 0,0,c,0,b, 0,0,0,1,0 };
        cm.postConcat(new ColorMatrix(arr));
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        editedBitmap = Bitmap.createBitmap(rotated.getWidth(), rotated.getHeight(), Bitmap.Config.ARGB_8888);
        new Canvas(editedBitmap).drawBitmap(rotated, 0, 0, paint);
        ivPreview.setImageBitmap(editedBitmap);
    }

    // ── API Background Remove (same as BgRemoveActivity)
    private void removeBackgroundAPI() {
        if (editedBitmap == null) return;

        progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("⏳ Background remove thaay che...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        final Bitmap bitmapToSend = editedBitmap.copy(Bitmap.Config.ARGB_8888, false);

        executor.execute(() -> {
            HttpURLConnection conn = null;
            try {
                // Convert bitmap to PNG bytes
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmapToSend.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] bytes = baos.toByteArray();

                String boundary = "----Boundary" + System.currentTimeMillis();
                URL url = new URL(BG_REMOVE_URL);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setConnectTimeout(30000);
                conn.setReadTimeout(180000);
                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                OutputStream os = conn.getOutputStream();
                java.io.PrintWriter writer = new java.io.PrintWriter(
                    new java.io.OutputStreamWriter(os, "UTF-8"), true);
                writer.append("--").append(boundary).append("\r\n");
                writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"image.png\"\r\n");
                writer.append("Content-Type: image/png\r\n\r\n");
                writer.flush();
                os.write(bytes);
                os.flush();
                writer.append("\r\n");
                writer.append("--").append(boundary).append("--").append("\r\n");
                writer.flush();
                writer.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = conn.getInputStream();
                    final Bitmap result = BitmapFactory.decodeStream(is);
                    is.close();

                    mainHandler.post(() -> {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        if (result != null) {
                            editedBitmap = result;
                            ivPreview.setImageBitmap(editedBitmap);
                            Toast.makeText(this, "✅ Background Remove Thaytu!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Image decode failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    mainHandler.post(() -> {
                        if (progressDialog != null && progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(this, "Error: " + responseCode, Toast.LENGTH_LONG).show();
                    });
                }
            } catch (Exception e) {
                mainHandler.post(() -> {
                    if (progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    Log.println(ASSERT,"Error",e.getMessage()+"");

                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } finally {
                if (conn != null) conn.disconnect();
            }
        });
    }

    private void saveAndReturn() {
        if (editedBitmap == null) { finish(); return; }
        try {
            // ✅ Trim transparent edges
            Bitmap trimmed = trimBitmap(editedBitmap);
            File file = new File(getCacheDir(), "edited_" + System.currentTimeMillis() + ".png");
            FileOutputStream fos = new FileOutputStream(file);
            trimmed.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Intent result = new Intent();
            result.putExtra(RESULT_EDITED_PATH, file.getAbsolutePath());
            setResult(RESULT_OK, result);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Save error!", Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap trimBitmap(Bitmap bmp) {
        int w = bmp.getWidth(), h = bmp.getHeight();
        int[] pixels = new int[w * h];
        bmp.getPixels(pixels, 0, w, 0, 0, w, h);
        int minX = w, minY = h, maxX = 0, maxY = 0;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int alpha = (pixels[y * w + x] >> 24) & 0xFF;
                if (alpha > 0) {
                    if (x < minX) minX = x;
                    if (x > maxX) maxX = x;
                    if (y < minY) minY = y;
                    if (y > maxY) maxY = y;
                }
            }
        }
        if (maxX < minX || maxY < minY) return bmp;
        return Bitmap.createBitmap(bmp, minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
        if (progressDialog != null && progressDialog.isShowing()) progressDialog.dismiss();
    }
}
