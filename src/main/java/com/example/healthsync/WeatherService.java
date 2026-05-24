package com.example.healthsync;

import android.os.AsyncTask;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherService {

    public interface WeatherCallback {
        void onSuccess(String temperature, String condition, String recommendation);
        void onError(String error);
    }

    public static void getWeather(String city, WeatherCallback callback) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // Используем Open-Meteo API (бесплатно, без ключа)
                    // Для Москвы координаты: 55.75, 37.62
                    String urlString = "https://api.open-meteo.com/v1/forecast?latitude=55.75&longitude=37.62&current_weather=true";
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);

                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder result = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    reader.close();
                    return result.toString();
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                if (result == null) {
                    callback.onError("Ошибка загрузки погоды");
                    return;
                }

                try {
                    JSONObject json = new JSONObject(result);
                    JSONObject current = json.getJSONObject("current_weather");

                    double temperature = current.getDouble("temperature");
                    int weatherCode = current.getInt("weathercode");

                    String condition;
                    switch (weatherCode) {
                        case 0: condition = "☀️ Ясно"; break;
                        case 1: condition = "🌤️ В основном ясно"; break;
                        case 2: condition = "⛅ Переменная облачность"; break;
                        case 3: condition = "☁️ Пасмурно"; break;
                        case 45: condition = "🌫️ Туман"; break;
                        case 51: condition = "🌧️ Легкая морось"; break;
                        case 61: condition = "🌧️ Дождь"; break;
                        case 71: condition = "❄️ Снег"; break;
                        default: condition = "🌡️ Облачно";
                    }

                    String recommendation;
                    if (temperature < -10) {
                        recommendation = "🥶 Очень холодно! Одевайтесь теплее.";
                    } else if (temperature < 0) {
                        recommendation = "❄️ Холодно. Одевайтесь по погоде.";
                    } else if (temperature < 10) {
                        recommendation = "🌬️ Прохладно. Рекомендуем 8000 шагов.";
                    } else if (temperature < 20) {
                        recommendation = "🌤️ Комфортно. Хороший день для прогулки!";
                    } else if (temperature < 30) {
                        recommendation = "☀️ Тепло! Не забывайте пить воду.";
                    } else {
                        recommendation = "🔥 Жарко! Пейте больше воды, гуляйте в тени.";
                    }

                    String tempText = (int) temperature + "°C";
                    callback.onSuccess(tempText, condition, recommendation);

                } catch (Exception e) {
                    callback.onError("Ошибка обработки погоды");
                }
            }
        }.execute();
    }
}