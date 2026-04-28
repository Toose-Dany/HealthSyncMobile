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
    private EditText heightInput, weightInput;
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

        // Сохраняем данные пользователя
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userName", name);
        editor.putString("userEmail", email);
        editor.putString("userPassword", password);
        editor.putBoolean("isLoggedIn", true);

        // Сохраняем рост и вес, если указаны
        if (!heightStr.isEmpty()) {
            editor.putFloat("height", Float.parseFloat(heightStr));
        }
        if (!weightStr.isEmpty()) {
            editor.putFloat("weight", Float.parseFloat(weightStr));
        }

        editor.apply();

        Toast.makeText(this, "Регистрация успешна!", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}