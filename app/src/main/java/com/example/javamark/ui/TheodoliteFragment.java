package com.example.javamark.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javamark.R;
import com.example.javamark.adapter.TheodoliteMeasurementAdapter;
import com.example.javamark.calculator.TheodoliteCalculator;
import com.example.javamark.model.AngleValue;
import com.example.javamark.model.StationMeasurement;
import com.example.javamark.model.TheodoliteJournal;
import com.example.javamark.storage.TheodoliteJournalStorage;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.List;

public class TheodoliteFragment extends Fragment implements TheodoliteMeasurementAdapter.OnMeasurementChangeListener {

    private static final String TAG = "TheodoliteFragment";
    private static final String ARG_JOURNAL = "journal";

    private RecyclerView recyclerView;
    private TheodoliteMeasurementAdapter adapter;
    private TheodoliteJournal currentJournal;
    private Button addStationButton;
    private Button saveButton;
    private MaterialButtonToggleGroup toggleViewMode;
    private View tableView;
    private TableLayout tableContent;
    private TheodoliteJournalStorage storage;
    private int lastStationNumber = 1;
    private int lastPointNumber1 = 1;
    private int lastPointNumber2 = 2;

    public TheodoliteFragment() {
        // Пустой конструктор, требуется для фрагментов
    }

    /**
     * Создает новый экземпляр фрагмента с пустым журналом
     */
    public static TheodoliteFragment newInstance() {
        return new TheodoliteFragment();
    }

