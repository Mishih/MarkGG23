package com.example.javamark.model;

import android.util.Log;
import java.io.Serializable;

/**
 * Класс, представляющий измерение на станции теодолитного хода
 */
public class StationMeasurement implements Serializable {
    private static final String TAG = "StationMeasurement";

    private int stationNumber;         // Номер станции
    private int pointNumber1;          // Номер первой точки визирования
    private int pointNumber2;          // Номер второй точки визирования

    // Расстояния до точек визирования
    private double distanceToPoint1;   // Расстояние до первой точки, м
    private double distanceToPoint2;   // Расстояние до второй точки, м

    // Отсчеты для первой точки
    private AngleValue leftCirclePoint1;  // Отсчет КЛ на первую точку
    private AngleValue rightCirclePoint1; // Отсчет КП на первую точку

    // Отсчеты для второй точки
    private AngleValue leftCirclePoint2;  // Отсчет КЛ на вторую точку
    private AngleValue rightCirclePoint2; // Отсчет КП на вторую точку

    // Расчетные величины
    private AngleValue angleLeftDifference;  // Разница между КЛ точек
    private AngleValue angleRightDifference; // Разница между КП точек (с учетом 360°)
    private AngleValue averageAngle;        // Средний угол

    public StationMeasurement() {
        this.leftCirclePoint1 = new AngleValue();
        this.rightCirclePoint1 = new AngleValue();
        this.leftCirclePoint2 = new AngleValue();
        this.rightCirclePoint2 = new AngleValue();
        this.angleLeftDifference = new AngleValue();
        this.angleRightDifference = new AngleValue();
        this.averageAngle = new AngleValue();
    }

    public StationMeasurement(int stationNumber, int pointNumber1, int pointNumber2) {
        this();
        this.stationNumber = stationNumber;
        this.pointNumber1 = pointNumber1;
        this.pointNumber2 = pointNumber2;
    }

    // Getters and Setters
    public int getStationNumber() {
        return stationNumber;
    }

    public void setStationNumber(int stationNumber) {
        this.stationNumber = stationNumber;
    }

    public int getPointNumber1() {
        return pointNumber1;
    }

    public void setPointNumber1(int pointNumber1) {
        this.pointNumber1 = pointNumber1;
    }

    public int getPointNumber2() {
        return pointNumber2;
    }

    public void setPointNumber2(int pointNumber2) {
        this.pointNumber2 = pointNumber2;
    }

    public double getDistanceToPoint1() {
        return distanceToPoint1;
    }

    public void setDistanceToPoint1(double distanceToPoint1) {
        this.distanceToPoint1 = distanceToPoint1;
    }

    public double getDistanceToPoint2() {
        return distanceToPoint2;
    }

    public void setDistanceToPoint2(double distanceToPoint2) {
        this.distanceToPoint2 = distanceToPoint2;
    }

    public AngleValue getLeftCirclePoint1() {
        return leftCirclePoint1;
    }

    public void setLeftCirclePoint1(AngleValue leftCirclePoint1) {
        this.leftCirclePoint1 = leftCirclePoint1;
    }

    public AngleValue getRightCirclePoint1() {
        return rightCirclePoint1;
    }

    public void setRightCirclePoint1(AngleValue rightCirclePoint1) {
        this.rightCirclePoint1 = rightCirclePoint1;
    }

    public AngleValue getLeftCirclePoint2() {
        return leftCirclePoint2;
    }

    public void setLeftCirclePoint2(AngleValue leftCirclePoint2) {
        this.leftCirclePoint2 = leftCirclePoint2;
    }

    public AngleValue getRightCirclePoint2() {
        return rightCirclePoint2;
    }

    public void setRightCirclePoint2(AngleValue rightCirclePoint2) {
        this.rightCirclePoint2 = rightCirclePoint2;
    }

    public AngleValue getAngleLeftDifference() {
        return angleLeftDifference;
    }

    public void setAngleLeftDifference(AngleValue angleLeftDifference) {
        this.angleLeftDifference = angleLeftDifference;
    }

    public AngleValue getAngleRightDifference() {
        return angleRightDifference;
    }

    public void setAngleRightDifference(AngleValue angleRightDifference) {
        this.angleRightDifference = angleRightDifference;
    }

    public AngleValue getAverageAngle() {
        return averageAngle;
    }

    public void setAverageAngle(AngleValue averageAngle) {
        this.averageAngle = averageAngle;
    }

    // Вычисления углов
    public void calculateAngles() {
        try {
            // Проверяем, что все необходимые отсчеты введены и не содержат нулевые значения
            if (leftCirclePoint1 != null && leftCirclePoint2 != null &&
                    rightCirclePoint1 != null && rightCirclePoint2 != null &&
                    (leftCirclePoint1.getDegrees() > 0 || leftCirclePoint1.getMinutes() > 0 || leftCirclePoint1.getSeconds() > 0) &&
                    (leftCirclePoint2.getDegrees() > 0 || leftCirclePoint2.getMinutes() > 0 || leftCirclePoint2.getSeconds() > 0) &&
                    (rightCirclePoint1.getDegrees() > 0 || rightCirclePoint1.getMinutes() > 0 || rightCirclePoint1.getSeconds() > 0) &&
                    (rightCirclePoint2.getDegrees() > 0 || rightCirclePoint2.getMinutes() > 0 || rightCirclePoint2.getSeconds() > 0)) {

                // Вычисляем разницу между отсчетами КЛ
                double kl1 = leftCirclePoint1.toDecimalDegrees();
                double kl2 = leftCirclePoint2.toDecimalDegrees();
                double klDiff = Math.abs(kl2 - kl1);

                Log.d(TAG, "КЛ1: " + kl1 + ", КЛ2: " + kl2 + ", Разница КЛ: " + klDiff);

                this.angleLeftDifference = AngleValue.fromDecimalDegrees(klDiff);

                // Вычисляем разницу между отсчетами КП
                double kp1 = rightCirclePoint1.toDecimalDegrees();
                double kp2 = rightCirclePoint2.toDecimalDegrees();
                double kpDiff = Math.abs(kp2 - kp1);

                // Для КП сначала находим разницу, потом из 360° вычитаем
                double adjustedKpDiff = 360.0 - kpDiff;

                Log.d(TAG, "КП1: " + kp1 + ", КП2: " + kp2 + ", Разница КП: " + kpDiff + ", Скорректированная разница КП: " + adjustedKpDiff);

                this.angleRightDifference = AngleValue.fromDecimalDegrees(adjustedKpDiff);

                // Вычисляем средний угол как среднее между двумя разницами
                double avgDiff = (klDiff + adjustedKpDiff) / 2.0;

                Log.d(TAG, "Средний угол: " + avgDiff);

                this.averageAngle = AngleValue.fromDecimalDegrees(avgDiff);
            } else {
                Log.d(TAG, "Не все необходимые отсчеты введены или содержат нулевые значения");
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при вычислении углов: " + e.getMessage());
            e.printStackTrace();
        }
    }
}