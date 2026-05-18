package com.example.newcardmaker;

import android.content.Context;
import android.graphics.*;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.TypedValue;

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

    private Bitmap imageBitmap = null;

    // ── NEW FIELDS ──
    private float doubleStrokeWidth = 0f;       // Text Outline (double stroke)
    private int doubleStrokeColor = Color.WHITE; // outer stroke color
    private float bgOpacity = 1f;               // Text Background opacity (0-1)
    private float glowRadius = 0f;              // Glow effect
    private int glowColor = Color.WHITE;
    private float skewX = 0f;                   // Skew X
    private float skewY = 0f;                   // Skew Y
    private boolean mirrorH = false;            // Mirror Horizontal
    private boolean mirrorV = false;            // Mirror Vertical
    private boolean waveMode = false;           // Wave text
    private float waveAmplitude = 20f;
    private float waveLength = 80f;
    private int textCase = 0;                   // 0=normal,1=UPPER,2=lower,3=Title
    private boolean superscript = false;
    private boolean subscript = false;
    private boolean overline = false;
    private int fontWeight = 400;               // Font weight 100-900
    private boolean autoFit = false;            // Auto-fit text to box
    private int columnCount = 1;               // Multi-column
    private float threeDDepth = 0f;            // 3D effect depth
    private int threeDColor = Color.parseColor("#808080");
    private android.view.ViewGroup.LayoutParams originalLayoutParams = null;

    public StrokeTextView(Context context) {
        super(context);
    }

    public StrokeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // ── IMAGE BITMAP ──
    public void setImageBitmap(Bitmap bitmap) {
        this.imageBitmap = bitmap;
        if (getWidth() > 0 && getHeight() > 0) applyImageShader();
        invalidate();
    }
    public void clearImageBitmap() {
        this.imageBitmap = null; this.textShader = null;
        getPaint().setShader(null); invalidate();
    }
    private void applyImageShader() {
        if (imageBitmap == null || getWidth() == 0 || getHeight() == 0) return;
        Bitmap scaled = Bitmap.createScaledBitmap(imageBitmap, getWidth(), getHeight(), true);
        this.textShader = new BitmapShader(scaled, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
    }

    // ── STROKE ──
    public void setStrokeColor(int color) { this.strokeColor = color; invalidate(); }
    public void setStrokeWidth(float width) { this.strokeWidth = width; invalidate(); requestLayout(); }
    public int getStrokeColor() { return strokeColor; }
    public float getStrokeWidth() { return strokeWidth; }

    // ── DOUBLE STROKE (Outline) ──
    public void setDoubleStrokeWidth(float w) { this.doubleStrokeWidth = w; invalidate(); }
    public void setDoubleStrokeColor(int c) { this.doubleStrokeColor = c; invalidate(); }
    public float getDoubleStrokeWidth() { return doubleStrokeWidth; }
    public int getDoubleStrokeColor() { return doubleStrokeColor; }

    // ── GLOW ──
    public void setGlowRadius(float r) { this.glowRadius = r; invalidate(); }
    public void setGlowColor(int c) { this.glowColor = c; invalidate(); }
    public float getGlowRadius() { return glowRadius; }
    public int getGlowColor() { return glowColor; }

    // ── SKEW ──
    public void setSkewX(float x) { this.skewX = x; invalidate(); }
    public void setSkewY(float y) { this.skewY = y; invalidate(); }
    public float getSkewX() { return skewX; }
    public float getSkewY() { return skewY; }

    // ── MIRROR ──
    public void setMirrorH(boolean m) { this.mirrorH = m; invalidate(); }
    public void setMirrorV(boolean m) { this.mirrorV = m; invalidate(); }
    public boolean isMirrorH() { return mirrorH; }
    public boolean isMirrorV() { return mirrorV; }

    // ── WAVE ──
    public void setWaveMode(boolean w) { this.waveMode = w; invalidate(); }
    public void setWaveAmplitude(float a) { this.waveAmplitude = a; invalidate(); }
    public void setWaveLength(float l) { this.waveLength = l; invalidate(); }
    public boolean isWaveMode() { return waveMode; }
    public float getWaveAmplitude() { return waveAmplitude; }

    // ── TEXT CASE ──
    public void setTextCase(int c) { this.textCase = c; invalidate(); }
    public int getTextCase() { return textCase; }

    // ── SUPERSCRIPT/SUBSCRIPT ──
    public void setSuperscript(boolean s) { this.superscript = s; if(s) subscript=false; invalidate(); requestLayout(); }
    public void setSubscript(boolean s) { this.subscript = s; if(s) superscript=false; invalidate(); requestLayout(); }
    public boolean isSuperscript() { return superscript; }
    public boolean isSubscript() { return subscript; }

    // ── OVERLINE ──
    public void setOverline(boolean o) { this.overline = o; invalidate(); }
    public boolean isOverline() { return overline; }

    // ── FONT WEIGHT ──
    public void setFontWeight(int weight) {
        this.fontWeight = weight;
        setTypeface(getTypeface(), weight >= 700 ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        getPaint().setFakeBoldText(weight >= 600);
        getPaint().setStrokeWidth(weight >= 800 ? (weight - 600) / 200f : 0);
        invalidate();
    }
    public int getFontWeight() { return fontWeight; }

    // ── BG OPACITY ──
    public void setBgOpacity(float op) {
        this.bgOpacity = op;
        android.graphics.drawable.Drawable bg = getBackground();
        if (bg != null) bg.setAlpha((int)(op * 255));
        invalidate();
    }
    public float getBgOpacity() { return bgOpacity; }

    // ── 3D EFFECT ──
    public void setThreeDDepth(float d) { this.threeDDepth = d; invalidate(); }
    public void setThreeDColor(int c) { this.threeDColor = c; invalidate(); }
    public float getThreeDDepth() { return threeDDepth; }
    public int getThreeDColor() { return threeDColor; }

    // ── AUTO FIT ──
    public void setAutoFit(boolean a) {
        this.autoFit = a;
        if (a && getWidth() > 0) fitTextToBox();
        invalidate();
    }
    private void fitTextToBox() {
        if (getWidth() <= 0 || getText().length() == 0) return;
        float targetW = getWidth() - getPaddingLeft() - getPaddingRight();
        float size = getTextSize();
        while (getPaint().measureText(getText().toString()) > targetW && size > 8f) {
            size--;
            setTextSize(TypedValue.COMPLEX_UNIT_PX, size);
        }
    }

    // ── SHADER ──
    public void setTextShader(Shader shader) { this.imageBitmap = null; this.textShader = shader; invalidate(); }
    public void setTextGradient(Shader shader) { this.imageBitmap = null; this.textShader = shader; invalidate(); }
    public Shader getTextShader() { return textShader; }

    // ── TEXT COLOR ──
    @Override
    public void setTextColor(int color) {
        fillColor = color; textShader = null; imageBitmap = null;
        getPaint().setShader(null); super.setTextColor(color);
    }

    // ── SHADOW ──
    @Override
    public void setShadowLayer(float radius, float dx, float dy, int color) {
        this.shadowRadius = radius; this.shadowDx = dx; this.shadowDy = dy; this.shadowColor = color;
        super.setShadowLayer(radius, dx, dy, color); invalidate();
    }
    public float getShadowRadius() { return shadowRadius; }
    public float getShadowDx() { return shadowDx; }
    public float getShadowDy() { return shadowDy; }
    public int getShadowColor() { return shadowColor; }

    // ── ARC ──
    public void setArcMode(boolean arcMode) { this.arcMode = arcMode; invalidate(); requestLayout(); }
    public void setArcAngle(float angle) { this.arcAngle = angle; invalidate(); requestLayout(); }
    public void setRadius(float radius) { this.radius = radius; invalidate(); requestLayout(); }
    public void setArcUp(boolean up) { this.arcUp = up; invalidate(); }
    public boolean isArcMode() { return arcMode; }
    public float getArcAngle() { return arcAngle; }
    public float getRadius() { return radius; }
    public boolean isArcUp() { return arcUp; }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (arcMode) {
            int size = (int)(radius * 2 + getTextSize() * 2 + 40);
            setMeasuredDimension(size, size);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (imageBitmap != null) applyImageShader();
        if (autoFit && w > 0) fitTextToBox();
        if (textShader != null) invalidate();
    }

    private String applyTextCase(String text) {
        switch (textCase) {
            case 1: return text.toUpperCase();
            case 2: return text.toLowerCase();
            case 3:
                StringBuilder sb = new StringBuilder();
                boolean cap = true;
                for (char c : text.toCharArray()) {
                    sb.append(cap ? Character.toUpperCase(c) : Character.toLowerCase(c));
                    cap = (c == ' ');
                }
                return sb.toString();
            default: return text;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Mirror/Skew transforms
        canvas.save();

        float cx = getWidth() / 2f, cy = getHeight() / 2f;
        if (mirrorH) canvas.scale(-1f, 1f, cx, cy);
        if (mirrorV) canvas.scale(1f, -1f, cx, cy);
        if (skewX != 0 || skewY != 0) {
            Matrix m = new Matrix();
            m.setSkew(skewX, skewY, cx, cy);
            canvas.concat(m);
        }

        if (arcMode) {
            drawArcText(canvas);
        } else if (waveMode) {
            drawWaveText(canvas);
        } else {
            drawNormalText(canvas);
        }
        canvas.restore();
    }

    private void drawNormalText(Canvas canvas) {
        Paint paint = getPaint();
        String text = applyTextCase(getText().toString());

        // ── Glow
        if (glowRadius > 0) {
            paint.setShadowLayer(glowRadius, 0, 0, glowColor);
        }

        // ── 3D effect (multiple shadow layers)
        if (threeDDepth > 0) {
            paint.setStyle(Paint.Style.FILL);
            paint.setShader(null);
            for (int i = (int)threeDDepth; i >= 1; i--) {
                int alpha = 200 - i * 20;
                if (alpha < 40) alpha = 40;
                paint.setColor(Color.argb(alpha, Color.red(threeDColor), Color.green(threeDColor), Color.blue(threeDColor)));
                canvas.save();
                canvas.translate(i, i);
                super.onDraw(canvas);
                canvas.restore();
            }
        }

        // ── Double stroke (outer)
        if (doubleStrokeWidth > 0) {
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth + doubleStrokeWidth * 2);
            paint.setColor(doubleStrokeColor);
            super.onDraw(canvas);
            paint.setStyle(Paint.Style.FILL);
        }

        // ── Normal stroke
        if (strokeWidth > 0) {
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth);
            super.setTextColor(strokeColor);
            super.onDraw(canvas);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(0);
        }

        // ── Fill
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        if (textShader != null) {
            paint.setShader(textShader);
        } else {
            paint.setShader(null);
        }
        super.setTextColor(fillColor);
        super.onDraw(canvas);

        // ── Overline
        if (overline) {
            paint.setShader(null);
            paint.setColor(fillColor);
            paint.setStyle(Paint.Style.FILL);
            float lineY = getPaddingTop() + 2f;
            canvas.drawRect(getPaddingLeft(), lineY, getWidth() - getPaddingRight(), lineY + 3f, paint);
        }

        if (glowRadius > 0) paint.setShadowLayer(shadowRadius, shadowDx, shadowDy, shadowColor);
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(null);
    }

    private void drawWaveText(Canvas canvas) {
        String text = applyTextCase(getText().toString());
        Paint paint = getPaint();
        paint.setStyle(Paint.Style.FILL);

        float x = getPaddingLeft();
        float baseY = getHeight() / 2f + getTextSize() / 3f;

        if (textShader != null) paint.setShader(textShader);
        else { paint.setShader(null); paint.setColor(fillColor); }

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            float wave = (float) Math.sin(x / waveLength * 2 * Math.PI) * waveAmplitude;
            canvas.drawText(String.valueOf(c), x, baseY + wave, paint);
            x += paint.measureText(String.valueOf(c));
        }
        paint.setShader(null);
    }

    private void drawArcText(Canvas canvas) {
        String text = applyTextCase(getText().toString());
        if (text.isEmpty()) return;

        float cx = getWidth() / 2f, cy = getHeight() / 2f;
        RectF oval = new RectF(cx - radius, cy - radius, cx + radius, cy + radius);
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
        if (doubleStrokeWidth > 0) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth + doubleStrokeWidth * 2);
            paint.setColor(doubleStrokeColor);
            canvas.drawTextOnPath(text, path, 0, 0, paint);
        }
        if (strokeWidth > 0) {
            paint.setShader(null); paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(strokeWidth); paint.setColor(strokeColor);
            canvas.drawTextOnPath(text, path, 0, 0, paint);
        }
        paint.setStyle(Paint.Style.FILL); paint.setStrokeWidth(0);
        if (textShader != null) { paint.setShader(textShader); paint.setColor(fillColor); }
        else { paint.setShader(null); paint.setColor(getCurrentTextColor()); }
        canvas.drawTextOnPath(text, path, 0, 0, paint);
        paint.setStyle(Paint.Style.FILL); paint.setShader(null);
    }
}
