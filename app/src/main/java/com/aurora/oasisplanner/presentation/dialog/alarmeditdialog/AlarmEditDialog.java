package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.model.pojo.events.Event;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.core.use_cases.EditEventUseCases;
import com.aurora.oasisplanner.data.tags.NotifType;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.databinding.AlarmEditBinding;
import com.aurora.oasisplanner.databinding.AlarmEditDatesBinding;
import com.aurora.oasisplanner.databinding.AlarmEditInfosBinding;
import com.aurora.oasisplanner.databinding.AlarmEditTimesBinding;
import com.aurora.oasisplanner.databinding.SpinnerElementBinding;
import com.aurora.oasisplanner.databinding.TabSelectorBinding;
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components.DateType;
import com.aurora.oasisplanner.presentation.util.OnTextChangeListener;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;
import com.aurora.oasisplanner.util.styling.Resources;
import com.aurora.oasisplanner.presentation.widget.multidatepicker.MultiDatePicker;
import com.aurora.oasisplanner.presentation.widget.tabselector.TabSelector;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AlarmEditDialog extends Fragment {
    // Root data
    private Event event;
    private AlarmEditBinding binding;
    // Components
    private MultiDatePicker datePicker;
    private TimePicker timePicker;
    private TabSelector tabSelector;
    // Data
    private List<LocalDate> selectedDates;
    private LocalTime selectedTime;
    // Associated Data
    private AlarmType type;
    private Importance importance;
    private DateType dateType = DateType.minutes;
    private NotifType notifType;

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

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

        EditEventUseCases editAlarmListUseCase = AppModule.retrieveEditEventUseCases();
        this.event = editAlarmListUseCase.retrieveAlarms();
        setOnSaveListener(editAlarmListUseCase.getOnSaveListener());

        assert event != null;

        this.type = event.alarmList.type;
        this.importance = event.alarmList.importance;
        this.selectedDates = event.alarmList.dates;
        this.selectedTime = event.alarmList.time;
        this.notifType = event.alarmList.getNotifType();
        if (notifType != null)
            this.dateType = notifType.dateType;

        binding = AlarmEditBinding.inflate(getLayoutInflater());
        onBind();

        return binding.getRoot();
    }

    public void onBind() {

        // Title Box
        binding.aedAlarmNameSuppBox.setText(event.contents);
        associateTitle(binding.aedAlarmNameBox);

        // Confirm Button
        binding.btnConfirm.setOnClickListener(
                (v)->onConfirm()
        );

        // Tab Selector
        tabSelector = binding.tabSelector;
        tabSelector.createOptionMenu(
                0,
                Arrays.asList(
                        new TabSelector.MenuItem(Resources.getString(R.string.edit_alarm_tab_infos)),
                        new TabSelector.MenuItem(Resources.getString(R.string.edit_alarm_tab_dates)),
                        new TabSelector.MenuItem(Resources.getString(R.string.edit_alarm_tab_times))
                ),
                (i, menu, vbinding)->{
                    switchPageAnimation(i, vbinding);
                    switchToPage(i, binding, getLayoutInflater());
                }
        );
    }

    private void switchPageAnimation(int i, TabSelectorBinding vbinding) {
        String[] colors = new String[]{
                "#FF0062EE", "#FF4A5A40", "#FFEE9337"
        };
        vbinding.selectContent.getBackground().setColorFilter(Color.parseColor(
                colors[i]
        ), PorterDuff.Mode.SRC_IN);
    }

    public void switchToPage(int i, AlarmEditBinding binding, LayoutInflater li) {
        binding.dialogContents.removeAllViews();
        switch (i) {
            case 0:
                AlarmEditInfosBinding alarmEditInfosBinding = AlarmEditInfosBinding.inflate(li);
                binding.dialogContents.addView(alarmEditInfosBinding.getRoot());

                // Alarm Box
                AlarmTypeAdapter adapter = new AlarmTypeAdapter(
                        li,
                        Arrays.asList(AlarmType.values())
                );
                TextInputLayout til = alarmEditInfosBinding.aedpiTagAlarmTypeBox.getSpinnerTil();
                alarmEditInfosBinding.aedpiTagAlarmTypeBox.setOnItemSelectListener(
                        adapter, type.ordinal(), type.getOutlineDrawable(),
                        (adapterView, view, position, id) -> {
                            type = AlarmType.values()[position];
                            til.setStartIconDrawable(type.getOutlineDrawable());
                        },
                        (v)->type.getType());

                // Importance Box
                ImportanceAdapter adapterImp = new ImportanceAdapter(
                        li,
                        Arrays.asList(Importance.values())
                );
                TextInputLayout tilImp = alarmEditInfosBinding.aedpiTagImportanceBox.getSpinnerTil();
                alarmEditInfosBinding.aedpiTagImportanceBox.setOnItemSelectListener(
                        adapterImp, importance.ordinal(), importance.getSimpleDrawable(),
                        (adapterView, view, position, id) -> {
                            importance = Importance.values()[position];
                            tilImp.setStartIconDrawable(importance.getSimpleDrawable());
                        },
                        (v)->importance.getImportance());

                // Dates and Times
                alarmEditInfosBinding.aedpiTagNotifDateBox.setText(DateTimesFormatter.toDate(selectedDates));
                alarmEditInfosBinding.aedpiTagNotifTimeBox.setText(DateTimesFormatter.toTime(selectedTime));
                alarmEditInfosBinding.aedpiTagNotifDateBox.setOnClickListener(
                        (v)->tabSelector.onClick(1)
                );
                alarmEditInfosBinding.aedpiTagNotifTimeBox.setOnClickListener(
                        (v)->tabSelector.onClick(2)
                );

                // Locations
                SpannableStringBuilder ssb = event.alarmList.getLoc();
                alarmEditInfosBinding.aedpiTagLocationBox.setOnChangeListener(
                        (_loc)-> {
                            if (_loc == null)
                                this.event.alarmList.removeKey(TagType.LOC.name());
                            else
                                this.event.alarmList.putArgs(TagType.LOC.name(), _loc);
                        }
                );
                if (ssb != null) {
                    alarmEditInfosBinding.aedpiTagLocationBox.setText(ssb.toString());
                } else {
                    alarmEditInfosBinding.aedpiTagLocationBox.setShowing(false);
                }

                // DateTime
                ArrayAdapter<DateType> dateTypeAdapter = new ArrayAdapter<DateType>(requireContext(), R.layout.datetype_spinner_element);
                dateTypeAdapter.addAll(DateType.values());

                if (notifType != null) {
                    alarmEditInfosBinding.aedpiTagDatetimeBox.setNotifType(notifType);
                    alarmEditInfosBinding.aedpiTagDatetimeBox.setOnChangeListener(
                            (_notifType)-> this.notifType = _notifType
                    );
                } else {
                    alarmEditInfosBinding.aedpiTagDatetimeBox.setShowing(false);
                }

                // Tags
                String tags = event.alarmList.getTagsString();
                if (tags != null && !tags.isEmpty()) {
                    alarmEditInfosBinding.aedpiTagTagsBox.setText(tags);
                    alarmEditInfosBinding.aedpiTagTagsBox.setOnChangeListener(
                            (_tags)->event.alarmList.putArgs(TagType.TAGS.name(), _tags)
                    );
                } else {
                    alarmEditInfosBinding.aedpiTagTagsBox.setShowing(false);
                }

                break;
            case 1:
                AlarmEditDatesBinding alarmEditDatesBinding = AlarmEditDatesBinding.inflate(li);
                binding.dialogContents.addView(alarmEditDatesBinding.getRoot());

                datePicker = alarmEditDatesBinding.picker;
                datePicker.minDateAllowed = LocalDate.now();
                
                LocalDate date = selectedDates.get(0);
                for (LocalDate d : selectedDates) {
                    if (!datePicker.minDateAllowed.isAfter(d)) {
                        date = d;
                        break;
                    }
                }
                datePicker.setMonth(date.getYear(), date.getMonthValue());
                datePicker.refresh();

                datePicker.getMultiSelected().clear();
                datePicker.getMultiSelected().addAll(selectedDates);
                datePicker.setOnUpdateListener(
                        (_picker)-> {
                            ArrayList<LocalDate> selected = _picker.getMultiSelected();
                            alarmEditDatesBinding.datesSelectedText.setText(DateTimesFormatter.toDate(selected));
                            selectedDates = selected;
                        }
                );
                datePicker.setFocus(date);
                break;
            case 2:
                AlarmEditTimesBinding alarmEditTimesBinding = AlarmEditTimesBinding.inflate(li);
                binding.dialogContents.addView(alarmEditTimesBinding.getRoot());

                timePicker = alarmEditTimesBinding.picker;
                timePicker.setHour(selectedTime.getHour());
                timePicker.setMinute(selectedTime.getMinute());
                timePicker.setOnTimeChangedListener(
                        (picker, hour, minute)-> {
                            selectedTime = LocalTime.of(hour, minute);
                        }
                );
                break;
        }
    }

    public void associateTitle(EditText editText) {
        editText.setText(event.alarmList.title);
        TextWatcher textWatcher = new OnTextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                event.alarmList.title = editText.getText().toString();
            }
        };
        editText.setTag(textWatcher);
        editText.addTextChangedListener(textWatcher);
    }

    private OnSaveListener onSaveListener;
    public void setOnSaveListener(OnSaveListener onSaveListener) {
        this.onSaveListener = onSaveListener;
    }

    public void onConfirm() {
        if (event.alarmList.title.trim().isEmpty()) {
            Toast.makeText(getContext(), R.string.page_no_title_warning, Toast.LENGTH_SHORT).show();
            return;
        }
        saveAlarms();
        navigateUp();
    }
    public void onCancel() {

    }
    public void onDiscard() {
        navigateUp();
    }

    public void saveAlarms() {
        event.alarmList.importance = importance;
        event.alarmList.type = type;
        if (notifType != null) {
            event.alarmList.putArgs(TagType.ALARM.name(), notifType.toString());
            event.alarmList.getAssociates().generateSubalarms();
        }
        event.putDates(selectedTime, selectedDates.toArray(new LocalDate[0]));

        if (onSaveListener != null)
            onSaveListener.save(event);
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

    @SuppressLint("RestrictedApi")
    private void navigateUp() {
        MainActivity.getNavController().popBackStack(R.id.navigation_alarmEditDialog, true);
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

    public static class AlarmTypeAdapter extends ArrayAdapter<AlarmType> {
        private LayoutInflater li;
        private List<AlarmType> typeList;

        public AlarmTypeAdapter(LayoutInflater li, List<AlarmType> typeList) {
            super(li.getContext(), R.layout.type_spinner_element);
            this.li = li;
            this.typeList = typeList;
        }

        @Override
        public int getCount() {
            return typeList != null ? typeList.size() : 0;
        }

        @Override
        public AlarmType getItem(int position) {
            return typeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override@
        SuppressLint("ViewHolder")
        public View getView(int position, View convertView, ViewGroup parent) {
            SpinnerElementBinding binding = SpinnerElementBinding.inflate(li);
            AlarmType type = typeList.get(position);
            binding.text.setText(type.toString());
            binding.icon.setImageDrawable(type.getDrawable());
            return binding.getRoot();
        }
    }

    public static class ImportanceAdapter extends ArrayAdapter<Importance> {
        private LayoutInflater li;
        private List<Importance> typeList;

        public ImportanceAdapter(LayoutInflater li, List<Importance> typeList) {
            super(li.getContext(), R.layout.type_spinner_element);
            this.li = li;
            this.typeList = typeList;
        }

        @Override
        public int getCount() {
            return typeList != null ? typeList.size() : 0;
        }

        @Override
        public Importance getItem(int position) {
            return typeList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            SpinnerElementBinding binding = SpinnerElementBinding.inflate(li);
            Importance type = typeList.get(position);
            binding.text.setText(type.toString());
            binding.icon.setImageDrawable(type.getDrawable());
            return binding.getRoot();
        }
    }

    public interface OnSaveListener {
        void save(Event event);
    }
}

