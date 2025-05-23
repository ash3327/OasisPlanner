package com.aurora.oasisplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.tags.Page;
import com.aurora.oasisplanner.databinding.HomeBinding;
import com.aurora.oasisplanner.data.viewmodels.AlarmsViewModel;
import com.aurora.oasisplanner.presentation.panels.homepage.HomeAlarmsAdapter;
import com.aurora.oasisplanner.presentation.widgets.multidatepicker.data.DateRange;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class HomeFragment extends Fragment {
    private HomeBinding binding;
    private AlarmsViewModel alarmsViewModel, alarmsViewModel2;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        MainActivity.page = Page.HOME;

        binding.setDate(LocalDate.now());
        LocalDateTime now = LocalDateTime.now();

        // Your Schedules
        RecyclerView recyclerView = binding.recommendationListSchedule;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setHasFixedSize(true);

        final HomeAlarmsAdapter adapter = new HomeAlarmsAdapter(recyclerView, false, false);
        adapter.monthIsOn = false;
        recyclerView.setAdapter(adapter);

        alarmsViewModel = new ViewModelProvider(this).get(AlarmsViewModel.class);
        alarmsViewModel.refreshAlarmsBetween("", now, now.plusDays(1));
        alarmsViewModel.getAlarms().observe(
                getViewLifecycleOwner(),
                (_alarms)->{
                    adapter.setAlarms(_alarms);
                    binding.homeScheduleWarning.setVisibility(adapter.getAlarmsCount()==0 ? View.VISIBLE : View.GONE);
                }
        );

        // Your Tasks
        // TODO: FUTURE: INCLUDE TASKS THAT DID NOT HAVE A FIXED TIME - OR THAT IS NOW IN WARN PERIOD
        RecyclerView recyclerView2 = binding.recommendationList;
        recyclerView2.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView2.setHasFixedSize(true);

        final HomeAlarmsAdapter adapter2 = new HomeAlarmsAdapter(recyclerView2, false, true);
        adapter2.monthIsOn = false;
        recyclerView2.setAdapter(adapter2);

        alarmsViewModel2 = new ViewModelProvider(this).get(AlarmsViewModel.class);
        alarmsViewModel2.refreshAlarmsBetween("", now, now.plusDays(7));
        alarmsViewModel2.getAlarms().observe(
                getViewLifecycleOwner(),
                (_alarms)->{
                    adapter2.setAlarms(_alarms);
                    binding.homeRecommendationWarning.setVisibility(adapter2.getAlarmsCount()==0 ? View.VISIBLE : View.GONE);
                }
        );

        return binding.getRoot();
    }

    public static Page currentPage = Page.HOME;
    @Override
    public void onResume() {
        binding.setDate(LocalDate.now());
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