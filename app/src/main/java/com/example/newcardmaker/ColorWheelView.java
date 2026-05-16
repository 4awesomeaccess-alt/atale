package com.example.newcardmaker;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Color Wheel View — HSV based circular color picker
 * Center = brightness slider (black to selected hue)
 * Outer ring = hue wheel
 */
public class ColorWheelView extends View {

    public interface OnColorChangedListener {
        void onColorChanged(int color);
    }

    private Paint huePaint;
    private Paint centerPaint;
    private Paint selectorPaint;
    private Paint borderPaint;

    private int[] hueColors;
    private SweepGradient hueShader;
    private RadialGradient centerShader;

    private float centerX, centerY;
    private float outerRadius, innerRadius;

    private float selectedHue = 0f;
    private float selectedSat = 1f;
    private float selectedVal = 1f;

    private float selectorX, selectorY;

    private OnColorChangedListener listener;

    public ColorWheelView(Context context) {
        super(context);
        init();
    }

    public ColorWheelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        huePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectorPaint.setStyle(Paint.Style.STROKE);
        selectorPaint.setStrokeWidth(3f);
        selectorPaint.setColor(Color.WHITE);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2f);
        borderPaint.setColor(Color.parseColor("#CCCCCC"));

        // Hue wheel colors (full spectrum)
        hueColors = new int[]{
            0xFFFF0000, // Red
            0xFFFF7F00, // Orange
            0xFFFFFF00, // Yellow
            0xFF00FF00, // Green
            0xFF00FFFF, // Cyan
            0xFF0000FF, // Blue
            0xFF7F00FF, // Violet
            0xFFFF00FF, // Magenta
            0xFFFF0000  // Red again (close the circle)
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        centerX = w / 2f;
        centerY = h / 2f;
        outerRadius = Math.min(w, h) / 2f - 4f;
        innerRadius = outerRadius * 0.65f;

        // Hue ring gradient
        hueShader = new SweepGradient(centerX, centerY, hueColors, null);

        // Initial selector position (top = 0° hue)
        selectorX = centerX;
        selectorY = centerY - (outerRadius + innerRadius) / 2f;

        // Center saturation/brightness radial
        updateCenterGradient();
    }

    private void updateCenterGradient() {
        int pureHue = Color.HSVToColor(new float[]{selectedHue, 1f, 1f});
        centerShader = new RadialGradient(
                centerX, centerY, innerRadius - 4f,
                new int[]{Color.WHITE, pureHue, Color.BLACK},
                new float[]{0f, 0.5f, 1f},
                Shader.TileMode.CLAMP
        );
        centerPaint.setShader(centerShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (outerRadius <= 0) return;

        // ── Draw Hue Ring ──
        huePaint.setShader(hueShader);
        huePaint.setStyle(Paint.Style.FILL);

        Path ringPath = new Path();
        RectF outerRect = new RectF(centerX - outerRadius, centerY - outerRadius,
                centerX + outerRadius, centerY + outerRadius);
        RectF innerRect = new RectF(centerX - innerRadius, centerY - innerRadius,
                centerX + innerRadius, centerY + innerRadius);
        ringPath.addOval(outerRect, Path.Direction.CW);
        ringPath.addOval(innerRect, Path.Direction.CCW);
        canvas.drawPath(ringPath, huePaint);

        // ── Draw Center Circle ──
        if (centerShader != null) {
            canvas.drawCircle(centerX, centerY, innerRadius - 4f, centerPaint);
        }

        // ── Draw Hue Selector ──
        canvas.drawCircle(selectorX, selectorY, 10f, selectorPaint);

        // ── Draw Center color dot ──
        Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setColor(getCurrentColor());
        float dotX = centerX + (selectedSat - 0.5f) * 2f * (innerRadius - 12f) * 0.7f;
        float dotY = centerY - (selectedVal - 0.5f) * 2f * (innerRadius - 12f) * 0.7f;
        canvas.drawCircle(dotX, dotY, 9f, dotPaint);
        selectorPaint.setStrokeWidth(2f);
        canvas.drawCircle(dotX, dotY, 9f, selectorPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        float dx = x - centerX;
        float dy = y - centerY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (event.getAction() == MotionEvent.ACTION_DOWN ||
            event.getAction() == MotionEvent.ACTION_MOVE) {

            if (dist >= innerRadius && dist <= outerRadius) {
                // Hue ring touched
                float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
                if (angle < 0) angle += 360f;
                selectedHue = angle;

                selectorX = centerX + ((outerRadius + innerRadius) / 2f) *
                        (float) Math.cos(Math.toRadians(angle));
                selectorY = centerY + ((outerRadius + innerRadius) / 2f) *
                        (float) Math.sin(Math.toRadians(angle));

                updateCenterGradient();
                notifyListener();
                invalidate();
                return true;

            } else if (dist < innerRadius - 4f) {
                // Center circle touched — saturation & brightness
                selectedSat = Math.max(0f, Math.min(1f, 0.5f + dx / (innerRadius - 12f) / 0.7f / 2f));
                selectedVal = Math.max(0f, Math.min(1f, 0.5f - dy / (innerRadius - 12f) / 0.7f / 2f));
                notifyListener();
                invalidate();
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    private void notifyListener() {
        if (listener != null) {
            listener.onColorChanged(getCurrentColor());
        }
    }

    public int getCurrentColor() {
        return Color.HSVToColor(new float[]{selectedHue, selectedSat, selectedVal});
    }

    public void setColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        selectedHue = hsv[0];
        selectedSat = hsv[1];
        selectedVal = hsv[2];

        if (outerRadius > 0) {
            selectorX = centerX + ((outerRadius + innerRadius) / 2f) *
                    (float) Math.cos(Math.toRadians(selectedHue));
            selectorY = centerY + ((outerRadius + innerRadius) / 2f) *
                    (float) Math.sin(Math.toRadians(selectedHue));
            updateCenterGradient();
        }
        invalidate();
    }

    public void setOnColorChangedListener(OnColorChangedListener l) {
        this.listener = l;
    }
}
