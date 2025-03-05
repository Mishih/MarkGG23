package com.example.javamark.calculator;

import android.util.Log;

import com.example.javamark.model.AngleValue;
import com.example.javamark.model.GyroscopicMeasurement;
import android.content.Context;
import android.widget.Toast;
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

            if (kl1 != null && kp1 != null) {
                // Сначала преобразуем оба угла в десятичные градусы для более точных вычислений
                double kl1DecimalDegrees = kl1.toDecimalDegrees();
                double kp1DecimalDegrees = kp1.toDecimalDegrees();

                // Вычисляем среднее значение
                double nPrimeDecimalDegrees = (kl1DecimalDegrees + kp1DecimalDegrees) / 2.0;

                // Создаем AngleValue из десятичных градусов
                AngleValue nPrime = AngleValue.fromDecimalDegrees(nPrimeDecimalDegrees);
                measurement.setNPrime(nPrime);

                // Отладочная информация
                Log.d(TAG, "N' вычисление: (" + kl1 + " + " + kp1 + ") / 2 = " + nPrime);
            }

            // N'' - аналогично для КЛ2 и КП2
            AngleValue kl2 = measurement.getKL2();
            AngleValue kp2 = measurement.getKP2();

            if (kl2 != null && kp2 != null) {
                // Сначала преобразуем оба угла в десятичные градусы для более точных вычислений
                double kl2DecimalDegrees = kl2.toDecimalDegrees();
                double kp2DecimalDegrees = kp2.toDecimalDegrees();

                // Вычисляем среднее значение
                double nDoublePrimeDecimalDegrees = (kl2DecimalDegrees + kp2DecimalDegrees) / 2.0;

                // Создаем AngleValue из десятичных градусов
                AngleValue nDoublePrime = AngleValue.fromDecimalDegrees(nDoublePrimeDecimalDegrees);
                measurement.setNDoublePrime(nDoublePrime);

                // Отладочная информация
                Log.d(TAG, "N'' вычисление: (" + kl2 + " + " + kp2 + ") / 2 = " + nDoublePrime);
            }

            // N = (N' + N'') / 2 - всегда рассчитываем, даже если разница больше допустимой
            if (measurement.getNPrime() != null && measurement.getNDoublePrime() != null) {
                double nPrimeDecimal = measurement.getNPrime().toDecimalDegrees();
                double nDoublePrimeDecimal = measurement.getNDoublePrime().toDecimalDegrees();
                double nDecimal = (nPrimeDecimal + nDoublePrimeDecimal) / 2.0;

                AngleValue n = AngleValue.fromDecimalDegrees(nDecimal);
                measurement.setN(n);

                // Отладочная информация
                double differenceInDegrees = Math.abs(nPrimeDecimal - nDoublePrimeDecimal);
                double differenceInSeconds = differenceInDegrees * 3600;

                Log.d(TAG, "Примычное направление: N' = " + measurement.getNPrime() +
                        ", N'' = " + measurement.getNDoublePrime() +
                        ", |N' - N''| = " + String.format("%.1f", differenceInSeconds) + "\" " +
                        (differenceInSeconds <= 30.0 ? "✓" : "✗") +
                        ", N = " + n);
            }
        } catch (Exception e) {
            Log.e(TAG, "Ошибка при вычислении примычного направления: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Полностью переработанный метод расчета поправки за закручивание торсиона
     * без использования промежуточных AngleValue для сложения
     */
    public void calculateTorsionCorrection() {
        try {
            Context context = null;
            if (measurement instanceof GyroscopicMeasurement) {
                context = ((GyroscopicMeasurement) measurement).getContext();
            }

            // 1. Расчет ψt = t × (n₀ - nₖ)
            double tDecimalDegrees = measurement.getT().toDecimalDegrees();
            double n0Value = measurement.getN0Value();
            double nkValue = measurement.getNkValue();
            double difference = n0Value - nkValue;

            // Вычисляем ψt в десятичных градусах
            double psiTDecimal = tDecimalDegrees * difference;

            // Отладочное сообщение
            String logMsg1 = String.format("ψt вычисление: %.6f° × (%.4f - %.4f = %.4f) = %.6f°",
                    tDecimalDegrees, n0Value, nkValue, difference, psiTDecimal);
            Log.d(TAG, logMsg1);

            // Сохраняем ψt в модели
            AngleValue psiT = AngleValue.fromDecimalDegrees(psiTDecimal);
            measurement.setPsiT(psiT);

            // 2. Получаем и сохраняем Nk как среднее между Nk' и Nk''
            if (measurement.getNkPrime() == null || measurement.getNkDoublePrime() == null) {
                Log.e(TAG, "Nk' или Nk'' равны null, невозможно продолжить вычисления");
                if (context != null) {
                    Toast.makeText(context, "Ошибка: Nk' или Nk'' не заданы", Toast.LENGTH_LONG).show();
                }
                return;
            }

            double nkPrimeDecimal = measurement.getNkPrime().toDecimalDegrees();
            double nkDoublePrimeDecimal = measurement.getNkDoublePrime().toDecimalDegrees();
            double nkDecimal = (nkPrimeDecimal + nkDoublePrimeDecimal) / 2.0;

            AngleValue nk = AngleValue.fromDecimalDegrees(nkDecimal);
            measurement.setNk(nk);

            // 3. Расчет ψk = Nk - N0
            if (measurement.getN0() == null) {
                Log.e(TAG, "N0 равен null, невозможно продолжить вычисления");
                if (context != null) {
                    Toast.makeText(context, "Ошибка: N0 не задан", Toast.LENGTH_LONG).show();
                }
                return;
            }

            double n0Decimal = measurement.getN0().toDecimalDegrees();
            double psiKDecimal = nkDecimal - n0Decimal;

            // Отладочное сообщение
            String logMsg2 = String.format("ψk вычисление: %.4f° - %.4f° = %.4f°",
                    nkDecimal, n0Decimal, psiKDecimal);
            Log.d(TAG, logMsg2);

            // Сохраняем ψk в модели
            AngleValue psiK = AngleValue.fromDecimalDegrees(psiKDecimal);
            measurement.setPsiK(psiK);

            // 4. Прямой расчет ε = (ψt + ψk) / D в десятичных градусах
            double D = measurement.getD();

            // КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: Работаем с десятичными градусами, а не с объектами AngleValue
            double epsilonDecimal = (psiTDecimal + psiKDecimal) / D;

            // Отладочное сообщение с подробностями
            String logMsg3 = String.format("ε вычисление: (%.6f° + %.6f° = %.6f°) / %.2f = %.6f°",
                    psiTDecimal, psiKDecimal, (psiTDecimal + psiKDecimal), D, epsilonDecimal);
            Log.d(TAG, logMsg3);

            if (context != null) {
                Toast.makeText(context, logMsg3, Toast.LENGTH_LONG).show();
            }

            // Создаем AngleValue из десятичных градусов
            AngleValue epsilon = AngleValue.fromDecimalDegrees(epsilonDecimal);
            measurement.setEpsilon(epsilon);

            // Отладочное сообщение с итоговыми результатами
            Log.d(TAG, "Результаты поправки за закручивание торсиона: " +
                    "ψt = " + psiT.toString() + " (" + psiTDecimal + "°), " +
                    "ψk = " + psiK.toString() + " (" + psiKDecimal + "°), " +
                    "ε = " + epsilon.toString() + " (" + epsilonDecimal + "°)");

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
        return AngleValue.fromDecimalDegrees(decimalDegrees);
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