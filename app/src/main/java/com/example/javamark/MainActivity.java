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

import com.example.javamark.model.Project;
import com.example.javamark.model.TheodoliteJournal;
import com.example.javamark.ui.CalculationFragment;
import com.example.javamark.ui.ReferencePointsFragment;
import com.example.javamark.ui.ResultsFragment;
import com.example.javamark.ui.SavedProjectsFragment;
import com.example.javamark.ui.SavedTheodoliteJournalsFragment;
import com.example.javamark.ui.TheodoliteFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        ResultsFragment.OnProjectSavedListener,
        SavedProjectsFragment.OnProjectLoadedListener,
        SavedTheodoliteJournalsFragment.OnJournalLoadedListener {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fabAddPoint;
    private ReferencePointsFragment referencePointsFragment;
    private CalculationFragment calculationFragment;
    private ResultsFragment resultsFragment;
    private SavedProjectsFragment savedProjectsFragment;
    private TheodoliteFragment theodoliteFragment;
    private SavedTheodoliteJournalsFragment savedJournalsFragment;
    private Project currentProject;

    // Заголовки вкладок
    private final String[] tabTitles = new String[] {
            "Исходные точки", "Вычисление", "Результаты", "Сохраненные",
            "Теодолитный ход", "Журналы хода"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Инициализация проекта
        currentProject = new Project("Новый проект");

        // Настройка Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Настройка ViewPager и TabLayout
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        fabAddPoint = findViewById(R.id.fab_add_point);

        // Создание фрагментов
        referencePointsFragment = ReferencePointsFragment.newInstance(currentProject);
        calculationFragment = CalculationFragment.newInstance(currentProject);
        resultsFragment = ResultsFragment.newInstance(currentProject);
        savedProjectsFragment = SavedProjectsFragment.newInstance();
        theodoliteFragment = TheodoliteFragment.newInstance();
        savedJournalsFragment = SavedTheodoliteJournalsFragment.newInstance();

        // Настройка адаптера для ViewPager
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // Подключение TabLayout к ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) ->
                tab.setText(tabTitles[position])).attach();

        // Настройка кнопки добавления точки
        fabAddPoint.setOnClickListener(view -> {
            // Определяем, на какой вкладке находимся
            int currentItem = viewPager.getCurrentItem();
            if (currentItem == 0) {
                // Вкладка исходных точек
                referencePointsFragment.addReferencePoint();
            }
        });

        // Слушатель изменения вкладок для показа/скрытия кнопки добавления
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // Показываем кнопку только на вкладке исходных точек для обратной засечки
                fabAddPoint.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_new_project) {
            confirmNewProject();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Показывает диалог подтверждения создания нового проекта
     */
    private void confirmNewProject() {
        new AlertDialog.Builder(this)
                .setTitle("Новый проект")
                .setMessage("Все несохраненные данные будут потеряны. Создать новый проект?")
                .setPositiveButton("Да", (dialog, which) -> createNewProject())
                .setNegativeButton("Отмена", null)
                .show();
    }

    /**
     * Создает новый проект
     */
    private void createNewProject() {
        currentProject = new Project("Новый проект");

        // Обновляем фрагменты
        referencePointsFragment = ReferencePointsFragment.newInstance(currentProject);
        calculationFragment = CalculationFragment.newInstance(currentProject);
        resultsFragment = ResultsFragment.newInstance(currentProject);

        // Обновляем адаптер ViewPager
        viewPager.setAdapter(new ViewPagerAdapter(this));

        // Переходим на первую вкладку
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onProjectSaved(Project project) {
        // Обновляем фрагмент сохраненных проектов
        savedProjectsFragment.refreshProjects();
    }

    @Override
    public void onProjectLoaded(Project project) {
        // Загружаем проект
        currentProject = project;

        // Обновляем фрагменты
        referencePointsFragment = ReferencePointsFragment.newInstance(currentProject);
        calculationFragment = CalculationFragment.newInstance(currentProject);
        resultsFragment = ResultsFragment.newInstance(currentProject);

        // Обновляем адаптер ViewPager
        viewPager.setAdapter(new ViewPagerAdapter(this));

        // Переходим на первую вкладку
        viewPager.setCurrentItem(0);
    }

    @Override
    public void onJournalLoaded(TheodoliteJournal journal) {
        // Создаем новый фрагмент теодолитного хода с загруженным журналом
        theodoliteFragment = TheodoliteFragment.newInstance(journal);

        // Обновляем адаптер ViewPager
        viewPager.setAdapter(new ViewPagerAdapter(this));

        // Переходим на вкладку теодолитного хода
        viewPager.setCurrentItem(4); // Индекс вкладки теодолитного хода
    }

    /**
     * Адаптер для ViewPager
     */
    private class ViewPagerAdapter extends FragmentStateAdapter {
        private final List<Fragment> fragments = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
            fragments.add(referencePointsFragment);
            fragments.add(calculationFragment);
            fragments.add(resultsFragment);
            fragments.add(savedProjectsFragment);
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