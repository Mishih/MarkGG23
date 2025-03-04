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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javamark.R;
import com.example.javamark.model.TheodoliteJournal;
import com.example.javamark.storage.TheodoliteJournalStorage;

import java.util.List;

/**
 * Фрагмент для отображения сохраненных журналов теодолитного хода
 */
public class SavedTheodoliteJournalsFragment extends Fragment {

    // Интерфейс обратного вызова для загрузки журнала
    public interface OnJournalLoadedListener {
        void onJournalLoaded(TheodoliteJournal journal);
    }

    // Интерфейс для обработки кликов по журналу в адаптере
    public interface OnJournalClickListener {
        void onJournalClick(TheodoliteJournal journal);
    }

    private RecyclerView recyclerView;
    private TextView noJournalsTextView;
    private TheodoliteJournalAdapter adapter;
    private TheodoliteJournalStorage storage;
    private OnJournalLoadedListener journalLoadedListener;

    // Пустой конструктор обязателен для Fragment
    public SavedTheodoliteJournalsFragment() {
    }

    // Статический метод создания экземпляра
    public static SavedTheodoliteJournalsFragment newInstance() {
        return new SavedTheodoliteJournalsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnJournalLoadedListener) {
            journalLoadedListener = (OnJournalLoadedListener) context;
        } else {
            throw new RuntimeException(context.toString() + " должен реализовать OnJournalLoadedListener");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = new TheodoliteJournalStorage(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_journals, container, false);

        recyclerView = view.findViewById(R.id.rv_saved_journals);
        noJournalsTextView = view.findViewById(R.id.tv_no_journals);

        // Настраиваем RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadJournals();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshJournals();
    }

    /**
     * Загружает журналы из хранилища и обновляет UI
     */
    private void loadJournals() {
        List<TheodoliteJournal> journals = storage.getAllJournals();

        if (journals.isEmpty()) {
            noJournalsTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noJournalsTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new TheodoliteJournalAdapter(journals, journal -> {
                if (journalLoadedListener != null) {
                    journalLoadedListener.onJournalLoaded(journal);
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * Обновляет список журналов
     */
    public void refreshJournals() {
        if (isAdded()) {
            loadJournals();
        }
    }

    /**
     * Адаптер для отображения сохраненных журналов
     */
    private class TheodoliteJournalAdapter extends RecyclerView.Adapter<TheodoliteJournalAdapter.ViewHolder> {

        private final List<TheodoliteJournal> journals;
        private final OnJournalClickListener listener;

        TheodoliteJournalAdapter(List<TheodoliteJournal> journals, OnJournalClickListener listener) {
            this.journals = journals;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_saved_journal, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            TheodoliteJournal journal = journals.get(position);
            holder.bind(journal);
        }

        @Override
        public int getItemCount() {
            return journals.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView journalNameTextView;
            private final TextView journalDateTextView;
            private final TextView journalStationsTextView;
            private final Button loadButton;
            private final Button deleteButton;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                journalNameTextView = itemView.findViewById(R.id.tv_journal_name);
                journalDateTextView = itemView.findViewById(R.id.tv_journal_date);
                journalStationsTextView = itemView.findViewById(R.id.tv_journal_stations);
                loadButton = itemView.findViewById(R.id.btn_load_journal);
                deleteButton = itemView.findViewById(R.id.btn_delete_journal);
            }

            void bind(TheodoliteJournal journal) {
                journalNameTextView.setText(journal.getName());
                journalDateTextView.setText(journal.getFormattedDate());
                journalStationsTextView.setText(getString(R.string.stations_count, journal.getMeasurements().size()));

                loadButton.setOnClickListener(v -> listener.onJournalClick(journal));

                deleteButton.setOnClickListener(v -> {
                    storage.deleteJournal(journal.getId());
                    refreshJournals();
                    Toast.makeText(getContext(), R.string.journal_deleted, Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
}