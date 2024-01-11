package com.aurora.oasisplanner.presentation.dialog.agendaeditdialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.model.pojo.Agenda;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.tags.Page;
import com.aurora.oasisplanner.databinding.PageBinding;
import com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components.SectionAdapter;

public class AgendaEditDialog extends Fragment {
    public static final String EXTRA_AGENDA_ID = "agendaId";
    public static final String EXTRA_ACTIVL_ID = "activityLId";

    private Agenda agenda;
    private long activityLId;

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

        long agendaId = getArguments().getLong(EXTRA_AGENDA_ID, -1);
        activityLId = getArguments().getLong(EXTRA_ACTIVL_ID, -1);
        if (agendaId != -1)
            agenda = AppModule.retrieveAgendaUseCases().get(agendaId);
        else
            agenda = Agenda.empty();

        PageBinding binding = PageBinding.inflate(getLayoutInflater());
        onBind(binding);

        return binding.getRoot();
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

    public void onBind(PageBinding binding) {
        //binding.header.setText(agenda.agenda.id <= 0 ? R.string.page_overhead_new_agenda : R.string.page_overhead_edit_agenda);
        binding.img.setImageResource(R.drawable.blur_v1);
        binding.agendaConfirmEdit.setOnClickListener(
                (v)->onConfirm()
        );
        /*binding.cancelButton.setOnClickListener(
                (v)->onCancel()
        );//*/

        associateTitle(binding.pageTitle);
        binding.pageTitle.setOnKeyListener((v, keyCode, event)->keyCode == KeyEvent.KEYCODE_ENTER);

        RecyclerView recyclerView = binding.pageSections;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);

        final SectionAdapter adapter = new SectionAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setScrollToFunc((oid, id)-> scrollTo(id, recyclerView));
        adapter.setBinaryLabel(
                new SectionAdapter.Label(
                        binding.pageYellowLabel,
                        binding.pageYellowLabelText,
                        binding.pageYellowLabelIcon
                ),
                new SectionAdapter.Label(
                        binding.pageYellowLabel2,
                        binding.pageYellowLabel2Text,
                        binding.pageYellowLabel2Icon
                )
        );
        int expandId = adapter.setAgenda(agenda, activityLId);
        recyclerView.post(()-> scrollTo(expandId, recyclerView));
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
        editText.setText(agenda.agenda.title);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                agenda.agenda.title = editText.getText().toString();
            }
        };
        editText.setTag(textWatcher);
        editText.addTextChangedListener(textWatcher);
    }

    public void onConfirm() {
        if (agenda.agenda.title.isEmpty()) {
            Toast.makeText(getContext(), R.string.page_no_title_warning, Toast.LENGTH_SHORT).show();
            return;
        }
        saveAgenda();
        navigateUp();
    }
    public void onCancel() {

    }
    public void onDiscard() {
        navigateUp();
    }
    public void showCancelConfirmDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.page_overhead_edit_agenda)
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

    public void saveAgenda() {
        AppModule.retrieveAgendaUseCases().put(agenda);
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

