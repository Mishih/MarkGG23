package com.example.javamark.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.javamark.model.Project;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для сохранения и загрузки проектов с использованием SharedPreferences и Gson
 */
public class ProjectStorage {
    private static final String PREFS_NAME = "geodesic_projects";
    private static final String PROJECTS_KEY = "saved_projects";
    private final SharedPreferences preferences;
    private final Gson gson;

    public ProjectStorage(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    /**
     * Сохраняет проект в хранилище
     * @param project Проект для сохранения
     * @return true, если сохранение прошло успешно
     */
    public boolean saveProject(Project project) {
        try {
            List<Project> projects = getAllProjects();

            // Проверяем, существует ли проект с таким ID
            boolean exists = false;
            for (int i = 0; i < projects.size(); i++) {
                if (projects.get(i).getId() == project.getId()) {
                    // Обновляем существующий проект
                    projects.set(i, project);
                    exists = true;
                    break;
                }
            }

            // Если проект новый, присваиваем ему ID и добавляем в список
            if (!exists) {
                project.setId(generateProjectId(projects));
                projects.add(project);
            }

            // Сохраняем обновленный список проектов
            String json = gson.toJson(projects);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(PROJECTS_KEY, json);
            editor.apply();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Получает все сохраненные проекты
     * @return Список проектов
     */
    public List<Project> getAllProjects() {
        String json = preferences.getString(PROJECTS_KEY, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<Project>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Получает проект по ID
     * @param projectId ID проекта
     * @return Проект или null, если не найден
     */
    public Project getProjectById(int projectId) {
        List<Project> projects = getAllProjects();
        for (Project project : projects) {
            if (project.getId() == projectId) {
                return project;
            }
        }
        return null;
    }

    /**
     * Удаляет проект из хранилища
     * @param projectId ID проекта для удаления
     * @return true, если удаление прошло успешно
     */
    public boolean deleteProject(int projectId) {
        try {
            List<Project> projects = getAllProjects();
            for (int i = 0; i < projects.size(); i++) {
                if (projects.get(i).getId() == projectId) {
                    projects.remove(i);
                    String json = gson.toJson(projects);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(PROJECTS_KEY, json);
                    editor.apply();
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Генерирует уникальный ID для нового проекта
     * @param projects Список существующих проектов
     * @return Уникальный ID
     */
    private int generateProjectId(List<Project> projects) {
        int maxId = 0;
        for (Project project : projects) {
            if (project.getId() > maxId) {
                maxId = project.getId();
            }
        }
        return maxId + 1;
    }
}