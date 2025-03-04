package com.example.javamark.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.javamark.model.GyroscopicMeasurement;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для сохранения и загрузки измерений гироскопического ориентирования
 */
public class GyroscopicMeasurementStorage {
    private static final String PREFS_NAME = "gyroscopic_measurements";
    private static final String MEASUREMENTS_KEY = "saved_measurements";
    private final SharedPreferences preferences;
    private final Gson gson;

    public GyroscopicMeasurementStorage(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    /**
     * Сохраняет измерение в хранилище
     * @param measurement Измерение для сохранения
     * @return true, если сохранение прошло успешно
     */
    public boolean saveMeasurement(GyroscopicMeasurement measurement) {
        try {
            List<GyroscopicMeasurement> measurements = getAllMeasurements();

            // Проверяем, существует ли измерение с таким ID
            boolean exists = false;
            for (int i = 0; i < measurements.size(); i++) {
                if (measurements.get(i).getId() == measurement.getId()) {
                    // Обновляем существующее измерение
                    measurements.set(i, measurement);
                    exists = true;
                    break;
                }
            }

            // Если измерение новое, присваиваем ему ID и добавляем в список
            if (!exists) {
                measurement.setId(generateMeasurementId(measurements));
                measurements.add(measurement);
            }

            // Сохраняем обновленный список измерений
            String json = gson.toJson(measurements);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(MEASUREMENTS_KEY, json);
            editor.apply();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Получает все сохраненные измерения
     * @return Список измерений
     */
    public List<GyroscopicMeasurement> getAllMeasurements() {
        String json = preferences.getString(MEASUREMENTS_KEY, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<GyroscopicMeasurement>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Получает измерение по ID
     * @param measurementId ID измерения
     * @return Измерение или null, если не найдено
     */
    public GyroscopicMeasurement getMeasurementById(int measurementId) {
        List<GyroscopicMeasurement> measurements = getAllMeasurements();
        for (GyroscopicMeasurement measurement : measurements) {
            if (measurement.getId() == measurementId) {
                return measurement;
            }
        }
        return null;
    }

    /**
     * Удаляет измерение из хранилища
     * @param measurementId ID измерения для удаления
     * @return true, если удаление прошло успешно
     */
    public boolean deleteMeasurement(int measurementId) {
        try {
            List<GyroscopicMeasurement> measurements = getAllMeasurements();
            for (int i = 0; i < measurements.size(); i++) {
                if (measurements.get(i).getId() == measurementId) {
                    measurements.remove(i);
                    String json = gson.toJson(measurements);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(MEASUREMENTS_KEY, json);
                    editor.apply();
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Генерирует уникальный ID для нового измерения
     * @param measurements Список существующих измерений
     * @return Уникальный ID
     */
    private int generateMeasurementId(List<GyroscopicMeasurement> measurements) {
        int maxId = 0;
        for (GyroscopicMeasurement measurement : measurements) {
            if (measurement.getId() > maxId) {
                maxId = measurement.getId();
            }
        }
        return maxId + 1;
    }
}