package com.aurora.oasisplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.tags.Pages;
import com.aurora.oasisplanner.databinding.HomeBinding;

import java.time.LocalDate;

public class HomeFragment extends Fragment {
    private HomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        binding.setDate(LocalDate.now());

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        binding.setDate(LocalDate.now());
        super.onResume();

        // ensuring consistent ui when the "go back to last page" button is clicked.
        MainActivity activity = (MainActivity) requireActivity();
        activity.uiChangeWhileNavigatingTo(Pages.HOME.getSideNav());
    }
}