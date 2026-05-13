package com.example.newcardmaker.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        findViewById(R.id.btn_import).setOnClickListener(v -> {
            openFilePicker();
        });

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
                // Fakhali JSON files j list ma lo
                if (file.getName().endsWith(".json")) {
                    designList.add(new DesignModel(file.getName(), file.getAbsolutePath()));
                }
            }
        }

        adapter = new DesignAdapter(designList);
        recyclerView.setAdapter(adapter);

        // Jo file na hoy to toast dekhaado
        if (designList.isEmpty()) {
            Toast.makeText(this, "No saved designs found!", Toast.LENGTH_SHORT).show();
        }
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
    }

    private void readJsonFromUri(Uri uri) {
        try {
            // Content resolver thi read karo
            InputStream inputStream = getContentResolver().openInputStream(uri);

            if (inputStream == null) {
                Toast.makeText(this, "Cannot open file!", Toast.LENGTH_SHORT).show();
                return;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }

            reader.close();
            inputStream.close();

            String jsonString = sb.toString().trim();

            Log.d("DEBUG_JSON", "Read JSON: " + jsonString); // Logcat ma check karo

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

            // MainActivity par moklo
            Intent intent = new Intent(ListActivity.this, MainActivity.class);
            intent.putExtra("loaded_json", jsonString);

            Log.d("DEBUG_JSON", "Starting MainActivity with JSON"); // Logcat ma check karo

            startActivity(intent);

        } catch (Exception e) {
            Log.e("DEBUG_JSON", "Error: " + e.getMessage());
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


}