package com.example.healthsync;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class HistoryActivity extends AppCompatActivity {

    private TextView historyText;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        prefs = getSharedPreferences("HealthSync", MODE_PRIVATE);
        historyText = findViewById(R.id.historyText);

        loadHistory();
    }

    private void loadHistory() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        Calendar cal = Calendar.getInstance();

        StringBuilder sb = new StringBuilder();
        sb.append("📊 ИСТОРИЯ АКТИВНОСТИ\n\n");

        for (int i = 0; i < 7; i++) {
            String date = sdf.format(cal.getTime());
            // Для чтения используем тот же формат, что и при сохранении
            String storageDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.getTime());

            int steps = prefs.getInt("steps_" + storageDate, 0);
            int water = prefs.getInt("water_" + storageDate, 0);
            float sleep = prefs.getFloat("sleep_" + storageDate, 0);
            int calories = prefs.getInt("calories_" + storageDate, 0);

            sb.append("📅 ").append(date).append("\n");
            sb.append("   👣 Шаги: ").append(steps).append("\n");
            sb.append("   💧 Вода: ").append(water).append(" мл\n");
            sb.append("   😴 Сон: ").append(sleep).append(" ч\n");
            sb.append("   🔥 Калории: ").append(calories).append(" ккал\n\n");

            cal.add(Calendar.DAY_OF_YEAR, -1);
        }

        historyText.setText(sb.toString());

        // Отладка: посмотрим какие ключи есть в SharedPreferences
        android.util.Log.d("HISTORY", "Все ключи: " + prefs.getAll().keySet());
    }
}