package com.example.newcardmaker;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Color Wheel View — Redesigned HSV circular color picker
 * Outer ring = smooth hue spectrum
 * Inner circle = saturation (X) + brightness (Y)
 * Thick ring border, glowing selectors, drop shadow
 */
public class ColorWheelView extends View {

    public interface OnColorChangedListener {
        void onColorChanged(int color);
    }

    private Paint huePaint;
    private Paint centerPaint;
    private Paint selectorRingPaint;
    private Paint selectorFillPaint;
    private Paint shadowPaint;
    private Paint borderPaint;
    private Paint centerDotPaint;
    private Paint centerDotBorderPaint;

    private int[] hueColors;
    private SweepGradient hueShader;
    private RadialGradient centerShader;

    private float centerX, centerY;
    private float outerRadius, innerRadius;
    private float ringThickness;

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
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    private void init() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);

        // Hue ring paint
        huePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        huePaint.setStyle(Paint.Style.FILL);

        // Center saturation/brightness paint
        centerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerPaint.setStyle(Paint.Style.FILL);

        // Hue selector — outer white ring
        selectorRingPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectorRingPaint.setStyle(Paint.Style.STROKE);
        selectorRingPaint.setStrokeWidth(3f);
        selectorRingPaint.setColor(Color.WHITE);
        selectorRingPaint.setShadowLayer(4f, 0, 0, Color.parseColor("#88000000"));

        // Hue selector — inner color fill
        selectorFillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectorFillPaint.setStyle(Paint.Style.FILL);

        // Shadow under wheel
        shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        shadowPaint.setStyle(Paint.Style.FILL);
        shadowPaint.setColor(Color.parseColor("#33000000"));
        shadowPaint.setShadowLayer(12f, 0, 4f, Color.parseColor("#55000000"));

        // Outer border of ring
        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(1.5f);
        borderPaint.setColor(Color.parseColor("#33FFFFFF"));

        // Center dot fill (current color)
        centerDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerDotPaint.setStyle(Paint.Style.FILL);

        // Center dot border
        centerDotBorderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        centerDotBorderPaint.setStyle(Paint.Style.STROKE);
        centerDotBorderPaint.setStrokeWidth(2.5f);
        centerDotBorderPaint.setColor(Color.WHITE);
        centerDotBorderPaint.setShadowLayer(6f, 0, 0, Color.parseColor("#88000000"));

        // Full smooth hue spectrum — 37 stops
        hueColors = new int[]{
            0xFFFF0000, // Red 0°
            0xFFFF2000,
            0xFFFF4000,
            0xFFFF6000,
            0xFFFF8000, // Orange 30°
            0xFFFFA000,
            0xFFFFBF00,
            0xFFFFD400,
            0xFFFFFF00, // Yellow 60°
            0xFFCCFF00,
            0xFF80FF00,
            0xFF40FF00,
            0xFF00FF00, // Green 120°
            0xFF00FF40,
            0xFF00FF80,
            0xFF00FFBF,
            0xFF00FFFF, // Cyan 180°
            0xFF00BFFF,
            0xFF0080FF,
            0xFF0040FF,
            0xFF0000FF, // Blue 240°
            0xFF4000FF,
            0xFF8000FF,
            0xFFBF00FF,
            0xFFFF00FF, // Magenta 300°
            0xFFFF00BF,
            0xFFFF0080,
            0xFFFF0040,
            0xFFFF0000  // Red again
        };
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        centerX = w / 2f;
        centerY = h / 2f;
        outerRadius = Math.min(w, h) / 2f - 8f;
        ringThickness = outerRadius * 0.22f; // Thicker ring
        innerRadius = outerRadius - ringThickness;

        hueShader = new SweepGradient(centerX, centerY, hueColors, null);

        // Initial selector on ring
        selectorX = centerX + (outerRadius - ringThickness / 2f);
        selectorY = centerY;

        updateCenterGradient();
    }

    private void updateCenterGradient() {
        if (innerRadius <= 1f) return; // guard: radius must be > 0
        int pureHue = Color.HSVToColor(new float[]{selectedHue, 1f, 1f});

        // Horizontal: white → hue (saturation)
        // Vertical: top bright → bottom dark (value)
        // Simulated with two overlapping gradients
        centerShader = new RadialGradient(
                centerX, centerY, innerRadius - 2f,
                new int[]{
                    Color.WHITE,
                    adjustAlpha(pureHue, 200),
                    Color.BLACK
                },
                new float[]{0f, 0.55f, 1f},
                Shader.TileMode.CLAMP
        );
        centerPaint.setShader(centerShader);
    }

    private int adjustAlpha(int color, int alpha) {
        return (color & 0x00FFFFFF) | (alpha << 24);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (outerRadius <= 0) return;

        // ── Shadow under wheel ──
        canvas.drawCircle(centerX, centerY + 3f, outerRadius + 2f, shadowPaint);

        // ── Hue Ring ──
        huePaint.setShader(hueShader);

        Path ringPath = new Path();
        RectF outerRect = new RectF(
            centerX - outerRadius, centerY - outerRadius,
            centerX + outerRadius, centerY + outerRadius);
        RectF innerRect = new RectF(
            centerX - innerRadius, centerY - innerRadius,
            centerX + innerRadius, centerY + innerRadius);
        ringPath.setFillType(Path.FillType.EVEN_ODD);
        ringPath.addOval(outerRect, Path.Direction.CW);
        ringPath.addOval(innerRect, Path.Direction.CCW);
        canvas.drawPath(ringPath, huePaint);

        // ── Outer border ──
        canvas.drawCircle(centerX, centerY, outerRadius, borderPaint);
        canvas.drawCircle(centerX, centerY, innerRadius, borderPaint);

        // ── Center circle ──
        if (centerShader != null) {
            canvas.drawCircle(centerX, centerY, innerRadius - 2f, centerPaint);
        }

        // ── Hue Selector (circle on ring) ──
        float selectorRadius = ringThickness * 0.48f;

        // Shadow
        Paint selectorShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        selectorShadow.setStyle(Paint.Style.FILL);
        selectorShadow.setColor(Color.parseColor("#44000000"));
        selectorShadow.setShadowLayer(8f, 0, 2f, Color.parseColor("#66000000"));
        canvas.drawCircle(selectorX, selectorY, selectorRadius + 1f, selectorShadow);

        // Fill with hue color
        selectorFillPaint.setColor(Color.HSVToColor(new float[]{selectedHue, 1f, 1f}));
        canvas.drawCircle(selectorX, selectorY, selectorRadius, selectorFillPaint);

        // White border
        canvas.drawCircle(selectorX, selectorY, selectorRadius, selectorRingPaint);

        // ── Center dot (current color) ──
        float dotX = getCenterDotX();
        float dotY = getCenterDotY();
        float dotR = innerRadius * 0.14f;

        // Shadow
        Paint dotShadow = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotShadow.setStyle(Paint.Style.FILL);
        dotShadow.setColor(Color.parseColor("#44000000"));
        dotShadow.setShadowLayer(6f, 0, 2f, Color.parseColor("#66000000"));
        canvas.drawCircle(dotX, dotY, dotR + 1f, dotShadow);

        // Dot fill
        centerDotPaint.setColor(getCurrentColor());
        canvas.drawCircle(dotX, dotY, dotR, centerDotPaint);

        // Dot white border
        canvas.drawCircle(dotX, dotY, dotR, centerDotBorderPaint);
    }

    private float getCenterDotX() {
        float r = (innerRadius - 4f) * 0.85f;
        return centerX + (selectedSat * 2f - 1f) * r;
    }

    private float getCenterDotY() {
        float r = (innerRadius - 4f) * 0.85f;
        return centerY - (selectedVal * 2f - 1f) * r;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() != MotionEvent.ACTION_DOWN &&
            event.getAction() != MotionEvent.ACTION_MOVE) {
            return super.onTouchEvent(event);
        }

        float x = event.getX();
        float y = event.getY();
        float dx = x - centerX;
        float dy = y - centerY;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        if (dist >= innerRadius && dist <= outerRadius) {
            // ── Hue ring ──
            float angle = (float) Math.toDegrees(Math.atan2(dy, dx));
            if (angle < 0) angle += 360f;
            selectedHue = angle;

            float midR = (outerRadius + innerRadius) / 2f;
            selectorX = centerX + midR * (float) Math.cos(Math.toRadians(angle));
            selectorY = centerY + midR * (float) Math.sin(Math.toRadians(angle));

            updateCenterGradient();
            notifyListener();
            invalidate();
            return true;

        } else if (dist < innerRadius - 2f) {
            // ── Saturation / Value ──
            float r = (innerRadius - 4f) * 0.85f;
            selectedSat = Math.max(0f, Math.min(1f, 0.5f + dx / r / 2f));
            selectedVal = Math.max(0f, Math.min(1f, 0.5f - dy / r / 2f));
            notifyListener();
            invalidate();
            return true;
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
            float midR = (outerRadius + innerRadius) / 2f;
            selectorX = centerX + midR * (float) Math.cos(Math.toRadians(selectedHue));
            selectorY = centerY + midR * (float) Math.sin(Math.toRadians(selectedHue));
            updateCenterGradient();
        }
        invalidate();
    }

    public void setOnColorChangedListener(OnColorChangedListener l) {
        this.listener = l;
    }
}
