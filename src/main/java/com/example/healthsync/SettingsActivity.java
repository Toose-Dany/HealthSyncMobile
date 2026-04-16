package com.example.healthsync;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class SettingsActivity extends AppCompatActivity {

    private EditText stepsGoalEdit, waterGoalEdit, sleepGoalEdit;
    private Button saveButton;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        prefs = getSharedPreferences("HealthSync", MODE_PRIVATE);

        stepsGoalEdit = findViewById(R.id.stepsGoalEdit);
        waterGoalEdit = findViewById(R.id.waterGoalEdit);
        sleepGoalEdit = findViewById(R.id.sleepGoalEdit);
        saveButton = findViewById(R.id.saveSettingsButton);

        loadSettings();

        saveButton.setOnClickListener(v -> saveSettings());
    }

    private void loadSettings() {
        stepsGoalEdit.setText(String.valueOf(prefs.getInt("stepsGoal", 10000)));
        waterGoalEdit.setText(String.valueOf(prefs.getInt("waterGoal", 2000)));
        sleepGoalEdit.setText(String.valueOf(prefs.getFloat("sleepGoal", 8)));
    }

    private void saveSettings() {
        try {
            int stepsGoal = Integer.parseInt(stepsGoalEdit.getText().toString());
            int waterGoal = Integer.parseInt(waterGoalEdit.getText().toString());
            float sleepGoal = Float.parseFloat(sleepGoalEdit.getText().toString());

            prefs.edit()
                    .putInt("stepsGoal", stepsGoal)
                    .putInt("waterGoal", waterGoal)
                    .putFloat("sleepGoal", sleepGoal)
                    .apply();

            Toast.makeText(this, "Настройки сохранены!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка ввода", Toast.LENGTH_SHORT).show();
        }
    }
}