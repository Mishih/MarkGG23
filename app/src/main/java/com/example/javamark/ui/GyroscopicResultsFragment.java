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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.javamark.R;
import com.example.javamark.calculator.GyroscopicCalculator;
import com.example.javamark.model.AngleValue;
import com.example.javamark.model.GyroscopicMeasurement;
import com.example.javamark.storage.GyroscopicMeasurementStorage;
import com.google.android.material.textfield.TextInputEditText;
import android.util.Log;
/**
 * Фрагмент для отображения результатов гироскопического ориентирования
 */
public class GyroscopicResultsFragment extends Fragment {

    private GyroscopicMeasurement measurement;
    private TextView tvN0, tvSumPsiTPsiK, tvEpsilon, tvAzimuth;
    private TextInputEditText etMeasurementName;
    private Button btnCalculate, btnSave, btnShowAllCalculations;
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
        tvN0 = view.findViewById(R.id.tv_N0_value);
        tvSumPsiTPsiK = view.findViewById(R.id.tv_N_difference_value);
        tvEpsilon = view.findViewById(R.id.tv_epsilon_value);
        tvAzimuth = view.findViewById(R.id.tv_azimuth_value);

        // Изменяем метку для tvSumPsiTPsiK
        TextView tvNDifferenceLabel = view.findViewById(R.id.tv_N_difference_label);
        if (tvNDifferenceLabel != null) {
            tvNDifferenceLabel.setText("Сумма (ψt + ψk):");
        } else {
            // Если элемент не найден, выводим предупреждение в лог
            Log.d("GyroscopicResults", "Элемент tv_N_difference_label не найден в макете");
        }

        // Инициализация поля ввода имени измерения
        etMeasurementName = view.findViewById(R.id.et_measurement_name);
        etMeasurementName.setText(measurement.getName());

        // Инициализация кнопок
        btnCalculate = view.findViewById(R.id.btn_calculate_azimuth);
        btnSave = view.findViewById(R.id.btn_save_measurement);

        // Добавляем новую кнопку для отображения всех расчетов
        btnShowAllCalculations = new Button(getContext());
        btnShowAllCalculations.setText("Показать все расчёты");
        btnShowAllCalculations.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        // Находим контейнер для кнопок и добавляем нашу новую кнопку
        ViewGroup buttonContainer = (ViewGroup) btnSave.getParent();
        buttonContainer.addView(btnShowAllCalculations, buttonContainer.indexOfChild(btnSave) + 1);

        btnShowAllCalculations.setOnClickListener(v -> showAllCalculations());

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
        // Отображение положения равновесия
        if (measurement.getN0() != null && measurement.getN0().getDegrees() != 0) {
            tvN0.setText(measurement.getN0().toString());
        } else {
            tvN0.setText("-");
        }

        // Вычисление и отображение суммы ψt + ψk
        if (measurement.getPsiT() != null && measurement.getPsiK() != null) {
            double psiTDecimal = measurement.getPsiT().toDecimalDegrees();
            double psiKDecimal = measurement.getPsiK().toDecimalDegrees();
            double sum = psiTDecimal + psiKDecimal;
            AngleValue sumAngle = AngleValue.fromDecimalDegrees(sum);
            tvSumPsiTPsiK.setText(sumAngle.toString());
        } else {
            tvSumPsiTPsiK.setText("-");
        }

        // Отображение поправки за закручивание торсиона
        if (measurement.getEpsilon() != null && measurement.getEpsilon().getDegrees() != 0) {
            tvEpsilon.setText(measurement.getEpsilon().toString());
        } else {
            tvEpsilon.setText("-");
        }

