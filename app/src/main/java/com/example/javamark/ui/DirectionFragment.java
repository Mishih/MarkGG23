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
import android.widget.Toast;
import android.util.Log;
/**
 * Фрагмент для ввода данных и вычисления примычного направления
 */
public class DirectionFragment extends Fragment {

    private GyroscopicMeasurement measurement;
    private TextInputEditText etKL1, etKP1, etKL2, etKP2;
    private TextView tvNPrime, tvNDoublePrime, tvN;
    private Button btnCalculate, btnNext;
    private GyroscopicCalculator calculator;

    public static DirectionFragment newInstance(GyroscopicMeasurement measurement) {
        DirectionFragment fragment = new DirectionFragment();
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
        View view = inflater.inflate(R.layout.fragment_direction, container, false);

        // Инициализация полей ввода
        etKL1 = view.findViewById(R.id.et_KL1);
        etKP1 = view.findViewById(R.id.et_KP1);
        etKL2 = view.findViewById(R.id.et_KL2);
        etKP2 = view.findViewById(R.id.et_KP2);

        // Инициализация полей вывода результатов
        tvNPrime = view.findViewById(R.id.tv_N_prime);
        tvNDoublePrime = view.findViewById(R.id.tv_N_double_prime);
        tvN = view.findViewById(R.id.tv_N);

        // Инициализация кнопок
        btnCalculate = view.findViewById(R.id.btn_calculate_direction);
        btnNext = view.findViewById(R.id.btn_next_to_torsion);

        // Установка начальных значений
        if (measurement.getKL1() != null && measurement.getKL1().getDegrees() != 0) {
            etKL1.setText(measurement.getKL1().toString());
        }
        if (measurement.getKP1() != null && measurement.getKP1().getDegrees() != 0) {
            etKP1.setText(measurement.getKP1().toString());
        }
        if (measurement.getKL2() != null && measurement.getKL2().getDegrees() != 0) {
            etKL2.setText(measurement.getKL2().toString());
        }
        if (measurement.getKP2() != null && measurement.getKP2().getDegrees() != 0) {
            etKP2.setText(measurement.getKP2().toString());
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
            viewPager.setCurrentItem(3); // Переход к фрагменту "Поправка за закручивание торсиона"
        });

