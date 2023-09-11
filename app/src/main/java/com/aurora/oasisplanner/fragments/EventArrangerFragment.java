package com.aurora.oasisplanner.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.tags.Pages;
import com.aurora.oasisplanner.databinding.ArrangerBinding;
import com.aurora.oasisplanner.databinding.ArrangerCalendarBinding;
import com.aurora.oasisplanner.databinding.ArrangerNotificationsBinding;
import com.aurora.oasisplanner.databinding.TabMenuBinding;
import com.aurora.oasisplanner.presentation.ui.alarms.AlarmsViewModel;
import com.aurora.oasisplanner.presentation.ui.alarms.components.AlarmsAdapter;
import com.aurora.oasisplanner.presentation.widget.multidatepicker.MultiDatePicker;
import com.aurora.oasisplanner.presentation.widget.tabselector.TabMenu;
import com.aurora.oasisplanner.util.styling.Resources;

import java.time.LocalDate;
import java.util.Arrays;

public class EventArrangerFragment extends Fragment {

    public static Pages currentPage = Pages.EVENTARRANGER;
    private AlarmsViewModel alarmsViewModel;
    private ArrangerBinding binding;
    private TabMenu tabMenu;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = ArrangerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tabMenu = binding.tabSelector;
        tabMenu.createOptionMenu(
                getId(currentPage),
                Arrays.asList(
                        new TabMenu.MenuItem(Resources.getString(R.string.tag_dashboard)),
                        new TabMenu.MenuItem(Resources.getString(R.string.tag_notification))
                ),
                (i, menu, vbinding)->{
                    switchPageAnimation(i, vbinding);
                    switchToPage(i);
                    uiChangeWhenNavigating();
                }
        );

        return root;
    }

    private void initNotifSubfragment(ArrangerNotificationsBinding binding) {
        RecyclerView recyclerView = binding.boxList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        final AlarmsAdapter adapter = new AlarmsAdapter(recyclerView);
        adapter.setOnChangeListener(
                (size)-> binding.textHome.setVisibility(size == 0 ? View.VISIBLE : View.INVISIBLE)
        );
        recyclerView.setAdapter(adapter);

        alarmsViewModel = new ViewModelProvider(this).get(AlarmsViewModel.class);
        alarmsViewModel.getAlarms().observe(getViewLifecycleOwner(), adapter::setAlarms);
    }

    private void initCalendarSubfragment(ArrangerCalendarBinding binding) {
        MultiDatePicker picker = binding.picker;
        picker.minDateAllowed = LocalDate.now();
        LocalDate date = LocalDate.now();
        picker.setMonth(date.getYear(), date.getMonthValue());
        picker.refresh();
    }

    private void switchPageAnimation(int i, TabMenuBinding vbinding) {
        String[] colors = new String[]{
                "#FF0062EE", "#FFEE9337"
        };
        vbinding.selectContent.getBackground().setColorFilter(Color.parseColor(
                colors[i]
        ), PorterDuff.Mode.SRC_IN);
    }

    public void switchToPage(int i) {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        switch (i) {
            case 0: navigateTo(Pages.DASHBOARD); break;
            case 1: navigateTo(Pages.EVENTARRANGER); break;
        }
    }

    private void navigateTo(Pages page) {
        binding.navHostFragment.removeAllViews();
        switch (page) {
            case DASHBOARD:
                currentPage = Pages.DASHBOARD;
                ArrangerCalendarBinding cbinding = ArrangerCalendarBinding.inflate(getLayoutInflater(), binding.navHostFragment, false);
                initCalendarSubfragment(cbinding);
                binding.navHostFragment.addView(cbinding.getRoot());
                break;
            case EVENTARRANGER:
                currentPage = Pages.EVENTARRANGER;
                ArrangerNotificationsBinding nbinding = ArrangerNotificationsBinding.inflate(getLayoutInflater(), binding.navHostFragment, false);
                initNotifSubfragment(nbinding);
                binding.navHostFragment.addView(nbinding.getRoot());
                break;
        }
    }
    private int getId(Pages page) {
        switch (page) {
            case DASHBOARD:
                return 0;
            case EVENTARRANGER:
                return 1;
        }
        return -1;
    }

    @Override
    public void onResume() {
        super.onResume();
        uiChangeWhenNavigating();
    }

    private void uiChangeWhenNavigating() {
        // ensuring consistent ui when the "go back to last page" button is clicked.
        MainActivity activity = (MainActivity) requireActivity();
        activity.navBarChangeWhileNavigatingTo(currentPage.getNav(), currentPage.getSideNav());
        activity.uiChangeWhileNavigatingTo(currentPage.getSideNav());
    }
}