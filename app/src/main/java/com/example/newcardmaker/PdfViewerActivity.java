package com.example.newcardmaker;

import static android.util.Log.ASSERT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PdfViewerActivity extends AppCompatActivity {

    private PdfRenderer         pdfRenderer;
    private PdfRenderer.Page    currentPage;
    private ParcelFileDescriptor fileDescriptor;

    private ImageView    imgPdfPage;
    private TextView     tvPageInfo;
    private Button       btnPrev, btnNext, btnClose;
//    private LinearLayout btnWhatsApp, btnGmail, btnTelegram, btnDrive, btnMore;

    Button btnWhatsApp;
    private int   currentPageIndex = 0;
    private int   totalPages       = 0;
    private File  pdfFile;

    // ── Link data
    private ArrayList<String> linkUrls = new ArrayList<>();
    private float[] linkX, linkY, linkW, linkH;
    private float   imgWidth, imgHeight;

    // ── Rendered bitmap size (for touch coordinate mapping)
    private int renderedBitmapWidth, renderedBitmapHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        // ── Bind
        imgPdfPage  = findViewById(R.id.img_pdf_page);
        tvPageInfo  = findViewById(R.id.tv_page_info);
        btnPrev     = findViewById(R.id.btn_pdf_prev);
        btnNext     = findViewById(R.id.btn_pdf_next);
        btnClose    = findViewById(R.id.btn_pdf_close);
        btnWhatsApp = findViewById(R.id.btn_pdf_share);


        // ── File path
        String filePath = getIntent().getStringExtra("PDF_PATH");
        if (filePath == null) { finish(); return; }

        pdfFile = new File(filePath);
        if (!pdfFile.exists()) { finish(); return; }

        // ── Link data receive
        ArrayList<String> urls = getIntent()
                .getStringArrayListExtra("LINK_URLS");
        if (urls != null) linkUrls = urls;
        linkX     = getIntent().getFloatArrayExtra("LINK_X");
        linkY     = getIntent().getFloatArrayExtra("LINK_Y");
        linkW     = getIntent().getFloatArrayExtra("LINK_W");
        linkH     = getIntent().getFloatArrayExtra("LINK_H");
        imgWidth  = getIntent().getFloatExtra("IMG_WIDTH",  1f);
        imgHeight = getIntent().getFloatExtra("IMG_HEIGHT", 1f);

        if (linkX == null) linkX = new float[0];
        if (linkY == null) linkY = new float[0];
        if (linkW == null) linkW = new float[0];
        if (linkH == null) linkH = new float[0];

        // ── Open PDF
        try {
            fileDescriptor = ParcelFileDescriptor.open(
                    pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fileDescriptor);
            totalPages  = pdfRenderer.getPageCount();
            showPage(0);
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }

        // ── Navigation
        btnPrev.setOnClickListener(v -> {
            if (currentPageIndex > 0) showPage(currentPageIndex - 1);
        });
        btnNext.setOnClickListener(v -> {
            if (currentPageIndex < totalPages - 1)
                showPage(currentPageIndex + 1);
        });

        btnClose.setOnClickListener(v -> finish());

        // ── Share
        btnWhatsApp.setOnClickListener(v     -> shareGeneral());

        // ── Touch listener — link click detect
        imgPdfPage.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                handleLinkTouch(event.getX(), event.getY(), v);
            }
            return true;
        });
    }

    // ── Page render
    @SuppressLint("SetTextI18n")
    private void showPage(int index) {
        if (pdfRenderer == null) return;

        if (currentPage != null) currentPage.close();

        currentPage      = pdfRenderer.openPage(index);
        currentPageIndex = index;

        int screenW = getResources().getDisplayMetrics().widthPixels;
        int height  = (int) ((float) currentPage.getHeight() /
                currentPage.getWidth() * screenW);

        renderedBitmapWidth  = screenW;
        renderedBitmapHeight = height;

        Bitmap bitmap = Bitmap.createBitmap(
                screenW, height, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(Color.WHITE);
        currentPage.render(bitmap, null, null,
                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

        // ── Link highlight overlay draw
        if (linkUrls.size() > 0) {
            bitmap = drawLinkOverlay(bitmap);
        }

        imgPdfPage.setImageBitmap(bitmap);
        tvPageInfo.setText("Page " + (index + 1) + " / " + totalPages);

        btnPrev.setEnabled(index > 0);
        btnNext.setEnabled(index < totalPages - 1);
        btnPrev.setAlpha(index > 0 ? 1f : 0.4f);
        btnNext.setAlpha(index < totalPages - 1 ? 1f : 0.4f);
    }

    // ── Link highlight overlay
    private Bitmap drawLinkOverlay(Bitmap original) {
        Bitmap mutable = original.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas  = new Canvas(mutable);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#440000FF")); // semi-transparent blue
        paint.setStyle(Paint.Style.FILL);

        Paint borderPaint = new Paint();
        borderPaint.setColor(Color.BLUE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3f);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(24f);
        textPaint.setAntiAlias(true);

        for (int i = 0; i < linkUrls.size(); i++) {
            if (i >= linkX.length) break;

            // Scale coordinates — original image → rendered bitmap
            float scaleX = renderedBitmapWidth  / imgWidth;
            float scaleY = renderedBitmapHeight / imgHeight;

            float rx = linkX[i] * scaleX;
            float ry = linkY[i] * scaleY;
            float rw = linkW[i] * scaleX;
            float rh = linkH[i] * scaleY;

            RectF rect = new RectF(rx, ry, rx + rw, ry + rh);

            canvas.drawRoundRect(rect, 8f, 8f, paint);
            canvas.drawRoundRect(rect, 8f, 8f, borderPaint);
            canvas.drawText("🔗", rx + 4, ry + rh - 4, textPaint);
        }

        return mutable;
    }

    // ── Touch → link click detect
    private void handleLinkTouch(float touchX, float touchY, View imageView) {
        if (linkUrls.isEmpty()) return;

        // ImageView ની actual rendered size
        float viewW = imageView.getWidth();
        float viewH = imageView.getHeight();

        // Bitmap → view scale
        float scaleX = renderedBitmapWidth  / imgWidth;
        float scaleY = renderedBitmapHeight / imgHeight;

        // View → bitmap coordinate
        float bitmapX = touchX * (renderedBitmapWidth  / viewW);
        float bitmapY = touchY * (renderedBitmapHeight / viewH);

        // Bitmap → original image coordinate
        float origX = bitmapX / scaleX;
        float origY = bitmapY / scaleY;

        for (int i = 0; i < linkUrls.size(); i++) {
            if (i >= linkX.length) break;

            float left   = linkX[i];
            float top    = linkY[i];
            float right  = linkX[i] + linkW[i];
            float bottom = linkY[i] + linkH[i];

            if (origX >= left && origX <= right &&
                    origY >= top  && origY <= bottom) {

                // ── Link found — open
                openLink(linkUrls.get(i));
                return;
            }
        }
    }

    // ── Open link
    private void openLink(String url) {
        try {
            // ── Map link confirm dialog
            new android.app.AlertDialog.Builder(this)
                    .setTitle("🔗 Link Open")
                    .setMessage(url)
                    .setPositiveButton("Google Maps માં ખોલો", (d, w) -> {
                        Intent mapIntent = new Intent(
                                Intent.ACTION_VIEW, Uri.parse(url));
                        mapIntent.setPackage("com.google.android.apps.maps");
                        try {
                            startActivity(mapIntent);
                        } catch (Exception e) {
                            // Maps app ન હોય
                            startActivity(new Intent(
                                    Intent.ACTION_VIEW, Uri.parse(url)));
                        }
                    })
                    .setNeutralButton("Browser માં ખોલો", (d, w) -> {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW, Uri.parse(url)));
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        } catch (Exception e) {
            Toast.makeText(this, "Link open error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // ── Share via app
    private void shareViaApp(String packageName) {
        try {
            Uri uri = getPdfUri();
            try {
                getPackageManager().getPackageInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                shareGeneral();
                return;
            }
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/pdf");
            intent.setPackage(packageName);
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, pdfFile.getName());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(intent);
        } catch (Exception e) {
            shareGeneral();
        }
    }

    // ── General share
    private void shareGeneral() {
        try {
            Uri uri = getPdfUri();
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, pdfFile.getName());
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(shareIntent, "PDF Share કરો"));
        } catch (Exception e) {
            Log.println(ASSERT,"error",e+"");
            Toast.makeText(this, "Share error: " + e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getPdfUri() {
        return FileProvider.getUriForFile(
                this,
                getPackageName() + ".provider",
                pdfFile);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (currentPage    != null) currentPage.close();
            if (pdfRenderer    != null) pdfRenderer.close();
            if (fileDescriptor != null) fileDescriptor.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}