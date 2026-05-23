package com.example.newcardmaker;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class FreehandCropView extends View {

    public interface OnSelectionDoneListener {
        void onSelectionDone(Path path, RectF bounds);
    }

    private OnSelectionDoneListener listener;

    // ── Path
    private Path        drawPath    = new Path();
    private List<float[]> points    = new ArrayList<>();

    // ── Image bounds
    private RectF imageBounds = new RectF();

    // ── Paint
    private Paint pathPaint;
    private Paint fillPaint;
    private Paint overlayPaint;
    private Paint dotPaint;
    private Paint startDotPaint;

    // ── State
    public enum Mode { FREEHAND, POLYGON }
    private Mode mode = Mode.FREEHAND;

    private boolean isDrawing   = false;
    private boolean isClosed    = false;
    private float   startX      = 0f;
    private float   startY      = 0f;
    private static final float CLOSE_THRESHOLD = 40f;

    // ── Polygon points
    private List<float[]> polygonPoints = new ArrayList<>();

    public FreehandCropView(Context context) {
        super(context);
        init();
    }

    public FreehandCropView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setColor(Color.WHITE);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(3f);
        pathPaint.setStrokeJoin(Paint.Join.ROUND);
        pathPaint.setStrokeCap(Paint.Cap.ROUND);
        pathPaint.setPathEffect(new DashPathEffect(
                new float[]{10f, 5f}, 0));

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fillPaint.setColor(Color.parseColor("#441565C0"));
        fillPaint.setStyle(Paint.Style.FILL);

        overlayPaint = new Paint();
        overlayPaint.setColor(Color.parseColor("#88000000"));
        overlayPaint.setStyle(Paint.Style.FILL);

        dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        dotPaint.setColor(Color.WHITE);
        dotPaint.setStyle(Paint.Style.FILL);

        startDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        startDotPaint.setColor(Color.parseColor("#FF4081"));
        startDotPaint.setStyle(Paint.Style.FILL);
    }

    public void setOnSelectionDoneListener(OnSelectionDoneListener l) {
        this.listener = l;
    }

    public void setImageBounds(RectF bounds) {
        this.imageBounds = new RectF(bounds);
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        clearSelection();
    }

    public Mode getMode() { return mode; }

    public boolean hasSelection() { return isClosed; }

    // ── Clear
    public void clearSelection() {
        drawPath.reset();
        points.clear();
        polygonPoints.clear();
        isDrawing  = false;
        isClosed   = false;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawPath.isEmpty()) return;

        // ── Overlay — selection ની બહાર dark
        if (isClosed) {
            // Full dark overlay
            canvas.drawRect(imageBounds, overlayPaint);

            // Selection area clear (xfermode)
            Paint clearPaint = new Paint();
            clearPaint.setXfermode(new PorterDuffXfermode(
                    PorterDuff.Mode.CLEAR));
            // Note: works properly with hardware acceleration off

            // Fill selection
            canvas.drawPath(drawPath, fillPaint);
        }

        // ── Path draw
        canvas.drawPath(drawPath, pathPaint);

        // ── Polygon dots
        if (mode == Mode.POLYGON) {
            for (int i = 0; i < polygonPoints.size(); i++) {
                float[] pt = polygonPoints.get(i);
                if (i == 0 && !isClosed) {
                    // Start dot — pink (close here)
                    canvas.drawCircle(pt[0], pt[1], 12f, startDotPaint);
                    canvas.drawCircle(pt[0], pt[1], 6f,  dotPaint);
                } else {
                    canvas.drawCircle(pt[0], pt[1], 7f, dotPaint);
                }
            }
        }

        // ── Close hint line (polygon mode)
        if (mode == Mode.POLYGON &&
                polygonPoints.size() >= 3 && !isClosed) {
            float[] last  = polygonPoints.get(
                    polygonPoints.size() - 1);
            float[] first = polygonPoints.get(0);
            Paint hintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            hintPaint.setColor(Color.parseColor("#88FFFFFF"));
            hintPaint.setStyle(Paint.Style.STROKE);
            hintPaint.setStrokeWidth(2f);
            hintPaint.setPathEffect(new DashPathEffect(
                    new float[]{8f, 8f}, 0));
            canvas.drawLine(last[0], last[1],
                    first[0], first[1], hintPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = Math.max(imageBounds.left,
                Math.min(event.getX(), imageBounds.right));
        float y = Math.max(imageBounds.top,
                Math.min(event.getY(), imageBounds.bottom));

        if (mode == Mode.FREEHAND) {
            return handleFreehand(event, x, y);
        } else {
            return handlePolygon(event, x, y);
        }
    }

    // ════════════════════════════════
    // Freehand
    // ════════════════════════════════

    private boolean handleFreehand(MotionEvent event,
                                   float x, float y) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isClosed) clearSelection();
                drawPath.reset();
                drawPath.moveTo(x, y);
                points.clear();
                points.add(new float[]{x, y});
                startX    = x;
                startY    = y;
                isDrawing = true;
                isClosed  = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (!isDrawing) break;
                drawPath.lineTo(x, y);
                points.add(new float[]{x, y});
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                if (!isDrawing) break;
                isDrawing = false;

                // ── Auto close
                drawPath.close();
                isClosed = true;
                invalidate();

                notifySelectionDone();
                break;
        }
        return true;
    }

    // ════════════════════════════════
    // Polygon
    // ════════════════════════════════

    private boolean handlePolygon(MotionEvent event,
                                  float x, float y) {
        if (event.getAction() != MotionEvent.ACTION_DOWN) return true;
        if (isClosed) {
            clearSelection();
            return true;
        }

        // ── First point close check
        if (polygonPoints.size() >= 3) {
            float[] first = polygonPoints.get(0);
            float dist = (float) Math.hypot(
                    x - first[0], y - first[1]);
            if (dist < CLOSE_THRESHOLD) {
                // Close polygon
                drawPath.close();
                isClosed = true;
                invalidate();
                notifySelectionDone();
                return true;
            }
        }

        // ── Add point
        polygonPoints.add(new float[]{x, y});

        if (polygonPoints.size() == 1) {
            drawPath.moveTo(x, y);
        } else {
            drawPath.lineTo(x, y);
        }

        invalidate();
        return true;
    }

    // ── Selection done notify
    private void notifySelectionDone() {
        if (listener == null) return;
        RectF bounds = new RectF();
        drawPath.computeBounds(bounds, true);
        listener.onSelectionDone(new Path(drawPath), bounds);
    }

    // ── Path → Bitmap coordinates
    public Path getPathInBitmap(Bitmap bmp, RectF ivBounds) {
        if (bmp == null || ivBounds.isEmpty()) return null;

        float scaleX = bmp.getWidth()  / ivBounds.width();
        float scaleY = bmp.getHeight() / ivBounds.height();

        Matrix matrix = new Matrix();
        matrix.setTranslate(-ivBounds.left, -ivBounds.top);
        matrix.postScale(scaleX, scaleY);

        Path bmpPath = new Path(drawPath);
        bmpPath.transform(matrix);
        return bmpPath;
    }
}