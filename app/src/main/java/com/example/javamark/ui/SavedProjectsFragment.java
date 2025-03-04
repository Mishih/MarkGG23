package com.example.javamark.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javamark.R;
import com.example.javamark.model.Project;
import com.example.javamark.storage.ProjectStorage;

import java.util.List;

public class SavedProjectsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView noProjectsTextView;
    private ProjectAdapter adapter;
    private ProjectStorage projectStorage;
    private OnProjectLoadedListener projectLoadedListener;

    // Интерфейс обратного вызова для загрузки проекта
    public interface OnProjectLoadedListener {
        void onProjectLoaded(Project project);
    }

    // Вынесенный интерфейс для слушателя клика из внутреннего класса
    public interface OnProjectClickListener {
        void onProjectClick(Project project);
    }

    public static SavedProjectsFragment newInstance() {
        return new SavedProjectsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnProjectLoadedListener) {
            projectLoadedListener = (OnProjectLoadedListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        projectStorage = new ProjectStorage(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_projects, container, false);

        recyclerView = view.findViewById(R.id.rv_saved_projects);
        noProjectsTextView = view.findViewById(R.id.tv_no_projects);

        // Настраиваем RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadProjects();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshProjects();
    }

    /**
     * Загружает проекты из хранилища и обновляет UI
     */
    private void loadProjects() {
        List<Project> projects = projectStorage.getAllProjects();

        if (projects.isEmpty()) {
            noProjectsTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noProjectsTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new ProjectAdapter(projects, project -> {
                if (projectLoadedListener != null) {
                    projectLoadedListener.onProjectLoaded(project);
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * Обновляет список проектов
     */
    public void refreshProjects() {
        if (isAdded()) {
            loadProjects();
        }
    }

    /**
     * Адаптер для отображения сохраненных проектов
     */
    private class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

        private final List<Project> projects;
        private final OnProjectClickListener listener;

        ProjectAdapter(List<Project> projects, OnProjectClickListener listener) {
            this.projects = projects;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_saved_project, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Project project = projects.get(position);
            holder.bind(project);
        }

        @Override
        public int getItemCount() {
            return projects.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView projectNameTextView;
            private final TextView projectDateTextView;
            private final Button loadButton;
            private final Button deleteButton;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                projectNameTextView = itemView.findViewById(R.id.tv_project_name);
                projectDateTextView = itemView.findViewById(R.id.tv_project_date);
                loadButton = itemView.findViewById(R.id.btn_load_project);
                deleteButton = itemView.findViewById(R.id.btn_delete_project);
            }

            void bind(Project project) {
                projectNameTextView.setText(project.getName());
                projectDateTextView.setText(project.getFormattedDate());

                loadButton.setOnClickListener(v -> listener.onProjectClick(project));

                deleteButton.setOnClickListener(v -> {
                    projectStorage.deleteProject(project.getId());
                    refreshProjects();
                });
            }
        }
    }
}