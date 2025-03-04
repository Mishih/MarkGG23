package com.example.javamark.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.javamark.R;
import com.example.javamark.calculator.DelambertCalculator;
import com.example.javamark.model.CalculationResult;
import com.example.javamark.model.Project;
import com.example.javamark.model.ReferencePoint;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class CalculationFragment extends Fragment {

    private Project currentProject;
    private TextInputEditText maxErrorEditText;
    private Spinner spinnerCombination1;
    private Spinner spinnerCombination2;
    private Button calculateButton;
    private List<String> combinationOptions;
    private List<int[]> possibleCombinations;

    public static CalculationFragment newInstance(Project project) {
        CalculationFragment fragment = new CalculationFragment();
        Bundle args = new Bundle();
        args.putSerializable("project", project);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentProject = (Project) getArguments().getSerializable("project");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calculation, container, false);

        maxErrorEditText = view.findViewById(R.id.et_max_error);
        spinnerCombination1 = view.findViewById(R.id.spinner_combination1);
        spinnerCombination2 = view.findViewById(R.id.spinner_combination2);
        calculateButton = view.findViewById(R.id.btn_calculate);

        // Заполняем спиннеры возможными комбинациями
        setupCombinations();

        // Обработчик нажатия на кнопку вычисления
        calculateButton.setOnClickListener(v -> performCalculation());

        return view;
    }

    /**
     * Настраивает спиннеры с возможными комбинациями точек
     */
    private void setupCombinations() {
        List<ReferencePoint> points = currentProject.getReferencePoints();
        if (points.size() < 3) {
            Toast.makeText(getContext(), "Недостаточно точек для расчёта", Toast.LENGTH_SHORT).show();
            return;
        }

        // Получаем возможные комбинации
        DelambertCalculator calculator = new DelambertCalculator(points, 2.0);
        possibleCombinations = calculator.generatePossibleCombinations();

        // Создаем список строк для отображения в спиннере
        combinationOptions = new ArrayList<>();
        for (int[] combination : possibleCombinations) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < combination.length; i++) {
                sb.append(points.get(combination[i]).getName());
                if (i < combination.length - 1) {
                    sb.append(", ");
                }
            }
            combinationOptions.add(sb.toString());
        }

        // Настраиваем адаптеры для спиннеров
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, combinationOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerCombination1.setAdapter(adapter);
        spinnerCombination2.setAdapter(adapter);

        // Устанавливаем разные начальные значения для спиннеров
        if (combinationOptions.size() > 1) {
            spinnerCombination1.setSelection(0);
            spinnerCombination2.setSelection(1);
        }
    }

    /**
     * Выполняет расчёт и переходит к фрагменту результатов
     */
    private void performCalculation() {
        if (!validateInput()) {
            return;
        }

        try {
            // Получаем максимально допустимую погрешность
            double maxError = Double.parseDouble(maxErrorEditText.getText().toString());
            currentProject.setMaxAllowableError(maxError);

            // Получаем выбранные комбинации
            int combination1Index = spinnerCombination1.getSelectedItemPosition();
            int combination2Index = spinnerCombination2.getSelectedItemPosition();

            // Создаем калькулятор и выполняем расчёт
            DelambertCalculator calculator = new DelambertCalculator(
                    currentProject.getReferencePoints(), maxError);

            CalculationResult result = calculator.calculate(
                    possibleCombinations.get(combination1Index),
                    possibleCombinations.get(combination2Index));

            // Сохраняем результат в проект
            currentProject.setResult(result);

            // Переходим к фрагменту результатов
            ViewPager2 viewPager = getActivity().findViewById(R.id.view_pager);
            viewPager.setCurrentItem(2); // Индекс фрагмента результатов
        } catch (Exception e) {
            Toast.makeText(getContext(), "Ошибка вычисления: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    /**
     * Проверяет корректность входных данных
     */
    private boolean validateInput() {
        if (currentProject.getReferencePoints().size() < 4) {
            Toast.makeText(getContext(), "Необходимо минимум 4 исходных пункта", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (maxErrorEditText.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), "Укажите допустимую погрешность", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Проверяем, все ли точки имеют координаты и углы
        for (ReferencePoint point : currentProject.getReferencePoints()) {
            if (Double.isNaN(point.getX()) || Double.isNaN(point.getY()) || Double.isNaN(point.getBeta())) {
                Toast.makeText(getContext(), "Заполните все координаты и углы", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

    /**
     * Обновляет данные проекта
     */
    public void updateProject(Project project) {
        this.currentProject = project;
        if (isAdded()) {
            setupCombinations();
        }
    }
}