        return view;
    }

    /**
     * Настраивает слушателей изменения текста для полей ввода
     */
    private void setupInputListeners() {
        etKL1.addTextChangedListener(new TextWatcher() {
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
                        measurement.setKL1(angle);
                    }
                } catch (Exception e) {
                    // Игнорируем ошибку парсинга
                }
            }
        });

        etKP1.addTextChangedListener(new TextWatcher() {
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
                        measurement.setKP1(angle);
                    }
                } catch (Exception e) {
                    // Игнорируем ошибку парсинга
                }
            }
        });

        etKL2.addTextChangedListener(new TextWatcher() {
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
                        measurement.setKL2(angle);
                    }
                } catch (Exception e) {
                    // Игнорируем ошибку парсинга
                }
            }
        });

        etKP2.addTextChangedListener(new TextWatcher() {
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
                        measurement.setKP2(angle);
                    }
                } catch (Exception e) {
                    // Игнорируем ошибку парсинга
                }
            }
        });
    }

    /**
     * Выполняет вычисление примычного направления
     */
    /**
     * Выполняет вычисление примычного направления
     */
    private void calculate() {
        try {
            // N' - для первой пары отсчетов (КЛ1 и КП1)
            AngleValue kl1 = measurement.getKL1();
            AngleValue kp1 = measurement.getKP1();

            if (kl1 != null && kp1 != null) {
                // Берем градусы из КЛ
                int nPrimeDegrees = kl1.getDegrees();

                // Складываем минуты и секунды КЛ с минутами и секундами КП, делим пополам
                double kl1Minutes = kl1.getMinutes() + (kl1.getSeconds() / 60.0);
                double kp1Minutes = kp1.getMinutes() + (kp1.getSeconds() / 60.0);

                double avgMinutes = (kl1Minutes + kp1Minutes) / 2.0;
                int minutes = (int)avgMinutes;
                double seconds = (avgMinutes - minutes) * 60.0;

                // Создаем AngleValue с градусами из КЛ и средними минутами/секундами
                AngleValue nPrime = new AngleValue(nPrimeDegrees, minutes, seconds);
                measurement.setNPrime(nPrime);

                // Отладочный вывод
                Toast.makeText(getContext(),
                        String.format("N': Градусы из КЛ=%d, средние мин.=%d, сек.=%.1f",
                                nPrimeDegrees, minutes, seconds),
                        Toast.LENGTH_LONG).show();
            }

            // N'' - аналогично для второй пары
            AngleValue kl2 = measurement.getKL2();
            AngleValue kp2 = measurement.getKP2();

            if (kl2 != null && kp2 != null) {
                // Берем градусы из КЛ
                int nDoublePrimeDegrees = kl2.getDegrees();

                // Складываем минуты и секунды КЛ с минутами и секундами КП, делим пополам
                double kl2Minutes = kl2.getMinutes() + (kl2.getSeconds() / 60.0);
                double kp2Minutes = kp2.getMinutes() + (kp2.getSeconds() / 60.0);

                double avgMinutes = (kl2Minutes + kp2Minutes) / 2.0;
                int minutes = (int)avgMinutes;
                double seconds = (avgMinutes - minutes) * 60.0;

                // Создаем AngleValue с градусами из КЛ и средними минутами/секундами
                AngleValue nDoublePrime = new AngleValue(nDoublePrimeDegrees, minutes, seconds);
                measurement.setNDoublePrime(nDoublePrime);
            }

            // N = (N' + N'') / 2 - всегда рассчитываем, даже если разница больше допустимой
            if (measurement.getNPrime() != null && measurement.getNDoublePrime() != null) {
                double nPrimeDecimal = measurement.getNPrime().toDecimalDegrees();
                double nDoublePrimeDecimal = measurement.getNDoublePrime().toDecimalDegrees();
                double nDecimal = (nPrimeDecimal + nDoublePrimeDecimal) / 2.0;

                AngleValue n = AngleValue.fromDecimalDegrees(nDecimal);
                measurement.setN(n);

                // Показываем результат N
                tvN.setText(n.toString() + " (среднее между N' и N'')");
            }

            updateResults();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка при расчете: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("DirectionFragment", "Ошибка при вычислении примычного направления: " + e.getMessage(), e);
        }
    }

    /**
     * Обновляет отображение результатов
     */
    private void updateResults() {
        if (measurement.getNPrime() != null && measurement.getNPrime().getDegrees() != 0) {
            tvNPrime.setText(measurement.getNPrime().toString());
        } else {
            tvNPrime.setText("-");
        }

        if (measurement.getNDoublePrime() != null && measurement.getNDoublePrime().getDegrees() != 0) {
            tvNDoublePrime.setText(measurement.getNDoublePrime().toString());
        } else {
            tvNDoublePrime.setText("-");
        }

        if (measurement.getN() != null && measurement.getN().getDegrees() != 0) {
            tvN.setText(measurement.getN().toString());
        } else {
            tvN.setText("-");
        }

        // Проверка допуска |N' - N''| <= 30"
        if (measurement.getNPrime() != null && measurement.getNDoublePrime() != null) {
            double nPrimeDecimal = measurement.getNPrime().toDecimalDegrees();
            double nDoublePrimeDecimal = measurement.getNDoublePrime().toDecimalDegrees();

            // Вычисляем разницу в секундах
            double differenceInDegrees = Math.abs(nPrimeDecimal - nDoublePrimeDecimal);
            double differenceInSeconds = differenceInDegrees * 3600;

            if (differenceInSeconds > 30.0) {
                // Выделяем красным, если не в допуске
                tvNPrime.setTextColor(getResources().getColor(R.color.red, null));
                tvNDoublePrime.setTextColor(getResources().getColor(R.color.red, null));

                // Форматируем сообщение с дробной частью до одного знака
                Toast.makeText(getContext(),
                        "Внимание! |N' - N''| = " + String.format("%.1f", differenceInSeconds) + "\" > 30\" (вне допуска)",
                        Toast.LENGTH_LONG).show();
            } else {
                // Нормальный цвет, если в допуске
                tvNPrime.setTextColor(getResources().getColor(android.R.color.black, null));
                tvNDoublePrime.setTextColor(getResources().getColor(android.R.color.black, null));
            }
        }
    }
}