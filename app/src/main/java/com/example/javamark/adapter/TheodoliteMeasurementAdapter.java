package com.example.javamark.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javamark.R;
import com.example.javamark.model.AngleValue;
import com.example.javamark.model.StationMeasurement;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Адаптер для списка измерений теодолитного хода
 */
public class TheodoliteMeasurementAdapter extends RecyclerView.Adapter<TheodoliteMeasurementAdapter.ViewHolder> {

    private final List<StationMeasurement> measurements;
    private final OnMeasurementChangeListener listener;

    /**
     * Интерфейс обратного вызова для изменений измерений
     */
    public interface OnMeasurementChangeListener {
        void onMeasurementChanged(int position, StationMeasurement measurement);
        void onMeasurementRemoved(int position);
    }

    public TheodoliteMeasurementAdapter(List<StationMeasurement> measurements, OnMeasurementChangeListener listener) {
        this.measurements = measurements;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_theodolite_measurement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StationMeasurement measurement = measurements.get(position);
        holder.bind(measurement, position);
    }

    @Override
    public int getItemCount() {
        return measurements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView stationNumberTextView;
        private final TextView pointNumbersTextView;
        private final TextInputEditText distanceEditText;

        // Поля для первой точки
        private final TextInputEditText leftCirclePoint1EditText;
        private final TextInputEditText rightCirclePoint1EditText;

        // Поля для второй точки
        private final TextInputEditText leftCirclePoint2EditText;
        private final TextInputEditText rightCirclePoint2EditText;

        // Результаты вычислений
        private final TextView leftDifferenceTextView;
        private final TextView rightDifferenceTextView;
        private final TextView averageAngleTextView;
        private final TextInputEditText slopeAngleEditText;
        private final TextView horizontalDistanceTextView;
        private final ImageButton removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            stationNumberTextView = itemView.findViewById(R.id.tv_station_number);
            pointNumbersTextView = itemView.findViewById(R.id.tv_point_numbers);
            distanceEditText = itemView.findViewById(R.id.et_distance);

            // Поля для первой точки
            leftCirclePoint1EditText = itemView.findViewById(R.id.et_left_circle_point1);
            rightCirclePoint1EditText = itemView.findViewById(R.id.et_right_circle_point1);

            // Поля для второй точки
            leftCirclePoint2EditText = itemView.findViewById(R.id.et_left_circle_point2);
            rightCirclePoint2EditText = itemView.findViewById(R.id.et_right_circle_point2);

            // Результаты вычислений
            leftDifferenceTextView = itemView.findViewById(R.id.tv_left_difference);
            rightDifferenceTextView = itemView.findViewById(R.id.tv_right_difference);
            averageAngleTextView = itemView.findViewById(R.id.tv_average_angle);
            slopeAngleEditText = itemView.findViewById(R.id.et_slope_angle);
            horizontalDistanceTextView = itemView.findViewById(R.id.tv_horizontal_distance);
            removeButton = itemView.findViewById(R.id.btn_remove_measurement);
        }

