package com.example.javamark;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.javamark.model.TheodoliteJournal;
import com.example.javamark.ui.SavedTheodoliteJournalsFragment;
import com.example.javamark.ui.TheodoliteFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

/**
 * Активность для работы с теодолитным ходом
 */
public class TheodoliteActivity extends AppCompatActivity
        implements SavedTheodoliteJournalsFragment.OnJournalLoadedListener {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fabAddStation;
    private TheodoliteFragment theodoliteFragment;
    private SavedTheodoliteJournalsFragment savedJournalsFragment;
    private TheodoliteJournal currentJournal;

    // Заголовки вкладок
    private final String[] tabTitles = new String[] {
            "Теодолитный ход", "Журналы теодолитного хода"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theodolite);

        // Инициализация журнала
        currentJournal = new TheodoliteJournal("Новый журнал");

        // Настройка Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Теодолитный ход");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // Настройка ViewPager и TabLayout
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        fabAddStation = findViewById(R.id.fab_add_station);

        // Создание фрагментов
        theodoliteFragment = TheodoliteFragment.newInstance();
        savedJournalsFragment = SavedTheodoliteJournalsFragment.newInstance();

        // Настройка адаптера для ViewPager
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Подключение TabLayout к ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(tabTitles[position])).attach();

        // Настройка кнопки добавления станции
        fabAddStation.setOnClickListener(view -> {
            if (viewPager.getCurrentItem() == 0) {
                // Вызываем метод добавления станции в фрагменте теодолитного хода
                ((TheodoliteFragment) pagerAdapter.createFragment(0)).showAddStationDialog();
            }
        });

        // Слушатель изменения вкладок для показа/скрытия кнопки добавления
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Показываем кнопку только на вкладке теодолитного хода
                fabAddStation.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        // Возврат к предыдущему экрану при нажатии на кнопку "Назад" в тулбаре
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.theodolite_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_new_journal) {
            confirmNewJournal();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Показывает диалог подтверждения создания нового журнала
     */
    private void confirmNewJournal() {
        new AlertDialog.Builder(this)
                .setTitle("Новый журнал")
                .setMessage("Все несохраненные данные будут потеряны. Создать новый журнал теодолитного хода?")
                .setPositiveButton("Да", (dialog, which) -> createNewJournal())
                .setNegativeButton("Отмена", null)
                .show();
    }

    /**
     * Создает новый журнал теодолитного хода
     */
    private void createNewJournal() {
        currentJournal = new TheodoliteJournal("Новый журнал");

        // Обновляем фрагмент
        theodoliteFragment = TheodoliteFragment.newInstance();

        // Обновляем адаптер ViewPager
        viewPager.setAdapter(new ViewPagerAdapter(this));

        // Переходим на первую вкладку
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onJournalLoaded(TheodoliteJournal journal) {
        // Загружаем журнал в фрагмент теодолитного хода
        theodoliteFragment = TheodoliteFragment.newInstance(journal);

        // Обновляем адаптер ViewPager
        viewPager.setAdapter(new ViewPagerAdapter(this));

        // Переходим на вкладку теодолитного хода
        viewPager.setCurrentItem(0);
    }

    /**
     * Адаптер для ViewPager
     */
    private class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
            fragments.add(theodoliteFragment);
            fragments.add(savedJournalsFragment);
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