package com.example.javamark.ui.gyroscopic;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.javamark.R;
import com.example.javamark.calculator.GyroscopicCalculator;
import com.example.javamark.model.AngleValue;
import com.example.javamark.model.GyroscopicMeasurement;
import com.google.android.material.textfield.TextInputEditText;

/**
 * Фрагмент для ввода данных и вычисления положения равновесия ЧЭ
 */
public class EquilibriumFragment extends Fragment {

    private GyroscopicMeasurement measurement;
    private TextInputEditText etN1, etN2, etN3, etN4;
    private TextView tvN0Prime, tvN0DoublePrime, tvN0;
    private Button btnCalculate, btnNext;
    private GyroscopicCalculator calculator;

    public static EquilibriumFragment newInstance(GyroscopicMeasurement measurement) {
        EquilibriumFragment fragment = new EquilibriumFragment();
        Bundle args = new Bundle();
        args.putSerializable("measurement", measurement);
        fragment.setArguments(args);
        return fragment;
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
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_equilibrium, container, false);

        // Инициализация полей ввода
        etN1 = view.findViewById(R.id.et_N1);
        etN2 = view.findViewById(R.id.et_N2);
        etN3 = view.findViewById(R.id.et_N3);
        etN4 = view.findViewById(R.id.et_N4);

        // Инициализация полей вывода результатов
        tvN0Prime = view.findViewById(R.id.tv_N0_prime);
        tvN0DoublePrime = view.findViewById(R.id.tv_N0_double_prime);
        tvN0 = view.findViewById(R.id.tv_N0);

        // Инициализация кнопок
        btnCalculate = view.findViewById(R.id.btn_calculate_equilibrium);
        btnNext = view.findViewById(R.id.btn_next_to_direction);

        // Установка начальных значений
        if (measurement.getN1() != null && measurement.getN1().getDegrees() != 0) {
            etN1.setText(measurement.getN1().toString());
        }
        if (measurement.getN2() != null && measurement.getN2().getDegrees() != 0) {
            etN2.setText(measurement.getN2().toString());
        }
        if (measurement.getN3() != null && measurement.getN3().getDegrees() != 0) {
            etN3.setText(measurement.getN3().toString());
        }
        if (measurement.getN4() != null && measurement.getN4().getDegrees() != 0) {
            etN4.setText(measurement.getN4().toString());
        }

        // Обновление результатов, если они уже есть
        updateResults();

        // Настройка слушателей для полей ввода
        setupInputListeners();

        // Настройка кнопки вычисления
        btnCalculate.setOnClickListener(v -> calculate());

        // Настройка кнопки перехода к следующему шагу
        btnNext.setOnClickListener(v -> {
            ViewPager2 viewPager = getActivity().findViewById(R.id.view_pager);
            viewPager.setCurrentItem(2); // Переход к фрагменту "Примычное направление"
        });

        return view;
    }

    /**
     * Настраивает слушателей изменения текста для полей ввода
     */
    private void setupInputListeners() {
        etN1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (!s.toString().isEmpty()) {
                        AngleValue angle = AngleValue.parseAngle(s.toString());
                        measurement.setN1(angle);
                    }
                } catch (Exception e) {
                    // Игнорируем ошибку парсинга
                }
            }
        });

        etN2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (!s.toString().isEmpty()) {
                        AngleValue angle = AngleValue.parseAngle(s.toString());
                        measurement.setN2(angle);
                    }
                } catch (Exception e) {
                    // Игнорируем ошибку парсинга
                }
            }
        });

        etN3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (!s.toString().isEmpty()) {
                        AngleValue angle = AngleValue.parseAngle(s.toString());
                        measurement.setN3(angle);
                    }
                } catch (Exception e) {
                    // Игнорируем ошибку парсинга
                }
            }
        });

        etN4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (!s.toString().isEmpty()) {
                        AngleValue angle = AngleValue.parseAngle(s.toString());
                        measurement.setN4(angle);
                    }
                } catch (Exception e) {
                    // Игнорируем ошибку парсинга
                }
            }
        });
    }

    /**
     * Выполняет вычисление положения равновесия ЧЭ
     */
    private void calculate() {
        calculator.calculateEquilibrium();
        updateResults();
    }

    /**
     * Обновляет отображение результатов
     */
    private void updateResults() {
        if (measurement.getN0Prime() != null && measurement.getN0Prime().getDegrees() != 0) {
            tvN0Prime.setText(measurement.getN0Prime().toString());
        }

        if (measurement.getN0DoublePrime() != null && measurement.getN0DoublePrime().getDegrees() != 0) {
            tvN0DoublePrime.setText(measurement.getN0DoublePrime().toString());
        }

        if (measurement.getN0() != null && measurement.getN0().getDegrees() != 0) {
            tvN0.setText(measurement.getN0().toString());
        }
    }
}