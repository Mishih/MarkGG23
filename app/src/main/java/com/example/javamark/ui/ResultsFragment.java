package com.example.javamark.ui;

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
import com.example.javamark.model.CalculationResult;
import com.example.javamark.model.Project;
import com.example.javamark.storage.ProjectStorage;

import java.text.DecimalFormat;
import java.util.List;

public class ResultsFragment extends Fragment {

    private Project currentProject;
    private TextView anglesResultsTextView;
    private TextView xComb1TextView;
    private TextView xComb2TextView;
    private TextView yComb1TextView;
    private TextView yComb2TextView;
    private TextView discrepancyTextView;
    private TextView dangerCircleTextView;
    private TextView finalXTextView;
    private TextView finalYTextView;
    private Button saveResultButton;
    private OnProjectSavedListener projectSavedListener;

    public interface OnProjectSavedListener {
        void onProjectSaved(Project project);
    }

    public static ResultsFragment newInstance(Project project) {
        ResultsFragment fragment = new ResultsFragment();
        Bundle args = new Bundle();
        args.putSerializable("project", project);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnProjectSavedListener) {
            projectSavedListener = (OnProjectSavedListener) context;
        }
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
        View view = inflater.inflate(R.layout.fragment_results, container, false);

        // Инициализация представлений
        anglesResultsTextView = view.findViewById(R.id.tv_angles_results);
        xComb1TextView = view.findViewById(R.id.tv_x_comb1);
        xComb2TextView = view.findViewById(R.id.tv_x_comb2);
        yComb1TextView = view.findViewById(R.id.tv_y_comb1);
        yComb2TextView = view.findViewById(R.id.tv_y_comb2);
        discrepancyTextView = view.findViewById(R.id.tv_discrepancy);
        dangerCircleTextView = view.findViewById(R.id.tv_danger_circle);
        finalXTextView = view.findViewById(R.id.tv_final_x);
        finalYTextView = view.findViewById(R.id.tv_final_y);
        saveResultButton = view.findViewById(R.id.btn_save_result);

        // Настраиваем обработчик нажатия на кнопку сохранения
        saveResultButton.setOnClickListener(v -> saveProject());

        // Отображаем результаты, если они доступны
        displayResults();

        return view;
    }

    /**
     * Отображает результаты вычислений
     */
    private void displayResults() {
        if (currentProject == null || currentProject.getResult() == null) {
            // Если результаты не доступны, скрываем содержимое
            return;
        }

        CalculationResult result = currentProject.getResult();
        DecimalFormat df = new DecimalFormat("#0.00");
        DecimalFormat df3 = new DecimalFormat("#0.000");

        // Отображаем дирекционные углы
        StringBuilder anglesBuilder = new StringBuilder();
        List<CalculationResult.DirectionalAngle> angles = result.getDirectionalAngles();
        for (CalculationResult.DirectionalAngle angle : angles) {
            anglesBuilder.append(angle.getPointName())
                    .append(": ")
                    .append(angle.getFormattedAngle())
                    .append("\n");
        }
        anglesResultsTextView.setText(anglesBuilder.toString());

        // Отображаем координаты из первой комбинации
        xComb1TextView.setText(df3.format(result.getX1()));
        yComb1TextView.setText(df3.format(result.getY1()));

        // Отображаем координаты из второй комбинации
        xComb2TextView.setText(df3.format(result.getX2()));
        yComb2TextView.setText(df3.format(result.getY2()));

        // Отображаем расхождение
        discrepancyTextView.setText(df.format(result.getDiscrepancyMeters()) + " см");

        // Отображаем информацию об опасной окружности
        if (result.isInsideDangerCircle()) {
            dangerCircleTextView.setText(getString(R.string.danger_circle_warning));
        } else {
            dangerCircleTextView.setText(getString(R.string.danger_circle_ok));
            dangerCircleTextView.setTextColor(getResources().getColor(R.color.green, null));
        }

        // Отображаем окончательные координаты
        if (result.getDiscrepancyMeters() <= currentProject.getMaxAllowableError()) {
            finalXTextView.setText(df3.format(result.getFinalX()) + " м");
            finalYTextView.setText(df3.format(result.getFinalY()) + " м");
        } else {
            finalXTextView.setText(getString(R.string.discrepancy_too_large));
            finalYTextView.setText(getString(R.string.discrepancy_too_large));
        }
    }

    /**
     * Сохраняет проект
     */
    private void saveProject() {
        if (currentProject != null && currentProject.getResult() != null) {
            ProjectStorage storage = new ProjectStorage(getContext());
            boolean saved = storage.saveProject(currentProject);

            if (saved) {
                Toast.makeText(getContext(), "Проект сохранен", Toast.LENGTH_SHORT).show();

                if (projectSavedListener != null) {
                    projectSavedListener.onProjectSaved(currentProject);
                }
            } else {
                Toast.makeText(getContext(), "Ошибка при сохранении проекта", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "Нет данных для сохранения", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Обновляет данные проекта
     */
    public void updateProject(Project project) {
        this.currentProject = project;
        if (isAdded()) {
            displayResults();
        }
    }
}