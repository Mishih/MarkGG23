package com.example.javamark.calculator;

import android.util.Log;

import com.example.javamark.model.AngleValue;
import com.example.javamark.model.GyroscopicMeasurement;

/**
 * Класс для выполнения расчётов гироскопического ориентирования
 */
public class GyroscopicCalculator {
    private static final String TAG = "GyroscopicCalculator";
    private GyroscopicMeasurement measurement;

    public GyroscopicCalculator(GyroscopicMeasurement measurement) {
        this.measurement = measurement;
    }

    /**
     * Вычисляет нуль торсиона
     */
    public void calculateZeroTorsion() {
        try {
            // n0' = (n1 + 2*n2 + n3) / 4
            double n0Prime = (measurement.getN1Value() + 2 * measurement.getN2Value() + measurement.getN3Value()) / 4.0;

            // n0'' = (n2 + 2*n3 + n4) / 4
            double n0DoublePrime = (measurement.getN2Value() + 2 * measurement.getN3Value() + measurement.getN4Value()) / 4.0;

            // n0 = (n0' + n0'') / 2
            double n0 = (n0Prime + n0DoublePrime) / 2.0;

            measurement.setN0Prime(n0Prime);
            measurement.setN0DoublePrime(n0DoublePrime);
            measurement.setN0(n0);

            Log.d(TAG, "Нуль торсиона: n0' = " + n0Prime + ", n0'' = " + n0DoublePrime + ", n0 = " + n0);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при вычислении нуля торсиона: " + e.getMessage(), e);
        }
    }

    /**
     * Вычисляет положение равновесия ЧЭ
     */
    public void calculateEquilibrium() {
        try {
            // N0' = (N1 + 2*N2 + N3) / 4
            double n1Decimal = measurement.getN1().toDecimalDegrees();
            double n2Decimal = measurement.getN2().toDecimalDegrees();
            double n3Decimal = measurement.getN3().toDecimalDegrees();
            double n4Decimal = measurement.getN4().toDecimalDegrees();

            double n0PrimeDecimal = (n1Decimal + 2 * n2Decimal + n3Decimal) / 4.0;
            double n0DoublePrimeDecimal = (n2Decimal + 2 * n3Decimal + n4Decimal) / 4.0;
            double n0Decimal = (n0PrimeDecimal + n0DoublePrimeDecimal) / 2.0;

            measurement.setN0Prime(AngleValue.fromDecimalDegrees(n0PrimeDecimal));
            measurement.setN0DoublePrime(AngleValue.fromDecimalDegrees(n0DoublePrimeDecimal));
            measurement.setN0(AngleValue.fromDecimalDegrees(n0Decimal));

            Log.d(TAG, "Положение равновесия ЧЭ: N0' = " + measurement.getN0Prime() +
                    ", N0'' = " + measurement.getN0DoublePrime() +
                    ", N0 = " + measurement.getN0());
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при вычислении положения равновесия ЧЭ: " + e.getMessage(), e);
        }
    }

    /**
     * Вычисляет примычное направление
     */
    public void calculateDirection() {
        try {
            // N' = КЛ градусы + среднее(КЛ минуты/секунды, КП минуты/секунды)
            AngleValue kl1 = measurement.getKL1();
            AngleValue kp1 = measurement.getKP1();

            // Берем градусы из КЛ
            int nPrimeDegrees = kl1.getDegrees();

            // Среднее значение минут
            int nPrimeMinutes = (kl1.getMinutes() + kp1.getMinutes()) / 2;

            // Среднее значение секунд
            double nPrimeSeconds = (kl1.getSeconds() + kp1.getSeconds()) / 2.0;

            AngleValue nPrime = new AngleValue(nPrimeDegrees, nPrimeMinutes, nPrimeSeconds);
            measurement.setNPrime(nPrime);

            // N'' = аналогично для КЛ2 и КП2
            AngleValue kl2 = measurement.getKL2();
            AngleValue kp2 = measurement.getKP2();

            int nDoublePrimeDegrees = kl2.getDegrees();
            int nDoublePrimeMinutes = (kl2.getMinutes() + kp2.getMinutes()) / 2;
            double nDoublePrimeSeconds = (kl2.getSeconds() + kp2.getSeconds()) / 2.0;

            AngleValue nDoublePrime = new AngleValue(nDoublePrimeDegrees, nDoublePrimeMinutes, nDoublePrimeSeconds);
            measurement.setNDoublePrime(nDoublePrime);

            // N = (N' + N'') / 2
            double nDecimal = (nPrime.toDecimalDegrees() + nDoublePrime.toDecimalDegrees()) / 2.0;
            measurement.setN(AngleValue.fromDecimalDegrees(nDecimal));

            Log.d(TAG, "Примычное направление: N' = " + nPrime +
                    ", N'' = " + nDoublePrime +
                    ", N = " + measurement.getN());
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при вычислении примычного направления: " + e.getMessage(), e);
        }
    }

