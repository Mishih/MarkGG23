package com.example.javamark.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для хранения результатов вычислений обратной засечки
 */
public class CalculationResult implements Serializable {
    private List<DirectionalAngle> directionalAngles;
    private double x1, y1; // Координаты точки P по первой комбинации
    private double x2, y2; // Координаты точки P по второй комбинации
    private double discrepancyMeters; // Расхождение между комбинациями в метрах
    private double finalX, finalY; // Окончательные координаты (среднее)
    private boolean isInsideDangerCircle; // Флаг нахождения внутри опасной окружности
    private String combinationInfo1; // Информация о первой комбинации точек
    private String combinationInfo2; // Информация о второй комбинации точек

    public CalculationResult() {
        this.directionalAngles = new ArrayList<>();
    }

    // Getters и Setters
    public List<DirectionalAngle> getDirectionalAngles() {
        return directionalAngles;
    }

    public void setDirectionalAngles(List<DirectionalAngle> directionalAngles) {
        this.directionalAngles = directionalAngles;
    }

    public double getX1() {
        return x1;
    }

    public void setX1(double x1) {
        this.x1 = x1;
    }

    public double getY1() {
        return y1;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public double getX2() {
        return x2;
    }

    public void setX2(double x2) {
        this.x2 = x2;
    }

    public double getY2() {
        return y2;
    }

    public void setY2(double y2) {
        this.y2 = y2;
    }

    public double getDiscrepancyMeters() {
        return discrepancyMeters;
    }

    public void setDiscrepancyMeters(double discrepancyMeters) {
        this.discrepancyMeters = discrepancyMeters;
    }

    public double getFinalX() {
        return finalX;
    }

    public void setFinalX(double finalX) {
        this.finalX = finalX;
    }

    public double getFinalY() {
        return finalY;
    }

    public void setFinalY(double finalY) {
        this.finalY = finalY;
    }

    public boolean isInsideDangerCircle() {
        return isInsideDangerCircle;
    }

    public void setInsideDangerCircle(boolean insideDangerCircle) {
        isInsideDangerCircle = insideDangerCircle;
    }

    public String getCombinationInfo1() {
        return combinationInfo1;
    }

    public void setCombinationInfo1(String combinationInfo1) {
        this.combinationInfo1 = combinationInfo1;
    }

    public String getCombinationInfo2() {
        return combinationInfo2;
    }

    public void setCombinationInfo2(String combinationInfo2) {
        this.combinationInfo2 = combinationInfo2;
    }

    public void addDirectionalAngle(DirectionalAngle angle) {
        this.directionalAngles.add(angle);
    }

    /**
     * Вспомогательный класс для хранения данных о дирекционном угле
     */
    public static class DirectionalAngle implements Serializable {
        private String pointName;
        private double angleInRadians;
        private double angleInDegrees;

        public DirectionalAngle(String pointName, double angleInRadians) {
            this.pointName = pointName;
            this.angleInRadians = angleInRadians;
            this.angleInDegrees = Math.toDegrees(angleInRadians);
        }

        public String getPointName() {
            return pointName;
        }

        public double getAngleInRadians() {
            return angleInRadians;
        }

        public double getAngleInDegrees() {
            return angleInDegrees;
        }

        public String getFormattedAngle() {
            int degrees = (int) angleInDegrees;
            int minutes = (int) ((angleInDegrees - degrees) * 60);
            double seconds = ((angleInDegrees - degrees) * 60 - minutes) * 60;
            return String.format("%d° %d′ %.1f″", degrees, minutes, seconds);
        }
    }
}