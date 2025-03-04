package com.example.javamark;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.javamark.model.GyroscopicMeasurement;
import com.example.javamark.ui.gyroscopic.ZeroTorsionFragment;
import com.example.javamark.ui.gyroscopic.EquilibriumFragment;
import com.example.javamark.ui.gyroscopic.DirectionFragment;
import com.example.javamark.ui.gyroscopic.TorsionCorrectionFragment;
import com.example.javamark.ui.gyroscopic.GyroscopicResultsFragment;
import com.example.javamark.ui.gyroscopic.SavedGyroscopicMeasurementsFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

/**
 * Активность для гироскопического ориентирования
 */
public class GyroscopicActivity extends AppCompatActivity implements
        GyroscopicResultsFragment.OnMeasurementSavedListener,
        SavedGyroscopicMeasurementsFragment.OnMeasurementLoadedListener {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private GyroscopicMeasurement currentMeasurement;

    // Фрагменты
    private ZeroTorsionFragment zeroTorsionFragment;
    private EquilibriumFragment equilibriumFragment;
    private DirectionFragment directionFragment;
    private TorsionCorrectionFragment torsionCorrectionFragment;
    private GyroscopicResultsFragment resultsFragment;
    private SavedGyroscopicMeasurementsFragment savedMeasurementsFragment;

    // Заголовки вкладок
    private final String[] tabTitles = new String[] {
            "Нуль торсиона", "Положение равновесия", "Примычное направление",
            "Поправка торсиона", "Результаты", "Сохраненные"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscopic);

        // Инициализация измерения
        currentMeasurement = new GyroscopicMeasurement("Новое измерение");

        // Настройка Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Гироскопическое ориентирование");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Настройка ViewPager и TabLayout
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        // Создание фрагментов
        zeroTorsionFragment = ZeroTorsionFragment.newInstance(currentMeasurement);
        equilibriumFragment = EquilibriumFragment.newInstance(currentMeasurement);
        directionFragment = DirectionFragment.newInstance(currentMeasurement);
        torsionCorrectionFragment = TorsionCorrectionFragment.newInstance(currentMeasurement);
        resultsFragment = GyroscopicResultsFragment.newInstance(currentMeasurement);
        savedMeasurementsFragment = SavedGyroscopicMeasurementsFragment.newInstance();

        // Настройка адаптера для ViewPager
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Подключение TabLayout к ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(tabTitles[position])).attach();
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Возврат к предыдущему экрану при нажатии на кнопку "Назад" в тулбаре
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gyroscopic_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_new_measurement) {
            confirmNewMeasurement();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Показывает диалог подтверждения создания нового измерения
     */
    private void confirmNewMeasurement() {
        new AlertDialog.Builder(this)
                .setTitle("Новое измерение")
                .setMessage("Все несохраненные данные будут потеряны. Создать новое измерение?")
                .setPositiveButton("Да", (dialog, which) -> createNewMeasurement())
                .setNegativeButton("Отмена", null)
                .show();
    }

    /**
     * Создает новое измерение
     */
    private void createNewMeasurement() {
        currentMeasurement = new GyroscopicMeasurement("Новое измерение");

        // Обновляем фрагменты
        zeroTorsionFragment = ZeroTorsionFragment.newInstance(currentMeasurement);
        equilibriumFragment = EquilibriumFragment.newInstance(currentMeasurement);
        directionFragment = DirectionFragment.newInstance(currentMeasurement);
        torsionCorrectionFragment = TorsionCorrectionFragment.newInstance(currentMeasurement);
        resultsFragment = GyroscopicResultsFragment.newInstance(currentMeasurement);

        // Обновляем адаптер ViewPager
        viewPager.setAdapter(new ViewPagerAdapter(this));

        // Переходим на первую вкладку
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onMeasurementSaved(GyroscopicMeasurement measurement) {
        // Обновляем фрагмент сохраненных измерений
        savedMeasurementsFragment.refreshMeasurements();
    }

    @Override
    public void onMeasurementLoaded(GyroscopicMeasurement measurement) {
        // Загружаем измерение
        currentMeasurement = measurement;

        // Обновляем фрагменты
        zeroTorsionFragment = ZeroTorsionFragment.newInstance(currentMeasurement);
        equilibriumFragment = EquilibriumFragment.newInstance(currentMeasurement);
        directionFragment = DirectionFragment.newInstance(currentMeasurement);
        torsionCorrectionFragment = TorsionCorrectionFragment.newInstance(currentMeasurement);
        resultsFragment = GyroscopicResultsFragment.newInstance(currentMeasurement);

        // Обновляем адаптер ViewPager
        viewPager.setAdapter(new ViewPagerAdapter(this));

        // Переходим на первую вкладку
        viewPager.setCurrentItem(0);
    }

    /**
     * Адаптер для ViewPager
     */
    private class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
            fragments.add(zeroTorsionFragment);
            fragments.add(equilibriumFragment);
            fragments.add(directionFragment);
            fragments.add(torsionCorrectionFragment);
            fragments.add(resultsFragment);
            fragments.add(savedMeasurementsFragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return fragments.size();
        }
    }
}