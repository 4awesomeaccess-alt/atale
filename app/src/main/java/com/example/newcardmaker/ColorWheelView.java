package com.example.newcardmaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Square 2 design: SV Box + Hue Bar (right)
 * - Large left square = Saturation (X) + Value/Brightness (Y)
 * - Right vertical bar = Hue
 */
public class ColorWheelView extends View {

    public interface OnColorChangedListener {
        void onColorChanged(int color);
    }

    private OnColorChangedListener listener;

    private final Paint svPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint svValuePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint huePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint thumbStroke = new Paint(Paint.ANTI_ALIAS_FLAG);

    private float selectedHue = 0f;   // 0..360
    private float selectedSat = 1f;   // 0..1
    private float selectedVal = 1f;   // 0..1

    private final RectF svRect = new RectF();
    private final RectF hueRect = new RectF();

    private float gap, barW, corner;

    private static final int TOUCH_SV = 1, TOUCH_HUE = 2;
    private int activeTouch = 0;

    public ColorWheelView(Context context) { super(context); init(); }
    public ColorWheelView(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setColor(Color.parseColor("#33000000"));
        borderPaint.setStrokeWidth(dp(1));

        thumbStroke.setStyle(Paint.Style.STROKE);
        thumbStroke.setColor(Color.WHITE);
        thumbStroke.setStrokeWidth(dp(2.5f));
        thumbStroke.setShadowLayer(dp(2), 0, 0, Color.parseColor("#88000000"));
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    private float dp(float v) { return v * getResources().getDisplayMetrics().density; }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        gap = dp(10);
        barW = dp(22);
        corner = dp(8);

        float pad = dp(4);
        float right = w - pad;
        float hueLeft = right - barW;
        float svRight = hueLeft - gap;

        svRect.set(pad, pad, svRight, h - pad);
        hueRect.set(hueLeft, pad, right, h - pad);

        buildShaders();
    }

    private void buildShaders() {
        if (svRect.width() <= 0 || svRect.height() <= 0) return;

        int pureHue = Color.HSVToColor(new float[]{selectedHue, 1f, 1f});
        Shader satShader = new LinearGradient(svRect.left, 0, svRect.right, 0,
                Color.WHITE, pureHue, Shader.TileMode.CLAMP);
        svPaint.setShader(satShader);
        Shader valShader = new LinearGradient(0, svRect.top, 0, svRect.bottom,
                Color.TRANSPARENT, Color.BLACK, Shader.TileMode.CLAMP);
        svValuePaint.setShader(valShader);

        int[] hues = new int[]{0xFFFF0000, 0xFFFFFF00, 0xFF00FF00, 0xFF00FFFF,
                0xFF0000FF, 0xFFFF00FF, 0xFFFF0000};
        Shader hueShader = new LinearGradient(0, hueRect.top, 0, hueRect.bottom,
                hues, null, Shader.TileMode.CLAMP);
        huePaint.setShader(hueShader);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (svRect.width() <= 0) return;

        // SV box
        canvas.drawRoundRect(svRect, corner, corner, svPaint);
        canvas.drawRoundRect(svRect, corner, corner, svValuePaint);
        canvas.drawRoundRect(svRect, corner, corner, borderPaint);

        float svx = svRect.left + selectedSat * svRect.width();
        float svy = svRect.top + (1f - selectedVal) * svRect.height();
        canvas.drawCircle(svx, svy, dp(7), thumbStroke);

        // Hue bar
        canvas.drawRoundRect(hueRect, corner, corner, huePaint);
        canvas.drawRoundRect(hueRect, corner, corner, borderPaint);
        float huey = hueRect.top + (selectedHue / 360f) * hueRect.height();
        canvas.drawCircle(hueRect.centerX(), huey, hueRect.width() / 2f + dp(2), thumbStroke);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (svRect.contains(x, y)) activeTouch = TOUCH_SV;
                else if (x >= hueRect.left - gap/2) activeTouch = TOUCH_HUE;
                else activeTouch = 0;
                getParent().requestDisallowInterceptTouchEvent(true);
                // fall through
            case MotionEvent.ACTION_MOVE:
                handleTouch(x, y);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                activeTouch = 0;
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void handleTouch(float x, float y) {
        if (activeTouch == TOUCH_SV) {
            selectedSat = clamp((x - svRect.left) / svRect.width());
            selectedVal = clamp(1f - (y - svRect.top) / svRect.height());
        } else if (activeTouch == TOUCH_HUE) {
            selectedHue = clamp((y - hueRect.top) / hueRect.height()) * 360f;
            buildShaders();
        } else {
            return;
        }
        invalidate();
        if (listener != null) listener.onColorChanged(getCurrentColor());
    }

    private float clamp(float v) { return Math.max(0f, Math.min(1f, v)); }

    public int getCurrentColor() {
        return Color.HSVToColor(new float[]{selectedHue, selectedSat, selectedVal});
    }

    public void setColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        selectedHue = hsv[0];
        selectedSat = hsv[1];
        selectedVal = hsv[2];
        buildShaders();
        invalidate();
    }

    public void setOnColorChangedListener(OnColorChangedListener l) {
        this.listener = l;
    }
}
