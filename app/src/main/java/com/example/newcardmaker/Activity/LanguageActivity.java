package com.example.newcardmaker.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.newcardmaker.R;

public class LanguageActivity extends AppCompatActivity {

    private static final String PREF_NAME = "app_prefs";
    private static final String KEY_LANG  = "selected_language";

    private LinearLayout btnGujarati, btnHindi, btnEnglish;
    private TextView checkGujarati, checkHindi, checkEnglish;
    private String selectedLang = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        btnGujarati  = findViewById(R.id.btn_gujarati);
        btnHindi     = findViewById(R.id.btn_hindi);
        btnEnglish   = findViewById(R.id.btn_english);
        checkGujarati = findViewById(R.id.check_gujarati);
        checkHindi    = findViewById(R.id.check_hindi);
        checkEnglish  = findViewById(R.id.check_english);
        LinearLayout btnContinue = findViewById(R.id.btn_continue);
        LinearLayout btnShapeActivity = findViewById(R.id.btn_shape_activity);

        if (btnShapeActivity != null) {
            btnShapeActivity.setOnClickListener(v -> {
                startActivity(new Intent(this, ShapeActivity.class));
            });
        }

        // Restore saved language
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        selectedLang = prefs.getString(KEY_LANG, "");

        // Restore server URL
        String savedUrl = prefs.getString("server_url", "");
        if (!savedUrl.isEmpty()) {
            com.example.newcardmaker.invite_online_database.invite_AppConstants.setServerUrl(savedUrl);
        }

        updateSelection();

        btnGujarati.setOnClickListener(v -> {
            selectedLang = "gujarati";
            updateSelection();
        });

        btnHindi.setOnClickListener(v -> {
            selectedLang = "hindi";
            updateSelection();
        });

        btnEnglish.setOnClickListener(v -> {
            selectedLang = "english";
            updateSelection();
        });

        btnContinue.setOnClickListener(v -> {
            if (selectedLang.isEmpty()) {
                Toast.makeText(this, "કૃપા કરી ભાષા પસંદ કરો", Toast.LENGTH_SHORT).show();
                return;
            }

            // Language wise server URL
            String serverUrl;
            switch (selectedLang) {
                case "hindi":
                    serverUrl = "https://hindi.gardenphoto.in/api.php";
                    break;
                case "english":
                    serverUrl = "https://english.gardenphoto.in/api.php";
                    break;
                default: // gujarati
                    serverUrl = "https://democrecrytonixinvitesan.gardenphoto.in/api.php";
                    break;
            }

            // Save selection
            getSharedPreferences(PREF_NAME, MODE_PRIVATE)
                    .edit()
                    .putString(KEY_LANG, selectedLang)
                    .putString("server_url", serverUrl)
                    .apply();

            // Set dynamic server URL
            com.example.newcardmaker.invite_online_database.invite_AppConstants.setServerUrl(serverUrl);

            // Open ListActivity
            startActivity(new Intent(this, ListActivity.class));
            finish();
        });
    }

    private void updateSelection() {
        // Reset all
        resetBtn(btnGujarati, checkGujarati);
        resetBtn(btnHindi, checkHindi);
        resetBtn(btnEnglish, checkEnglish);

        // Highlight selected
        switch (selectedLang) {
            case "gujarati":
                selectBtn(btnGujarati, checkGujarati);
                break;
            case "hindi":
                selectBtn(btnHindi, checkHindi);
                break;
            case "english":
                selectBtn(btnEnglish, checkEnglish);
                break;
        }
    }

    private void resetBtn(LinearLayout btn, TextView check) {
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setColor(Color.WHITE);
        bg.setCornerRadius(dpToPx(16));
        bg.setStroke(dpToPx(1), Color.parseColor("#E2E8F0"));
        btn.setBackground(bg);
        check.setVisibility(View.GONE);
    }

    private void selectBtn(LinearLayout btn, TextView check) {
        android.graphics.drawable.GradientDrawable bg = new android.graphics.drawable.GradientDrawable();
        bg.setColor(Color.parseColor("#E8F0FE"));
        bg.setCornerRadius(dpToPx(16));
        bg.setStroke(dpToPx(2), Color.parseColor("#1565C0"));
        btn.setBackground(bg);
        check.setVisibility(View.VISIBLE);
    }

    private int dpToPx(int dp) {
        return (int)(dp * getResources().getDisplayMetrics().density);
    }
}