    /**
     * Создает новый экземпляр фрагмента с загруженным журналом
     */
    public static TheodoliteFragment newInstance(TheodoliteJournal journal) {
        TheodoliteFragment fragment = new TheodoliteFragment();
        Bundle args = new Bundle();
        // Сериализуем журнал в JSON для передачи через Bundle
        args.putString(ARG_JOURNAL, new Gson().toJson(journal));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Инициализация хранилища
        storage = new TheodoliteJournalStorage(getContext());

        // Проверка наличия аргументов
        if (getArguments() != null && getArguments().containsKey(ARG_JOURNAL)) {
            try {
                // Десериализация журнала из JSON
                String journalJson = getArguments().getString(ARG_JOURNAL);
                currentJournal = new Gson().fromJson(journalJson, TheodoliteJournal.class);
                Log.d(TAG, "Загружен журнал: " + currentJournal.getName() +
                        " с " + currentJournal.getMeasurements().size() + " измерениями");
            } catch (Exception e) {
                Log.e(TAG, "Ошибка при загрузке журнала: " + e.getMessage());
                currentJournal = new TheodoliteJournal("Новый журнал теодолитного хода");
            }
        } else {
            // Создание нового журнала, если нет аргументов
            currentJournal = new TheodoliteJournal("Новый журнал теодолитного хода");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theodolite, container, false);

        recyclerView = view.findViewById(R.id.rv_theodolite_measurements);
        addStationButton = view.findViewById(R.id.btn_add_station);
        saveButton = view.findViewById(R.id.btn_save_theodolite);
        toggleViewMode = view.findViewById(R.id.toggle_view_mode);
        tableView = view.findViewById(R.id.sv_table_view);
        tableContent = view.findViewById(R.id.tl_content);

        // Настраиваем RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TheodoliteMeasurementAdapter(currentJournal.getMeasurements(), this);
        recyclerView.setAdapter(adapter);

        // Настраиваем обработчики
        addStationButton.setOnClickListener(v -> showAddStationDialog());
        saveButton.setOnClickListener(v -> saveJournal());

        // Если журнал был загружен, отображаем сообщение
        if (getArguments() != null && getArguments().containsKey(ARG_JOURNAL)) {
            Toast.makeText(getContext(), "Журнал \"" + currentJournal.getName() + "\" загружен", Toast.LENGTH_SHORT).show();
        }

        // Переключение режимов просмотра
        toggleViewMode.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btn_edit_mode) {
                    recyclerView.setVisibility(View.VISIBLE);
                    tableView.setVisibility(View.GONE);
                } else if (checkedId == R.id.btn_table_mode) {
                    recyclerView.setVisibility(View.GONE);
                    tableView.setVisibility(View.VISIBLE);
                    updateTableView();
                }
            }
        });

        // Исправляем отображение кнопок переключения режимов
        Button editModeButton = view.findViewById(R.id.btn_edit_mode);
        Button tableModeButton = view.findViewById(R.id.btn_table_mode);

        // Устанавливаем ширину кнопок
        ViewGroup.LayoutParams paramsEdit = editModeButton.getLayoutParams();
        paramsEdit.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        editModeButton.setLayoutParams(paramsEdit);

        ViewGroup.LayoutParams paramsTable = tableModeButton.getLayoutParams();
        paramsTable.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        tableModeButton.setLayoutParams(paramsTable);

        return view;
    }

    /**
     * Показывает диалог добавления новой станции
     * Теперь метод публичный, чтобы его можно было вызывать из активности
     */
    public void showAddStationDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle(R.string.add_station);

        // Создаем макет диалога
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_station, null);
        builder.setView(dialogView);

        TextView stationNumberTextView = dialogView.findViewById(R.id.et_new_station_number);
        TextView pointNumber1TextView = dialogView.findViewById(R.id.et_new_point_number1);
        TextView pointNumber2TextView = dialogView.findViewById(R.id.et_new_point_number2);

        // Устанавливаем предполагаемые значения
        stationNumberTextView.setText(String.valueOf(lastStationNumber));
        pointNumber1TextView.setText(String.valueOf(lastPointNumber1));
        pointNumber2TextView.setText(String.valueOf(lastPointNumber2));

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            try {
                int stationNumber = Integer.parseInt(stationNumberTextView.getText().toString());
                int pointNumber1 = Integer.parseInt(pointNumber1TextView.getText().toString());
                int pointNumber2 = Integer.parseInt(pointNumber2TextView.getText().toString());

                // Создаем новое измерение
                StationMeasurement measurement = new StationMeasurement(stationNumber, pointNumber1, pointNumber2);
                currentJournal.addMeasurement(measurement);
                adapter.notifyItemInserted(currentJournal.getMeasurements().size() - 1);

                // Запоминаем последние использованные номера
                lastStationNumber = stationNumber;
                lastPointNumber1 = pointNumber1;
                lastPointNumber2 = pointNumber2;

                // Прокручиваем к добавленному элементу
                recyclerView.smoothScrollToPosition(currentJournal.getMeasurements().size() - 1);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), R.string.invalid_input, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);

        builder.show();
    }

    /**
     * Сохраняет журнал теодолитного хода
     */
    private void saveJournal() {
        if (currentJournal.getMeasurements().isEmpty()) {
            Toast.makeText(getContext(), R.string.no_measurements, Toast.LENGTH_SHORT).show();
            return;
        }

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        builder.setTitle(R.string.save_theodolite);

        // Создаем макет диалога для ввода имени журнала
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_save_journal, null);
        builder.setView(dialogView);

        TextView journalNameTextView = dialogView.findViewById(R.id.et_journal_name);
        journalNameTextView.setText(currentJournal.getName());

        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            String journalName = journalNameTextView.getText().toString();
            if (!journalName.isEmpty()) {
                currentJournal.setName(journalName);
                boolean saved = storage.saveJournal(currentJournal);
                if (saved) {
                    Toast.makeText(getContext(), R.string.journal_saved, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), R.string.save_error, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), R.string.enter_journal_name, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton(android.R.string.cancel, null);

        builder.show();
    }

    /**
     * Обновляет табличное представление журнала
     */
    private void updateTableView() {
        tableContent.removeAllViews();
        DecimalFormat df = new DecimalFormat("#0.00");

        List<StationMeasurement> measurements = currentJournal.getMeasurements();
        for (StationMeasurement measurement : measurements) {
            TableRow row = new TableRow(getContext());

            // Номер станции
            TextView stationView = createTableCell(String.valueOf(measurement.getStationNumber()));
            row.addView(stationView);

            // Номера точек
            TextView pointsView = createTableCell(
                    measurement.getPointNumber1() + " → " + measurement.getPointNumber2());
            row.addView(pointsView);

            // Длина
            TextView distanceView = createTableCell(
                    measurement.getDistance() > 0 ? df.format(measurement.getDistance()) : "-");
            row.addView(distanceView);

            // КЛ точка 1
            TextView leftCircle1View = createTableCell(
                    measurement.getLeftCirclePoint1() != null ?
                            measurement.getLeftCirclePoint1().toString() : "-");
            row.addView(leftCircle1View);

            // КП точка 1
            TextView rightCircle1View = createTableCell(
                    measurement.getRightCirclePoint1() != null ?
                            measurement.getRightCirclePoint1().toString() : "-");
            row.addView(rightCircle1View);

            // КЛ точка 2
            TextView leftCircle2View = createTableCell(
                    measurement.getLeftCirclePoint2() != null ?
                            measurement.getLeftCirclePoint2().toString() : "-");
            row.addView(leftCircle2View);

            // КП точка 2
            TextView rightCircle2View = createTableCell(
                    measurement.getRightCirclePoint2() != null ?
                            measurement.getRightCirclePoint2().toString() : "-");
            row.addView(rightCircle2View);

            // Разница КЛ
            TextView leftDiffView = createTableCell(
                    measurement.getAngleLeftDifference() != null ?
                            measurement.getAngleLeftDifference().toString() : "-");
            row.addView(leftDiffView);

            // Разница КП
            TextView rightDiffView = createTableCell(
                    measurement.getAngleRightDifference() != null ?
                            measurement.getAngleRightDifference().toString() : "-");
            row.addView(rightDiffView);

            // Средний угол
            TextView averageAngleView = createTableCell(
                    measurement.getAverageAngle() != null ?
                            measurement.getAverageAngle().toString() : "-");
            row.addView(averageAngleView);

            // Угол наклона
            TextView slopeAngleView = createTableCell(
                    measurement.getSlopeAngle() != null ?
                            measurement.getSlopeAngle().toString() : "-");
            row.addView(slopeAngleView);

            // Горизонтальное проложение
            TextView horizontalDistanceView = createTableCell(
                    measurement.getHorizontalDistance() > 0 ?
                            df.format(measurement.getHorizontalDistance()) : "-");
            row.addView(horizontalDistanceView);

            tableContent.addView(row);
        }
    }

    /**
     * Создает ячейку таблицы с текстом
     */
    private TextView createTableCell(String text) {
        TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setBackgroundResource(R.drawable.table_cell_border);
        return textView;
    }

    @Override
    public void onMeasurementChanged(int position, StationMeasurement measurement) {
        // Пересчитываем все необходимые значения
        measurement.calculateAngles();
        measurement.calculateHorizontalDistance();

        // Если включен режим таблицы, обновляем её
        if (toggleViewMode.getCheckedButtonId() == R.id.btn_table_mode) {
            updateTableView();
        }
    }

    @Override
    public void onMeasurementRemoved(int position) {
        if (position >= 0 && position < currentJournal.getMeasurements().size()) {
            currentJournal.getMeasurements().remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, currentJournal.getMeasurements().size());

            // Если включен режим таблицы, обновляем её
            if (toggleViewMode.getCheckedButtonId() == R.id.btn_table_mode) {
                updateTableView();
            }
        }
    }
}