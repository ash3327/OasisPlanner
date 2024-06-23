package com.aurora.oasisplanner.fragments;

import static android.view.View.VISIBLE;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.tags.Page;
import com.aurora.oasisplanner.databinding.FragmentProjectBinding;
import com.aurora.oasisplanner.databinding.SubfragmentAnalyticsBinding;
import com.aurora.oasisplanner.databinding.SubfragmentProjectBinding;
import com.aurora.oasisplanner.databinding.TabMenuBinding;
import com.aurora.oasisplanner.presentation.widgets.tabselector.TabMenu;
import com.google.android.material.textfield.TextInputLayout;

public class ProjectsFragment extends Fragment {

    public static Page currentPage = Page.PROJECTS;
    private FragmentProjectBinding binding;
    private TabMenu tabMenu;
    private ViewBinding subpageBinding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProjectBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        goToPage(getId(currentPage));

        return root;
    }

    public void goToPage(int i) {
        switchToPage(i);
        uiChangeWhenNavigating();
    }

    private void initAnalyticsSubfragment(SubfragmentAnalyticsBinding binding) {
        subpageBinding = binding;
    }

    private void initProjectSubfragment(SubfragmentProjectBinding binding) {
        subpageBinding = binding;
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
            case 0: navigateTo(Page.PROJECTS); break;
            case 1: navigateTo(Page.ANALYTICS); break;
        }
    }

    private void navigateTo(Page page) {
        binding.navHostFragmentProject.removeAllViews();
        switch (page) {
            case PROJECTS:
                MainActivity.page = Page.PROJECTS;
                currentPage = Page.PROJECTS;
                SubfragmentProjectBinding cbinding = SubfragmentProjectBinding.inflate(getLayoutInflater(), binding.navHostFragmentProject, false);
                initProjectSubfragment(cbinding);
                binding.navHostFragmentProject.addView(cbinding.getRoot());
                break;
            case ANALYTICS:
                MainActivity.page = Page.ANALYTICS;
                currentPage = Page.ANALYTICS;
                SubfragmentAnalyticsBinding nbinding = SubfragmentAnalyticsBinding.inflate(getLayoutInflater(), binding.navHostFragmentProject, false);
                initAnalyticsSubfragment(nbinding);
                binding.navHostFragmentProject.addView(nbinding.getRoot());
                break;
        }
    }
    private int getId(Page page) {
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

    // INFO: Options Menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        menu.clear();
        if (currentPage == Page.PROJECTS)
            inflater.inflate(R.menu.projects_menu, menu);
        else
            inflater.inflate(R.menu.projects_analysis_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.projects_search:
                assert currentPage.equals(Page.PROJECTS);
                SubfragmentProjectBinding nbinding = (SubfragmentProjectBinding) subpageBinding;
                TextInputLayout til = nbinding.tagSearchTil;
                til.setVisibility(til.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                item.setIcon(til.getVisibility() == VISIBLE ? R.drawable.ic_search_contract : R.drawable.ic_search);
                break;
            case R.id.projects_analytics:
                goToPage(1);
                requireActivity().invalidateOptionsMenu();
                break;
            case R.id.projects_listView:
                goToPage(0);
                requireActivity().invalidateOptionsMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}