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
import com.example.javamark.model.GyroscopicMeasurement;
import com.google.android.material.textfield.TextInputEditText;

import java.text.DecimalFormat;
import android.widget.Toast;
/**
 * Фрагмент для ввода данных и вычисления нуля торсиона
 */
public class ZeroTorsionFragment extends Fragment {

    private GyroscopicMeasurement measurement;
    private TextInputEditText etN1, etN2, etN3, etN4;
    private TextView tvN0Prime, tvN0DoublePrime, tvN0;
    private Button btnCalculate, btnNext;
    private GyroscopicCalculator calculator;

    public static ZeroTorsionFragment newInstance(GyroscopicMeasurement measurement) {
        ZeroTorsionFragment fragment = new ZeroTorsionFragment();
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
        View view = inflater.inflate(R.layout.fragment_zero_torsion, container, false);

        // Инициализация полей ввода
        etN1 = view.findViewById(R.id.et_n1);
        etN2 = view.findViewById(R.id.et_n2);
        etN3 = view.findViewById(R.id.et_n3);
        etN4 = view.findViewById(R.id.et_n4);

        // Инициализация полей вывода результатов
        tvN0Prime = view.findViewById(R.id.tv_n0_prime);
        tvN0DoublePrime = view.findViewById(R.id.tv_n0_double_prime);
        tvN0 = view.findViewById(R.id.tv_n0);

        // Инициализация кнопок
        btnCalculate = view.findViewById(R.id.btn_calculate_zero_torsion);
        btnNext = view.findViewById(R.id.btn_next_to_equilibrium);

        // Установка начальных значений
        if (measurement.getN1Value() != 0) {
            etN1.setText(String.valueOf(measurement.getN1Value()));
        }
        if (measurement.getN2Value() != 0) {
            etN2.setText(String.valueOf(measurement.getN2Value()));
        }
        if (measurement.getN3Value() != 0) {
            etN3.setText(String.valueOf(measurement.getN3Value()));
        }
        if (measurement.getN4Value() != 0) {
            etN4.setText(String.valueOf(measurement.getN4Value()));
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
            viewPager.setCurrentItem(1); // Переход к фрагменту "Положение равновесия ЧЭ"
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
                    if (s.toString().isEmpty()) {
                        measurement.setN1(0);
                    } else {
                        measurement.setN1(Double.parseDouble(s.toString()));
                    }
                } catch (NumberFormatException e) {
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
                    if (s.toString().isEmpty()) {
                        measurement.setN2(0);
                    } else {
                        measurement.setN2(Double.parseDouble(s.toString()));
                    }
                } catch (NumberFormatException e) {
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
                    if (s.toString().isEmpty()) {
                        measurement.setN3(0);
                    } else {
                        measurement.setN3(Double.parseDouble(s.toString()));
                    }
                } catch (NumberFormatException e) {
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
                    if (s.toString().isEmpty()) {
                        measurement.setN4(0);
                    } else {
                        measurement.setN4(Double.parseDouble(s.toString()));
                    }
                } catch (NumberFormatException e) {
                    // Игнорируем ошибку парсинга
                }
            }
        });
    }

    /**
     * Выполняет вычисление нуля торсиона
     */
    private void calculate() {
        calculator.calculateZeroTorsion();
        updateResults();
    }

    /**
     * Обновляет отображение результатов
     */
    private void updateResults() {
        DecimalFormat df = new DecimalFormat("#0.00");

        if (measurement.getN0PrimeValue() != 0) {
            tvN0Prime.setText(df.format(measurement.getN0PrimeValue()));
        }

        if (measurement.getN0DoublePrimeValue() != 0) {
            tvN0DoublePrime.setText(df.format(measurement.getN0DoublePrimeValue()));
        }

        if (measurement.getN0Value() != 0) {
            tvN0.setText(df.format(measurement.getN0Value()));
        }

        // Проверка допуска |n0' - n0''| <= 1
        if (measurement.getN0PrimeValue() != 0 && measurement.getN0DoublePrimeValue() != 0) {
            double difference = Math.abs(measurement.getN0PrimeValue() - measurement.getN0DoublePrimeValue());
            if (difference > 1.0) {
                // Выделяем красным, если не в допуске
                tvN0Prime.setTextColor(getResources().getColor(R.color.red, null));
                tvN0DoublePrime.setTextColor(getResources().getColor(R.color.red, null));
                Toast.makeText(getContext(), "Внимание! |n0' - n0''| = " + df.format(difference) + " > 1", Toast.LENGTH_SHORT).show();
            } else {
                // Нормальный цвет, если в допуске
                tvN0Prime.setTextColor(getResources().getColor(android.R.color.black, null));
                tvN0DoublePrime.setTextColor(getResources().getColor(android.R.color.black, null));
            }
        }

        // Проверка допуска |ni - n0| <= 40 для всех i
        double n0Value = measurement.getN0Value();
        if (n0Value != 0) {
            boolean outOfRange = false;
            StringBuilder message = new StringBuilder("Вне допуска: ");

            if (Math.abs(measurement.getN1Value() - n0Value) > 40) {
                outOfRange = true;
                message.append("|n1-n0| ");
            }
            if (Math.abs(measurement.getN2Value() - n0Value) > 40) {
                outOfRange = true;
                message.append("|n2-n0| ");
            }
            if (Math.abs(measurement.getN3Value() - n0Value) > 40) {
                outOfRange = true;
                message.append("|n3-n0| ");
            }
            if (Math.abs(measurement.getN4Value() - n0Value) > 40) {
                outOfRange = true;
                message.append("|n4-n0| ");
            }

            if (outOfRange) {
                tvN0.setTextColor(getResources().getColor(R.color.red, null));
                Toast.makeText(getContext(), message.toString() + "> 40", Toast.LENGTH_SHORT).show();
            } else {
                tvN0.setTextColor(getResources().getColor(android.R.color.black, null));
            }
        }
    }
}