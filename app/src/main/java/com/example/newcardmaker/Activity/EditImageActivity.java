package com.example.newcardmaker.Activity;

import android.content.Intent;
import android.graphics.*;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.newcardmaker.R;
import java.io.*;

public class EditImageActivity extends AppCompatActivity {

    public static final String EXTRA_IMAGE_URI = "image_uri";
    public static final String RESULT_EDITED_PATH = "edited_path";
    public static final int REQUEST_CODE = 2001;

    private ImageView ivPreview;
    private Bitmap originalBitmap, editedBitmap;
    private float brightness = 0f, contrast = 1f, saturation = 1f;
    private int rotation = 0;
    private boolean flipH = false, flipV = false;

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
        findViewById(R.id.btn_remove_bg).setOnClickListener(v -> removeBackground());
        findViewById(R.id.btn_reset).setOnClickListener(v -> {
            brightness=0; contrast=1; saturation=1; rotation=0; flipH=false; flipV=false;
            ((SeekBar)findViewById(R.id.sb_brightness)).setProgress(100);
            ((SeekBar)findViewById(R.id.sb_contrast)).setProgress(100);
            ((SeekBar)findViewById(R.id.sb_saturation)).setProgress(100);
            applyEdits();
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

    private void removeBackground() {
        if (editedBitmap == null) return;
        Bitmap m = editedBitmap.copy(Bitmap.Config.ARGB_8888, true);
        int bgColor = m.getPixel(0, 0);
        for (int x = 0; x < m.getWidth(); x++)
            for (int y = 0; y < m.getHeight(); y++)
                if (colorDist(m.getPixel(x,y), bgColor) < 50)
                    m.setPixel(x, y, Color.TRANSPARENT);
        editedBitmap = m;
        ivPreview.setImageBitmap(editedBitmap);
    }

    private float colorDist(int c1, int c2) {
        int r=Color.red(c1)-Color.red(c2), g=Color.green(c1)-Color.green(c2), b=Color.blue(c1)-Color.blue(c2);
        return (float)Math.sqrt(r*r+g*g+b*b);
    }

    private void saveAndReturn() {
        if (editedBitmap == null) { finish(); return; }
        try {
            File file = new File(getCacheDir(), "edited_" + System.currentTimeMillis() + ".png");
            FileOutputStream fos = new FileOutputStream(file);
            editedBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            Intent result = new Intent();
            result.putExtra(RESULT_EDITED_PATH, file.getAbsolutePath());
            setResult(RESULT_OK, result);
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Save error!", Toast.LENGTH_SHORT).show();
        }
    }
}
