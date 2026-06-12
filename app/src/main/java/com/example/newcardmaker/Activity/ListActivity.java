package com.example.newcardmaker.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newcardmaker.DesignAdapter;
import com.example.newcardmaker.DesignModel;
import com.example.newcardmaker.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    DesignAdapter adapter;
    List<DesignModel> designList = new ArrayList<>();

    private static final int PICK_JSON_FILE = 100;
    private static final int PICK_THUMBNAIL_IMG = 101;
    private DesignModel editingModel = null;
    private int editingPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.btn_import).setOnClickListener(v -> {
            openFilePicker();
        });

        // PDF List button
        View btnPdfList = findViewById(R.id.btn_pdf_list);
        if (btnPdfList != null) {
            btnPdfList.setOnClickListener(v -> {
                startActivity(new Intent(this, PdfListActivity.class));
            });
        }

        // Image List button
        View btnImageList = findViewById(R.id.btn_image_list);
        if (btnImageList != null) {
            btnImageList.setOnClickListener(v -> {
                startActivity(new Intent(this, ImageListActivity.class));
            });
        }

        // Category button
        View btnCategory = findViewById(R.id.btn_category);
        if (btnCategory != null) {
            btnCategory.setOnClickListener(v -> {
                startActivity(new Intent(this, CategoryActivity.class));
            });
        }

        loadFiles();
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/json");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Also allow */json or text files
        String[] mimeTypes = {"application/json", "text/plain", "*/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);

        startActivityForResult(
                Intent.createChooser(intent, "Select JSON File"),
                PICK_JSON_FILE
        );
    }

    private void loadFiles() {
        designList.clear();
        // MainActivity ma jya save kari hati e j folder read karo
        File directory = getExternalFilesDir(null);
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".json")) {
                    // Same name .img file check karo
                    String imgPath = "";
                    File imgFile = new File(file.getParent(),
                            file.getName().replace(".json", ".img"));
                    if (imgFile.exists()) {
                        try {
                            imgPath = new String(java.nio.file.Files.readAllBytes(imgFile.toPath())).trim();
                        } catch (Exception e) { e.printStackTrace(); }
                    }
                    designList.add(new DesignModel(file.getName(), file.getAbsolutePath(), imgPath));
                }
            }
        }

        adapter = new DesignAdapter(designList);
        adapter.setOnEditImageListener((model, position) -> {
            editingModel = model;
            editingPosition = position;
            Intent gi = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gi, PICK_THUMBNAIL_IMG);
        });
        recyclerView.setAdapter(adapter);

        // Jo file na hoy to toast dekhaado
        if (designList.isEmpty()) {
            Toast.makeText(this, "No saved designs found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFiles();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_JSON_FILE &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {

            Uri fileUri = data.getData();
            readJsonFromUri(fileUri);
        }

        // Thumbnail image change
        if (requestCode == PICK_THUMBNAIL_IMG &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null &&
                editingModel != null) {

            Uri imgUri = data.getData();
            try {
                // Copy selected image to internal dir
                File jsonFile = new File(editingModel.getFilePath());
                String baseName = jsonFile.getName().replace(".json", "");
                File destImg = new File(jsonFile.getParent(), baseName + "_thumb.jpg");

                InputStream is = getContentResolver().openInputStream(imgUri);
                java.io.FileOutputStream fos = new java.io.FileOutputStream(destImg);
                byte[] buf = new byte[4096];
                int len;
                while ((len = is.read(buf)) > 0) fos.write(buf, 0, len);
                fos.close(); is.close();

                // Update .img file to point to new thumbnail path
                File imgRefFile = new File(jsonFile.getParent(), baseName + ".img");
                java.io.FileOutputStream imgFos = new java.io.FileOutputStream(imgRefFile);
                imgFos.write(destImg.getAbsolutePath().getBytes());
                imgFos.close();

                // Update model + refresh
                editingModel.setImagePath(destImg.getAbsolutePath());
                if (editingPosition >= 0) adapter.notifyItemChanged(editingPosition);
                Toast.makeText(this, "Thumbnail updated!", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Image update fail: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            editingModel = null;
            editingPosition = -1;
        }
    }

    private void readJsonFromUri(Uri uri) {
        try {
            // Content resolver thi read karo (bytes — file encrypted hoy shake)
            InputStream inputStream = getContentResolver().openInputStream(uri);

            if (inputStream == null) {
                Toast.makeText(this, "Cannot open file!", Toast.LENGTH_SHORT).show();
                return;
            }

            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            byte[] buf = new byte[4096];
            int n;
            while ((n = inputStream.read(buf)) > 0) baos.write(buf, 0, n);
            inputStream.close();
            byte[] rawBytes = baos.toByteArray();

            // Decrypt (shared/local/plaintext auto-detect)
            String jsonString = com.example.newcardmaker.EncryptionHelper.decryptAny(rawBytes).trim();

            Log.d("DEBUG_JSON", "Read JSON len: " + jsonString.length());

            if (jsonString.isEmpty()) {
                Toast.makeText(this, "File is empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate JSON
            try {
                new JSONObject(jsonString);
            } catch (JSONException e) {
                try {
                    new JSONArray(jsonString); // Array hoy to
                } catch (JSONException e2) {
                    Toast.makeText(this, "Invalid JSON format!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            // ✅ File copy to app directory — re-encrypt with LOCAL key
            String fileName = "design_" + System.currentTimeMillis() + ".json";
            File destFile = new File(getExternalFilesDir(null), fileName);
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(destFile)) {
                fos.write(com.example.newcardmaker.EncryptionHelper.encryptLocal(jsonString));
            }

            Toast.makeText(this, "✅ " + fileName + " add થઈ ગઈ!", Toast.LENGTH_SHORT).show();
            loadFiles(); // List refresh

        } catch (Exception e) {
            Log.e("DEBUG_JSON", "Error: " + e.getMessage());
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}