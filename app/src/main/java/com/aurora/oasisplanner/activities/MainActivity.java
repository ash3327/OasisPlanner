package com.aurora.oasisplanner.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
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
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.tags.Page;
import com.aurora.oasisplanner.databinding.MainBinding;
import com.aurora.oasisplanner.fragments.EventArrangerFragment;
import com.aurora.oasisplanner.fragments.ProjectsFragment;
import com.aurora.oasisplanner.util.notificationfeatures.NotificationModule;
import com.aurora.oasisplanner.util.notificationfeatures.NotificationModule.NotificationMode;
import com.aurora.oasisplanner.util.styling.Resources;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static MainActivity main;
    public static Page page;
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
        navigateTo(Page.HOME);
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
                navigateTo(Page.EVENTARRANGER);

                long agendaId = extras.getLong(NotificationModule.NOTIFICATION_CONTENT);
                long activityLId = extras.getLong(NotificationModule.NOTIFICATION_ACTIVITY);
                AppModule.retrieveAgendaUseCases().editAgendaUseCase.invoke(agendaId, activityLId);
            }

            intent.removeExtra(NotificationModule.NOTIFICATION_MODE);
        } catch (Exception e) {}
    }

    /** setting up the database */
    private void setupDatabase() {
        // INFO: setup database and usecases
        AppModule.retrieveAgendaUseCases()
                .editAgendaUseCase
                .setFragmentManager(getSupportFragmentManager());
        AppModule.retrieveAgendaUseCases()
                .editAlarmListUseCase
                .setFragmentManager(getSupportFragmentManager());
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
                        Page.sideNavList
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
    public void navigateTo(Page page) {
        if (activelyNavigating) return;
        activelyNavigating = true;
        @IdRes int bottomBarId = page.getNav();
        if (bottomBarId == Page.EVENTARRANGER.getNav())
            EventArrangerFragment.currentPage = page;
        if (bottomBarId == Page.PROJECTS.getNav())
            ProjectsFragment.currentPage = page;
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(bottomBarId);
        uiChangeWhileNavigatingTo(page.getSideNav());
        activelyNavigating = false;
    }
    public void navigateTo(@IdRes int itemId) {
        if (activelyNavigating) return;
        activelyNavigating = true;
        Navigation.findNavController(this, R.id.nav_host_fragment).navigate(itemId);
        uiChangeWhileNavigatingTo(itemId);
        activelyNavigating = false;
    }
    public void uiChangeWhileNavigatingTo(@IdRes int itemId) {
        boolean isHome = itemId == Page.HOME.getSideNav();
        toolbar.setBackgroundColor(isHome ? Color.TRANSPARENT : Color.WHITE);
        binding.container.setBackgroundColor(isHome ? Color.WHITE : Resources.getColor(R.color.background));
    }
    public void navBarChangeWhileNavigatingTo(@IdRes int bottomBarId, @IdRes int sidebarId) {
        navBar.setSelectedItemId(bottomBarId);
        navigationView.setCheckedItem(sidebarId);
    }

    public NavController getNavController() {
        return Navigation.findNavController(this, R.id.nav_host_fragment);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()) {
            case R.id.navigation_home: navigateTo(Page.HOME); break;
            case R.id.navigation_dashboard: navigateTo(Page.DASHBOARD); break;
            case R.id.navigation_project: navigateTo(Page.PROJECTS); break;
            case R.id.navigation_eventarranger: navigateTo(Page.EVENTARRANGER); break;
            case R.id.navigation_memos: navigateTo(Page.MEMOS); break;
            case R.id.navigation_analytics: navigateTo(Page.ANALYTICS); break;
            case R.id.navigation_settings: navigateTo(Page.SETTINGS); break;
        }

        mDrawerLayout.closeDrawers();

        return true;
    }
}