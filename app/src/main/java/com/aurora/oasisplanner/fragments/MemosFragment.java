package com.aurora.oasisplanner.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.tags.Page;
import com.aurora.oasisplanner.databinding.MemoFragmentBinding;
import com.aurora.oasisplanner.presentation.ui.memos.MemoViewModel;
import com.aurora.oasisplanner.presentation.ui.memos.components.MemosAdapter;

import java.util.Objects;

public class MemosFragment extends Fragment {

    private MemoFragmentBinding binding;
    private MemoViewModel memosViewModel;
    private String searchEntry = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = MemoFragmentBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        navigate();
        return root;
    }

    public static Page currentPage = Page.MEMOS;

    public void navigate() {
        MainActivity.page = Page.MEMOS;
        currentPage = Page.MEMOS;

        RecyclerView recyclerView = binding.boxList;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        final MemosAdapter adapter = new MemosAdapter(recyclerView);
        adapter.setOnChangeListener(
                (size)-> binding.textHome.setVisibility(size == 0 ? View.VISIBLE : View.INVISIBLE)
        );
        recyclerView.setAdapter(adapter);

        memosViewModel = new ViewModelProvider(this).get(MemoViewModel.class);
        memosViewModel.refreshMemos(searchEntry==null ? "" : searchEntry);
        memosViewModel.getMemos().observe(getViewLifecycleOwner(), adapter::setMemos);

        binding.tagSearchTv.setOnKeyListener(
                (v, keyCode, event)->{
                    if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                        refreshSearchResults(adapter);
                    }
                    return false;
                }
        );
        binding.tagSearchTil.setEndIconOnClickListener(
                (v)->{
                    binding.tagSearchTv.setText("");
                    refreshSearchResults(adapter);
                }
        );
    }

    private void refreshSearchResults(MemosAdapter adapter) {
        String str = Objects.requireNonNull(binding.tagSearchTv.getText()).toString();
        memosViewModel.refreshMemos(str);
        memosViewModel.getMemos().observe(getViewLifecycleOwner(), adapter::setMemos);
        searchEntry = str;
        binding.textHome.setText(searchEntry.isEmpty() ? R.string.tips_no_memos : R.string.tips_memo_not_found);
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