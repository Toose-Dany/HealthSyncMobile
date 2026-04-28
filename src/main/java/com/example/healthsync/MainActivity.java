package com.example.healthsync;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Основные показатели
    private int syncCoins = 150;
    private double weight = 72.5;
    private double height = 178;
    private int heartRate = 68;
    private int systolic = 118;
    private int diastolic = 75;

    // Дневные метрики
    private int steps = 0;
    private int stepsGoal = 10000;
    private int waterMl = 0;
    private int waterGoal = 2000;
    private float sleepHours = 0;
    private float sleepGoal = 8;
    private int calories = 0;

    // Данные для графиков
    private int[] weekSteps = {0, 0, 0, 0, 0, 0, 0};
    private int[] weekWater = {0, 0, 0, 0, 0, 0, 0};
    private float[] weekSleep = {0, 0, 0, 0, 0, 0, 0};
    private String currentChart = "steps";

    // UI элементы
    private TextView syncCoinText, heightText, weightText, bmiText, bmiCategoryText;
    private TextView heartRateText, bloodPressureText, sleepHoursText, sleepScoreText;
    private TextView waterGoalText, stepsGoalText, sleepGoalText;
    private ProgressBar waterProgressBar, stepsProgressBar, sleepProgressBar;
    private TextView footerStepsText, footerCaloriesText, footerWaterText, footerSleepText;
    private TextView dailyInsightText, bioAgeText;
    private View[] graphBars;
    private Button chartStepsBtn, chartWaterBtn, chartSleepBtn;
    private SharedPreferences prefs;
    private DrawerLayout drawerLayout;

    private Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("HealthSync", MODE_PRIVATE);
        loadUserData();
        loadSettings();

        initViews();
        loadAllHistory();
        setupClickListeners();
        updateUI();
        updateChart();
        updateDailyInsight();
        updateBioAge();

        // Настройка Toolbar и меню
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Перезагружаем данные при возврате на главный экран
        loadUserData();
        loadSettings();
        loadAllHistory();
        updateUI();
        updateChart();
        updateDailyInsight();
        updateBioAge();
    }

    private void loadUserData() {
        weight = prefs.getFloat("weight", 72.5f);
        height = prefs.getFloat("height", 178f);
        syncCoins = prefs.getInt("syncCoins", 150);
    }

    private void loadSettings() {
        stepsGoal = prefs.getInt("stepsGoal", 10000);
        waterGoal = prefs.getInt("waterGoal", 2000);
        sleepGoal = prefs.getFloat("sleepGoal", 8);
    }

    private void initViews() {
        syncCoinText = findViewById(R.id.syncCoinText);
        heightText = findViewById(R.id.heightText);
        weightText = findViewById(R.id.weightText);
        bmiText = findViewById(R.id.bmiText);
        bmiCategoryText = findViewById(R.id.bmiCategoryText);
        heartRateText = findViewById(R.id.heartRateText);
        bloodPressureText = findViewById(R.id.bloodPressureText);
        sleepHoursText = findViewById(R.id.sleepHoursText);
        sleepScoreText = findViewById(R.id.sleepScoreText);
        waterGoalText = findViewById(R.id.waterGoalText);
        stepsGoalText = findViewById(R.id.stepsGoalText);
        sleepGoalText = findViewById(R.id.sleepGoalText);
        waterProgressBar = findViewById(R.id.waterProgressBar);
        stepsProgressBar = findViewById(R.id.stepsProgressBar);
        sleepProgressBar = findViewById(R.id.sleepProgressBar);
        footerStepsText = findViewById(R.id.footerStepsText);
        footerCaloriesText = findViewById(R.id.footerCaloriesText);
        footerWaterText = findViewById(R.id.footerWaterText);
        footerSleepText = findViewById(R.id.footerSleepText);
        dailyInsightText = findViewById(R.id.dailyInsightText);
        bioAgeText = findViewById(R.id.bioAgeText);

        graphBars = new View[]{
                findViewById(R.id.day1Bar), findViewById(R.id.day2Bar),
                findViewById(R.id.day3Bar), findViewById(R.id.day4Bar),
                findViewById(R.id.day5Bar), findViewById(R.id.day6Bar),
                findViewById(R.id.day7Bar)
        };

        chartStepsBtn = findViewById(R.id.chartStepsBtn);
        chartWaterBtn = findViewById(R.id.chartWaterBtn);
        chartSleepBtn = findViewById(R.id.chartSleepBtn);
    }

    private void setupClickListeners() {
        findViewById(R.id.quickStepsButton).setOnClickListener(v -> quickSteps());
        findViewById(R.id.quickWaterButton).setOnClickListener(v -> quickWater());
        findViewById(R.id.quickPressureButton).setOnClickListener(v -> quickPressure());

        findViewById(R.id.addStepsGoalButton).setOnClickListener(v -> addSteps());
        findViewById(R.id.addWaterGoalButton).setOnClickListener(v -> addWater());
        findViewById(R.id.addSleepGoalButton).setOnClickListener(v -> addSleep());

        findViewById(R.id.addVitalsButton).setOnClickListener(v -> addVitals());
        findViewById(R.id.logSleepButton).setOnClickListener(v -> logSleep());

        waterGoalText.setOnClickListener(v -> editWaterGoal());
        stepsGoalText.setOnClickListener(v -> editStepsGoal());
        sleepGoalText.setOnClickListener(v -> editSleepGoal());

        bmiText.setOnClickListener(v -> showBMIInfo());
        bloodPressureText.setOnClickListener(v -> showBPInfo());

        chartStepsBtn.setOnClickListener(v -> { currentChart = "steps"; updateChart(); highlightChartButton(); });
        chartWaterBtn.setOnClickListener(v -> { currentChart = "water"; updateChart(); highlightChartButton(); });
        chartSleepBtn.setOnClickListener(v -> { currentChart = "sleep"; updateChart(); highlightChartButton(); });
        highlightChartButton();
    }

    private void highlightChartButton() {
        chartStepsBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(android.R.color.darker_gray)));
        chartWaterBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(android.R.color.darker_gray)));
        chartSleepBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(android.R.color.darker_gray)));

        switch (currentChart) {
            case "steps":
                chartStepsBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_green_dark)));
                break;
            case "water":
                chartWaterBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_blue_dark)));
                break;
            case "sleep":
                chartSleepBtn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_purple)));
                break;
        }
    }

    private void updateUI() {
        syncCoinText.setText(String.valueOf(syncCoins));
        heightText.setText(String.valueOf((int) height));
        weightText.setText(String.valueOf(weight));

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

        heartRateText.setText(String.valueOf(heartRate));
        bloodPressureText.setText(systolic + "/" + diastolic);
        updatePressureColor();

        sleepHoursText.setText(String.valueOf(sleepHours));
        sleepHoursText.setTextColor(getColor(android.R.color.black));
        int sleepScore = calculateSleepScore();
        sleepScoreText.setText(String.valueOf(sleepScore));

        waterGoalText.setText(waterMl + "/" + waterGoal + " мл");
        waterProgressBar.setProgress(Math.min((int)((float) waterMl / waterGoal * 100), 100));

        stepsGoalText.setText(steps + "/" + stepsGoal);
        stepsProgressBar.setProgress(Math.min((int)((float) steps / stepsGoal * 100), 100));

        sleepGoalText.setText(sleepHours + "/" + sleepGoal + " ч");
        sleepProgressBar.setProgress(Math.min((int)(sleepHours / sleepGoal * 100), 100));

        footerStepsText.setText(String.valueOf(steps));
        footerCaloriesText.setText(String.valueOf(calories));
        footerWaterText.setText(waterMl + " мл");
        footerSleepText.setText(sleepHours + " ч");
    }

    private void updatePressureColor() {
        if (systolic < 90) {
            bloodPressureText.setTextColor(getColor(android.R.color.holo_orange_dark));
        } else if (systolic < 120) {
            bloodPressureText.setTextColor(getColor(android.R.color.holo_green_dark));
        } else if (systolic < 130) {
            bloodPressureText.setTextColor(getColor(android.R.color.holo_green_dark));
        } else if (systolic < 140) {
            bloodPressureText.setTextColor(getColor(android.R.color.holo_orange_dark));
        } else {
            bloodPressureText.setTextColor(getColor(android.R.color.holo_red_dark));
        }
    }

    private int calculateSleepScore() {
        if (sleepHours == 0) return 0;
        if (sleepHours >= 7 && sleepHours <= 8) return 85;
        if (sleepHours >= 6) return 70;
        if (sleepHours > 8) return 75;
        return 50;
    }

    private void updateChart() {
        if (graphBars == null) return;

        int maxSteps = 10000;
        int maxWater = 2000;
        float maxSleep = 10;
        String[] dayNames = {"ПН", "ВТ", "СР", "ЧТ", "ПТ", "СБ", "ВС"};

        for (int i = 0; i < graphBars.length; i++) {
            int heightPercent = 0;
            int color = 0;
            String valueText = "";

            switch (currentChart) {
                case "steps":
                    heightPercent = (int) ((weekSteps[i] / (float) maxSteps) * 100);
                    valueText = weekSteps[i] + " шагов";
                    if (weekSteps[i] >= stepsGoal) {
                        color = getColor(android.R.color.holo_green_dark);
                    } else if (weekSteps[i] >= stepsGoal / 2) {
                        color = getColor(android.R.color.holo_orange_dark);
                    } else {
                        color = getColor(android.R.color.holo_red_dark);
                    }
                    break;
                case "water":
                    heightPercent = (int) ((weekWater[i] / (float) maxWater) * 100);
                    valueText = weekWater[i] + " мл";
                    if (weekWater[i] >= waterGoal) {
                        color = getColor(android.R.color.holo_green_dark);
                    } else if (weekWater[i] >= waterGoal / 2) {
                        color = getColor(android.R.color.holo_orange_dark);
                    } else {
                        color = getColor(android.R.color.holo_red_dark);
                    }
                    break;
                case "sleep":
                    heightPercent = (int) ((weekSleep[i] / maxSleep) * 100);
                    valueText = weekSleep[i] + " ч";
                    if (weekSleep[i] >= sleepGoal) {
                        color = getColor(android.R.color.holo_green_dark);
                    } else if (weekSleep[i] >= sleepGoal / 2) {
                        color = getColor(android.R.color.holo_orange_dark);
                    } else {
                        color = getColor(android.R.color.holo_red_dark);
                    }
                    break;
            }

            int finalHeight = Math.max(20, Math.min(heightPercent, 100));
            graphBars[i].getLayoutParams().height = dpToPx(finalHeight);
            graphBars[i].setBackgroundColor(color);
            graphBars[i].requestLayout();

            final int position = i;
            final String value = valueText;
            graphBars[i].setOnClickListener(v ->
                    Toast.makeText(MainActivity.this, dayNames[position] + ": " + value, Toast.LENGTH_SHORT).show());
        }
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    private void saveAllHistory() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        String today = sdf.format(new java.util.Date());

        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("steps_" + today, steps);
        editor.putInt("water_" + today, waterMl);
        editor.putFloat("sleep_" + today, sleepHours);
        editor.putInt("totalSteps", prefs.getInt("totalSteps", 0) + steps);
        editor.putInt("totalWater", prefs.getInt("totalWater", 0) + waterMl);
        editor.putFloat("totalSleep", prefs.getFloat("totalSleep", 0) + sleepHours);
        editor.putInt("syncCoins", syncCoins);
        editor.putFloat("weight", (float) weight);
        editor.putFloat("height", (float) height);
        editor.apply();

        java.util.Calendar cal = java.util.Calendar.getInstance();
        for (int i = 8; i <= 14; i++) {
            cal.add(java.util.Calendar.DAY_OF_YEAR, -1);
            String oldDate = sdf.format(cal.getTime());
            editor.remove("steps_" + oldDate);
            editor.remove("water_" + oldDate);
            editor.remove("sleep_" + oldDate);
        }
        editor.apply();
        loadAllHistory();
    }

    private void loadAllHistory() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        java.util.Calendar cal = java.util.Calendar.getInstance();

        for (int i = 6; i >= 0; i--) {
            String date = sdf.format(cal.getTime());
            weekSteps[i] = prefs.getInt("steps_" + date, 0);
            weekWater[i] = prefs.getInt("water_" + date, 0);
            weekSleep[i] = prefs.getFloat("sleep_" + date, 0);
            cal.add(java.util.Calendar.DAY_OF_YEAR, -1);
        }
        updateChart();
    }

    private void updateDailyInsight() {
        String[] insights = {
                "❤️ Пульс в норме. Для здоровья сердца проходите 8000+ шагов ежедневно.",
                "💧 Вам нужно выпить " + Math.max(0, waterGoal - waterMl) + " мл воды.",
                "👣 Сегодня вы прошли " + steps + " шагов. " + (steps >= stepsGoal ? "Цель выполнена! 🎉" : "До цели осталось " + (stepsGoal - steps)),
                "😴 " + (sleepHours == 0 ? "Запишите сон для анализа" : "Сон: " + sleepHours + " ч. " + (sleepHours >= sleepGoal ? "Отлично выспались! ✨" : "Старайтесь спать больше")),
                "⚖️ " + bmiCategoryText.getText(),
                "🫀 " + getPressureDescription()
        };
        dailyInsightText.setText("💡 " + insights[random.nextInt(insights.length)]);
    }

    private String getPressureDescription() {
        if (systolic < 90) return "Пониженное давление. Пейте больше воды.";
        if (systolic < 120) return "Оптимальное давление! Так держать!";
        if (systolic < 130) return "Нормальное давление";
        if (systolic < 140) return "Высокое нормальное давление. Следите за солью.";
        if (systolic < 160) return "Повышенное давление. Обратитесь к врачу.";
        return "Высокое давление! Срочно к врачу!";
    }

    private void updateBioAge() {
        int actualAge = 28;
        int bioAge = actualAge;

        if (steps > 7000 && sleepHours >= 7 && heartRate < 70 && systolic < 120) {
            bioAge = Math.max(18, actualAge - 3);
        } else if (steps < 3000 || sleepHours < 6 || heartRate > 80 || systolic > 130) {
            bioAge = actualAge + 5;
        } else if (steps < 5000 || sleepHours < 6.5) {
            bioAge = actualAge + 2;
        }

        String comparison = bioAge < actualAge ? "Моложе! 🎉" : (bioAge > actualAge ? "Старше ⚠️" : "Совпадает ✅");
        bioAgeText.setText("Биологический возраст: " + bioAge + "\n(фактический: " + actualAge + ") " + comparison);
    }

    private void quickSteps() {
        steps += 500;
        calories += 30;
        syncCoins++;
        updateUI();
        updateDailyInsight();
        updateBioAge();
        saveAllHistory();
        Toast.makeText(this, "👣 +500 шагов! +1 SyncCoin", Toast.LENGTH_SHORT).show();
        if (steps >= stepsGoal) {
            Toast.makeText(this, "🎉 Поздравляем! Вы выполнили норму шагов!", Toast.LENGTH_LONG).show();
        }
    }

    private void quickWater() {
        waterMl += 250;
        syncCoins++;
        updateUI();
        updateDailyInsight();
        saveAllHistory();
        Toast.makeText(this, "💧 +250 мл воды! +1 SyncCoin", Toast.LENGTH_SHORT).show();
        if (waterMl >= waterGoal) {
            Toast.makeText(this, "🎉 Отлично! Вы выполнили норму воды!", Toast.LENGTH_LONG).show();
        }
    }

    private void quickPressure() {
        systolic = random.nextInt(20) + 110;
        diastolic = random.nextInt(15) + 70;
        heartRate = random.nextInt(15) + 60;
        syncCoins += 2;
        updateUI();
        updateDailyInsight();
        updateBioAge();
        Toast.makeText(this, "🫀 Давление: " + systolic + "/" + diastolic + "\n" + getPressureDescription() + " +2 SyncCoin", Toast.LENGTH_LONG).show();
    }

    private void addSteps() {
        steps += 500;
        calories += 30;
        updateUI();
        updateDailyInsight();
        updateBioAge();
        saveAllHistory();
    }

    private void addWater() {
        waterMl += 250;
        updateUI();
        updateDailyInsight();
        saveAllHistory();
    }

    private void addSleep() {
        sleepHours = Math.min(sleepGoal + 2, sleepHours + 1);
        syncCoins += 2;
        updateUI();
        updateDailyInsight();
        updateBioAge();
        saveAllHistory();
        Toast.makeText(this, "😴 +1 час сна! +2 SyncCoin", Toast.LENGTH_SHORT).show();
        if (sleepHours >= sleepGoal) {
            Toast.makeText(this, "🎉 Здорово! Вы выспались!", Toast.LENGTH_LONG).show();
        }
    }

    private void addVitals() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Введите показатели");

        View view = getLayoutInflater().inflate(R.layout.dialog_vitals, null);
        EditText pulseInput = view.findViewById(R.id.pulseInput);
        EditText bpInput = view.findViewById(R.id.bpInput);

        pulseInput.setText(String.valueOf(heartRate));
        bpInput.setText(systolic + "/" + diastolic);

        builder.setView(view);
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            try {
                heartRate = Integer.parseInt(pulseInput.getText().toString());
                String[] bp = bpInput.getText().toString().split("/");
                if (bp.length == 2) {
                    systolic = Integer.parseInt(bp[0]);
                    diastolic = Integer.parseInt(bp[1]);
                }
                syncCoins += 5;
                updateUI();
                updateDailyInsight();
                updateBioAge();
                saveAllHistory();
                Toast.makeText(this, "❤️ Показатели сохранены! +5 SyncCoin", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(this, "Ошибка ввода", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void logSleep() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Запись сна");

        EditText input = new EditText(this);
        input.setHint("Часы сна");
        input.setText(String.valueOf(sleepHours));
        builder.setView(input);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            try {
                float newSleep = Float.parseFloat(input.getText().toString());
                if (newSleep > 0 && newSleep < 24) {
                    sleepHours = newSleep;
                    syncCoins += 3;
                    updateUI();
                    updateDailyInsight();
                    updateBioAge();
                    saveAllHistory();
                    Toast.makeText(this, "😴 Сон записан! +3 SyncCoin", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception ex) {
                Toast.makeText(this, "Ошибка ввода", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void editWaterGoal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Цель по воде");

        EditText input = new EditText(this);
        input.setHint("мл в день");
        input.setText(String.valueOf(waterGoal));
        builder.setView(input);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            try {
                waterGoal = Integer.parseInt(input.getText().toString());
                prefs.edit().putInt("waterGoal", waterGoal).apply();
                updateUI();
                Toast.makeText(this, "💧 Цель обновлена!", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(this, "Ошибка ввода", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void editStepsGoal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Цель по шагам");

        EditText input = new EditText(this);
        input.setHint("Шагов в день");
        input.setText(String.valueOf(stepsGoal));
        builder.setView(input);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            try {
                stepsGoal = Integer.parseInt(input.getText().toString());
                prefs.edit().putInt("stepsGoal", stepsGoal).apply();
                updateUI();
                Toast.makeText(this, "👣 Цель обновлена!", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(this, "Ошибка ввода", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void editSleepGoal() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Цель по сну");

        EditText input = new EditText(this);
        input.setHint("Часов сна");
        input.setText(String.valueOf(sleepGoal));
        builder.setView(input);

        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            try {
                sleepGoal = Float.parseFloat(input.getText().toString());
                prefs.edit().putFloat("sleepGoal", sleepGoal).apply();
                updateUI();
                Toast.makeText(this, "😴 Цель обновлена!", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                Toast.makeText(this, "Ошибка ввода", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void showBMIInfo() {
        double bmi = weight / Math.pow(height / 100, 2);
        String message = "Ваш ИМТ: " + String.format("%.1f", bmi) + "\n\n";
        message += "Категория: " + bmiCategoryText.getText() + "\n\n";
        message += "РЕКОМЕНДАЦИИ:\n";

        if (bmi < 18.5) {
            message += "• Увеличьте калорийность рациона\n• Добавьте белки и полезные жиры\n• Занимайтесь силовыми тренировками";
        } else if (bmi < 25) {
            message += "• Отличный показатель!\n• Поддерживайте текущий образ жизни\n• Регулярно занимайтесь спортом";
        } else if (bmi < 30) {
            message += "• Увеличьте физическую активность\n• Уменьшите потребление сахара\n• Добавьте кардионагрузки";
        } else {
            message += "• Обратитесь к врачу\n• Разработайте план снижения веса\n• Не занимайтесь самолечением";
        }

        new AlertDialog.Builder(this)
                .setTitle("Анализ ИМТ")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showBPInfo() {
        String message = "Ваше давление: " + systolic + "/" + diastolic + "\n";
        message += "Пульс: " + heartRate + " уд/мин\n\n";
        message += "Статус: " + getPressureDescription() + "\n\n";
        message += "РЕКОМЕНДАЦИИ:\n";

        if (systolic < 90) {
            message += "• Пейте больше воды\n• Ешьте чаще, но меньше\n• Выпейте кофе или чай\n• Отдохните";
        } else if (systolic < 120) {
            message += "• Отличное давление!\n• Продолжайте в том же духе\n• Регулярно измеряйте давление";
        } else if (systolic < 130) {
            message += "• Нормальное давление\n• Следите за питанием\n• Ограничьте соль";
        } else if (systolic < 140) {
            message += "• Уменьшите соль до 5г в день\n• Больше двигайтесь\n• Контролируйте вес";
        } else if (systolic < 160) {
            message += "• Обратитесь к врачу\n• Исключите соль\n• Откажитесь от алкоголя\n• Нормализуйте сон";
        } else {
            message += "• СРОЧНО к врачу!\n• Примите прописанные лекарства\n• Вызовите скорую при ухудшении";
        }

        new AlertDialog.Builder(this)
                .setTitle("Анализ давления")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // уже на главной
        } else if (id == R.id.nav_profile) {
            startActivity(new Intent(this, ProfileActivity.class));
        } else if (id == R.id.nav_history) {
            startActivity(new Intent(this, HistoryActivity.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }  else if (id == R.id.nav_logout) {
        prefs.edit().putBoolean("isLoggedIn", false).clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}