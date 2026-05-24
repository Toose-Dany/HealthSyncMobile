package com.example.healthsync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameInput, emailInput, passwordInput, confirmPasswordInput;
    private EditText heightInput, weightInput, ageInput;
    private Button registerButton;
    private TextView loginLink;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        prefs = getSharedPreferences("HealthSync", MODE_PRIVATE);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        nameInput = findViewById(R.id.registerNameInput);
        emailInput = findViewById(R.id.registerEmailInput);
        passwordInput = findViewById(R.id.registerPasswordInput);
        confirmPasswordInput = findViewById(R.id.registerConfirmPasswordInput);
        heightInput = findViewById(R.id.registerHeightInput);
        weightInput = findViewById(R.id.registerWeightInput);
        ageInput = findViewById(R.id.registerAgeInput);
        registerButton = findViewById(R.id.registerButton);
        loginLink = findViewById(R.id.loginLink);
    }

    private void setupClickListeners() {
        registerButton.setOnClickListener(v -> attemptRegister());
        loginLink.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }

    private void attemptRegister() {
        String name = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString();
        String confirmPassword = confirmPasswordInput.getText().toString();
        String heightStr = heightInput.getText().toString().trim();
        String weightStr = weightInput.getText().toString().trim();
        String ageStr = ageInput.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Заполните обязательные поля", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 4) {
            Toast.makeText(this, "Пароль должен быть не менее 4 символов", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userName", name);
        editor.putString("userEmail", email);
        editor.putString("userPassword", password);
        editor.putBoolean("isLoggedIn", true);

        // Сохраняем рост
        if (!heightStr.isEmpty()) {
            try {
                float height = Float.parseFloat(heightStr);
                if (height >= 100 && height <= 250) {
                    editor.putFloat("height", height);
                }
            } catch (NumberFormatException e) {}
        }

        // Сохраняем вес
        if (!weightStr.isEmpty()) {
            try {
                float weight = Float.parseFloat(weightStr);
                if (weight >= 20 && weight <= 300) {
                    editor.putFloat("weight", weight);
                }
            } catch (NumberFormatException e) {}
        }

        // Сохраняем возраст
        if (!ageStr.isEmpty()) {
            try {
                int age = Integer.parseInt(ageStr);
                if (age >= 10 && age <= 120) {
                    editor.putInt("age", age);
                }
            } catch (NumberFormatException e) {}
        }

        editor.apply();

        Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}