package com.example.javamark.adapter;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javamark.R;
import com.example.javamark.model.ReferencePoint;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class ReferencePointAdapter extends RecyclerView.Adapter<ReferencePointAdapter.ViewHolder> {

    private final List<ReferencePoint> referencePoints;
    private final OnReferencePointChangeListener listener;

    public interface OnReferencePointChangeListener {
        void onReferencePointChanged(int position, ReferencePoint point);
        void onReferencePointRemoved(int position);
    }

    public ReferencePointAdapter(List<ReferencePoint> referencePoints, OnReferencePointChangeListener listener) {
        this.referencePoints = referencePoints;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reference_point, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ReferencePoint point = referencePoints.get(position);
        holder.bind(point, position);
    }

    @Override
    public int getItemCount() {
        return referencePoints.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView pointNameTextView;
        private final TextInputEditText xCoordEditText;
        private final TextInputEditText yCoordEditText;
        private final TextInputEditText angleEditText;
        private final ImageButton removeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pointNameTextView = itemView.findViewById(R.id.tv_point_name);
            xCoordEditText = itemView.findViewById(R.id.et_x_coord);
            yCoordEditText = itemView.findViewById(R.id.et_y_coord);
            angleEditText = itemView.findViewById(R.id.et_angle);
            removeButton = itemView.findViewById(R.id.btn_remove_point);
        }

        public void bind(ReferencePoint point, int position) {
            // Установка имени точки (T₁, T₂, и т.д.)
            pointNameTextView.setText(point.getName());

            // Установка текущих значений в поля ввода
            xCoordEditText.setText(String.valueOf(point.getX()));
            yCoordEditText.setText(String.valueOf(point.getY()));
            angleEditText.setText(String.valueOf(point.getBeta()));

            // Обработка изменений в поле X
            xCoordEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        double value = s.toString().isEmpty() ? 0 : Double.parseDouble(s.toString());
                        point.setX(value);
                        listener.onReferencePointChanged(getAdapterPosition(), point);
                    } catch (NumberFormatException e) {
                        // Игнорируем ошибку парсинга
                    }
                }
            });

            // Обработка изменений в поле Y
            yCoordEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        double value = s.toString().isEmpty() ? 0 : Double.parseDouble(s.toString());
                        point.setY(value);
                        listener.onReferencePointChanged(getAdapterPosition(), point);
                    } catch (NumberFormatException e) {
                        // Игнорируем ошибку парсинга
                    }
                }
            });

            // Обработка изменений в поле угла beta
            angleEditText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        double value = s.toString().isEmpty() ? 0 : Double.parseDouble(s.toString());
                        point.setBeta(value);
                        listener.onReferencePointChanged(getAdapterPosition(), point);
                    } catch (NumberFormatException e) {
                        // Игнорируем ошибку парсинга
                    }
                }
            });

            // Обработка нажатия на кнопку удаления
            removeButton.setOnClickListener(v -> {
                listener.onReferencePointRemoved(getAdapterPosition());
            });
        }
    }
}