    /**
     * Вычисляет поправку за закручивание торсиона
     */
    public void calculateTorsionCorrection() {
        try {
            // ψt = t * (n0 - nk)
            double n0 = measurement.getN0Value();
            double nk = measurement.getNkValue();
            double tDecimal = measurement.getT().toDecimalDegrees();

            double psiTDecimal = tDecimal * (n0 - nk);
            measurement.setPsiT(AngleValue.fromDecimalDegrees(psiTDecimal));

            // ψk = Nk - N0
            // Nk = (Nk' + Nk'') / 2
            double nkPrimeDecimal = measurement.getNkPrime().toDecimalDegrees();
            double nkDoublePrimeDecimal = measurement.getNkDoublePrime().toDecimalDegrees();
            double nkDecimal = (nkPrimeDecimal + nkDoublePrimeDecimal) / 2.0;

            measurement.setNk(AngleValue.fromDecimalDegrees(nkDecimal));

            double n0Decimal = measurement.getN0().toDecimalDegrees();
            double psiKDecimal = nkDecimal - n0Decimal;

            measurement.setPsiK(AngleValue.fromDecimalDegrees(psiKDecimal));

            // ε = (ψt + ψk) / D
            double psiTValue = measurement.getPsiT().toDecimalDegrees();
            double psiKValue = measurement.getPsiK().toDecimalDegrees();
            double D = measurement.getD();

            double epsilonDecimal = (psiTValue + psiKValue) / D;
            measurement.setEpsilon(AngleValue.fromDecimalDegrees(epsilonDecimal));

            Log.d(TAG, "Поправка за закручивание торсиона: ψt = " + measurement.getPsiT() +
                    ", ψk = " + measurement.getPsiK() +
                    ", ε = " + measurement.getEpsilon());
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при вычислении поправки за закручивание торсиона: " + e.getMessage(), e);
        }
    }

    /**
     * Вычисляет гироскопический азимут
     */
    public void calculateGyroscopicAzimuth() {
        try {
            // Г = N - N0 + ε
            double nDecimal = measurement.getN().toDecimalDegrees();
            double n0Decimal = measurement.getN0().toDecimalDegrees();
            double epsilonDecimal = measurement.getEpsilon().toDecimalDegrees();

            double azimuthDecimal = nDecimal - n0Decimal + epsilonDecimal;

            // Приведение к диапазону [0, 360)
            while (azimuthDecimal < 0) {
                azimuthDecimal += 360.0;
            }
            while (azimuthDecimal >= 360.0) {
                azimuthDecimal -= 360.0;
            }

            measurement.setGyroscopicAzimuth(AngleValue.fromDecimalDegrees(azimuthDecimal));

            Log.d(TAG, "Гироскопический азимут: Г = " + measurement.getGyroscopicAzimuth());
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при вычислении гироскопического азимута: " + e.getMessage(), e);
        }
    }

    /**
     * Выполняет все вычисления последовательно
     */
    public void calculateAll() {
        calculateZeroTorsion();
        calculateEquilibrium();
        calculateDirection();
        calculateTorsionCorrection();
        calculateGyroscopicAzimuth();
    }
}