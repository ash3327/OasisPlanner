package com.aurora.oasisplanner.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.pojo.events.Alarm;
import com.aurora.oasisplanner.data.tags.Page;
import com.aurora.oasisplanner.databinding.FragmentSettingsBinding;
import com.aurora.oasisplanner.util.Configs;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmNotificationService;
import com.aurora.oasisplanner.util.notificationfeatures.NotificationHelperTest;
import com.aurora.oasisplanner.util.permissions.Permissions;
import com.aurora.oasisplanner.util.permissions.Utils;
import com.aurora.oasisplanner.util.styling.Resources;
import com.aurora.oasisplanner.util.styling.Styles;

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

        ListView listView_dev = binding.settingsListViewDev;
        ArrayAdapter<String> adapter_dev = new ArrayAdapter<>(
                this.requireContext(),
                R.layout.settings_element
        );
        adapter_dev.addAll(Resources.getStringArr(R.array.settings_dev));
        listView_dev.setAdapter(adapter_dev);
        listView_dev.setOnItemClickListener((p, v, pos, i)->handle_dev(pos));

        return binding.getRoot();
    }

    public void handle(int pos) {
        switch (pos) {
            case 0:
                Permissions.requestPermissions(requireActivity(), null, false);
                break;
            case 1:
                Configs.clickSoundIsOn = !Configs.clickSoundIsOn;
                String notifText = Resources.getString(R.string.settings_notifClickSound);
                Toast.makeText(getContext(), Styles.substituteText(notifText, Configs.clickSoundIsOn), Toast.LENGTH_SHORT).show();
                break;
            case 2:
                AppModule.forceUpdateAlarms(this);
                break;
        }
    }

    public void handle_dev(int pos) {
        switch (pos) {
            case 0:
                AppModule.provideExecutor().execute(()->{
                    Alarm alarm = AppModule.retrieveAlarmUseCases().getAlarms().getValue().get(0);
                    Log.d("test3", alarm+"");
                    AlarmNotificationService.notify(requireContext(), alarm);
                });
                NotificationHelperTest.showNotification(requireContext(), "TITLE", "MSG");
                break;
            case 1:
                Utils.setSkipRequestPermission(requireContext(), false);
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