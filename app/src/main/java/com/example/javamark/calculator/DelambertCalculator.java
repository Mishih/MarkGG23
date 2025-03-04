package com.example.javamark.calculator;

import com.example.javamark.model.CalculationResult;
import com.example.javamark.model.ReferencePoint;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для выполнения расчётов по методу Деламбра
 */
public class DelambertCalculator {
    private final List<ReferencePoint> referencePoints;
    private final double maxAllowableError;

    public DelambertCalculator(List<ReferencePoint> referencePoints, double maxAllowableError) {
        this.referencePoints = referencePoints;
        this.maxAllowableError = maxAllowableError;
    }

    /**
     * Выполняет полный расчёт обратной засечки
     * @param combination1 Список индексов точек для первой комбинации (размер 3)
     * @param combination2 Список индексов точек для второй комбинации (размер 3)
     * @return Результат вычислений
     */
    public CalculationResult calculate(int[] combination1, int[] combination2) {
        if (referencePoints.size() < 4) {
            throw new IllegalArgumentException("Необходимо минимум 4 исходных пункта");
        }

        CalculationResult result = new CalculationResult();

        // 1. Вычисление дирекционных углов
        calculateDirectionalAngles(result);

        // 2. Вычисление координат точки P по первой комбинации
        ReferencePoint point1 = referencePoints.get(combination1[0]);
        ReferencePoint point2 = referencePoints.get(combination1[1]);
        ReferencePoint point3 = referencePoints.get(combination1[2]);

        double[] coords1 = calculateCoordinates(point1, point2, point3,
                result.getDirectionalAngles().get(combination1[0]).getAngleInRadians(),
                result.getDirectionalAngles().get(combination1[1]).getAngleInRadians(),
                result.getDirectionalAngles().get(combination1[2]).getAngleInRadians());

        result.setX1(coords1[0]);
        result.setY1(coords1[1]);
        result.setCombinationInfo1(point1.getName() + ", " + point2.getName() + ", " + point3.getName());

        // 3. Вычисление координат точки P по второй комбинации
        ReferencePoint point4 = referencePoints.get(combination2[0]);
        ReferencePoint point5 = referencePoints.get(combination2[1]);
        ReferencePoint point6 = referencePoints.get(combination2[2]);

        double[] coords2 = calculateCoordinates(point4, point5, point6,
                result.getDirectionalAngles().get(combination2[0]).getAngleInRadians(),
                result.getDirectionalAngles().get(combination2[1]).getAngleInRadians(),
                result.getDirectionalAngles().get(combination2[2]).getAngleInRadians());

        result.setX2(coords2[0]);
        result.setY2(coords2[1]);
        result.setCombinationInfo2(point4.getName() + ", " + point5.getName() + ", " + point6.getName());

        // 4. Вычисление расхождения между комбинациями
        double discrepancy = calculateDiscrepancy(coords1[0], coords1[1], coords2[0], coords2[1]);
        result.setDiscrepancyMeters(discrepancy);

        // 5. Проверка на допустимое расхождение
        if (discrepancy <= maxAllowableError) {
            // Вычисление окончательных координат как среднего
            result.setFinalX((coords1[0] + coords2[0]) / 2);
            result.setFinalY((coords1[1] + coords2[1]) / 2);
        }

        // 6. Проверка на опасную окружность
        boolean isInsideDangerCircle = checkDangerCircle(result.getFinalX(), result.getFinalY(), point1, point2, point3);
        result.setInsideDangerCircle(isInsideDangerCircle);

        return result;
    }

    /**
     * Вычисляет дирекционные углы по формуле Деламбра
     */
    private void calculateDirectionalAngles(CalculationResult result) {
        // Находим исходную точку для расчёта первого дирекционного угла (обычно первая точка)
        ReferencePoint initialPoint = referencePoints.get(0);

        // Рассчитываем первый дирекционный угол alpha1_P
        double alpha1_P = calculateInitialDirectionalAngle(0);
        result.addDirectionalAngle(new CalculationResult.DirectionalAngle(initialPoint.getName(), alpha1_P));

        // Рассчитываем остальные дирекционные углы через первый и углы бета
        for (int i = 1; i < referencePoints.size(); i++) {
            ReferencePoint currentPoint = referencePoints.get(i);
            double betaSum = 0;

            // Суммируем все углы бета от начальной точки до текущей
            for (int j = 0; j < i; j++) {
                betaSum += referencePoints.get(j).getBetaInRadians();
            }

            // Вычисляем текущий дирекционный угол
            double alpha_P = alpha1_P + betaSum;
            // Приводим к диапазону [0, 2π)
            while (alpha_P >= 2 * Math.PI) {
                alpha_P -= 2 * Math.PI;
            }
            while (alpha_P < 0) {
                alpha_P += 2 * Math.PI;
            }

            result.addDirectionalAngle(new CalculationResult.DirectionalAngle(currentPoint.getName(), alpha_P));
        }
    }

