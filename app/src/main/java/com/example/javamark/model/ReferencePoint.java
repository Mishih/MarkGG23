package com.example.javamark.model;

import java.io.Serializable;

/**
 * Класс представляет исходный пункт с известными координатами
 * и измеренным углом (бета) для обратной засечки
 */
public class ReferencePoint implements Serializable {
    private int id;
    private String name;
    private double x;
    private double y;
    private double beta; // Угол в градусах

    public ReferencePoint() {
    }

    public ReferencePoint(int id, String name, double x, double y, double beta) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.beta = beta;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getBeta() {
        return beta;
    }

    public void setBeta(double beta) {
        this.beta = beta;
    }

    // Метод для преобразования угла бета из градусов в радианы
    public double getBetaInRadians() {
        return Math.toRadians(beta);
    }

    // Метод для вычисления котангенса угла бета
    public double getCtgBeta() {
        return 1.0 / Math.tan(getBetaInRadians());
    }
}