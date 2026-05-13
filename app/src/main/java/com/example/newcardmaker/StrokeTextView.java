package com.example.newcardmaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

public class StrokeTextView extends androidx.appcompat.widget.AppCompatTextView {

    private int strokeColor = Color.BLACK;
    private float strokeWidth = 0f;
    private int fillColor = Color.BLACK;
    private Shader textShader = null;

    private boolean arcMode = false;
    private float arcAngle = 180f;
    private float radius = 200f;
    private boolean arcUp = true;

    private float shadowRadius = 0f;
    private float shadowDx = 0f;
    private float shadowDy = 0f;
    private int shadowColor = Color.TRANSPARENT;

    // ✅ NEW: Image bitmap field
    private Bitmap imageBitmap = null;

    public StrokeTextView(Context context) {
        super(context);
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // ════════════════════════════════════════
    // ✅ NEW: IMAGE BITMAP SHADER
    // ════════════════════════════════════════

    public void setImageBitmap(Bitmap bitmap) {
        this.imageBitmap = bitmap;
        // Image set thay tyare size already available hoy to shader banavo
        if (getWidth() > 0 && getHeight() > 0) {
            applyImageShader();
        }
        invalidate();
    }

    public void clearImageBitmap() {
        this.imageBitmap = null;
        this.textShader = null;
        getPaint().setShader(null);
        invalidate();
    }

    private void applyImageShader() {
        if (imageBitmap == null || getWidth() == 0 || getHeight() == 0) return;

        // Bitmap ne view na exact size ma scale karo — text ma perfect fit thay
        Bitmap scaled = Bitmap.createScaledBitmap(
                imageBitmap, getWidth(), getHeight(), true
        );

        BitmapShader shader = new BitmapShader(
                scaled,
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP
        );

        this.textShader = shader;
    }

    // ════════════════════════════════════════
    // STROKE
    // ════════════════════════════════════════

    public void setStrokeColor(int color) {
        this.strokeColor = color;
        invalidate();
    }

    public void setStrokeWidth(float width) {
        this.strokeWidth = width;
        invalidate();
        requestLayout();
    }

    public int getStrokeColor() { return strokeColor; }
    public float getStrokeWidth() { return strokeWidth; }

    // ════════════════════════════════════════
    // SHADER / GRADIENT
    // ════════════════════════════════════════

    public void setTextShader(Shader shader) {
        this.imageBitmap = null; // ✅ Image clear — gradient set thay tyare
        this.textShader = shader;
        invalidate();
    }

    public void setTextGradient(Shader shader) {
        this.imageBitmap = null; // ✅ Image clear
        this.textShader = shader;
        invalidate();
    }

    public Shader getTextShader() {
        return textShader;
    }

    // ════════════════════════════════════════
    // TEXT COLOR
    // ════════════════════════════════════════

    @Override
    public void setTextColor(int color) {
        fillColor = color;
        textShader = null;
        imageBitmap = null; // ✅ Image clear — solid color set thay tyare
        getPaint().setShader(null);
        super.setTextColor(color);
    }

    // ════════════════════════════════════════
    // SHADOW
    // ════════════════════════════════════════

    @Override
    public void setShadowLayer(float radius, float dx, float dy, int color) {
        this.shadowRadius = radius;
        this.shadowDx     = dx;
        this.shadowDy     = dy;
        this.shadowColor  = color;
        super.setShadowLayer(radius, dx, dy, color);
        invalidate();
    }

    public float getShadowRadius() { return shadowRadius; }
    public float getShadowDx()     { return shadowDx; }
    public float getShadowDy()     { return shadowDy; }
    public int   getShadowColor()  { return shadowColor; }

    // ════════════════════════════════════════
    // ARC MODE
    // ════════════════════════════════════════

    public void setArcMode(boolean arcMode) {
        this.arcMode = arcMode;
        invalidate();
        requestLayout();
    }

    public void setArcAngle(float angle) {
        this.arcAngle = angle;
        invalidate();
        requestLayout();
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidate();
        requestLayout();
    }

    public void setArcUp(boolean up) {
        this.arcUp = up;
        invalidate();
    }

    public boolean isArcMode()    { return arcMode; }
    public float   getArcAngle()  { return arcAngle; }
    public float   getRadius()    { return radius; }
    public boolean isArcUp()      { return arcUp; }

    // ════════════════════════════════════════
    // MEASURE
    // ════════════════════════════════════════

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (arcMode) {
            int size = (int)(radius * 2 + getTextSize() * 2 + 40);
            setMeasuredDimension(size, size);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    // ════════════════════════════════════════
    // SIZE CHANGE — ✅ Image shader yahan banavo
    // ════════════════════════════════════════

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (imageBitmap != null) {
            // ✅ Size available thayo — hve shader banavi shakay
            applyImageShader();
        }
        if (textShader != null) {
            invalidate();
        }
    }

    // ════════════════════════════════════════
    // DRAW
    // ════════════════════════════════════════

    @Override
    protected void onDraw(Canvas canvas) {
        if (arcMode) {
            drawArcText(canvas);
        } else {
            drawNormalText(canvas);
        }
    }

    // ════════════════════════════════════════
    // NORMAL TEXT DRAW
    // ════════════════════════════════════════

    private void drawNormalText(Canvas canvas) {
        Paint paint = getPaint();

        // ── Step 1: Stroke draw
        if (strokeWidth > 0) {
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth);
            super.setTextColor(strokeColor);
            super.onDraw(canvas);

            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(0);
        }

        // ── Step 2: Fill draw
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);

        if (textShader != null) {
            paint.setShader(textShader);
            super.setTextColor(fillColor);
            super.onDraw(canvas);
            paint.setShader(null);
        } else {
            paint.setShader(null);
            super.setTextColor(fillColor);
            super.onDraw(canvas);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setShader(null);
    }

    // ════════════════════════════════════════
    // ARC TEXT DRAW
    // ════════════════════════════════════════

    private void drawArcText(Canvas canvas) {
        String text = getText().toString();
        if (text.isEmpty()) return;

        float cx = getWidth()  / 2f;
        float cy = getHeight() / 2f;

        RectF oval = new RectF(
                cx - radius, cy - radius,
                cx + radius, cy + radius);

        Path path = new Path();
        if (arcUp) {
            float startAngle = 180f + (180f - arcAngle) / 2f;
            path.addArc(oval, startAngle, arcAngle);
        } else {
            float startAngle = -(180f - arcAngle) / 2f;
            path.addArc(oval, startAngle, arcAngle);
        }

        Paint paint = getPaint();
        paint.setAntiAlias(true);

        // ── Stroke draw
        if (strokeWidth > 0) {
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth);
            paint.setColor(strokeColor);
            canvas.drawTextOnPath(text, path, 0, 0, paint);
        }

        // ── Fill draw
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);

        if (textShader != null) {
            paint.setShader(textShader);
            paint.setColor(fillColor);
            canvas.drawTextOnPath(text, path, 0, 0, paint);
            paint.setShader(null);
        } else {
            paint.setShader(null);
            paint.setColor(getCurrentTextColor());
            canvas.drawTextOnPath(text, path, 0, 0, paint);
        }

        paint.setStyle(Paint.Style.FILL);
        paint.setShader(null);
    }
}