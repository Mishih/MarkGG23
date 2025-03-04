package com.example.javamark.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс, представляющий журнал теодолитного хода
 */
public class TheodoliteJournal implements Serializable {
    private int id;
    private String name;
    private List<StationMeasurement> measurements;
    private java.util.Date createdAt;

    public TheodoliteJournal() {
        this.measurements = new ArrayList<>();
        this.createdAt = new java.util.Date();
    }

    public TheodoliteJournal(String name) {
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

    public List<StationMeasurement> getMeasurements() {
        return measurements;
    }

    public void setMeasurements(List<StationMeasurement> measurements) {
        this.measurements = measurements;
    }

    public java.util.Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.util.Date createdAt) {
        this.createdAt = createdAt;
    }

    // Вспомогательные методы
    public void addMeasurement(StationMeasurement measurement) {
        this.measurements.add(measurement);
    }

    public String getFormattedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm");
        return sdf.format(createdAt);
    }
}