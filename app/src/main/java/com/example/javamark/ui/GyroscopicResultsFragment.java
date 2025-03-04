package com.example.javamark.ui.gyroscopic;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.javamark.R;
import com.example.javamark.calculator.GyroscopicCalculator;
import com.example.javamark.model.GyroscopicMeasurement;
import com.example.javamark.storage.GyroscopicMeasurementStorage;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Фрагмент для отображения результатов гироскопического ориентирования
 */
public class GyroscopicResultsFragment extends Fragment {

    private GyroscopicMeasurement measurement;
    private TextView tvN, tvN0, tvNDifference, tvEpsilon, tvAzimuth;
    private TextInputEditText etMeasurementName;
    private Button btnCalculate, btnSave;
    private GyroscopicCalculator calculator;
    private GyroscopicMeasurementStorage storage;
    private OnMeasurementSavedListener measurementSavedListener;

    public interface OnMeasurementSavedListener {
        void onMeasurementSaved(GyroscopicMeasurement measurement);
    }

    public static GyroscopicResultsFragment newInstance(GyroscopicMeasurement measurement) {
        GyroscopicResultsFragment fragment = new GyroscopicResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable("measurement", measurement);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnMeasurementSavedListener) {
            measurementSavedListener = (OnMeasurementSavedListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            measurement = (GyroscopicMeasurement) getArguments().getSerializable("measurement");
        }
        if (measurement == null) {
            measurement = new GyroscopicMeasurement("Новое измерение");
        }
        calculator = new GyroscopicCalculator(measurement);
        storage = new GyroscopicMeasurementStorage(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gyroscopic_results, container, false);

        // Инициализация полей вывода результатов
        tvN = view.findViewById(R.id.tv_N_value);
        tvN0 = view.findViewById(R.id.tv_N0_value);
        tvNDifference = view.findViewById(R.id.tv_N_difference_value);
        tvEpsilon = view.findViewById(R.id.tv_epsilon_value);
        tvAzimuth = view.findViewById(R.id.tv_azimuth_value);

        // Инициализация поля ввода имени измерения
        etMeasurementName = view.findViewById(R.id.et_measurement_name);
        etMeasurementName.setText(measurement.getName());

        // Инициализация кнопок
        btnCalculate = view.findViewById(R.id.btn_calculate_azimuth);
        btnSave = view.findViewById(R.id.btn_save_measurement);

        // Обновление результатов, если они уже есть
        updateResults();

        // Настройка кнопки вычисления
        btnCalculate.setOnClickListener(v -> {
            // Выполнение всех вычислений в правильной последовательности
            calculator.calculateAll();
            updateResults();
        });

        // Настройка кнопки сохранения
        btnSave.setOnClickListener(v -> saveMeasurement());

        return view;
    }

    /**
     * Обновляет отображение результатов
     */
    private void updateResults() {
        // Отображение примычного направления
        if (measurement.getN() != null && measurement.getN().getDegrees() != 0) {
            tvN.setText(measurement.getN().toString());
        }

        // Отображение положения равновесия
        if (measurement.getN0() != null && measurement.getN0().getDegrees() != 0) {
            tvN0.setText(measurement.getN0().toString());
        }

        // Вычисление разности N - N0
        if (measurement.getN() != null && measurement.getN0() != null &&
                measurement.getN().getDegrees() != 0 && measurement.getN0().getDegrees() != 0) {
            double nDecimal = measurement.getN().toDecimalDegrees();
            double n0Decimal = measurement.getN0().toDecimalDegrees();
            double difference = nDecimal - n0Decimal;
            tvNDifference.setText(String.format("%.4f°", difference));
        }

        // Отображение поправки за закручивание торсиона
        if (measurement.getEpsilon() != null && measurement.getEpsilon().getDegrees() != 0) {
            tvEpsilon.setText(measurement.getEpsilon().toString());
        }

        // Отображение гироскопического азимута
        if (measurement.getGyroscopicAzimuth() != null && measurement.getGyroscopicAzimuth().getDegrees() != 0) {
            tvAzimuth.setText(measurement.getGyroscopicAzimuth().toString());
        }
    }

    /**
     * Сохраняет измерение
     */
    private void saveMeasurement() {
        String name = etMeasurementName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(getContext(), "Введите название измерения", Toast.LENGTH_SHORT).show();
            return;
        }

        if (measurement.getGyroscopicAzimuth() == null || measurement.getGyroscopicAzimuth().getDegrees() == 0) {
            Toast.makeText(getContext(), "Сначала выполните вычисление азимута", Toast.LENGTH_SHORT).show();
            return;
        }

        measurement.setName(name);

        boolean saved = storage.saveMeasurement(measurement);
        if (saved) {
            Toast.makeText(getContext(), "Измерение сохранено", Toast.LENGTH_SHORT).show();

            if (measurementSavedListener != null) {
                measurementSavedListener.onMeasurementSaved(measurement);
            }
        } else {
            Toast.makeText(getContext(), "Ошибка при сохранении измерения", Toast.LENGTH_SHORT).show();
        }
    }
}