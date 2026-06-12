package com.example.newcardmaker.Activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.newcardmaker.R;

public class SettingsActivity extends AppCompatActivity {

    public static final String PREF_NAME = "app_prefs";
    public static final String KEY_VIBRATION = "vibration_enabled";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        final SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        View back = findViewById(R.id.btn_settings_back);
        if (back != null) back.setOnClickListener(v -> finish());

        final SwitchCompat switchVibration = findViewById(R.id.switch_vibration);
        if (switchVibration != null) {
            switchVibration.setChecked(prefs.getBoolean(KEY_VIBRATION, true));
            switchVibration.setOnCheckedChangeListener((b, isChecked) ->
                    prefs.edit().putBoolean(KEY_VIBRATION, isChecked).apply());
        }

        View row = findViewById(R.id.row_vibration);
        if (row != null && switchVibration != null) {
            row.setOnClickListener(v -> switchVibration.toggle());
        }
    }
}
