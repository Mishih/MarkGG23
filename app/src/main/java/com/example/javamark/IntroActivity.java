package com.example.javamark;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Стартовый экран для выбора функциональности приложения
 */
public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        Button btnStartGeodesic = findViewById(R.id.btn_start_geodesic);
        Button btnStartTheodolite = findViewById(R.id.btn_start_theodolite);
        Button btnStartGyroscopic = findViewById(R.id.btn_start_gyroscopic);

        btnStartGeodesic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Запуск активности для обратной геодезической засечки
                Intent intent = new Intent(IntroActivity.this, GeodesicActivity.class);
                startActivity(intent);
            }
        });

        btnStartTheodolite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Запуск активности для теодолитного хода
                Intent intent = new Intent(IntroActivity.this, TheodoliteActivity.class);
                startActivity(intent);
            }
        });

        btnStartGyroscopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Запуск активности для гироскопического ориентирования
                Intent intent = new Intent(IntroActivity.this, GyroscopicActivity.class);
                startActivity(intent);
            }
        });
    }
}