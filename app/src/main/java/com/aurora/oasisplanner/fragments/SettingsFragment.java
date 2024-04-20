package com.aurora.oasisplanner.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ListAdapter;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.OasisApp;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.tags.Page;
import com.aurora.oasisplanner.databinding.FragmentSettingsBinding;
import com.aurora.oasisplanner.util.permissions.Permissions;
import com.aurora.oasisplanner.util.styling.Resources;

public class SettingsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentSettingsBinding binding = FragmentSettingsBinding.inflate(getLayoutInflater(), container, false);
        //View root = inflater.inflate(R.layout.fragment_settings, container, false);
        MainActivity.page = Page.SETTINGS;

        ListView listView = binding.settingsListView;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this.requireContext(),
                R.layout.settings_element
        );
        adapter.addAll(Resources.getStringArr(R.array.settings));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((p, v, pos, i)->handle(pos));

        return binding.getRoot();
    }

    public void handle(int pos) {
        switch (pos) {
            case 0:
                Permissions.requestProtectedAppsPermission(requireActivity(), null, false);
                break;
            case 1:
                AppModule.forceUpdateAlarms(this);
                break;
        }
    }

    public static Page currentPage = Page.SETTINGS;
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