        // Отображение гироскопического азимута
        if (measurement.getGyroscopicAzimuth() != null && measurement.getGyroscopicAzimuth().getDegrees() != 0) {
            tvAzimuth.setText(measurement.getGyroscopicAzimuth().toString());
        } else {
            tvAzimuth.setText("-");
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

    /**
     * Показывает все расчеты в диалоговом окне
     */
    private void showAllCalculations() {
        StringBuilder allInfo = new StringBuilder();

        // Раздел 1: Нуль торсиона
        allInfo.append("=== 1. НУЛЬ ТОРСИОНА ===\n\n");
        allInfo.append("n₁ = ").append(measurement.getN1Value()).append("\n");
        allInfo.append("n₂ = ").append(measurement.getN2Value()).append("\n");
        allInfo.append("n₃ = ").append(measurement.getN3Value()).append("\n");
        allInfo.append("n₄ = ").append(measurement.getN4Value()).append("\n\n");

        allInfo.append("n₀' = (n₁ + 2n₂ + n₃) / 4 = ")
                .append(String.format("%.4f", measurement.getN0PrimeValue())).append("\n");

        allInfo.append("n₀'' = (n₂ + 2n₃ + n₄) / 4 = ")
                .append(String.format("%.4f", measurement.getN0DoublePrimeValue())).append("\n");

        allInfo.append("n₀ = (n₀' + n₀'') / 2 = ")
                .append(String.format("%.4f", measurement.getN0Value())).append("\n\n");

        // Раздел 2: Положение равновесия ЧЭ
        allInfo.append("=== 2. ПОЛОЖЕНИЕ РАВНОВЕСИЯ ЧЭ ===\n\n");
        if (measurement.getN1() != null) allInfo.append("N₁ = ").append(measurement.getN1().toString()).append("\n");
        if (measurement.getN2() != null) allInfo.append("N₂ = ").append(measurement.getN2().toString()).append("\n");
        if (measurement.getN3() != null) allInfo.append("N₃ = ").append(measurement.getN3().toString()).append("\n");
        if (measurement.getN4() != null) allInfo.append("N₄ = ").append(measurement.getN4().toString()).append("\n\n");

        if (measurement.getN0Prime() != null)
            allInfo.append("N₀' = ").append(measurement.getN0Prime().toString()).append("\n");

        if (measurement.getN0DoublePrime() != null)
            allInfo.append("N₀'' = ").append(measurement.getN0DoublePrime().toString()).append("\n");

        if (measurement.getN0() != null)
            allInfo.append("N₀ = ").append(measurement.getN0().toString()).append("\n\n");

        // Раздел 3: Примычное направление
        allInfo.append("=== 3. ПРИМЫЧНОЕ НАПРАВЛЕНИЕ ===\n\n");
        if (measurement.getKL1() != null) allInfo.append("КЛ₁ = ").append(measurement.getKL1().toString()).append("\n");
        if (measurement.getKP1() != null) allInfo.append("КП₁ = ").append(measurement.getKP1().toString()).append("\n");
        if (measurement.getKL2() != null) allInfo.append("КЛ₂ = ").append(measurement.getKL2().toString()).append("\n");
        if (measurement.getKP2() != null) allInfo.append("КП₂ = ").append(measurement.getKP2().toString()).append("\n\n");

        if (measurement.getNPrime() != null)
            allInfo.append("N' = ").append(measurement.getNPrime().toString()).append("\n");

        if (measurement.getNDoublePrime() != null)
            allInfo.append("N'' = ").append(measurement.getNDoublePrime().toString()).append("\n");

        if (measurement.getN() != null)
            allInfo.append("N = ").append(measurement.getN().toString()).append(" (среднее между N' и N'')\n\n");

        // Раздел 4: Поправка за закручивание торсиона
        allInfo.append("=== 4. ПОПРАВКА ЗА ЗАКРУЧИВАНИЕ ТОРСИОНА ===\n\n");
        allInfo.append("nₖ = ").append(measurement.getNkValue()).append("\n");

        if (measurement.getT() != null) {
            allInfo.append("t = ").append(measurement.getT().toString())
                    .append(" (").append(String.format("%.6f", measurement.getT().toDecimalDegrees())).append("°)\n");
        }

        if (measurement.getNkPrime() != null)
            allInfo.append("Nₖ' = ").append(measurement.getNkPrime().toString()).append("\n");

        if (measurement.getNkDoublePrime() != null)
            allInfo.append("Nₖ'' = ").append(measurement.getNkDoublePrime().toString()).append("\n");

        if (measurement.getNk() != null)
            allInfo.append("Nₖ = ").append(measurement.getNk().toString()).append("\n\n");

        // Вычисления поправок
        if (measurement.getPsiT() != null) {
            allInfo.append("ψt = t(n₀-nₖ) = ")
                    .append(measurement.getPsiT().toString()).append("\n");
        }

        if (measurement.getPsiK() != null) {
            allInfo.append("ψk = Nₖ - N₀ = ")
                    .append(measurement.getPsiK().toString()).append("\n");
        }

        // Сумма ψt + ψk
        if (measurement.getPsiT() != null && measurement.getPsiK() != null) {
            double psiTDecimal = measurement.getPsiT().toDecimalDegrees();
            double psiKDecimal = measurement.getPsiK().toDecimalDegrees();
            double sum = psiTDecimal + psiKDecimal;
            AngleValue sumAngle = AngleValue.fromDecimalDegrees(sum);

            allInfo.append("ψt + ψk = ")
                    .append(sumAngle.toString()).append("\n");
        }

        allInfo.append("D = ").append(measurement.getD()).append("\n");

        if (measurement.getEpsilon() != null) {
            allInfo.append("ε = (ψt + ψk) / D = ")
                    .append(measurement.getEpsilon().toString()).append("\n\n");
        }

        // Раздел 5: Результат - гироскопический азимут
        allInfo.append("=== 5. ГИРОСКОПИЧЕСКИЙ АЗИМУТ ===\n\n");
        allInfo.append("Г = N - N₀ + ε\n");

        if (measurement.getGyroscopicAzimuth() != null) {
            allInfo.append("Г = ").append(measurement.getGyroscopicAzimuth().toString()).append("\n");
        }


        // Добавляем раздел с проверкой допусков
        allInfo.append("=== 6. ПРОВЕРКА ДОПУСКОВ ===\n\n");

        // Проверка |n0' - n0''| <= 1
        if (measurement.getN0PrimeValue() != 0 && measurement.getN0DoublePrimeValue() != 0) {
            double difference = Math.abs(measurement.getN0PrimeValue() - measurement.getN0DoublePrimeValue());
            allInfo.append("1. |n0' - n0''| = ")
                    .append(String.format("%.4f", difference))
                    .append(" ≤ 1: ")
                    .append(difference <= 1.0 ? "✓ (допуск)" : "✗ (вне допуска!)")
                    .append("\n");
        }

        // Проверка |ni - n0| <= 40 для всех i
        double n0Value = measurement.getN0Value();
        if (n0Value != 0) {
            allInfo.append("2. |ni - n0| ≤ 40:\n");

            double diff1 = Math.abs(measurement.getN1Value() - n0Value);
            allInfo.append("   |n1 - n0| = ")
                    .append(String.format("%.4f", diff1))
                    .append(": ")
                    .append(diff1 <= 40.0 ? "✓" : "✗ ВНЕ ДОПУСКА!")
                    .append("\n");

            double diff2 = Math.abs(measurement.getN2Value() - n0Value);
            allInfo.append("   |n2 - n0| = ")
                    .append(String.format("%.4f", diff2))
                    .append(": ")
                    .append(diff2 <= 40.0 ? "✓" : "✗ ВНЕ ДОПУСКА!")
                    .append("\n");

            double diff3 = Math.abs(measurement.getN3Value() - n0Value);
            allInfo.append("   |n3 - n0| = ")
                    .append(String.format("%.4f", diff3))
                    .append(": ")
                    .append(diff3 <= 40.0 ? "✓" : "✗ ВНЕ ДОПУСКА!")
                    .append("\n");

            double diff4 = Math.abs(measurement.getN4Value() - n0Value);
            allInfo.append("   |n4 - n0| = ")
                    .append(String.format("%.4f", diff4))
                    .append(": ")
                    .append(diff4 <= 40.0 ? "✓" : "✗ ВНЕ ДОПУСКА!")
                    .append("\n");
        }

        // Проверка |N0' - N0''| <= 30"
        if (measurement.getN0Prime() != null && measurement.getN0DoublePrime() != null) {
            double n0PrimeDecimal = measurement.getN0Prime().toDecimalDegrees();
            double n0DoublePrimeDecimal = measurement.getN0DoublePrime().toDecimalDegrees();
            double differenceInDegrees = Math.abs(n0PrimeDecimal - n0DoublePrimeDecimal);
            double differenceInSeconds = differenceInDegrees * 3600;

            allInfo.append("3. |N0' - N0''| = ")
                    .append(String.format("%.1f", differenceInSeconds))
                    .append("\" ≤ 30\": ")
                    .append(differenceInSeconds <= 30.0 ? "✓ (допуск)" : "✗ (вне допуска!)")
                    .append("\n");
        }

        // Проверка |N' - N''| <= 30"
        if (measurement.getNPrime() != null && measurement.getNDoublePrime() != null) {
            double nPrimeDecimal = measurement.getNPrime().toDecimalDegrees();
            double nDoublePrimeDecimal = measurement.getNDoublePrime().toDecimalDegrees();
            double differenceInDegrees = Math.abs(nPrimeDecimal - nDoublePrimeDecimal);
            double differenceInSeconds = differenceInDegrees * 3600;

            allInfo.append("4. |N' - N''| = ")
                    .append(String.format("%.1f", differenceInSeconds))
                    .append("\" ≤ 30\": ")
                    .append(differenceInSeconds <= 30.0 ? "✓ (допуск)" : "✗ (вне допуска!)")
                    .append("\n");
        }

        // Проверка |Nk' - Nk''| <= 6"
        if (measurement.getNkPrime() != null && measurement.getNkDoublePrime() != null) {
            double nkPrimeDecimal = measurement.getNkPrime().toDecimalDegrees();
            double nkDoublePrimeDecimal = measurement.getNkDoublePrime().toDecimalDegrees();
            double differenceInDegrees = Math.abs(nkPrimeDecimal - nkDoublePrimeDecimal);
            double differenceInSeconds = differenceInDegrees * 3600;

            allInfo.append("5. |Nk' - Nk''| = ")
                    .append(String.format("%.1f", differenceInSeconds))
                    .append("\" ≤ 6\": ")
                    .append(differenceInSeconds <= 6.0 ? "✓ (допуск)" : "✗ (вне допуска!)")
                    .append("\n");
        }

        // Проверка |ψk| <= 1°
        if (measurement.getPsiK() != null) {
            double psiKDegrees = Math.abs(measurement.getPsiK().toDecimalDegrees());

            allInfo.append("6. |ψk| = ")
                    .append(String.format("%.4f", psiKDegrees))
                    .append("° ≤ 1°: ")
                    .append(psiKDegrees <= 1.0 ? "✓ (допуск)" : "✗ (вне допуска!)")
                    .append("\n");
        }

        // Отображаем диалог с полной информацией
        new AlertDialog.Builder(getContext())
                .setTitle("Полные расчеты")
                .setMessage(allInfo.toString())
                .setPositiveButton("OK", null)
                .show();

    }
}