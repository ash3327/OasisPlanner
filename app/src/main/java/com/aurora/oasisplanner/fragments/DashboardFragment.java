package com.aurora.oasisplanner.fragments;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.tags.Pages;
import com.aurora.oasisplanner.databinding.CalendarBinding;
import com.aurora.oasisplanner.databinding.NotificationsBinding;
import com.aurora.oasisplanner.databinding.TabMenuBinding;
import com.aurora.oasisplanner.presentation.widget.tabselector.TabMenu;
import com.aurora.oasisplanner.util.styling.Resources;

import java.util.Arrays;
import java.util.Calendar;

public class DashboardFragment extends Fragment {
    private TabMenu tabMenu;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CalendarBinding binding = CalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        tabMenu = binding.tabSelector;
        tabMenu.createOptionMenu(
                0,
                Arrays.asList(
                        new TabMenu.MenuItem(Resources.getString(R.string.tag_dashboard)),
                        new TabMenu.MenuItem(Resources.getString(R.string.tag_notification))
                ),
                (i, menu, vbinding)->{
                    switchPageAnimation(i, vbinding);
                    if (i == 0) return;
                    switchToPage(i);
                }
        );

        return root;
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
            case 0: activity.navigateTo(Pages.DASHBOARD); break;
            case 1: activity.navigateTo(Pages.EVENTARRANGER); break;
        }
    }
}