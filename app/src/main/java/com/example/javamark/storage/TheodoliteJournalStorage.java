package com.example.javamark.storage;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.javamark.model.TheodoliteJournal;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Класс для сохранения и загрузки журналов теодолитного хода
 */
public class TheodoliteJournalStorage {
    private static final String PREFS_NAME = "theodolite_journals";
    private static final String JOURNALS_KEY = "saved_journals";
    private final SharedPreferences preferences;
    private final Gson gson;

    public TheodoliteJournalStorage(Context context) {
        preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    /**
     * Сохраняет журнал в хранилище
     * @param journal Журнал для сохранения
     * @return true, если сохранение прошло успешно
     */
    public boolean saveJournal(TheodoliteJournal journal) {
        try {
            List<TheodoliteJournal> journals = getAllJournals();

            // Проверяем, существует ли журнал с таким ID
            boolean exists = false;
            for (int i = 0; i < journals.size(); i++) {
                if (journals.get(i).getId() == journal.getId()) {
                    // Обновляем существующий журнал
                    journals.set(i, journal);
                    exists = true;
                    break;
                }
            }

            // Если журнал новый, присваиваем ему ID и добавляем в список
            if (!exists) {
                journal.setId(generateJournalId(journals));
                journals.add(journal);
            }

            // Сохраняем обновленный список журналов
            String json = gson.toJson(journals);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(JOURNALS_KEY, json);
            editor.apply();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Получает все сохраненные журналы
     * @return Список журналов
     */
    public List<TheodoliteJournal> getAllJournals() {
        String json = preferences.getString(JOURNALS_KEY, "");
        if (json.isEmpty()) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<List<TheodoliteJournal>>() {}.getType();
        return gson.fromJson(json, type);
    }

    /**
     * Получает журнал по ID
     * @param journalId ID журнала
     * @return Журнал или null, если не найден
     */
    public TheodoliteJournal getJournalById(int journalId) {
        List<TheodoliteJournal> journals = getAllJournals();
        for (TheodoliteJournal journal : journals) {
            if (journal.getId() == journalId) {
                return journal;
            }
        }
        return null;
    }

    /**
     * Удаляет журнал из хранилища
     * @param journalId ID журнала для удаления
     * @return true, если удаление прошло успешно
     */
    public boolean deleteJournal(int journalId) {
        try {
            List<TheodoliteJournal> journals = getAllJournals();
            for (int i = 0; i < journals.size(); i++) {
                if (journals.get(i).getId() == journalId) {
                    journals.remove(i);
                    String json = gson.toJson(journals);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString(JOURNALS_KEY, json);
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
     * Генерирует уникальный ID для нового журнала
     * @param journals Список существующих журналов
     * @return Уникальный ID
     */
    private int generateJournalId(List<TheodoliteJournal> journals) {
        int maxId = 0;
        for (TheodoliteJournal journal : journals) {
            if (journal.getId() > maxId) {
                maxId = journal.getId();
            }
        }
        return maxId + 1;
    }
}