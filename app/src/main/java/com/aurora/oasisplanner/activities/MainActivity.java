package com.aurora.oasisplanner.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.MenuItem;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.datasource.AppDatabase;
import com.aurora.oasisplanner.data.repository.AgendaRepository;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.repository.AlarmRepository;
import com.aurora.oasisplanner.data.tags.Pages;
import com.aurora.oasisplanner.databinding.MainBinding;
import com.aurora.oasisplanner.fragments.EventArrangerFragment;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmScheduler;
import com.aurora.oasisplanner.util.notificationfeatures.NotificationModule;
import com.aurora.oasisplanner.util.notificationfeatures.NotificationModule.NotificationMode;
import com.aurora.oasisplanner.util.styling.Resources;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static MainActivity main;
    private static BottomNavigationView navBar;
    private static NavigationView navigationView;
    private PowerManager.WakeLock partialWakeLock;
    private MainBinding binding;
    private int selectedTabId = 0;

    private DrawerLayout mDrawerLayout;
    private CharSequence mTitle;
    Toolbar toolbar;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        main = this;
        setTheme(R.style.Theme_OasisPlanner);
        super.onCreate(savedInstanceState);

        setupDatabase();
        setupUI();
        navigateTo(Pages.HOME);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            String notificationMode = extras.getString(NotificationModule.NOTIFICATION_MODE);

            if (NotificationMode.AGENDA.equals(NotificationMode.valueOf(notificationMode))) {
                navigateTo(Pages.EVENTARRANGER);

                long agendaId = extras.getLong(NotificationModule.NOTIFICATION_CONTENT);
                AppModule.retrieveAgendaUseCases().editAgendaUseCase.invoke(agendaId);
            }

            intent.removeExtra(NotificationModule.NOTIFICATION_MODE);
        } catch (Exception e) {}
    }

    /** setting up the database */
    private void setupDatabase() {
        // INFO: setup database and usecases
        AppDatabase db = AppModule.provideAppDatabase(getApplication());
        AlarmScheduler alarmScheduler = new AlarmScheduler(getApplicationContext());
        AgendaRepository agendaRepository = AppModule.provideAgendaRepository(db, alarmScheduler);
        AppModule.provideAgendaUseCases(agendaRepository)
                .editAgendaUseCase
                .setFragmentManager(getSupportFragmentManager());
        AppModule.provideAgendaUseCases(agendaRepository)
                .editAlarmListUseCase
                .setFragmentManager(getSupportFragmentManager());

        // INFO: setup alarms
        AlarmRepository alarmRepository = AppModule.provideAlarmRepository(db);
        alarmRepository.schedule(alarmScheduler);
    }

    void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        refreshToolbar();
    }
    void refreshToolbar() {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    void setupDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /** setting up the UI. */
    @SuppressLint("SourceLockedOrientationActivity")
    private void setupUI(){
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mDrawerLayout = binding.drawer;
        setupToolbar();
        setupDrawerToggle();

        navigationView = binding.navPane.navView;
        navigationView.setNavigationItemSelectedListener(this);
        NavigationMenuView navMenu = (NavigationMenuView) navigationView.getChildAt(0);
        navMenu.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(
                this, navController,
                new AppBarConfiguration.Builder(
                        Pages.sideNavList
                ).build());
        BottomNavigationView bar = findViewById(R.id.nav_view_portrait);
        bar.setOnItemSelectedListener((menuItem) -> {
            if (selectedTabId == menuItem.getItemId())
                return false;
            selectedTabId = menuItem.getItemId();
            navigateTo(selectedTabId);
            return true;
        });
        bar.setSelectedItemId(selectedTabId);

        navBar = bar;
    }

    private boolean activelyNavigating = false;
    public void navigateTo(Pages page) {
        if (activelyNavigating) return;
        activelyNavigating = true;
        @IdRes int bottomBarId = page.getNav(), sidebarId = page.getSideNav();
        navBar.setSelectedItemId(bottomBarId);
        navigationView.setCheckedItem(sidebarId);
        if (bottomBarId == Pages.EVENTARRANGER.getNav())
            EventArrangerFragment.currentPage = page;
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(bottomBarId);
        uiChangeWhileNavigatingTo(page.getSideNav());
        activelyNavigating = false;
    }
    public void navigateTo(@IdRes int itemId) {
        if (activelyNavigating) return;
        activelyNavigating = true;
        navBar.setSelectedItemId(itemId);
        navigationView.setCheckedItem(itemId);
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(itemId);
        uiChangeWhileNavigatingTo(itemId);
        activelyNavigating = false;
    }
    public void uiChangeWhileNavigatingTo(@IdRes int itemId) {
        boolean isHome = itemId == Pages.HOME.getSideNav();
        toolbar.setBackgroundColor(isHome ? Color.TRANSPARENT : Color.WHITE);
        binding.container.setBackgroundColor(isHome ? Color.WHITE : Resources.getColor(R.color.background));
    }

    public NavController getNavController() {
        return Navigation.findNavController(this, R.id.nav_host_fragment);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.navigation_home: navigateTo(Pages.HOME); break;
            case R.id.navigation_dashboard: navigateTo(Pages.DASHBOARD); break;
            case R.id.navigation_project: navigateTo(Pages.PROJECTS); break;
            case R.id.navigation_eventarranger: navigateTo(Pages.EVENTARRANGER); break;
            case R.id.navigation_memos: navigateTo(Pages.MEMOS); break;
            case R.id.navigation_analytics: navigateTo(Pages.ANALYTICS); break;
            case R.id.navigation_settings: navigateTo(Pages.SETTINGS); break;
        }

        mDrawerLayout.closeDrawers();

        return true;
    }
}