// 1. Добавим новый класс для визуализации теодолитного хода

package com.example.javamark.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.example.javamark.model.AngleValue;
import com.example.javamark.model.StationMeasurement;
import com.example.javamark.model.TheodoliteJournal;

import java.util.ArrayList;
import java.util.List;

/**
 * Пользовательский View для отображения визуализации теодолитного хода
 */
public class TheodoliteVisualizationView extends View {
    private static final String TAG = "TheodoliteVisualization";

    private TheodoliteJournal journal;
    private List<PointF> points; // Список точек ломаной линии
    private float scale = 1.0f;  // Масштаб отображения
    private float offsetX = 0;   // Смещение по X для центрирования
    private float offsetY = 0;   // Смещение по Y для центрирования

    // Настройки отрисовки
    private Paint linePaint;
    private Paint pointPaint;
    private Paint textPaint;
    private Paint startPointPaint;

    public TheodoliteVisualizationView(Context context) {
        super(context);
        init();
    }

    public TheodoliteVisualizationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TheodoliteVisualizationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        points = new ArrayList<>();

        // Инициализация кисти для линий
        linePaint = new Paint();
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(4);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);

        // Инициализация кисти для точек
        pointPaint = new Paint();
        pointPaint.setColor(Color.RED);
        pointPaint.setStrokeWidth(10);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setAntiAlias(true);

        // Инициализация кисти для номеров точек
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(30);
        textPaint.setAntiAlias(true);

        // Инициализация кисти для начальной точки
        startPointPaint = new Paint();
        startPointPaint.setColor(Color.GREEN);
        startPointPaint.setStrokeWidth(15);
        startPointPaint.setStyle(Paint.Style.FILL);
        startPointPaint.setAntiAlias(true);
    }

    /**
     * Устанавливает журнал теодолитного хода и пересчитывает координаты точек
     */
    public void setJournal(TheodoliteJournal journal) {
        this.journal = journal;
        calculatePoints();
        invalidate(); // Перерисовка View
    }

    /**
     * Рассчитывает координаты точек теодолитного хода на основе журнала
     */
    private void calculatePoints() {
        if (journal == null || journal.getMeasurements() == null || journal.getMeasurements().isEmpty()) {
            Log.d(TAG, "Журнал пуст или не содержит измерений");
            return;
        }

        points.clear();

        // Условные начальные координаты первой точки
        PointF currentPoint = new PointF(0, 0);
        points.add(new PointF(currentPoint.x, currentPoint.y));

        // Начальное условное направление - вдоль оси X
        double currentDirection = 0; // в радианах, 0 соответствует направлению вправо

        List<StationMeasurement> measurements = journal.getMeasurements();
        for (StationMeasurement measurement : measurements) {
            // Получаем горизонтальное проложение (расстояние)
            float distance = (float) measurement.getHorizontalDistance();

            // Получаем угол между направлениями
            AngleValue averageAngle = measurement.getAverageAngle();
            if (averageAngle == null) {
                // Если угол не рассчитан, пропускаем точку
                Log.d(TAG, "Пропускаем точку из-за отсутствия среднего угла");
                continue;
            }

            // Конвертируем угол из градусов в радианы (против часовой стрелки)
            double angleInRadians = Math.toRadians(averageAngle.toDecimalDegrees());

            // Обновляем текущее направление
            currentDirection += angleInRadians;

            // Нормализуем направление в пределах [0, 2π)
            while (currentDirection >= 2 * Math.PI) {
                currentDirection -= 2 * Math.PI;
            }
            while (currentDirection < 0) {
                currentDirection += 2 * Math.PI;
            }

            // Вычисляем координаты следующей точки
            float nextX = currentPoint.x + distance * (float) Math.cos(currentDirection);
            float nextY = currentPoint.y + distance * (float) Math.sin(currentDirection);

            PointF nextPoint = new PointF(nextX, nextY);
            points.add(nextPoint);

            // Обновляем текущую точку
            currentPoint = nextPoint;
        }

        // Вычисляем масштаб и смещение для отображения всех точек
        calculateScaleAndOffset();
    }

    /**
     * Вычисляет масштаб и смещение для отображения всех точек
     */
    private void calculateScaleAndOffset() {
        if (points.isEmpty()) {
            return;
        }

        // Находим границы ломаной
        float minX = Float.MAX_VALUE;
        float minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float maxY = Float.MIN_VALUE;

        for (PointF point : points) {
            minX = Math.min(minX, point.x);
            minY = Math.min(minY, point.y);
            maxX = Math.max(maxX, point.x);
            maxY = Math.max(maxY, point.y);
        }

        // Добавляем отступы
        float padding = 50;

        // Вычисляем размеры ломаной
        float width = maxX - minX + 2 * padding;
        float height = maxY - minY + 2 * padding;

        // Вычисляем масштаб так, чтобы ломаная помещалась в View
        float scaleX = getWidth() / width;
        float scaleY = getHeight() / height;
        scale = Math.min(scaleX, scaleY);

        // Вычисляем смещение для центрирования
        offsetX = -minX * scale + padding;
        offsetY = -minY * scale + padding;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (points.isEmpty()) {
            return;
        }

        // Рисуем ломаную линию
        Path path = new Path();

        // Начинаем с первой точки
        PointF firstPoint = transformPoint(points.get(0));
        path.moveTo(firstPoint.x, firstPoint.y);

        // Рисуем начальную точку (зеленым)
        canvas.drawCircle(firstPoint.x, firstPoint.y, 10, startPointPaint);
        canvas.drawText("1", firstPoint.x + 15, firstPoint.y, textPaint);

        // Добавляем остальные точки
        for (int i = 1; i < points.size(); i++) {
            PointF point = transformPoint(points.get(i));
            path.lineTo(point.x, point.y);

            // Рисуем точку
            canvas.drawCircle(point.x, point.y, 7, pointPaint);

            // Рисуем номер точки
            canvas.drawText(String.valueOf(i + 1), point.x + 15, point.y, textPaint);
        }

        // Рисуем ломаную
        canvas.drawPath(path, linePaint);
    }

    /**
     * Преобразует координаты точки с учетом масштаба и смещения
     */
    private PointF transformPoint(PointF point) {
        float x = point.x * scale + offsetX;
        float y = getHeight() - (point.y * scale + offsetY); // Инвертируем Y для соответствия экранным координатам
        return new PointF(x, y);
    }

    /**
     * Обновляет размеры View и пересчитывает масштаб и смещение
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        calculateScaleAndOffset();
    }
}