    /**
     * Вычисляет начальный дирекционный угол по формуле Деламбра
     */
    private double calculateInitialDirectionalAngle(int initialPointIndex) {
        // Получаем необходимые точки
        ReferencePoint point1 = referencePoints.get(initialPointIndex);
        ReferencePoint point2 = referencePoints.get((initialPointIndex + 1) % referencePoints.size());
        ReferencePoint point3 = referencePoints.get((initialPointIndex + 2) % referencePoints.size());

        // Получаем координаты
        double x1 = point1.getX();
        double y1 = point1.getY();
        double x2 = point2.getX();
        double y2 = point2.getY();
        double x3 = point3.getX();
        double y3 = point3.getY();

        // Получаем котангенсы углов
        double ctgBeta1 = point1.getCtgBeta();
        double ctgBeta2 = point2.getCtgBeta();

        // Вычисляем тангенс дирекционного угла по формуле Деламбра
        double numerator = (y2 - y1) * ctgBeta1 + (y1 - y3) * ctgBeta2 - x2 + x3;
        double denominator = (x2 - x1) * ctgBeta1 + (x1 - x3) * ctgBeta2 + y2 - y3;

        double tanAlpha1_P = numerator / denominator;

        // Вычисляем сам угол
        double alpha1_P = Math.atan(tanAlpha1_P);

        // Определяем четверть для корректного значения угла
        if (denominator < 0) {
            alpha1_P += Math.PI;
        }
        if (alpha1_P < 0) {
            alpha1_P += 2 * Math.PI;
        }

        return alpha1_P;
    }

    /**
     * Вычисляет координаты точки P по формуле Гаусса
     */
    private double[] calculateCoordinates(ReferencePoint point1, ReferencePoint point2, ReferencePoint point3,
                                          double alpha1, double alpha2, double alpha3) {
        // Координаты для первой точки
        double x1 = point1.getX();
        double y1 = point1.getY();
        double x2 = point2.getX();
        double y2 = point2.getY();
        double x3 = point3.getX();
        double y3 = point3.getY();

        // Котангенсы дирекционных углов
        double ctgAlpha1 = 1.0 / Math.tan(alpha1);
        double ctgAlpha2 = 1.0 / Math.tan(alpha2);

        // Вычисление координат по формуле Гаусса
        double xP = x1 + ((x2 - x1) * ctgAlpha1 + (x3 - x1) * ctgAlpha2) / (ctgAlpha1 + ctgAlpha2);
        double yP = y1 + ((y2 - y1) * ctgAlpha1 + (y3 - y1) * ctgAlpha2) / (ctgAlpha1 + ctgAlpha2);

        return new double[] {xP, yP};
    }

    /**
     * Вычисляет расхождение между двумя комбинациями в метрах
     */
    private double calculateDiscrepancy(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) * 100; // Переводим в сантиметры
    }

    /**
     * Проверяет, находится ли точка P внутри опасной окружности
     */
    private boolean checkDangerCircle(double xP, double yP, ReferencePoint p1, ReferencePoint p2, ReferencePoint p3) {
        // Реализуем проверку на опасную окружность
        // Опасная окружность - это окружность, проходящая через три исходных пункта

        // Находим центр окружности, проходящей через три точки
        double x1 = p1.getX();
        double y1 = p1.getY();
        double x2 = p2.getX();
        double y2 = p2.getY();
        double x3 = p3.getX();
        double y3 = p3.getY();

        double A = x1 * (y2 - y3) - y1 * (x2 - x3) + x2 * y3 - x3 * y2;
        double B = (x1 * x1 + y1 * y1) * (y3 - y2) + (x2 * x2 + y2 * y2) * (y1 - y3) + (x3 * x3 + y3 * y3) * (y2 - y1);
        double C = (x1 * x1 + y1 * y1) * (x2 - x3) + (x2 * x2 + y2 * y2) * (x3 - x1) + (x3 * x3 + y3 * y3) * (x1 - x2);
        double D = (x1 * x1 + y1 * y1) * (x3 * y2 - x2 * y3) + (x2 * x2 + y2 * y2) * (x1 * y3 - x3 * y1) + (x3 * x3 + y3 * y3) * (x2 * y1 - x1 * y2);

        if (Math.abs(A) < 1e-10) {
            // Точки коллинеарны, окружность не может быть определена
            return false;
        }

        double xCenter = -B / (2 * A);
        double yCenter = -C / (2 * A);

        // Вычисляем радиус окружности
        double radius = Math.sqrt((xCenter - x1) * (xCenter - x1) + (yCenter - y1) * (yCenter - y1));

        // Проверяем, находится ли точка P внутри этой окружности
        double distanceToP = Math.sqrt((xCenter - xP) * (xCenter - xP) + (yCenter - yP) * (yCenter - yP));

        // Если дистанция меньше радиуса, точка внутри окружности
        return distanceToP < radius;
    }

    /**
     * Генерирует возможные комбинации из трех точек
     * @return Список возможных комбинаций (каждая комбинация - массив из трех индексов)
     */
    public List<int[]> generatePossibleCombinations() {
        List<int[]> combinations = new ArrayList<>();
        int n = referencePoints.size();

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    combinations.add(new int[] {i, j, k});
                }
            }
        }

        return combinations;
    }
}