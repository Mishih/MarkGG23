// 2. Добавим новый фрагмент для отображения визуализации

package com.example.javamark.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.javamark.R;
import com.example.javamark.model.TheodoliteJournal;
import com.google.gson.Gson;

/**
 * Диалоговый фрагмент для отображения визуализации теодолитного хода
 */
public class TheodoliteVisualizationFragment extends DialogFragment {
    private static final String ARG_JOURNAL = "journal";

    private TheodoliteJournal journal;
    private TheodoliteVisualizationView visualizationView;

    /**
     * Создает новый экземпляр фрагмента с переданным журналом
     */
    public static TheodoliteVisualizationFragment newInstance(TheodoliteJournal journal) {
        TheodoliteVisualizationFragment fragment = new TheodoliteVisualizationFragment();
        Bundle args = new Bundle();
        // Сериализуем журнал в JSON для передачи через Bundle
        args.putString(ARG_JOURNAL, new Gson().toJson(journal));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Устанавливаем полноэкранный стиль для диалога
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Material_Light_Dialog_MinWidth);

        // Проверка наличия аргументов
        if (getArguments() != null && getArguments().containsKey(ARG_JOURNAL)) {
            try {
                // Десериализация журнала из JSON
                String journalJson = getArguments().getString(ARG_JOURNAL);
                journal = new Gson().fromJson(journalJson, TheodoliteJournal.class);
            } catch (Exception e) {
                e.printStackTrace();
                journal = null;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theodolite_visualization, container, false);

        // Инициализация компонентов
        TextView titleTextView = view.findViewById(R.id.tv_visualization_title);
        visualizationView = view.findViewById(R.id.theodolite_visualization_view);
        Button closeButton = view.findViewById(R.id.btn_close_visualization);

        // Установка заголовка
        if (journal != null) {
            titleTextView.setText("Визуализация теодолитного хода: " + journal.getName());
        } else {
            titleTextView.setText("Визуализация теодолитного хода");
        }

        // Установка данных для визуализации
        if (journal != null) {
            visualizationView.setJournal(journal);
        }

        // Обработчик нажатия на кнопку закрытия
        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }
}