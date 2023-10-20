package com.aurora.oasisplanner.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.tags.Page;

public class MemosFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_memos, container, false);
        MainActivity.page = Page.MEMOS;
        return root;
    }

    public static Page currentPage = Page.MEMOS;
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