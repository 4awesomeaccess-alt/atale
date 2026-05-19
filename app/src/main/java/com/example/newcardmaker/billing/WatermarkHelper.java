package com.example.newcardmaker.billing;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

public class WatermarkHelper {

    private static final String WATERMARK_TEXT = "Made with Card Maker";

    /**
     * Free user na export bitmap par watermark add karo
     */
    public static Bitmap addWatermark(Bitmap original) {
        Bitmap mutable = original.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(mutable);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.WHITE);
        paint.setAlpha(160); // semi-transparent
        paint.setTextSize(mutable.getWidth() * 0.035f);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setShadowLayer(4f, 2f, 2f, Color.BLACK);

        // Bottom-right corner ma
        float textWidth = paint.measureText(WATERMARK_TEXT);
        float x = mutable.getWidth() - textWidth - 20;
        float y = mutable.getHeight() - 20;

        canvas.drawText(WATERMARK_TEXT, x, y, paint);

        return mutable;
    }
}
