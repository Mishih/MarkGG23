package com.example.javamark.model;

import android.content.Context;
import java.io.Serializable;
import java.util.Date;

/**
 * Класс для хранения данных гироскопического ориентирования
 */
public class GyroscopicMeasurement implements Serializable {
    private int id;
    private String name;
    private Date createdAt;

    // transient, чтобы не сериализовать контекст
    private transient Context context;

    // Нуль торсиона (цифровые значения)
    private double n1Value, n2Value, n3Value, n4Value;
    private double n0PrimeValue, n0DoublePrimeValue, n0Value;

    // Положение равновесия ЧЭ (угловые значения)
    private AngleValue N1, N2, N3, N4;
    private AngleValue N0Prime, N0DoublePrime, N0;

    // Примычное направление
    private AngleValue KL1, KP1, KL2, KP2;
    private AngleValue NPrime, NDoublePrime, N;

    // Поправка за закручивание торсиона
    private double nkValue = 40.0; // По умолчанию 40
    private AngleValue t; // По умолчанию 0°1'
    private AngleValue NkPrime, NkDoublePrime, Nk;
    private AngleValue psiT, psiK;
    private double D = 1.0; // Добротность по умолчанию 1.0
    private AngleValue epsilon;

    // Результат
    private AngleValue gyroscopicAzimuth;

    public GyroscopicMeasurement() {
        this.createdAt = new Date();
        // Инициализация углов
        this.N1 = new AngleValue();
        this.N2 = new AngleValue();
        this.N3 = new AngleValue();
        this.N4 = new AngleValue();
        this.N0Prime = new AngleValue();
        this.N0DoublePrime = new AngleValue();
        this.N0 = new AngleValue();

        this.KL1 = new AngleValue();
        this.KP1 = new AngleValue();
        this.KL2 = new AngleValue();
        this.KP2 = new AngleValue();
        this.NPrime = new AngleValue();
        this.NDoublePrime = new AngleValue();
        this.N = new AngleValue();

        this.t = new AngleValue(0, 1, 0); // 0°1'0"
        this.NkPrime = new AngleValue();
        this.NkDoublePrime = new AngleValue();
        this.Nk = new AngleValue();
        this.psiT = new AngleValue();
        this.psiK = new AngleValue();
        this.epsilon = new AngleValue();

        this.gyroscopicAzimuth = new AngleValue();
    }

    public GyroscopicMeasurement(String name) {
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

    // Context getter/setter
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    // Нуль торсиона (цифровые значения)
    public double getN1Value() {
        return n1Value;
    }

    public void setN1(double n1) {
        this.n1Value = n1;
    }

    public double getN2Value() {
        return n2Value;
    }

    public void setN2(double n2) {
        this.n2Value = n2;
    }

    public double getN3Value() {
        return n3Value;
    }

    public void setN3(double n3) {
        this.n3Value = n3;
    }

    public double getN4Value() {
        return n4Value;
    }

    public void setN4(double n4) {
        this.n4Value = n4;
    }

    public double getN0PrimeValue() {
        return n0PrimeValue;
    }

    public void setN0Prime(double n0Prime) {
        this.n0PrimeValue = n0Prime;
    }

    public double getN0DoublePrimeValue() {
        return n0DoublePrimeValue;
    }

    public void setN0DoublePrime(double n0DoublePrime) {
        this.n0DoublePrimeValue = n0DoublePrime;
    }

    public double getN0Value() {
        return n0Value;
    }

    public void setN0(double n0) {
        this.n0Value = n0;
    }

    // Положение равновесия ЧЭ (угловые значения)
    public AngleValue getN1() {
        return N1;
    }

    public void setN1(AngleValue n1) {
        N1 = n1;
    }

    public AngleValue getN2() {
        return N2;
    }

    public void setN2(AngleValue n2) {
        N2 = n2;
    }

    public AngleValue getN3() {
        return N3;
    }

    public void setN3(AngleValue n3) {
        N3 = n3;
    }

    public AngleValue getN4() {
        return N4;
    }

    public void setN4(AngleValue n4) {
        N4 = n4;
    }

    public AngleValue getN0Prime() {
        return N0Prime;
    }

    public void setN0Prime(AngleValue n0Prime) {
        N0Prime = n0Prime;
    }

    public AngleValue getN0DoublePrime() {
        return N0DoublePrime;
    }

    public void setN0DoublePrime(AngleValue n0DoublePrime) {
        N0DoublePrime = n0DoublePrime;
    }

    public AngleValue getN0() {
        return N0;
    }

    public void setN0(AngleValue n0) {
        N0 = n0;
    }

    // Примычное направление
    public AngleValue getKL1() {
        return KL1;
    }

    public void setKL1(AngleValue KL1) {
        this.KL1 = KL1;
    }

    public AngleValue getKP1() {
        return KP1;
    }

    public void setKP1(AngleValue KP1) {
        this.KP1 = KP1;
    }

    public AngleValue getKL2() {
        return KL2;
    }

    public void setKL2(AngleValue KL2) {
        this.KL2 = KL2;
    }

    public AngleValue getKP2() {
        return KP2;
    }

    public void setKP2(AngleValue KP2) {
        this.KP2 = KP2;
    }

    public AngleValue getNPrime() {
        return NPrime;
    }

    public void setNPrime(AngleValue NPrime) {
        this.NPrime = NPrime;
    }

    public AngleValue getNDoublePrime() {
        return NDoublePrime;
    }

    public void setNDoublePrime(AngleValue NDoublePrime) {
        this.NDoublePrime = NDoublePrime;
    }

    public AngleValue getN() {
        return N;
    }

    public void setN(AngleValue n) {
        N = n;
    }

    // Поправка за закручивание торсиона
    public double getNkValue() {
        return nkValue;
    }

    public void setNk(double nk) {
        this.nkValue = nk;
    }

    public AngleValue getT() {
        return t;
    }

    public void setT(AngleValue t) {
        this.t = t;
    }

    public AngleValue getNkPrime() {
        return NkPrime;
    }

    public void setNkPrime(AngleValue nkPrime) {
        NkPrime = nkPrime;
    }

    public AngleValue getNkDoublePrime() {
        return NkDoublePrime;
    }

    public void setNkDoublePrime(AngleValue nkDoublePrime) {
        NkDoublePrime = nkDoublePrime;
    }

    public AngleValue getNk() {
        return Nk;
    }

    public void setNk(AngleValue nk) {
        Nk = nk;
    }

    public AngleValue getPsiT() {
        return psiT;
    }

    public void setPsiT(AngleValue psiT) {
        this.psiT = psiT;
    }

    public AngleValue getPsiK() {
        return psiK;
    }

    public void setPsiK(AngleValue psiK) {
        this.psiK = psiK;
    }

    public double getD() {
        return D;
    }

    public void setD(double d) {
        D = d;
    }

    public AngleValue getEpsilon() {
        return epsilon;
    }

    public void setEpsilon(AngleValue epsilon) {
        this.epsilon = epsilon;
    }

    // Результат
    public AngleValue getGyroscopicAzimuth() {
        return gyroscopicAzimuth;
    }

    public void setGyroscopicAzimuth(AngleValue gyroscopicAzimuth) {
        this.gyroscopicAzimuth = gyroscopicAzimuth;
    }

    // Форматированная дата
    public String getFormattedDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm");
        return sdf.format(createdAt);
    }
}