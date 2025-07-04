package com.aurora.oasisplanner.fragments;

import static android.view.View.VISIBLE;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.tags.Page;
import com.aurora.oasisplanner.databinding.ArrangerBinding;
import com.aurora.oasisplanner.databinding.ArrangerCalendarBinding;
import com.aurora.oasisplanner.databinding.ArrangerNotificationsBinding;
import com.aurora.oasisplanner.data.viewmodels.AlarmsViewModel;
import com.aurora.oasisplanner.presentation.panels.mainlists.AlarmsAdapter;
import com.aurora.oasisplanner.presentation.widgets.multidatepicker.MultiDatePicker;
import com.aurora.oasisplanner.presentation.widgets.multidatepicker.data.DateRange;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.util.Objects;

public class EventArrangerFragment extends Fragment {

    public static Page currentPage = Page.EVENTARRANGER;
    private AlarmsViewModel alarmsViewModel;
    private ArrangerBinding binding;
    private String searchEntry;
    private ViewDataBinding subpageBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = ArrangerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        goToPage(getId(currentPage));

        return root;
    }

    public void goToPage(int i) {
        switchToPage(i);
        uiChangeWhenNavigating();
    }

    private void initNotifSubfragment(ArrangerNotificationsBinding binding) {
        subpageBinding = binding;
        RecyclerView recyclerView = binding.boxList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        final AlarmsAdapter adapter = new AlarmsAdapter(recyclerView, true);
        recyclerView.setAdapter(adapter);

        alarmsViewModel = new ViewModelProvider(this).get(AlarmsViewModel.class);
        alarmsViewModel.refreshAlarms(searchEntry==null ? "" : searchEntry);
        alarmsViewModel.getAlarms().observe(
                getViewLifecycleOwner(),
                (_alarms) -> {
                    adapter.setAlarms(_alarms);
                    binding.textHome.setVisibility(adapter.getAlarmsCount() == 0 ? View.VISIBLE : View.INVISIBLE);
                }
        );

        binding.tagSearchTv.setOnKeyListener(
                (v, keyCode, event)->{
                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                        refreshSearchResults(adapter, binding);
                    }
                    return false;
                }
        );
        binding.tagSearchTil.setEndIconOnClickListener(
                (v)->{
                    binding.tagSearchTv.setText("");
                    refreshSearchResults(adapter, binding);
                }
        );
        binding.notifFragShowToday.setOnClickListener(
                (v)-> binding.boxList.scrollToPosition(0)
        );
        binding.notifFragPrevMo.setOnClickListener(
                (v)-> adapter.scrollToPrevMonth(binding.boxList)
        );
        binding.notifFragNextMo.setOnClickListener(
                (v)-> adapter.scrollToNextMonth(binding.boxList)
        );
        binding.boxList.addOnScrollListener(
                new RecyclerView.OnScrollListener(){
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState){
                        super.onScrollStateChanged(recyclerView, newState);

                        binding.notifFragChangeScroll.setAlpha(newState != SCROLL_STATE_IDLE ? 1.f : .75f);
                        binding.notifFragChangeScroll.setVisibility(
                                recyclerView.computeVerticalScrollOffset() != 0 ?
                                        View.VISIBLE : View.GONE);
                    }
                }
        );
    }

    private void initCalendarSubfragment(ArrangerCalendarBinding binding) {
        subpageBinding = binding;

        // Preparation
        LocalDate date = LocalDate.now();

        // List of Events
        RecyclerView recyclerView = binding.boxList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        final AlarmsAdapter adapter = new AlarmsAdapter(recyclerView, false);
        adapter.monthIsOn = false;
        recyclerView.setAdapter(adapter);

        alarmsViewModel = new ViewModelProvider(this).get(AlarmsViewModel.class);
        alarmsViewModel.refreshAlarmsBetween("", new DateRange().setPoint(date));
        alarmsViewModel.getAlarms().observe(getViewLifecycleOwner(), adapter::setAlarms);

        // Date Picker
        MultiDatePicker picker = binding.picker;
        picker.minDateAllowed = LocalDate.now();
        picker.setMonth(date.getYear(), date.getMonthValue());
        picker.setOnUpdateListener(
                (_picker)-> {
                    DateRange selected = _picker.getRangeSelected();
                    refreshSearchResultsWithDateRange(adapter, selected);
                }
        );
        picker.refresh();
    }

    private void switchToPage(int i) {
        MainActivity activity = (MainActivity) getActivity();
        assert activity != null;
        switch (i) {
            case 0: navigateTo(Page.DASHBOARD); break;
            case 1: navigateTo(Page.EVENTARRANGER); break;
        }
    }

    private void navigateTo(Page page) {
        binding.navHostFragmentNotification.removeAllViews();
        switch (page) {
            case DASHBOARD:
                MainActivity.page = Page.DASHBOARD;
                currentPage = Page.DASHBOARD;
                ArrangerCalendarBinding cbinding = ArrangerCalendarBinding.inflate(getLayoutInflater(), binding.navHostFragmentNotification, false);
                initCalendarSubfragment(cbinding);
                binding.navHostFragmentNotification.addView(cbinding.getRoot());
                break;
            case EVENTARRANGER:
                MainActivity.page = Page.EVENTARRANGER;
                currentPage = Page.EVENTARRANGER;
                ArrangerNotificationsBinding nbinding = ArrangerNotificationsBinding.inflate(getLayoutInflater(), binding.navHostFragmentNotification, false);
                initNotifSubfragment(nbinding);
                binding.navHostFragmentNotification.addView(nbinding.getRoot());
                break;
        }
    }
    private int getId(Page page) {
        switch (page) {
            case DASHBOARD:
                return 0;
            case EVENTARRANGER:
                return 1;
        }
        return -1;
    }

    private void refreshSearchResults(AlarmsAdapter adapter, ArrangerNotificationsBinding nbinding) {
        String str = Objects.requireNonNull(nbinding.tagSearchTv.getText()).toString();
        alarmsViewModel.refreshAlarms(str);
        searchEntry = str;
        refreshDisplayResults(adapter, nbinding);
        nbinding.textHome.setText(searchEntry.isEmpty() ? R.string.tips_no_agendas : R.string.tips_memo_not_found);
    }
    private void refreshSearchResultsWithDateRange(AlarmsAdapter adapter, DateRange dateRange) {
        alarmsViewModel.refreshAlarmsBetween("", dateRange); //TODO: add search function?
        refreshDisplayResults(adapter, null);
    }
    private void refreshDisplayResults(AlarmsAdapter adapter, ArrangerNotificationsBinding nbinding) {
        alarmsViewModel.getAlarms().observe(
                getViewLifecycleOwner(),
                (_alarms) -> {
                    adapter.setAlarms(_alarms);
                    if (nbinding != null)
                        nbinding.textHome.setVisibility(adapter.getAlarmsCount() == 0 ? View.VISIBLE : View.INVISIBLE);
                }
            );
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

    // INFO: Options Menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        menu.clear();
        if (currentPage == Page.EVENTARRANGER)
            inflater.inflate(R.menu.event_arranger_menu, menu);
        else
            inflater.inflate(R.menu.event_arranger_calendar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.eventArranger_search:
                assert currentPage.equals(Page.EVENTARRANGER);
                ArrangerNotificationsBinding nbinding = (ArrangerNotificationsBinding) subpageBinding;
                TextInputLayout til = nbinding.tagSearchTil;
                til.setVisibility(til.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                item.setIcon(til.getVisibility() == VISIBLE ? R.drawable.ic_search_contract : R.drawable.ic_search);
                break;
            case R.id.eventArranger_calendar:
                goToPage(0);
                requireActivity().invalidateOptionsMenu();
                break;
            case R.id.eventArranger_listView:
                goToPage(1);
                requireActivity().invalidateOptionsMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}