package com.aurora.oasisplanner.presentation.dialog.agendaeditdialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.util.Switch;
import com.aurora.oasisplanner.databinding.PageBinding;
import com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components.SectionAdapter;
import com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components.SectionItemAdapter;
import com.aurora.oasisplanner.presentation.widget.taginputeidittext.TagInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class AgendaEditDialog extends Fragment {
    public static final String EXTRA_AGENDA_ID = "agendaId";
    public static final String EXTRA_ACTIVL_ID = "activityLId";

    private Agenda agenda;
    private long activityLId;

    public static final int NO_ACTIVITY_SELECTED = -2;
    private List<_Activity> selected = new ArrayList<>();
    private PageBinding binding;

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

        binding = PageBinding.inflate(getLayoutInflater());

        AppModule.provideExecutor().submit(()->{
            long agendaId = getArguments().getLong(EXTRA_AGENDA_ID, -1);
            activityLId = getArguments().getLong(EXTRA_ACTIVL_ID, -1);
            if (agendaId != -1)
                agenda = AppModule.retrieveAgendaUseCases().get(agendaId);
            else
                agenda = Agenda.empty();

            binding.getRoot().post(this::onBind);
        });

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

    public void onBind() {
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

        show(selected);
    }

    public void showActivities(Agenda agenda) {
        binding.pageGreyBar1.setVisibility(View.GONE);
        binding.pageActivities.setVisibility(View.GONE);
        binding.pageSectionsActivities.setVisibility(View.VISIBLE);
        binding.pageSectionsEvents.setVisibility(View.GONE);

        RecyclerView recyclerView = binding.pageSectionsActivities;
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
        adapter.setOnClickListener((actv)->showEvents(Collections.singletonList(actv)));
        binding.pageAddItemEditText.setOnEnterListener(
                (s)->adapter.insert(ActivityType.Type.activity, 0, s));
        //int expandId =
        adapter.setAgenda(agenda, activityLId);
        //recyclerView.post(()-> scrollTo(expandId, recyclerView));
    }
    public void showEvents(List<_Activity> selected) {
        binding.pageGreyBar1.setVisibility(View.VISIBLE);
        binding.pageActivities.setVisibility(View.VISIBLE);
        binding.pageActivities.setTags(selected.stream().map((s)->s.descr.toString())
                .reduce(TagInputEditText.SEP, (a,b)->a+TagInputEditText.SEP+b)+TagInputEditText.SEP);
        binding.pageActivities.setOnUpdateListener((tags)->{
            if (tags.trim().isEmpty())
                showActivities(agenda);
        });
        binding.pageActivities.format();

        binding.pageSectionsActivities.setVisibility(View.GONE);
        binding.pageSectionsEvents.setVisibility(View.VISIBLE);

        RecyclerView recyclerView = binding.pageSectionsEvents;
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        recyclerView.setHasFixedSize(true);

        Switch tSwitch = new Switch(false);
        final SectionItemAdapter adapter = new SectionItemAdapter(
                (alarmList)->show(selected), recyclerView, tSwitch
        );
        tSwitch.observe((state)-> {

        }, true);
        recyclerView.setAdapter(adapter);

        ExecutorService executor = AppModule.provideExecutor();
        executor.submit(()->{
            try {
                //TODO: selection of multiple activities.
                _Activity actv = selected.get(0);
                Activity activity = actv.getCache();
                assert activity != null;
                binding.getRoot().post(()->{
                    adapter.setGroup(activity);
                });
                binding.pageAddItemEditText.setOnEnterListener(
                        (s)->adapter.insert(ActivityType.Type.activity, 0, s));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    public void show(List<_Activity> selected) {
        if (selected == null || selected.size() == 0)
            showActivities(agenda);
        else
            showEvents(selected);
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

