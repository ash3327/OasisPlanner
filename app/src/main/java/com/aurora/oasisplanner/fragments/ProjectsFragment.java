package com.aurora.oasisplanner.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
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
import com.aurora.oasisplanner.databinding.ArrangerCalendarBinding;
import com.aurora.oasisplanner.databinding.ArrangerNotificationsBinding;
import com.aurora.oasisplanner.databinding.FragmentProjectBinding;
import com.aurora.oasisplanner.databinding.SubfragmentAnalyticsBinding;
import com.aurora.oasisplanner.databinding.SubfragmentProjectBinding;
import com.aurora.oasisplanner.databinding.TabMenuBinding;
import com.aurora.oasisplanner.presentation.ui.alarms.AlarmsViewModel;
import com.aurora.oasisplanner.presentation.ui.alarms.components.AlarmsAdapter;
import com.aurora.oasisplanner.presentation.widget.tabselector.TabMenu;
import com.aurora.oasisplanner.util.styling.Resources;

import java.util.Arrays;

public class ProjectsFragment extends Fragment {

    public static Pages currentPage = Pages.PROJECTS;
    private FragmentProjectBinding binding;
    private TabMenu tabMenu;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProjectBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tabMenu = binding.tabSelector;
        tabMenu.createOptionMenu(
                getId(currentPage),
                Arrays.asList(
                        new TabMenu.MenuItem(Resources.getString(R.string.tag_project)),
                        new TabMenu.MenuItem(Resources.getString(R.string.tag_analytics))
                ),
                (i, menu, vbinding)->{
                    switchPageAnimation(i, vbinding);
                    switchToPage(i);
                    uiChangeWhenNavigating();
                }
        );

        return root;
    }

    private void initAnalyticsSubfragment(SubfragmentAnalyticsBinding binding) {

    }

    private void initProjectSubfragment(SubfragmentProjectBinding binding) {

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
            case 0: navigateTo(Pages.PROJECTS); break;
            case 1: navigateTo(Pages.ANALYTICS); break;
        }
    }

    private void navigateTo(Pages page) {
        binding.navHostFragment.removeAllViews();
        switch (page) {
            case PROJECTS:
                currentPage = Pages.PROJECTS;
                SubfragmentProjectBinding cbinding = SubfragmentProjectBinding.inflate(getLayoutInflater(), binding.navHostFragment, false);
                initProjectSubfragment(cbinding);
                binding.navHostFragment.addView(cbinding.getRoot());
                break;
            case ANALYTICS:
                currentPage = Pages.ANALYTICS;
                SubfragmentAnalyticsBinding nbinding = SubfragmentAnalyticsBinding.inflate(getLayoutInflater(), binding.navHostFragment, false);
                initAnalyticsSubfragment(nbinding);
                binding.navHostFragment.addView(nbinding.getRoot());
                break;
        }
    }
    private int getId(Pages page) {
        switch (page) {
            case PROJECTS:
                return 0;
            case ANALYTICS:
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