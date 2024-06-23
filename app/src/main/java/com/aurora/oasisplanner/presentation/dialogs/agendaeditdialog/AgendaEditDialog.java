package com.aurora.oasisplanner.presentation.dialogs.agendaeditdialog;
import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_DRAG;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.presentation.dialogs.agendaeditdialog.util.AgendaAccessUtil;
import com.aurora.oasisplanner.presentation.dialogs.alarmeditdialog.AlarmEditDialog;
import com.aurora.oasisplanner.presentation.util.OnTextChangeListener;
import com.aurora.oasisplanner.presentation.util.Switch;
import com.aurora.oasisplanner.databinding.PageBinding;
import com.aurora.oasisplanner.presentation.dialogs.agendaeditdialog.components.ActivityAdapter;
import com.aurora.oasisplanner.presentation.dialogs.agendaeditdialog.components.EventAdapter;
import com.aurora.oasisplanner.presentation.dialogs.agendaeditdialog.components._BaseAdapter;
import com.aurora.oasisplanner.presentation.widgets.taginputeidittext.TagInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class AgendaEditDialog extends Fragment {
    public static final String EXTRA_AGENDA_ID = "agendaId";
    public static final String EXTRA_ACTIVL_ID = "activityLId";
    public static final String EXTRA_EVENT_ID = "eventLId";

    private Agenda agenda;
    private long activityLId;
    private long eventLId;

    private List<_Activity> selected = new ArrayList<>();
    private PageBinding binding;
    private boolean alarmEditOpened = false;
    private int mEventPinned = AgendaAccessUtil.NIL_VAL;

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
            eventLId = getArguments().getLong(EXTRA_EVENT_ID, -1);

            if (agenda == null)
                agenda = AgendaAccessUtil.fetchAgenda(agendaId);

            if (activityLId != AgendaAccessUtil.LId_NULL) {
                _Activity actv = AgendaAccessUtil.fetchActivity(agenda, activityLId);
                if (actv != null)
                    selected = Collections.singletonList(actv);
            }
            binding.getRoot().post(this::onBind);
        });

        return binding.getRoot();
    }

    /** Checks if Agenda is just placeholder. */
    public static boolean checkIfPlaceholder(long agendaId) {
        return AgendaAccessUtil.fetchAgenda(agendaId).agenda.args.containsKey("NIL");
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
        requireActivity().setTitle(agenda.agenda.id <= 0 ? R.string.page_overhead_new_agenda : R.string.page_overhead_edit_agenda);

        binding.img.setImageResource(R.drawable.blur_v1);
        binding.agendaConfirmEdit.setOnClickListener(
                (v)->onConfirm()
        );

        associateTitle(binding.pageTitle);
        binding.pageTitle.setOnKeyListener((v, keyCode, event)->keyCode == KeyEvent.KEYCODE_ENTER);
        show(selected);
    }

    private ActivityAdapter activityAdapter = null;
    private EventAdapter eventAdapter = null;
    public void showActivities(Agenda agenda) {
        eventLId = AgendaAccessUtil.LId_NULL;
        eventAdapter = null;
        binding.agendaPageEdit.setVisibility(View.GONE);
        binding.agendaPageMove.setVisibility(View.GONE);

        binding.pageHeader1.setVisibility(View.GONE);
        binding.pageActivities.setVisibility(View.GONE);
        binding.pageSectionsActivities.setVisibility(View.VISIBLE);
        binding.pageSectionsEvents.setVisibility(View.GONE);

        binding.pageHeader0.getEditButton().setVisibility(View.VISIBLE);
        binding.pageHeader0.getDetailsButton().setVisibility(View.GONE);

        RecyclerView recyclerView = binding.pageSectionsActivities;
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()));
        recyclerView.setHasFixedSize(false);

        Switch tSwitch = new Switch(false);
        final ActivityAdapter adapter = activityAdapter
                = new ActivityAdapter(this::checkboxOnSelect, recyclerView, tSwitch, activityLId);

        setupEditToolbar(tSwitch, adapter);
        associateDragToReorder(adapter, recyclerView);
        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener((actv)->{
            showEvents(Collections.singletonList(actv));
        });
        binding.pageAddItemEditText.setOnEnterListener(
                (s)->adapter.insert(ActivityType.Type.activity, 0, s));
        adapter.setAgenda(agenda);

        binding.pageHeader0.setEditButtonBehavior(
                ()->!adapter.isEditable(), adapter::setEditable
        );
    }
    public void showEvents(List<_Activity> selected) {
        binding.agendaPageEdit.setVisibility(View.VISIBLE);
        binding.agendaPageMove.setVisibility(View.VISIBLE);

        binding.pageHeader1.setVisibility(View.VISIBLE);
        binding.pageActivities.setVisibility(View.VISIBLE);
        binding.pageSectionsActivities.setVisibility(View.GONE);
        binding.pageSectionsEvents.setVisibility(View.VISIBLE);

        binding.pageHeader0.getEditButton().setVisibility(View.GONE);
        binding.pageHeader0.getDetailsButton().setVisibility(View.GONE);
        binding.pageHeader1.getEditButton().setVisibility(View.VISIBLE);
        binding.pageHeader1.getDetailsButton().setVisibility(View.VISIBLE);

        RecyclerView recyclerView = binding.pageSectionsEvents;
        recyclerView.setHasFixedSize(false);

        Switch tSwitch = new Switch(false);
        AlarmEditDialog.OnSaveListener onEventSaveListener = (_event)-> show(selected);
        final EventAdapter adapter = eventAdapter = new EventAdapter(
                onEventSaveListener,
                this::checkboxOnSelect,
                (_pinnedId)-> mEventPinned = _pinnedId,
                recyclerView, tSwitch, agenda, eventLId, mEventPinned
        );

        setupEditToolbar(tSwitch, adapter);
        associateDragToReorder(adapter, recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(binding.getRoot().getContext()){
            @Override
            public void onLayoutCompleted(RecyclerView.State state) {
                super.onLayoutCompleted(state);
                _Event event = adapter.getPinnedEvent();
                if (event != null && !alarmEditOpened) {
                    alarmEditOpened = true;
                    AppModule.retrieveEditEventUseCases().invoke(event, event.activityDescr, onEventSaveListener, null);
                }
            }
        });

        ExecutorService executor = AppModule.provideExecutor();
        executor.submit(()->{
            try {
                //TODO: selection of multiple activities.
                _Activity actv = selected.get(0);
                Activity activity = actv.getCache();
                assert activity != null;
                binding.getRoot().post(()-> adapter.setEventList(activity));
                binding.pageAddItemEditText.setOnEnterListener(
                        (s)->adapter.insert(ActivityType.Type.activity, 0, s));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // INFO: Showing a list of activities that the event belongs to.
        binding.pageActivities.setTags(selected.stream().map((s)->s.title.toString().replace(" ", "_"))
                .reduce(TagInputEditText.SEP, (a,b)->a+TagInputEditText.SEP+b)+TagInputEditText.SEP);
        binding.pageActivities.setOnUpdateListener((tags)->{
            if (tags.trim().isEmpty()) {
                if (eventAdapter != null)
                    eventAdapter.save();
                showActivities(agenda);
            }
        });
        binding.pageActivities.format();

        binding.pageHeader1.setEditButtonBehavior(
                ()->!adapter.isEditable(), adapter::setEditable
        );
        binding.pageHeader1.setDetailsButtonBehavior(
                ()->!adapter.isExpanded(),
                (b)->{
                    adapter.setExpanded(b);
                    binding.pageHeader1.turnOffEditButton();
                }
        );
    }
    public void show(List<_Activity> selected) {
        if (selected == null || selected.size() == 0)
            showActivities(agenda);
        else
            showEvents(selected);
    }

    public void checkboxOnSelect(boolean isFull) {
        binding.agendaPageCheckbox.setTag(true);
        binding.agendaPageCheckbox.setChecked(isFull);
        binding.agendaPageCheckbox.setTag(null);
    }

    public void setupEditToolbar(Switch tSwitch, _BaseAdapter adapter) {
        tSwitch.observe((state)-> {
            binding.agendaPageSelectionTools.setVisibility(state ? View.VISIBLE : View.GONE);
            binding.pageAddItemEditText.setEditable(!state);
        }, true);
        binding.agendaPageDelete.setOnClickListener((v)-> adapter.removeChecked());
        binding.agendaPageEdit.setOnClickListener((v)-> adapter.editTagOfChecked());
        binding.agendaPageMove.setOnClickListener((v)-> adapter.moveChecked());
        binding.agendaPageCheckbox.setOnCheckedChangeListener((v,checked)->{
            if (v.getTag() != null)
                return;
            if (checked) // to full
                adapter.checkAll();
            else // to empty
                adapter.clearChecked();
        });
    }

    public void associateDragToReorder(_BaseAdapter adapter, RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0){

            private int lastPos = -1;

            @Override
            public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (viewHolder == null)
                    return;
                if (actionState == ACTION_STATE_DRAG)
                    viewHolder.itemView.setAlpha(.5f);
                lastPos = viewHolder.getAdapterPosition();
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                if (toPosition != lastPos) {
                    adapter.swapItems(fromPosition, toPosition);
                    lastPos = toPosition;
                }
                return true;
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setAlpha(1f);
                lastPos = -1;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {}

            @Override
            public boolean isLongPressDragEnabled() {return false;}
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        adapter.setItemTouchHelper(itemTouchHelper);
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
        TextWatcher textWatcher = new OnTextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                agenda.agenda.title = editText.getText().toString();
            }
        };
        editText.setTag(textWatcher);
        editText.addTextChangedListener(textWatcher);
    }

    public void onConfirm() {
        if (agenda.agenda.title.trim().isEmpty()) {
            Toast.makeText(getContext(), R.string.page_no_title_warning, Toast.LENGTH_SHORT).show();
            return;
        }
        _BaseAdapter.save(activityAdapter);
        _BaseAdapter.save(eventAdapter);
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

    @SuppressLint("RestrictedApi")
    private void navigateUp() {

//        MainActivity.getNavController().navigateUp();
        MainActivity.getNavController().popBackStack(R.id.navigation_agendaEditDialog, true);

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
            case R.id.editAgenda_discard:
                showCancelConfirmDialog();
                return true;
//                onDiscard();
//                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

