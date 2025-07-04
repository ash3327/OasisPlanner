package com.aurora.oasisplanner.presentation.dialogs.memoeditdialog;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.memos._Memo;
import com.aurora.oasisplanner.databinding.MemoPageBinding;
import com.aurora.oasisplanner.presentation.widgets.taginputeidittext.TagInputEditText;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.Objects;

public class MemoEditDialog extends Fragment {
    public static final String EXTRA_MEMO_ID = "memoId";

    private _Memo memo;
    private MemoPageBinding vbinding;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        assert getArguments() != null;

        setHasOptionsMenu(true);
        MainActivity activity = (MainActivity) requireActivity();
        MainActivity.bottomBar.setVisibility(View.GONE);
        activity.setDrawerLocked(true);
        activity.mDrawerToggle.setDrawerIndicatorEnabled(false);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        activity.mDrawerToggle.setToolbarNavigationClickListener((v)-> showCancelConfirmDialog());

        activity.getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        showCancelConfirmDialog();
                    }
                });

        long memoId = getArguments().getLong(EXTRA_MEMO_ID, -1);
        if (memoId != -1)
            memo = AppModule.retrieveMemoUseCases().get(memoId);
        else
            memo = _Memo.empty();

        MemoPageBinding binding = MemoPageBinding.inflate(getLayoutInflater());
        onBind(binding);

        return binding.getRoot();
    }

    public void onBind(MemoPageBinding binding) {
        vbinding = binding;
        requireActivity().setTitle(memo.id <= 0 ? R.string.page_overhead_new_memo : R.string.page_overhead_edit_agenda);

        binding.img.setImageResource(R.drawable.blur_v1);
        binding.memoConfirmEdit.setOnClickListener(
                (v)->onConfirm()
        );

        associateTitle(binding.pageTitle);
        binding.pageTitle.setOnKeyListener((v, keyCode, event)->keyCode == KeyEvent.KEYCODE_ENTER);
        binding.tagContentTv.setText(memo.contents);
        binding.tagTagsTv.setText(memo.getTagsString());
        binding.deleteButton.setVisibility(memo.id == -1 ? View.GONE : View.VISIBLE);
        binding.deleteButton.setOnClickListener((v)->onDelete());
    }

    public void scrollTo(int pos, RecyclerView recyclerView) {
        Context rContext = recyclerView.getContext();
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(rContext){
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(pos > 0 ? pos-1 : 0);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null)
            layoutManager.startSmoothScroll(smoothScroller);
    }

    public void associateTitle(EditText editText) {
        editText.setText(memo.title);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                memo.title = new SpannableStringBuilder(editText.getText().toString());
            }
        };
        editText.setTag(textWatcher);
        editText.addTextChangedListener(textWatcher);
    }

    public void onConfirm() {
        if (memo.title.toString().isEmpty()) {
            Toast.makeText(getContext(), R.string.page_no_title_warning, Toast.LENGTH_SHORT).show();
            return;
        }
        if (saveMemo())
            navigateUp();
    }
    public void onCancel() {

    }
    public void onDiscard() {
        navigateUp();
    }
    public void onDelete() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.memo_delete)
                .setMessage(R.string.memo_delete_confirm)
                .setIcon(R.drawable.ic_symb_cross)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    deleteMemo();
                    navigateUp();
                })
                .setNegativeButton(android.R.string.no, null).show();
    }
    public void showCancelConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.page_overhead_edit_memo)
                .setMessage(R.string.save_change)
                .setPositiveButton(R.string.page_confirm, (dialog, whichButton) -> {
                    onConfirm();
                })
                .setNegativeButton(R.string.page_cancel, (dialog, which) -> {
                    onCancel();
                })
                .setNeutralButton(R.string.discard, ((dialog, which) -> {
                    onDiscard();
                })).show();
    }

    public boolean saveMemo() {
        TextInputEditText tiet = vbinding.tagContentTv;
        TagInputEditText tagtiet = vbinding.tagTagsTv;
        SpannableStringBuilder ssb = new SpannableStringBuilder(tiet.getText());
        ssb.clearSpans();
        if (ssb.length() == 0) {
            Toast.makeText(getContext(), R.string.tab_no_content_warning, Toast.LENGTH_SHORT).show();
            return false;
        }
        memo.contents = ssb;
        memo.tags = Arrays.asList(Objects.requireNonNull(tagtiet.getText()).toString().split(TagInputEditText.SEP));
        AppModule.retrieveMemoUseCases().put(memo);
        return true;
    }

    public boolean deleteMemo() {
        AppModule.retrieveMemoUseCases().delete(memo);
        return true;
    }

    private void navigateUp() {
        Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).navigateUp();
    }

    // INFO: Options Menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.edit_agenda_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onDestroy() {
        MainActivity.bottomBar.setVisibility(View.VISIBLE);
        MainActivity activity = MainActivity.main;
        activity.setDrawerLocked(false);
        activity.mDrawerToggle.setDrawerIndicatorEnabled(true);
        activity.refreshToolbar();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showCancelConfirmDialog();
                return true;
            case R.id.editAgenda_discard:
                onDiscard();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

