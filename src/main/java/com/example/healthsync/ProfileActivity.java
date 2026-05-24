package com.example.healthsync;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {

    private TextView heightText, weightText, bmiText, bmiCategoryText, ageText;
    private TextView totalStepsText, totalWaterText, totalSleepText, totalCaloriesText, totalCoinsText;
    private TextView daysActiveText, achievementsCountText;
    private Button editProfileButton;

    private SharedPreferences prefs;
    private double weight = 72.5;
    private double height = 178;
    private int age = 0;
    private int syncCoins = 150;
    private int steps = 0;
    private int waterMl = 0;
    private float sleepHours = 0;
    private int calories = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        prefs = getSharedPreferences("HealthSync", MODE_PRIVATE);
        loadSavedData();

        initViews();
        setupClickListeners();
        updateUI();
    }


    private void loadSavedData() {
        weight = prefs.getFloat("weight", 72.5f);
        height = prefs.getFloat("height", 178f);
        age = prefs.getInt("age", 0);
        syncCoins = prefs.getInt("syncCoins", 150);
        steps = prefs.getInt("totalSteps", 0);
        waterMl = prefs.getInt("totalWater", 0);
        sleepHours = prefs.getFloat("totalSleep", 0);
        calories = prefs.getInt("totalCalories", 0);
    }

    private void initViews() {
        heightText = findViewById(R.id.profileHeightText);
        weightText = findViewById(R.id.profileWeightText);
        bmiText = findViewById(R.id.profileBMIText);
        bmiCategoryText = findViewById(R.id.profileBMICategoryText);
        ageText = findViewById(R.id.profileAgeText);
        totalStepsText = findViewById(R.id.totalStepsText);
        totalWaterText = findViewById(R.id.totalWaterText);
        totalSleepText = findViewById(R.id.totalSleepText);
        totalCaloriesText = findViewById(R.id.totalCaloriesText);
        totalCoinsText = findViewById(R.id.totalCoinsText);
        daysActiveText = findViewById(R.id.daysActiveText);
        achievementsCountText = findViewById(R.id.achievementsCountText);
        editProfileButton = findViewById(R.id.editProfileButton);
    }

    private void setupClickListeners() {
        editProfileButton.setOnClickListener(v -> showEditProfileDialog());
    }

    private void updateUI() {
        heightText.setText((int) height + " см");
        weightText.setText(weight + " кг");

        if (age > 0) {
            ageText.setText(age + " лет");
        } else {
            ageText.setText("не указан");
        }

        totalCoinsText.setText(String.valueOf(syncCoins));

        double bmi = weight / Math.pow(height / 100, 2);
        bmiText.setText(String.format("%.1f", bmi));

        if (bmi < 18.5) {
            bmiCategoryText.setText("Недостаточный вес ⚠️");
            bmiCategoryText.setTextColor(getColor(android.R.color.holo_orange_dark));
        } else if (bmi < 25) {
            bmiCategoryText.setText("Нормальный вес ✅");
            bmiCategoryText.setTextColor(getColor(android.R.color.holo_green_dark));
        } else if (bmi < 30) {
            bmiCategoryText.setText("Избыточный вес ⚠️");
            bmiCategoryText.setTextColor(getColor(android.R.color.holo_orange_dark));
        } else {
            bmiCategoryText.setText("Ожирение ❌");
            bmiCategoryText.setTextColor(getColor(android.R.color.holo_red_dark));
        }

        totalStepsText.setText(String.format("%,d", steps));
        totalWaterText.setText(waterMl + " мл");
        totalSleepText.setText(String.format("%.1f", sleepHours) + " ч");
        totalCaloriesText.setText(String.format("%,d", calories) + " ккал");

        int daysActive = prefs.getInt("daysActive", 1);
        daysActiveText.setText(daysActive + " дней");

        int achievements = prefs.getInt("achievements", 3);
        achievementsCountText.setText(String.valueOf(achievements));
    }

    private void showEditProfileDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Редактировать профиль");

        View view = getLayoutInflater().inflate(R.layout.dialog_edit_profile, null);
        EditText heightInput = view.findViewById(R.id.editHeightInput);
        EditText weightInput = view.findViewById(R.id.editWeightInput);
        EditText ageInput = view.findViewById(R.id.editAgeInput);

        heightInput.setText(String.valueOf((int) height));
        weightInput.setText(String.valueOf(weight));

        if (age > 0) {
            ageInput.setText(String.valueOf(age));
        } else {
            ageInput.setText("");
        }

        builder.setView(view);
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            try {
                double newHeight = Double.parseDouble(heightInput.getText().toString());
                double newWeight = Double.parseDouble(weightInput.getText().toString());
                int newAge = Integer.parseInt(ageInput.getText().toString());

                boolean needRestart = false;

                if (newHeight > 100 && newHeight < 250) {
                    height = newHeight;
                    prefs.edit().putFloat("height", (float) height).apply();
                } else {
                    Toast.makeText(this, "Некорректный рост (100-250 см)", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newWeight > 20 && newWeight < 300) {
                    weight = newWeight;
                    prefs.edit().putFloat("weight", (float) weight).apply();
                } else {
                    Toast.makeText(this, "Некорректный вес (20-300 кг)", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newAge >= 10 && newAge <= 120) {
                    age = newAge;
                    prefs.edit().putInt("age", age).apply();
                } else {
                    Toast.makeText(this, "Некорректный возраст (10-120 лет)", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateUI();
                Toast.makeText(this, "Профиль обновлен!", Toast.LENGTH_SHORT).show();

            } catch (Exception ex) {
                Toast.makeText(this, "Ошибка ввода", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }
}