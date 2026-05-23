package com.example.newcardmaker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Full screen color picker overlay.
 * Touch anywhere → screen pixel color pick થાય.
 * Close → cancel.
 */
public class ScreenColorPickerOverlay {

    public interface OnColorPickedListener {
        void onColorPicked(int color);
        void onColorPreview(int color); // real-time drag preview
        void onCancelled();
    }

    private final Activity activity;
    private final OnColorPickedListener listener;
    private WindowManager windowManager;
    private FrameLayout overlayRoot;

    // Magnifier preview
    private View colorPreviewBox;
    private TextView tvHexPreview;
    private TextView tvInstruction;

    private Bitmap screenBitmap;

    public ScreenColorPickerOverlay(Activity activity, OnColorPickedListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void show() {
        windowManager = (WindowManager) activity.getSystemService(Activity.WINDOW_SERVICE);

        // ── Root overlay (full screen transparent) ──
        overlayRoot = new FrameLayout(activity);

        // ── Semi-transparent dim background ──
        overlayRoot.setBackgroundColor(Color.parseColor("#88000000"));

        // ── Top instruction bar ──
        LinearLayout topBar = new LinearLayout(activity);
        topBar.setOrientation(LinearLayout.HORIZONTAL);
        topBar.setBackgroundColor(Color.parseColor("#CC1C2529"));
        topBar.setGravity(Gravity.CENTER_VERTICAL);
        topBar.setPadding(dp(12), dp(8), dp(12), dp(8));

        tvInstruction = new TextView(activity);
        tvInstruction.setText("👆 Touch anywhere to pick color");
        tvInstruction.setTextColor(Color.parseColor("#F3F4F6"));
        tvInstruction.setTextSize(13);
        LinearLayout.LayoutParams instrLp = new LinearLayout.LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        tvInstruction.setLayoutParams(instrLp);
        topBar.addView(tvInstruction);

        // Close button
        TextView btnClose = new TextView(activity);
        btnClose.setText("✕ Close");
        btnClose.setTextColor(Color.WHITE);
        btnClose.setTextSize(12);
        btnClose.setGravity(Gravity.CENTER);
        btnClose.setPadding(dp(12), dp(6), dp(12), dp(6));
        GradientDrawable closeBg = new GradientDrawable();
        closeBg.setColor(Color.parseColor("#EF4444"));
        closeBg.setCornerRadius(dp(6));
        btnClose.setBackground(closeBg);
        topBar.addView(btnClose);

        FrameLayout.LayoutParams topBarLp = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        topBarLp.gravity = Gravity.TOP;
        overlayRoot.addView(topBar, topBarLp);

        // ── Color preview magnifier (follows finger) ──
        LinearLayout magnifier = new LinearLayout(activity);
        magnifier.setOrientation(LinearLayout.VERTICAL);
        magnifier.setGravity(Gravity.CENTER);
        magnifier.setPadding(dp(8), dp(8), dp(8), dp(8));
        GradientDrawable magBg = new GradientDrawable();
        magBg.setColor(Color.parseColor("#CC1C2529"));
        magBg.setCornerRadius(dp(10));
        magBg.setStroke(dp(2), Color.WHITE);
        magnifier.setBackground(magBg);

        colorPreviewBox = new View(activity);
        LinearLayout.LayoutParams previewLp = new LinearLayout.LayoutParams(dp(60), dp(60));
        GradientDrawable previewBg = new GradientDrawable();
        previewBg.setShape(GradientDrawable.OVAL);
        previewBg.setColor(Color.RED);
        previewBg.setStroke(dp(2), Color.WHITE);
        colorPreviewBox.setBackground(previewBg);
        colorPreviewBox.setLayoutParams(previewLp);
        magnifier.addView(colorPreviewBox);

        tvHexPreview = new TextView(activity);
        tvHexPreview.setText("#FF0000");
        tvHexPreview.setTextColor(Color.WHITE);
        tvHexPreview.setTextSize(11);
        tvHexPreview.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams hexLp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        hexLp.topMargin = dp(4);
        tvHexPreview.setLayoutParams(hexLp);
        magnifier.addView(tvHexPreview);

        FrameLayout.LayoutParams magLp = new FrameLayout.LayoutParams(dp(90), dp(100));
        magLp.gravity = Gravity.TOP | Gravity.START;
        magnifier.setVisibility(View.INVISIBLE);
        overlayRoot.addView(magnifier, magLp);

        // ── Touch listener — screen pixel pick ──
        overlayRoot.setOnTouchListener((v, event) -> {
            int x = (int) event.getRawX();
            int y = (int) event.getRawY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    magnifier.setVisibility(View.VISIBLE);

                    // Magnifier position — finger ની ઉપર દેખાય
                    int magX = Math.max(0, x - dp(45));
                    int magY = Math.max(dp(60), y - dp(120));
                    magLp.leftMargin = magX;
                    magLp.topMargin = magY;
                    magnifier.setLayoutParams(magLp);

                    // Screen pixel color
                    int pickedColor = getPixelColor(x, y);
                    String hex = String.format("#%06X", (0xFFFFFF & pickedColor));

                    // Update magnifier preview
                    ((GradientDrawable) colorPreviewBox.getBackground()).setColor(pickedColor);
                    tvHexPreview.setText(hex);
                    tvInstruction.setText("Color: " + hex);

                    // ✅ Real-time text color update
                    if (listener != null) listener.onColorPreview(pickedColor);
                    return true;

                case MotionEvent.ACTION_UP:
                    // Color select — apply
                    int finalColor = getPixelColor(x, y);
                    dismiss();
                    if (listener != null) listener.onColorPicked(finalColor);
                    return true;
            }
            return false;
        });

        // Close button click
        btnClose.setOnClickListener(vv -> {
            dismiss();
            if (listener != null) listener.onCancelled();
        });

        // ── Window params ──
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;

        // Screen screenshot capture
        captureScreen();

        activity.addContentView(overlayRoot, new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
    }

    private void captureScreen() {
        View rootView = activity.getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        rootView.buildDrawingCache();
        Bitmap cache = rootView.getDrawingCache();
        if (cache != null) {
            screenBitmap = Bitmap.createBitmap(cache);
        }
        rootView.setDrawingCacheEnabled(false);
    }

    private int getPixelColor(int x, int y) {
        if (screenBitmap == null) return Color.WHITE;
        int safeX = Math.max(0, Math.min(x, screenBitmap.getWidth() - 1));
        int safeY = Math.max(0, Math.min(y, screenBitmap.getHeight() - 1));
        return screenBitmap.getPixel(safeX, safeY);
    }

    public void dismiss() {
        if (overlayRoot != null && overlayRoot.getParent() != null) {
            ((ViewGroup) overlayRoot.getParent()).removeView(overlayRoot);
            overlayRoot = null;
        }
        if (screenBitmap != null && !screenBitmap.isRecycled()) {
            screenBitmap.recycle();
            screenBitmap = null;
        }
    }

    private int dp(int value) {
        return (int) (value * activity.getResources().getDisplayMetrics().density);
    }
}
