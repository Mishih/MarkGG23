package com.example.javamark.ui.gyroscopic;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.javamark.R;
import com.example.javamark.calculator.GyroscopicCalculator;
import com.example.javamark.model.AngleValue;
import com.example.javamark.model.GyroscopicMeasurement;
import com.google.android.material.textfield.TextInputEditText;
import android.util.Log;
/**
 * Фрагмент для ввода данных и вычисления поправки за закручивание торсиона
 */
public class TorsionCorrectionFragment extends Fragment {
    private static final String TAG = "TorsionCorrection";  // Добавляем константу TAG для логирования
    private GyroscopicMeasurement measurement;
    private TextInputEditText etNk, etT, etNkPrime, etNkDoublePrime, etD;
    private TextView tvPsiT, tvPsiK, tvEpsilon;
    private Button btnCalculate, btnNext;
    private GyroscopicCalculator calculator;

    public static TorsionCorrectionFragment newInstance(GyroscopicMeasurement measurement) {
        TorsionCorrectionFragment fragment = new TorsionCorrectionFragment();
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
        View view = inflater.inflate(R.layout.fragment_torsion_correction, container, false);

        // Инициализация полей ввода
        etNk = view.findViewById(R.id.et_nk);
        etT = view.findViewById(R.id.et_t);
        etNkPrime = view.findViewById(R.id.et_Nk_prime);
        etNkDoublePrime = view.findViewById(R.id.et_Nk_double_prime);
        etD = view.findViewById(R.id.et_D);

        // Инициализация полей вывода результатов
        tvPsiT = view.findViewById(R.id.tv_psi_t);
        tvPsiK = view.findViewById(R.id.tv_psi_k);
        tvEpsilon = view.findViewById(R.id.tv_epsilon);

        // Инициализация кнопок
        btnCalculate = view.findViewById(R.id.btn_calculate_torsion);
        btnNext = view.findViewById(R.id.btn_next_to_results);

        // Установка начальных значений
        if (measurement.getNkValue() != 40.0) {
            etNk.setText(String.valueOf(measurement.getNkValue()));
        } else {
            etNk.setText("40");
        }

        if (measurement.getT() != null && !measurement.getT().toString().equals("0°1′0.0″")) {
            etT.setText(measurement.getT().toString());
        } else {
            etT.setText("0°1′0.0″");
        }

        if (measurement.getNkPrime() != null && measurement.getNkPrime().getDegrees() != 0) {
            etNkPrime.setText(measurement.getNkPrime().toString());
        }

        if (measurement.getNkDoublePrime() != null && measurement.getNkDoublePrime().getDegrees() != 0) {
            etNkDoublePrime.setText(measurement.getNkDoublePrime().toString());
        }

        if (measurement.getD() != 1.0) {
            etD.setText(String.valueOf(measurement.getD()));
        } else {
            etD.setText("1.0");
        }

        // Настройка слушателей для полей ввода
        setupInputListeners();

        // Настройка кнопки вычисления
        btnCalculate.setOnClickListener(v -> calculate());

        // Настройка кнопки перехода к следующему шагу
        btnNext.setOnClickListener(v -> {
            ViewPager2 viewPager = getActivity().findViewById(R.id.view_pager);
            viewPager.setCurrentItem(4); // Переход к фрагменту "Результаты"
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Обновление результатов, если они уже есть - ПЕРЕНЕСЕНО СЮДА
        updateResults();
    }

    /**
     * Настраивает слушателей изменения текста для полей ввода
     */
    private void setupInputListeners() {
        etNk.addTextChangedListener(new TextWatcher() {
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
                        measurement.setNk(40.0);
                    } else {
                        measurement.setNk(Double.parseDouble(s.toString()));
                    }
                } catch (NumberFormatException e) {
                    // Игнорируем ошибку парсинга
                }
            }
        });