        public void bind(StationMeasurement measurement, int position) {
            // Отображение станции и точек
            stationNumberTextView.setText(String.valueOf(measurement.getStationNumber()));
            pointNumbersTextView.setText(measurement.getPointNumber1() + " → " + measurement.getPointNumber2());

            // Отображение расстояния
            if (measurement.getDistance() > 0) {
                distanceEditText.setText(String.valueOf(measurement.getDistance()));
            } else {
                distanceEditText.setText("");
            }

            // Отображение отсчетов для первой точки
            if (measurement.getLeftCirclePoint1() != null) {
                leftCirclePoint1EditText.setText(measurement.getLeftCirclePoint1().toString());
            } else {
                leftCirclePoint1EditText.setText("");
            }

            if (measurement.getRightCirclePoint1() != null) {
                rightCirclePoint1EditText.setText(measurement.getRightCirclePoint1().toString());
            } else {
                rightCirclePoint1EditText.setText("");
            }

            // Отображение отсчетов для второй точки
            if (measurement.getLeftCirclePoint2() != null) {
                leftCirclePoint2EditText.setText(measurement.getLeftCirclePoint2().toString());
            } else {
                leftCirclePoint2EditText.setText("");
            }

            if (measurement.getRightCirclePoint2() != null) {
                rightCirclePoint2EditText.setText(measurement.getRightCirclePoint2().toString());
            } else {
                rightCirclePoint2EditText.setText("");
            }

            // Отображение угла наклона
            if (measurement.getSlopeAngle() != null) {
                slopeAngleEditText.setText(measurement.getSlopeAngle().toString());
            } else {
                slopeAngleEditText.setText("");
            }

            // Отображение вычисленных значений
            updateCalculatedValues(measurement);

            // Обработка изменений в поле расстояния
            distanceEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        if (!s.toString().isEmpty()) {
                            double distance = Double.parseDouble(s.toString());
                            measurement.setDistance(distance);
                            measurement.calculateHorizontalDistance();
                            updateCalculatedValues(measurement);
                            notifyChange(measurement);
                        }
                    } catch (NumberFormatException e) {
                        // Игнорируем ошибку парсинга
                    }
                }
            });

            // Обработка изменений в отсчетах для первой точки
            leftCirclePoint1EditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        String angleString = s.toString();
                        if (!angleString.isEmpty()) {
                            AngleValue angle = AngleValue.parseAngle(angleString);
                            measurement.setLeftCirclePoint1(angle);
                            measurement.calculateAngles();
                            updateCalculatedValues(measurement);
                            notifyChange(measurement);
                        }
                    } catch (Exception e) {
                        // Игнорируем ошибку парсинга
                    }
                }
            });

            rightCirclePoint1EditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        String angleString = s.toString();
                        if (!angleString.isEmpty()) {
                            AngleValue angle = AngleValue.parseAngle(angleString);
                            measurement.setRightCirclePoint1(angle);
                            measurement.calculateAngles();
                            updateCalculatedValues(measurement);
                            notifyChange(measurement);
                        }
                    } catch (Exception e) {
                        // Игнорируем ошибку парсинга
                    }
                }
            });

            // Обработка изменений в отсчетах для второй точки
            leftCirclePoint2EditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        String angleString = s.toString();
                        if (!angleString.isEmpty()) {
                            AngleValue angle = AngleValue.parseAngle(angleString);
                            measurement.setLeftCirclePoint2(angle);
                            measurement.calculateAngles();
                            updateCalculatedValues(measurement);
                            notifyChange(measurement);
                        }
                    } catch (Exception e) {
                        // Игнорируем ошибку парсинга
                    }
                }
            });

            rightCirclePoint2EditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        String angleString = s.toString();
                        if (!angleString.isEmpty()) {
                            AngleValue angle = AngleValue.parseAngle(angleString);
                            measurement.setRightCirclePoint2(angle);
                            measurement.calculateAngles();
                            updateCalculatedValues(measurement);
                            notifyChange(measurement);
                        }
                    } catch (Exception e) {
                        // Игнорируем ошибку парсинга
                    }
                }
            });

            // Обработка изменений в поле угла наклона
            slopeAngleEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        String angleString = s.toString();
                        if (!angleString.isEmpty()) {
                            AngleValue angle = AngleValue.parseAngle(angleString);
                            measurement.setSlopeAngle(angle);
                            measurement.calculateHorizontalDistance();
                            updateCalculatedValues(measurement);
                            notifyChange(measurement);
                        }
                    } catch (Exception e) {
                        // Игнорируем ошибку парсинга
                    }
                }
            });

            // Обработка нажатия на кнопку удаления
            removeButton.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMeasurementRemoved(getAdapterPosition());
                }
            });
        }

        private void updateCalculatedValues(StationMeasurement measurement) {
            DecimalFormat df = new DecimalFormat("#0.00");

            // Отображение разницы КЛ
            if (measurement.getAngleLeftDifference() != null) {
                leftDifferenceTextView.setText(measurement.getAngleLeftDifference().toString());
            } else {
                leftDifferenceTextView.setText("-");
            }

            // Отображение разницы КП
            if (measurement.getAngleRightDifference() != null) {
                rightDifferenceTextView.setText(measurement.getAngleRightDifference().toString());
            } else {
                rightDifferenceTextView.setText("-");
            }

            // Отображение среднего угла
            if (measurement.getAverageAngle() != null) {
                averageAngleTextView.setText(measurement.getAverageAngle().toString());
            } else {
                averageAngleTextView.setText("-");
            }

            // Отображение горизонтального проложения
            if (measurement.getHorizontalDistance() > 0) {
                horizontalDistanceTextView.setText(df.format(measurement.getHorizontalDistance()));
            } else {
                horizontalDistanceTextView.setText("-");
            }
        }

        private void notifyChange(StationMeasurement measurement) {
            if (listener != null) {
                listener.onMeasurementChanged(getAdapterPosition(), measurement);
            }
        }
    }

    /**
     * Удаляет измерение из списка
     */
    public void removeMeasurement(int position) {
        if (position >= 0 && position < measurements.size()) {
            measurements.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, measurements.size());
        }
    }
}