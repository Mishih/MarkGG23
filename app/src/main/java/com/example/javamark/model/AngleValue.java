package com.example.javamark.model;

import android.util.Log;
import java.io.Serializable;
import java.util.Locale;

/**
 * Класс для хранения значения угла в формате градусы, минуты, секунды
 */
public class AngleValue implements Serializable {
    private static final String TAG = "AngleValue";

    private int degrees;    // Градусы
    private int minutes;    // Минуты
    private double seconds; // Секунды

    public AngleValue() {
        this.degrees = 0;
        this.minutes = 0;
        this.seconds = 0.0;
    }

    public AngleValue(int degrees, int minutes, double seconds) {
        this.degrees = degrees;
        this.minutes = minutes;
        this.seconds = seconds;
    }

    // Getters and Setters
    public int getDegrees() {
        return degrees;
    }

    public void setDegrees(int degrees) {
        this.degrees = degrees;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        if (minutes >= 0 && minutes < 60) {
            this.minutes = minutes;
        } else {
            throw new IllegalArgumentException("Минуты должны быть в диапазоне [0, 59]");
        }
    }

    public double getSeconds() {
        return seconds;
    }

    public void setSeconds(double seconds) {
        if (seconds >= 0.0 && seconds < 60.0) {
            this.seconds = seconds;
        } else {
            throw new IllegalArgumentException("Секунды должны быть в диапазоне [0, 59.999...]");
        }
    }

    // Преобразование в десятичные градусы
    public double toDecimalDegrees() {
        return degrees + (minutes / 60.0) + (seconds / 3600.0);
    }

    // Создание из десятичных градусов
    public static AngleValue fromDecimalDegrees(double decimalDegrees) {
        try {
            double absDecimalDegrees = Math.abs(decimalDegrees);
            int degrees = (int) absDecimalDegrees;
            double minutesDecimal = (absDecimalDegrees - degrees) * 60.0;
            int minutes = (int) minutesDecimal;
            double seconds = (minutesDecimal - minutes) * 60.0;

            // Округляем секунды до 1 знака после запятой
            seconds = Math.round(seconds * 10) / 10.0;

            // Если секунды округлились до 60.0
            if (seconds >= 60.0) {
                seconds = 0.0;
                minutes += 1;
            }

            // Если минуты стали 60
            if (minutes >= 60) {
                minutes = 0;
                degrees += 1;
            }

            Log.d(TAG, "Преобразование из " + decimalDegrees + " в " + degrees + "° " + minutes + "′ " + seconds + "″");

            return new AngleValue(degrees, minutes, seconds);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при преобразовании десятичных градусов: " + e.getMessage());
            return new AngleValue(0, 0, 0);
        }
    }

    @Override
    public String toString() {
        // Используем US локаль для использования точки как десятичного разделителя
        return String.format(Locale.US, "%d°%d′%.1f″", degrees, minutes, seconds);
    }

    // Парсинг строкового представления угла
    public static AngleValue parseAngle(String angleString) {
        try {
            Log.d(TAG, "Парсинг угла: " + angleString);

            // Поддержка различных форматов ввода
            // 123°45′67.8″ или 123 45 67.8 или 123:45:67.8
            String normalizedString = angleString
                    .replace("°", " ")
                    .replace("′", " ")
                    .replace("″", " ")
                    .replace(":", " ")
                    .replace(",", ".") // Заменяем запятую на точку для правильного парсинга
                    .trim();

            String[] parts = normalizedString.split("\\s+");

            int degrees = Integer.parseInt(parts[0]);
            int minutes = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            double seconds = parts.length > 2 ? Double.parseDouble(parts[2]) : 0.0;

            Log.d(TAG, "Распознано: " + degrees + "° " + minutes + "′ " + seconds + "″");

            return new AngleValue(degrees, minutes, seconds);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при парсинге угла: " + e.getMessage());
            throw new IllegalArgumentException("Неверный формат угла. Используйте формат: градусы минуты секунды, например, 123 45 67.8");
        }
    }
}