package com.example.javamark.calculator;

import com.example.javamark.model.AngleValue;
import com.example.javamark.model.StationMeasurement;
import com.example.javamark.model.TheodoliteJournal;

/**
 * Класс для выполнения расчётов теодолитного хода
 */
public class TheodoliteCalculator {

    private TheodoliteJournal journal;

    public TheodoliteCalculator(TheodoliteJournal journal) {
        this.journal = journal;
    }

    /**
     * Выполняет все вычисления для журнала теодолитного хода
     */
    public void calculateAll() {
        if (journal.getMeasurements() == null || journal.getMeasurements().isEmpty()) {
            return;
        }

        for (StationMeasurement measurement : journal.getMeasurements()) {
            calculateMeasurement(measurement);
        }
    }

    /**
     * Выполняет вычисления для одного измерения
     */
    public void calculateMeasurement(StationMeasurement measurement) {
        // Вычисление углов (разница КЛ, разница КП, средний угол)
        measurement.calculateAngles();

        // Вычисление горизонтального проложения
        measurement.calculateHorizontalDistance();
    }

    /**
     * Вычисляет место нуля (MO) и вертикальный угол по формулам из изображения
     * @param leftCircle отсчет при круге лево (КЛ)
     * @param rightCircle отсчет при круге право (КП)
     * @return массив из двух значений: [MO, verticalAngle]
     */
    public static AngleValue[] calculateVerticalAngle(AngleValue leftCircle, AngleValue rightCircle) {
        double kl = leftCircle.toDecimalDegrees();
        double kp = rightCircle.toDecimalDegrees();

        // Вычисление места нуля MO = (КП + КЛ) / 2
        double mo = (kp + kl) / 2.0;

        // Вычисление вертикального угла ν = MO - КП
        double v = mo - kp;

        AngleValue moAngle = AngleValue.fromDecimalDegrees(mo);
        AngleValue verticalAngle = AngleValue.fromDecimalDegrees(v);

        return new AngleValue[] {moAngle, verticalAngle};
    }

    /**
     * Проверяет допустимую разность между КЛ и КП
     * @param leftCircle отсчет при круге лево (КЛ)
     * @param rightCircle отсчет при круге право (КП)
     * @param allowableDifference допустимая разность в угловых минутах
     * @return true, если разность не превышает допустимую
     */
    public static boolean checkCircleDifference(AngleValue leftCircle, AngleValue rightCircle, double allowableDifference) {
        double kl = leftCircle.toDecimalDegrees();
        double kp = rightCircle.toDecimalDegrees();

        // Преобразуем допустимую разность из минут в градусы
        double allowableDifferenceInDegrees = allowableDifference / 60.0;

        return Math.abs(kl - kp) <= allowableDifferenceInDegrees;
    }
}