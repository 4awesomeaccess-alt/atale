package com.example.newcardmaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;

public class ArcTextView extends StrokeTextView {

    private float arcAngle  = 180f;  // 30=slight, 180=half, 360=full
    private float radius    = 200f;  // circle radius
    private boolean arcUp   = true;  // true=ઉપર, false=નીચે
    private boolean arcMode = true;  // true=arc, false=normal straight

    public ArcTextView(Context context) {
        super(context);
        init();
    }

    public ArcTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setBackground(null);
    }

    // ── Setters
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

    public void setArcMode(boolean arcMode) {
        this.arcMode = arcMode;
        invalidate();
        requestLayout();
    }

    // ── Getters
    public float getArcAngle()  { return arcAngle; }
    public float getRadius()    { return radius; }
    public boolean isArcUp()    { return arcUp; }
    public boolean isArcMode()  { return arcMode; }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (arcMode) {
            // Arc mode — circle size
            int size = (int) (radius * 2 + getTextSize() * 2 + 40);
            setMeasuredDimension(size, size);
        } else {
            // Normal mode — default TextView measure
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!arcMode) {
            // Normal text — StrokeTextView ની onDraw() call
            super.onDraw(canvas);
            return;
        }

        // ── Arc mode drawing
        float cx = getWidth()  / 2f;
        float cy = getHeight() / 2f;

        RectF oval = new RectF(
                cx - radius,
                cy - radius,
                cx + radius,
                cy + radius
        );

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

        // ── Stroke (outline) — StrokeTextView ની stroke settings વાપરો
        if (getStrokeWidth() > 0) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(getStrokeWidth());
            paint.setColor(getStrokeColor());
            canvas.drawTextOnPath(getText().toString(), path, 0, 0, paint);
        }

        // ── Fill (normal text)
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getCurrentTextColor());
        canvas.drawTextOnPath(getText().toString(), path, 0, 0, paint);

        // Paint reset
        paint.setStyle(Paint.Style.FILL);
    }
}