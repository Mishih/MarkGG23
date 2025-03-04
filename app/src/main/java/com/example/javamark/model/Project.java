package com.example.javamark.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Класс представляет проект обратной засечки со всеми исходными данными и результатами
 */
public class Project implements Serializable {
    private int id;
    private String name;
    private Date createdAt;
    private List<ReferencePoint> referencePoints;
    private double maxAllowableError;
    private CalculationResult result;

    public Project() {
        this.referencePoints = new ArrayList<>();
        this.createdAt = new Date();
    }

    public Project(String name) {
        this();
        this.name = name;
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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public List<ReferencePoint> getReferencePoints() {
        return referencePoints;
    }

    public void setReferencePoints(List<ReferencePoint> referencePoints) {
        this.referencePoints = referencePoints;
    }

    public double getMaxAllowableError() {
        return maxAllowableError;
    }

    public void setMaxAllowableError(double maxAllowableError) {
        this.maxAllowableError = maxAllowableError;
    }

    public CalculationResult getResult() {
        return result;
    }

    public void setResult(CalculationResult result) {
        this.result = result;
    }

    // Вспомогательные методы
    public void addReferencePoint(ReferencePoint point) {
        this.referencePoints.add(point);
    }

    public boolean hasMinimumRequiredPoints() {
        return referencePoints.size() >= 4;
    }

    public String getFormattedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm");
        return sdf.format(createdAt);
    }
}