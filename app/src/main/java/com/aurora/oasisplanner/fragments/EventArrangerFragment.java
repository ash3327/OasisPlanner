package com.aurora.oasisplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.tags.Pages;
import com.aurora.oasisplanner.databinding.NotificationsBinding;
import com.aurora.oasisplanner.presentation.ui.alarms.AlarmsViewModel;
import com.aurora.oasisplanner.presentation.ui.alarms.components.AlarmsAdapter;

public class EventArrangerFragment extends Fragment {

    private AlarmsViewModel alarmsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsBinding binding = NotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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

        return root;
    }
}