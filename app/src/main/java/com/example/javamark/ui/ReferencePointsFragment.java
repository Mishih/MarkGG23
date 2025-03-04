package com.example.javamark.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javamark.R;
import com.example.javamark.adapter.ReferencePointAdapter;
import com.example.javamark.model.Project;
import com.example.javamark.model.ReferencePoint;

public class ReferencePointsFragment extends Fragment implements ReferencePointAdapter.OnReferencePointChangeListener {

    private RecyclerView recyclerView;
    private ReferencePointAdapter adapter;
    private TextView minPointsLabel;
    private Project currentProject;
    private boolean initialPointsAdded = false;

    public static ReferencePointsFragment newInstance(Project project) {
        ReferencePointsFragment fragment = new ReferencePointsFragment();
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
        if (currentProject == null) {
            currentProject = new Project("Новый проект");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reference_points, container, false);

        recyclerView = view.findViewById(R.id.rv_reference_points);
        minPointsLabel = view.findViewById(R.id.label_min_points);

        // Настраиваем RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Добавляем начальные точки, если список пуст и это не было сделано ранее
        if (currentProject.getReferencePoints().isEmpty() && !initialPointsAdded) {
            addInitialPoints();
            initialPointsAdded = true;
        }

        adapter = new ReferencePointAdapter(currentProject.getReferencePoints(), this);
        recyclerView.setAdapter(adapter);

        updateMinPointsLabel();

        return view;
    }

    /**
     * Добавляет новую исходную точку в список
     */
    public void addReferencePoint() {
        int pointNumber = getNextPointNumber();
        ReferencePoint newPoint = new ReferencePoint(
                pointNumber,
                "T" + pointNumber,
                0.0,
                0.0,
                0.0
        );
        currentProject.addReferencePoint(newPoint);
        adapter.notifyItemInserted(currentProject.getReferencePoints().size() - 1);
        updateMinPointsLabel();
    }

    /**
     * Определяет следующий номер точки, избегая дублирования
     */
    private int getNextPointNumber() {
        int maxNumber = 0;
        for (ReferencePoint point : currentProject.getReferencePoints()) {
            if (point.getId() > maxNumber) {
                maxNumber = point.getId();
            }
        }
        return maxNumber + 1;
    }

    /**
     * Добавляет начальные 4 точки при создании нового проекта
     */
    private void addInitialPoints() {
        currentProject.addReferencePoint(new ReferencePoint(1, "T₁", 0.0, 0.0, 0.0));
        currentProject.addReferencePoint(new ReferencePoint(2, "T₂", 0.0, 0.0, 0.0));
        currentProject.addReferencePoint(new ReferencePoint(3, "T₃", 0.0, 0.0, 0.0));
        currentProject.addReferencePoint(new ReferencePoint(4, "T₄", 0.0, 0.0, 0.0));
    }

    /**
     * Обновляет текст с информацией о минимальном количестве точек
     */
    private void updateMinPointsLabel() {
        if (currentProject.hasMinimumRequiredPoints()) {
            minPointsLabel.setText(getString(R.string.min_points_satisfied));
            minPointsLabel.setTextColor(getResources().getColor(R.color.green, null));
        } else {
            minPointsLabel.setText(getString(R.string.min_points_required));
            minPointsLabel.setTextColor(getResources().getColor(R.color.red, null));
        }
    }

    @Override
    public void onReferencePointChanged(int position, ReferencePoint point) {
        // Данные уже обновлены в адаптере, просто сохраняем проект
        // или реагируем на изменения, если нужно
    }

    @Override
    public void onReferencePointRemoved(int position) {
        currentProject.getReferencePoints().remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, currentProject.getReferencePoints().size());
        updateMinPointsLabel();
    }

    /**
     * @return Текущий проект с введёнными данными
     */
    public Project getCurrentProject() {
        return currentProject;
    }
}