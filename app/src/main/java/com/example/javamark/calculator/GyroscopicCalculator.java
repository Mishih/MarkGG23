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
            // N' - для первой пары отсчетов (КЛ1 и КП1)
            AngleValue kl1 = measurement.getKL1();
            AngleValue kp1 = measurement.getKP1();

            // Берем градусы из КЛ
            int nPrimeDegrees = kl1.getDegrees();

            // Для минут и секунд: преобразуем в десятичные градусы, но только минуты и секунды
            double kl1DecimalMinSec = (kl1.getMinutes() / 60.0) + (kl1.getSeconds() / 3600.0);
            double kp1DecimalMinSec = (kp1.getMinutes() / 60.0) + (kp1.getSeconds() / 3600.0);

            // Среднее значение минут и секунд
            double nPrimeDecimalMinSec = (kl1DecimalMinSec + kp1DecimalMinSec) / 2.0;

            // Преобразуем обратно в минуты и секунды
            int nPrimeMinutes = (int)(nPrimeDecimalMinSec * 60);
            double nPrimeSeconds = (nPrimeDecimalMinSec * 60 - nPrimeMinutes) * 60;

            AngleValue nPrime = new AngleValue(nPrimeDegrees, nPrimeMinutes, nPrimeSeconds);
            measurement.setNPrime(nPrime);

            // N'' - аналогично для КЛ2 и КП2
            AngleValue kl2 = measurement.getKL2();
            AngleValue kp2 = measurement.getKP2();

            int nDoublePrimeDegrees = kl2.getDegrees();

            double kl2DecimalMinSec = (kl2.getMinutes() / 60.0) + (kl2.getSeconds() / 3600.0);
            double kp2DecimalMinSec = (kp2.getMinutes() / 60.0) + (kp2.getSeconds() / 3600.0);

            double nDoublePrimeDecimalMinSec = (kl2DecimalMinSec + kp2DecimalMinSec) / 2.0;

            int nDoublePrimeMinutes = (int)(nDoublePrimeDecimalMinSec * 60);
            double nDoublePrimeSeconds = (nDoublePrimeDecimalMinSec * 60 - nDoublePrimeMinutes) * 60;

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
            Context context = null;
            if (measurement instanceof GyroscopicMeasurement) {
                if (((GyroscopicMeasurement) measurement).getContext() != null) {
                    context = ((GyroscopicMeasurement) measurement).getContext();
                }
            }

            // ψt = t * (n0 - nk)
            double tDecimalDegrees = measurement.getT().toDecimalDegrees();
            double n0 = measurement.getN0Value(); // Берем n0 из нуля торсиона
            double nk = measurement.getNkValue();

            double n0_minus_nk = n0 - nk;

            // Вычисляем ψt в десятичных градусах
            double psiTDecimalDegrees = tDecimalDegrees * n0_minus_nk;

            // Отладочное сообщение
            String logMsg = String.format("ψt вычисление: %.4f° * (%.4f - %.4f = %.4f) = %.4f°",
                    tDecimalDegrees, n0, nk, n0_minus_nk, psiTDecimalDegrees);
            Log.d(TAG, logMsg);

            if (context != null) {
                Toast.makeText(context, logMsg, Toast.LENGTH_LONG).show();
            }

            // Преобразуем результат обратно в AngleValue, с сохранением знака
            AngleValue psiT = handleNegativeAngle(psiTDecimalDegrees);
            measurement.setPsiT(psiT);

            // ψk = Nk - N0
            // Сначала вычислим Nk как среднее между Nk' и Nk''
            AngleValue nkPrime = measurement.getNkPrime();
            AngleValue nkDoublePrime = measurement.getNkDoublePrime();

            if (nkPrime == null || nkDoublePrime == null) {
                Log.e(TAG, "Nk' или Nk'' равны null, невозможно вычислить ψk");
                if (context != null) {
                    Toast.makeText(context, "Ошибка: Nk' или Nk'' не заданы", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // Преобразуем в десятичные градусы для математических операций
            double nkPrimeDecimal = nkPrime.toDecimalDegrees();
            double nkDoublePrimeDecimal = nkDoublePrime.toDecimalDegrees();
            double nkDecimal = (nkPrimeDecimal + nkDoublePrimeDecimal) / 2.0;

            // Сохраняем Nk
            AngleValue nk_angle = AngleValue.fromDecimalDegrees(nkDecimal);
            measurement.setNk(nk_angle);

            // Получаем N0 из положения равновесия ЧЭ
            AngleValue n0Angle = measurement.getN0();
            if (n0Angle == null) {
                Log.e(TAG, "N0 равен null, невозможно вычислить ψk");
                if (context != null) {
                    Toast.makeText(context, "Ошибка: N0 не задан", Toast.LENGTH_LONG).show();
                }
                return;
            }

            double n0Decimal = n0Angle.toDecimalDegrees();

            // Вычисляем ψk в десятичных градусах
            double psiKDecimal = nkDecimal - n0Decimal;

            // Отладочное сообщение
            String logMsg2 = String.format("ψk вычисление: %.4f° - %.4f° = %.4f°",
                    nkDecimal, n0Decimal, psiKDecimal);
            Log.d(TAG, logMsg2);

            if (context != null) {
                Toast.makeText(context, logMsg2, Toast.LENGTH_LONG).show();
            }

            // Преобразуем результат обратно в AngleValue, с сохранением знака
            AngleValue psiK = handleNegativeAngle(psiKDecimal);
            measurement.setPsiK(psiK);

            // ε = (ψt + ψk) / D
            double psiTDecimal = psiT.toDecimalDegrees();
            double psiKDecimal2 = psiK.toDecimalDegrees();
            double D = measurement.getD();

            double sum = psiTDecimal + psiKDecimal2;

            // Вычисляем ε в десятичных градусах
            double epsilonDecimal = sum / D;

            // Отладочное сообщение
            String logMsg3 = String.format("ε вычисление: (%.4f° + %.4f° = %.4f°) / %.4f = %.4f°",
                    psiTDecimal, psiKDecimal2, sum, D, epsilonDecimal);
            Log.d(TAG, logMsg3);

            if (context != null) {
                Toast.makeText(context, logMsg3, Toast.LENGTH_LONG).show();
            }

            // Преобразуем результат обратно в AngleValue, с сохранением знака
            AngleValue epsilon = handleNegativeAngle(epsilonDecimal);
            measurement.setEpsilon(epsilon);

            Log.d(TAG, "Результаты поправки за закручивание торсиона: " +
                    "ψt = " + psiT + ", " +
                    "ψk = " + psiK + ", " +
                    "ε = " + epsilon);
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при вычислении поправки за закручивание торсиона: " + e.getMessage(), e);
            e.printStackTrace();
            if (measurement instanceof GyroscopicMeasurement &&
                    ((GyroscopicMeasurement) measurement).getContext() != null) {
                Toast.makeText(((GyroscopicMeasurement) measurement).getContext(),
                        "Ошибка: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
    /**
     * Обрабатывает отрицательные углы, сохраняя знак
     */
    private AngleValue handleNegativeAngle(double decimalDegrees) {
        boolean isNegative = decimalDegrees < 0;
        double absDegrees = Math.abs(decimalDegrees);

        int degrees = (int) absDegrees;
        double minutesDecimal = (absDegrees - degrees) * 60.0;
        int minutes = (int) minutesDecimal;
        double seconds = (minutesDecimal - minutes) * 60.0;

        // Округляем секунды до одного знака после запятой
        seconds = Math.round(seconds * 10) / 10.0;

        // Если секунды округлились до 60
        if (seconds >= 60.0) {
            seconds = 0;
            minutes++;
        }

        // Если минуты стали 60
        if (minutes >= 60) {
            minutes = 0;
            degrees++;
        }

        // Применяем знак только к градусам для сохранения правильного представления
        if (isNegative) {
            degrees = -degrees;
        }

        return new AngleValue(degrees, minutes, seconds);
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