        etT.addTextChangedListener(new TextWatcher() {
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
                        measurement.setT(angle);
                    }
                } catch (Exception e) {
                    // Игнорируем ошибку парсинга
                }
            }
        });

        etNkPrime.addTextChangedListener(new TextWatcher() {
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
                        measurement.setNkPrime(angle);
                    }
                } catch (Exception e) {
                    // Игнорируем ошибку парсинга
                }
            }
        });

        etNkDoublePrime.addTextChangedListener(new TextWatcher() {
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
                        measurement.setNkDoublePrime(angle);
                    }
                } catch (Exception e) {
                    // Игнорируем ошибку парсинга
                }
            }
        });

        etD.addTextChangedListener(new TextWatcher() {
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
                        measurement.setD(1.0);
                    } else {
                        measurement.setD(Double.parseDouble(s.toString()));
                    }
                } catch (NumberFormatException e) {
                    // Игнорируем ошибку парсинга
                }
            }
        });
    }

    /**
     * Выполняет вычисление поправки за закручивание торсиона
     */
    /**
     * Выполняет вычисление поправки за закручивание торсиона - совершенно новый подход
     */
    private void calculate() {
        try {
            // Ручной расчет ψt = t × (n₀ - nₖ)
            double t_value = 0.0;
            if (measurement.getT() != null) {
                t_value = measurement.getT().toDecimalDegrees();
            } else {
                Toast.makeText(getContext(), "Ошибка: не задан температурный коэффициент t", Toast.LENGTH_SHORT).show();
                return;
            }

            double n0_value = measurement.getN0Value(); // Берем n0 из нуля торсиона
            double nk_value = measurement.getNkValue();  // Значение nk

            // Разность (n₀ - nₖ)
            double difference = n0_value - nk_value;

            // Вычисляем ψt в десятичных градусах
            double psiT_decimal = t_value * difference;

            // Обязательно выводим детальное сообщение для отладки
            Toast.makeText(getContext(),
                    String.format("Расчет ψt: %.6f° × (%.2f - %.2f = %.2f) = %.6f°",
                            t_value, n0_value, nk_value, difference, psiT_decimal),
                    Toast.LENGTH_LONG).show();

            // Преобразуем в градусы, минуты, секунды с учетом знака
            boolean isNegative = psiT_decimal < 0;
            double absPsiT = Math.abs(psiT_decimal);

            int degrees = (int)absPsiT;
            double minutesDecimal = (absPsiT - degrees) * 60.0;
            int minutes = (int)minutesDecimal;
            double seconds = (minutesDecimal - minutes) * 60.0;

            // Создаем новый объект AngleValue и сохраняем его
            AngleValue psiT;
            if (isNegative) {
                if (degrees > 0) {
                    psiT = new AngleValue(-degrees, minutes, seconds);
                } else {
                    // Особый случай - градусы равны нулю, минусуем минуты
                    psiT = new AngleValue(0, -minutes, seconds);
                }
            } else {
                psiT = new AngleValue(degrees, minutes, seconds);
            }

            // Принудительно устанавливаем правильное отображение, игнорируя AngleValue.toString()
            String psiTString = isNegative ?
                    String.format("-0°%d′%.1f″", minutes, seconds) :
                    String.format("0°%d′%.1f″", minutes, seconds);

            if (degrees > 0) {
                psiTString = isNegative ?
                        String.format("-%d°%d′%.1f″", degrees, minutes, seconds) :
                        String.format("%d°%d′%.1f″", degrees, minutes, seconds);
            }
            measurement.setPsiTDirectValue(psiT_decimal);


            // Сохраняем в модель, но отображаем принудительное значение
            measurement.setPsiT(psiT);
            tvPsiT.setText(psiTString);

            // Расчет для Nk (среднее между Nk' и Nk'')
            if (measurement.getNkPrime() != null && measurement.getNkDoublePrime() != null) {
                double nkPrimeDecimal = measurement.getNkPrime().toDecimalDegrees();
                double nkDoublePrimeDecimal = measurement.getNkDoublePrime().toDecimalDegrees();
                double nkDecimal = (nkPrimeDecimal + nkDoublePrimeDecimal) / 2.0;

                AngleValue nk = AngleValue.fromDecimalDegrees(nkDecimal);
                measurement.setNk(nk);

                // Расчет ψk = Nk - N0 (напрямую в десятичных градусах)
                if (measurement.getN0() != null) {
                    double n0Decimal = measurement.getN0().toDecimalDegrees();
                    double psiKDecimal = nkDecimal - n0Decimal;

                    // Преобразуем в AngleValue и сохраняем
                    AngleValue psiK = AngleValue.fromDecimalDegrees(psiKDecimal);
                    measurement.setPsiK(psiK);
                    tvPsiK.setText(psiK.toString());

                    // Расчет ε = (ψt + ψk)/D в десятичных градусах
                    double D = measurement.getD();

                    // Прямое суммирование десятичных значений
                    double sum = psiT_decimal + psiKDecimal;
                    double epsilonDecimal = sum / D;

                    // Для отладки показываем полный расчет
                    Toast.makeText(getContext(),
                            String.format("Сумма: %.6f° + %.6f° = %.6f°\nε = %.6f° / %.1f = %.6f°",
                                    psiT_decimal, psiKDecimal, sum, sum, D, epsilonDecimal),
                            Toast.LENGTH_LONG).show();

                    // Преобразуем в AngleValue и сохраняем
                    AngleValue epsilon = AngleValue.fromDecimalDegrees(epsilonDecimal);
                    measurement.setEpsilon(epsilon);
                    tvEpsilon.setText(epsilon.toString());
                }
            } else {
                Toast.makeText(getContext(), "Введите значения Nk' и Nk'' для расчета ψk и ε", Toast.LENGTH_SHORT).show();
            }

            // Проверка допусков
            checkTolerances();

        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка при расчете: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Ошибка расчета: " + e.getMessage(), e);
        }
    }
    /**
     * Проверяет допуски для значений
     */
    private void checkTolerances() {
        // Проверка допуска |Nk' - Nk''| <= 6"
        if (measurement.getNkPrime() != null && measurement.getNkDoublePrime() != null) {
            double nkPrimeDecimal = measurement.getNkPrime().toDecimalDegrees();
            double nkDoublePrimeDecimal = measurement.getNkDoublePrime().toDecimalDegrees();
            double differenceInDegrees = Math.abs(nkPrimeDecimal - nkDoublePrimeDecimal);
            double differenceInSeconds = differenceInDegrees * 3600;

            if (differenceInSeconds > 6.0) {
                // Ищем текстовые поля для Nk' и Nk''
                TextInputEditText etNkPrime = getView().findViewById(R.id.et_Nk_prime);
                TextInputEditText etNkDoublePrime = getView().findViewById(R.id.et_Nk_double_prime);

                if (etNkPrime != null && etNkDoublePrime != null) {
                    etNkPrime.setTextColor(getResources().getColor(R.color.red, null));
                    etNkDoublePrime.setTextColor(getResources().getColor(R.color.red, null));

                    // Форматируем сообщение с дробной частью до одного знака
                    Toast.makeText(getContext(),
                            "Внимание! |Nk' - Nk''| = " + String.format("%.1f", differenceInSeconds) + "\" > 6\"",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // Нормальный цвет, если в допуске
                TextInputEditText etNkPrime = getView().findViewById(R.id.et_Nk_prime);
                TextInputEditText etNkDoublePrime = getView().findViewById(R.id.et_Nk_double_prime);

                if (etNkPrime != null && etNkDoublePrime != null) {
                    etNkPrime.setTextColor(getResources().getColor(android.R.color.black, null));
                    etNkDoublePrime.setTextColor(getResources().getColor(android.R.color.black, null));
                }
            }
        }

        // Проверка допуска |ψk| <= 1°
        if (measurement.getPsiK() != null) {
            double psiKDegrees = Math.abs(measurement.getPsiK().toDecimalDegrees());
            if (psiKDegrees > 1.0) {
                tvPsiK.setTextColor(getResources().getColor(R.color.red, null));
                Toast.makeText(getContext(),
                        "Внимание! |ψk| = " + String.format("%.4f", psiKDegrees) + "° > 1°",
                        Toast.LENGTH_SHORT).show();
            } else {
                tvPsiK.setTextColor(getResources().getColor(android.R.color.black, null));
            }
        }
    }
    /**
     * Обновляет отображение результатов
     */
    private void updateResults() {
        // Проверка на null view
        if (getView() == null) {
            return;
        }

        // Проверка на null элементов
        if (tvPsiT == null || tvPsiK == null || tvEpsilon == null) {
            return;
        }

        if (measurement.getPsiT() != null) {
            tvPsiT.setText(measurement.getPsiT().toString());
            tvPsiT.setTextColor(getResources().getColor(android.R.color.black, null));
        } else {
            tvPsiT.setText("-");
        }

        if (measurement.getPsiK() != null) {
            tvPsiK.setText(measurement.getPsiK().toString());

            // Проверка допуска |ψk| <= 1°
            double psiKDegrees = Math.abs(measurement.getPsiK().toDecimalDegrees());
            if (psiKDegrees > 1.0) {
                tvPsiK.setTextColor(getResources().getColor(R.color.red, null));
                Toast.makeText(getContext(),
                        "Внимание! |ψk| = " + String.format("%.4f", psiKDegrees) + "° > 1°",
                        Toast.LENGTH_SHORT).show();
            } else {
                tvPsiK.setTextColor(getResources().getColor(android.R.color.black, null));
            }

        } else {
            tvPsiK.setText("-");
        }

        if (measurement.getEpsilon() != null) {
            tvEpsilon.setText(measurement.getEpsilon().toString());
            tvEpsilon.setTextColor(getResources().getColor(android.R.color.black, null));
        } else {
            tvEpsilon.setText("-");
        }

        // Проверка допуска |Nk' - Nk''| <= 6"
        if (measurement.getNkPrime() != null && measurement.getNkDoublePrime() != null) {
            double nkPrimeDecimal = measurement.getNkPrime().toDecimalDegrees();
            double nkDoublePrimeDecimal = measurement.getNkDoublePrime().toDecimalDegrees();

            // Вычисляем разницу в секундах
            double differenceInDegrees = Math.abs(nkPrimeDecimal - nkDoublePrimeDecimal);
            double differenceInSeconds = differenceInDegrees * 3600;

            if (differenceInSeconds > 6.0) {
                // Ищем текстовые поля для Nk' и Nk''
                TextInputEditText etNkPrime = getView().findViewById(R.id.et_Nk_prime);
                TextInputEditText etNkDoublePrime = getView().findViewById(R.id.et_Nk_double_prime);

                if (etNkPrime != null && etNkDoublePrime != null) {
                    etNkPrime.setTextColor(getResources().getColor(R.color.red, null));
                    etNkDoublePrime.setTextColor(getResources().getColor(R.color.red, null));

                    // Форматируем сообщение с дробной частью до одного знака
                    Toast.makeText(getContext(),
                            "Внимание! |Nk' - Nk''| = " + String.format("%.1f", differenceInSeconds) + "\" > 6\"",
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                // Нормальный цвет, если в допуске
                TextInputEditText etNkPrime = getView().findViewById(R.id.et_Nk_prime);
                TextInputEditText etNkDoublePrime = getView().findViewById(R.id.et_Nk_double_prime);

                if (etNkPrime != null && etNkDoublePrime != null) {
                    etNkPrime.setTextColor(getResources().getColor(android.R.color.black, null));
                    etNkDoublePrime.setTextColor(getResources().getColor(android.R.color.black, null));
                }
            }
        }
    }
}