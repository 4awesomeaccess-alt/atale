package com.example.newcardmaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class CropOverlayView extends View {

    public interface OnCropChangedListener {
        void onCropChanged(RectF cropRect);
    }

    // ── Class top માં
    private float aspectW = 0f;
    private float aspectH = 0f;

    private OnCropChangedListener listener;

    // ── Crop rect (view coordinates)
    private RectF cropRect = new RectF();

    // ── Image bounds (actual image on screen)
    private RectF imageBounds = new RectF();

    // ── Paint
    private Paint overlayPaint;
    private Paint borderPaint;
    private Paint handlePaint;
    private Paint gridPaint;

    // ── Handle size
    private static final float HANDLE_SIZE  = 40f;
    private static final float HANDLE_TOUCH = 50f;
    private static final float MIN_CROP     = 80f;

    // ── Touch state
    private enum DragMode {
        NONE,
        MOVE,
        TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT,
        TOP, BOTTOM, LEFT, RIGHT
    }

    private DragMode dragMode = DragMode.NONE;
    private float lastX, lastY;

    public CropOverlayView(Context context) {
        super(context);
        init();
    }

    public CropOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        overlayPaint = new Paint();
        overlayPaint.setColor(Color.parseColor("#88000000"));
        overlayPaint.setStyle(Paint.Style.FILL);

        borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2f);

        handlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handlePaint.setColor(Color.WHITE);
        handlePaint.setStyle(Paint.Style.FILL);

        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#55FFFFFF"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1f);
    }

    public void setAspectRatioLock(float w, float h) {
        this.aspectW = w;
        this.aspectH = h;
        // ── Current crop rect ને ratio પ્રમાણે adjust
        if (w > 0 && h > 0 && !imageBounds.isEmpty()) {
            float cx = (cropRect.left + cropRect.right)  / 2f;
            float cy = (cropRect.top  + cropRect.bottom) / 2f;
            float cw = cropRect.right - cropRect.left;
            float ch = cw * (h / w);

            // image bounds ચેક
            float newL = cx - cw / 2f;
            float newT = cy - ch / 2f;
            float newR = cx + cw / 2f;
            float newB = cy + ch / 2f;

            // clamp
            if (newL < imageBounds.left) {
                newL = imageBounds.left;
                newR = newL + cw;
            }
            if (newR > imageBounds.right) {
                newR = imageBounds.right;
                newL = newR - cw;
            }
            if (newT < imageBounds.top) {
                newT = imageBounds.top;
                newB = newT + ch;
            }
            if (newB > imageBounds.bottom) {
                newB = imageBounds.bottom;
                newT = newB - ch;
            }

            cropRect.set(newL, newT, newR, newB);
        }
        invalidate();
    }

    // ── handleDrag() માં ratio enforce
    private void enforceAspectRatio() {
        if (aspectW <= 0 || aspectH <= 0) return;

        float w = cropRect.right - cropRect.left;
        float h = cropRect.bottom - cropRect.top;
        float targetRatio = aspectW / aspectH;
        float currentRatio = w / h;

        if (Math.abs(currentRatio - targetRatio) < 0.01f) return;

        // Width ને base ઉપર height adjust
        float newH = w / targetRatio;

        if (cropRect.top + newH > imageBounds.bottom) {
            newH = imageBounds.bottom - cropRect.top;
            float newW = newH * targetRatio;
            cropRect.right = cropRect.left + newW;
        }

        cropRect.bottom = cropRect.top + newH;
    }


    public void setOnCropChangedListener(OnCropChangedListener l) {
        this.listener = l;
    }

    // ── Image bounds set (ImageView ની actual image position)
    public void setImageBounds(RectF bounds) {
        this.imageBounds = new RectF(bounds);
        // ── Initial crop = full image
        cropRect.set(bounds);
        invalidate();
    }

    // ── Current crop rect get
    public RectF getCropRect() {
        return new RectF(cropRect);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (cropRect.isEmpty()) return;

        float l = cropRect.left;
        float t = cropRect.top;
        float r = cropRect.right;
        float b = cropRect.bottom;

        // ── Overlay — crop ની બહાર
        canvas.drawRect(imageBounds.left, imageBounds.top,
                imageBounds.right, t, overlayPaint); // top
        canvas.drawRect(imageBounds.left, b,
                imageBounds.right, imageBounds.bottom, overlayPaint); // bottom
        canvas.drawRect(imageBounds.left, t,
                l, b, overlayPaint); // left
        canvas.drawRect(r, t,
                imageBounds.right, b, overlayPaint); // right

        // ── Border
        canvas.drawRect(l, t, r, b, borderPaint);

        // ── Grid (3x3)
        float w = (r - l) / 3f;
        float h = (b - t) / 3f;
        canvas.drawLine(l + w, t, l + w, b, gridPaint);
        canvas.drawLine(l + 2*w, t, l + 2*w, b, gridPaint);
        canvas.drawLine(l, t + h, r, t + h, gridPaint);
        canvas.drawLine(l, t + 2*h, r, t + 2*h, gridPaint);

        // ── Corner handles
        float hs = HANDLE_SIZE / 2f;

        // Top-Left
        drawCornerHandle(canvas, l, t, true, true);
        // Top-Right
        drawCornerHandle(canvas, r, t, false, true);
        // Bottom-Left
        drawCornerHandle(canvas, l, b, true, false);
        // Bottom-Right
        drawCornerHandle(canvas, r, b, false, false);

        // ── Edge handles (mid points)
        drawEdgeHandle(canvas, (l + r) / 2f, t);  // top
        drawEdgeHandle(canvas, (l + r) / 2f, b);  // bottom
        drawEdgeHandle(canvas, l, (t + b) / 2f);  // left
        drawEdgeHandle(canvas, r, (t + b) / 2f);  // right
    }

    private void drawCornerHandle(Canvas canvas,
                                  float x, float y,
                                  boolean isLeft, boolean isTop) {
        float len = HANDLE_SIZE;
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.WHITE);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(4f);
        p.setStrokeCap(Paint.Cap.ROUND);

        float hx = isLeft ? x + len : x - len;
        float hy = isTop  ? y + len : y - len;

        canvas.drawLine(x, y, hx, y, p);
        canvas.drawLine(x, y, x, hy, p);
    }

    private void drawEdgeHandle(Canvas canvas, float x, float y) {
        float hs = HANDLE_SIZE / 2.5f;
        canvas.drawRoundRect(
                x - hs, y - hs / 2.5f,
                x + hs, y + hs / 2.5f,
                4f, 4f, handlePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (cropRect.isEmpty()) return true;

        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                dragMode = getDragMode(x, y);
                lastX = x;
                lastY = y;
                return dragMode != DragMode.NONE;

            case MotionEvent.ACTION_MOVE:
                if (dragMode == DragMode.NONE) break;
                float dx = x - lastX;
                float dy = y - lastY;
                handleDrag(dx, dy);
                lastX = x;
                lastY = y;
                invalidate();
                if (listener != null) listener.onCropChanged(getCropRect());
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                dragMode = DragMode.NONE;
                break;
        }
        return true;
    }

    private DragMode getDragMode(float x, float y) {
        float l = cropRect.left;
        float t = cropRect.top;
        float r = cropRect.right;
        float b = cropRect.bottom;
        float ht = HANDLE_TOUCH;

        // ── Corners first (priority)
        if (near(x, l, ht) && near(y, t, ht)) return DragMode.TOP_LEFT;
        if (near(x, r, ht) && near(y, t, ht)) return DragMode.TOP_RIGHT;
        if (near(x, l, ht) && near(y, b, ht)) return DragMode.BOTTOM_LEFT;
        if (near(x, r, ht) && near(y, b, ht)) return DragMode.BOTTOM_RIGHT;

        // ── Edges
        if (near(x, l, ht) && y > t && y < b) return DragMode.LEFT;
        if (near(x, r, ht) && y > t && y < b) return DragMode.RIGHT;
        if (near(y, t, ht) && x > l && x < r) return DragMode.TOP;
        if (near(y, b, ht) && x > l && x < r) return DragMode.BOTTOM;

        // ── Inside = move
        if (x > l && x < r && y > t && y < b) return DragMode.MOVE;

        return DragMode.NONE;
    }

    private boolean near(float a, float b, float threshold) {
        return Math.abs(a - b) < threshold;
    }

    private void handleDrag(float dx, float dy) {
        float il = imageBounds.left;
        float it = imageBounds.top;
        float ir = imageBounds.right;
        float ib = imageBounds.bottom;

        switch (dragMode) {
            case MOVE:
                float nL = cropRect.left  + dx;
                float nT = cropRect.top   + dy;
                float nR = cropRect.right + dx;
                float nB = cropRect.bottom + dy;
                // Clamp to image bounds
                if (nL < il) { nR -= (nL - il); nL = il; }
                if (nT < it) { nB -= (nT - it); nT = it; }
                if (nR > ir) { nL -= (nR - ir); nR = ir; }
                if (nB > ib) { nT -= (nB - ib); nB = ib; }
                cropRect.set(nL, nT, nR, nB);
                break;

            case TOP_LEFT:
                cropRect.left = Math.max(il,
                        Math.min(cropRect.left + dx,
                                cropRect.right - MIN_CROP));
                cropRect.top  = Math.max(it,
                        Math.min(cropRect.top + dy,
                                cropRect.bottom - MIN_CROP));
                break;

            case TOP_RIGHT:
                cropRect.right = Math.min(ir,
                        Math.max(cropRect.right + dx,
                                cropRect.left + MIN_CROP));
                cropRect.top   = Math.max(it,
                        Math.min(cropRect.top + dy,
                                cropRect.bottom - MIN_CROP));
                break;

            case BOTTOM_LEFT:
                cropRect.left   = Math.max(il,
                        Math.min(cropRect.left + dx,
                                cropRect.right - MIN_CROP));
                cropRect.bottom = Math.min(ib,
                        Math.max(cropRect.bottom + dy,
                                cropRect.top + MIN_CROP));
                break;

            case BOTTOM_RIGHT:
                cropRect.right  = Math.min(ir,
                        Math.max(cropRect.right + dx,
                                cropRect.left + MIN_CROP));
                cropRect.bottom = Math.min(ib,
                        Math.max(cropRect.bottom + dy,
                                cropRect.top + MIN_CROP));
                break;

            case TOP:
                cropRect.top = Math.max(it,
                        Math.min(cropRect.top + dy,
                                cropRect.bottom - MIN_CROP));
                break;

            case BOTTOM:
                cropRect.bottom = Math.min(ib,
                        Math.max(cropRect.bottom + dy,
                                cropRect.top + MIN_CROP));
                break;

            case LEFT:
                cropRect.left = Math.max(il,
                        Math.min(cropRect.left + dx,
                                cropRect.right - MIN_CROP));
                break;

            case RIGHT:
                cropRect.right = Math.min(ir,
                        Math.max(cropRect.right + dx,
                                cropRect.left + MIN_CROP));
                break;
        }
        if (aspectW > 0 && aspectH > 0) {
            enforceAspectRatio();
        }
    }

    // ── View coordinates → Bitmap coordinates convert
    public android.graphics.Rect getCropRectInBitmap(
            Bitmap bitmap, RectF imageViewBounds) {

        if (bitmap == null || imageViewBounds.isEmpty()) return null;

        float scaleX = bitmap.getWidth()  / imageViewBounds.width();
        float scaleY = bitmap.getHeight() / imageViewBounds.height();

        int left   = (int)((cropRect.left   - imageViewBounds.left) * scaleX);
        int top    = (int)((cropRect.top    - imageViewBounds.top)  * scaleY);
        int right  = (int)((cropRect.right  - imageViewBounds.left) * scaleX);
        int bottom = (int)((cropRect.bottom - imageViewBounds.top)  * scaleY);

        // Clamp
        left   = Math.max(0, Math.min(left,   bitmap.getWidth()));
        top    = Math.max(0, Math.min(top,    bitmap.getHeight()));
        right  = Math.max(0, Math.min(right,  bitmap.getWidth()));
        bottom = Math.max(0, Math.min(bottom, bitmap.getHeight()));

        if (right <= left || bottom <= top) return null;

        return new android.graphics.Rect(left, top, right, bottom);
    }
}