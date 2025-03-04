package com.example.javamark.ui.gyroscopic;

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
import com.example.javamark.model.GyroscopicMeasurement;
import com.example.javamark.storage.GyroscopicMeasurementStorage;

import java.util.List;

/**
 * Фрагмент для отображения сохраненных измерений гироскопического ориентирования
 */
public class SavedGyroscopicMeasurementsFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView noMeasurementsTextView;
    private MeasurementAdapter adapter;
    private GyroscopicMeasurementStorage storage;
    private OnMeasurementLoadedListener measurementLoadedListener;

    public interface OnMeasurementLoadedListener {
        void onMeasurementLoaded(GyroscopicMeasurement measurement);
    }

    public interface OnMeasurementClickListener {
        void onMeasurementClick(GyroscopicMeasurement measurement);
    }

    public static SavedGyroscopicMeasurementsFragment newInstance() {
        return new SavedGyroscopicMeasurementsFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnMeasurementLoadedListener) {
            measurementLoadedListener = (OnMeasurementLoadedListener) context;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storage = new GyroscopicMeasurementStorage(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_saved_gyroscopic_measurements, container, false);

        recyclerView = view.findViewById(R.id.rv_saved_measurements);
        noMeasurementsTextView = view.findViewById(R.id.tv_no_measurements);

        // Настраиваем RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadMeasurements();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshMeasurements();
    }

    /**
     * Загружает измерения из хранилища и обновляет UI
     */
    private void loadMeasurements() {
        List<GyroscopicMeasurement> measurements = storage.getAllMeasurements();

        if (measurements.isEmpty()) {
            noMeasurementsTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noMeasurementsTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new MeasurementAdapter(measurements, measurement -> {
                if (measurementLoadedListener != null) {
                    measurementLoadedListener.onMeasurementLoaded(measurement);
                }
            });
            recyclerView.setAdapter(adapter);
        }
    }

    /**
     * Обновляет список измерений
     */
    public void refreshMeasurements() {
        if (isAdded()) {
            loadMeasurements();
        }
    }

    /**
     * Адаптер для отображения сохраненных измерений
     */
    private class MeasurementAdapter extends RecyclerView.Adapter<MeasurementAdapter.ViewHolder> {

        private final List<GyroscopicMeasurement> measurements;
        private final OnMeasurementClickListener listener;

        MeasurementAdapter(List<GyroscopicMeasurement> measurements, OnMeasurementClickListener listener) {
            this.measurements = measurements;
            this.listener = listener;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_saved_gyroscopic_measurement, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GyroscopicMeasurement measurement = measurements.get(position);
            holder.bind(measurement);
        }

        @Override
        public int getItemCount() {
            return measurements.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private final TextView measurementNameTextView;
            private final TextView measurementDateTextView;
            private final TextView measurementAzimuthTextView;
            private final Button loadButton;
            private final Button deleteButton;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                measurementNameTextView = itemView.findViewById(R.id.tv_measurement_name);
                measurementDateTextView = itemView.findViewById(R.id.tv_measurement_date);
                measurementAzimuthTextView = itemView.findViewById(R.id.tv_measurement_azimuth);
                loadButton = itemView.findViewById(R.id.btn_load_measurement);
                deleteButton = itemView.findViewById(R.id.btn_delete_measurement);
            }

            void bind(GyroscopicMeasurement measurement) {
                measurementNameTextView.setText(measurement.getName());
                measurementDateTextView.setText(measurement.getFormattedDate());

                if (measurement.getGyroscopicAzimuth() != null) {
                    measurementAzimuthTextView.setText("Азимут: " + measurement.getGyroscopicAzimuth().toString());
                } else {
                    measurementAzimuthTextView.setText("Азимут не вычислен");
                }

                loadButton.setOnClickListener(v -> listener.onMeasurementClick(measurement));

                deleteButton.setOnClickListener(v -> {
                    storage.deleteMeasurement(measurement.getId());
                    refreshMeasurements();
                    Toast.makeText(getContext(), "Измерение удалено", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }
}