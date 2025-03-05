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

/**
 * Фрагмент для ввода данных и вычисления поправки за закручивание торсиона
 */
public class TorsionCorrectionFragment extends Fragment {

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
    private void calculate() {
        // Выполняем штатный расчет
        calculator.calculateTorsionCorrection();

        // Проверяем, установлено ли значение psiT
        if (measurement.getPsiT() == null) {
            // Проверяем наличие всех необходимых данных
            if (measurement.getT() != null && measurement.getN0Value() != 0) {
                // Если значение не установлено, делаем ручной расчет
                double tDecimal = measurement.getT().toDecimalDegrees();
                double difference = measurement.getN0Value() - measurement.getNkValue();
                double psiTDecimal = tDecimal * difference;

                // Преобразуем в AngleValue и устанавливаем в модель
                AngleValue psiT = AngleValue.fromDecimalDegrees(psiTDecimal);
                measurement.setPsiT(psiT);

                // Отображаем значение в отладочных целях
                Toast.makeText(getContext(),
                        "psiT = " + psiTDecimal + "° -> " + psiT.toString(),
                        Toast.LENGTH_SHORT).show();

                // Теперь пробуем вычислить ψk и ε
                if (measurement.getNk() != null && measurement.getN0() != null) {
                    // ψk = Nk - N0
                    double nkDecimal = measurement.getNk().toDecimalDegrees();
                    double n0Decimal = measurement.getN0().toDecimalDegrees();
                    double psiKDecimal = nkDecimal - n0Decimal;

                    AngleValue psiK = AngleValue.fromDecimalDegrees(psiKDecimal);
                    measurement.setPsiK(psiK);

                    // ε = (ψt + ψk) / D
                    double sumDecimal = psiTDecimal + psiKDecimal;
                    double epsilonDecimal = sumDecimal / measurement.getD();

                    AngleValue epsilon = AngleValue.fromDecimalDegrees(epsilonDecimal);
                    measurement.setEpsilon(epsilon);
                }
            } else {
                Toast.makeText(getContext(),
                        "Недостаточно данных для расчета. Убедитесь, что вы выполнили шаг 'Нуль торсиона'",
                        Toast.LENGTH_LONG).show();
            }
        }

        // Обновляем отображение результатов
        updateResults();
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