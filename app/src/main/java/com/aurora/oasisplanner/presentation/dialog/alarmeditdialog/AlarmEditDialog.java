package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.model.pojo.events.Event;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.core.use_cases.EditEventUseCases;
import com.aurora.oasisplanner.data.tags.NotifType;
import com.aurora.oasisplanner.databinding.AlarmEditBinding;
import com.aurora.oasisplanner.databinding.AlarmEditDatesBinding;
import com.aurora.oasisplanner.databinding.AlarmEditInfosBinding;
import com.aurora.oasisplanner.databinding.AlarmEditTimesBinding;
import com.aurora.oasisplanner.databinding.SpinnerElementBinding;
import com.aurora.oasisplanner.databinding.TabSelectorBinding;
import com.aurora.oasisplanner.util.styling.DateTimesFormatter;
import com.aurora.oasisplanner.util.styling.Resources;
import com.aurora.oasisplanner.presentation.widget.multidatepicker.MultiDatePicker;
import com.aurora.oasisplanner.presentation.widget.tabselector.TabSelector;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

public class AlarmEditDialog extends AppCompatDialogFragment {

    private Event event;
    private Dialog dialog;

    public AlarmType type;
    public Importance importance;
    private String contents;
    private MultiDatePicker datePicker;
    private TimePicker timePicker;
    private TabSelector tabSelector;
    private List<LocalDate> selectedDates;
    private LocalTime selectedTime;

    private AlarmTagEditDialog.DateType dateType = AlarmTagEditDialog.DateType.minutes;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        EditEventUseCases editAlarmListUseCase = AppModule.retrieveEditEventUseCases();
        this.event = editAlarmListUseCase.retrieveAlarms();

        assert event != null;

        this.type = event.alarmList.type;
        this.importance = event.alarmList.importance;
        this.selectedDates = event.alarmList.dates;
        this.selectedTime = event.alarmList.time;
        this.contents = event.alarmList.title;

        AlarmEditBinding binding = AlarmEditBinding.inflate(getLayoutInflater());
        onBind(binding);

        ViewGroup vg = (ViewGroup) binding.getRoot();
        dialog = super.onCreateDialog(savedInstanceState);
        dialog.setContentView(vg);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        new Handler().post(() -> getDialog().getWindow().setLayout(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
    }

    public void onBind(AlarmEditBinding binding) {

        binding.btnConfirm.setOnClickListener(
                (v)->onConfirm()
        );
        binding.btnCancel.setOnClickListener(
                (v)->onCancel()
        );

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
        binding.dialogContents.setLayoutParams(
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        binding.placeholder.getHeight())
        );
        switch (i) {
            case 0:
                AlarmEditInfosBinding alarmEditInfosBinding = AlarmEditInfosBinding.inflate(li);
                binding.dialogContents.addView(alarmEditInfosBinding.getRoot());

                // Title Box
                associateTitle(alarmEditInfosBinding.aedpiTagContentBox.getEditText());

                // Alarm Box
                AlarmTypeAdapter adapter = new AlarmTypeAdapter(
                        li,
                        Arrays.asList(AlarmType.values())
                );
                TextInputLayout til = alarmEditInfosBinding.aedpiTagAlarmTypeBox.getSpinnerTil();
                alarmEditInfosBinding.aedpiTagAlarmTypeBox.setOnItemSelectListener(
                        adapter, type.toString(), type.getOutlineDrawable(),
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
                        adapterImp, importance.toString(), importance.getSimpleDrawable(),
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
                if (ssb != null)
                    alarmEditInfosBinding.aedpiTagLocationBox.setText(ssb.toString());
                else
                    alarmEditInfosBinding.aedpiTagLocationBox.setVisibility(View.GONE);

                // DateTime
                ArrayAdapter<AlarmTagEditDialog.DateType> dateTypeAdapter = new ArrayAdapter<AlarmTagEditDialog.DateType>(requireContext(), R.layout.datetype_spinner_element);
                dateTypeAdapter.addAll(AlarmTagEditDialog.DateType.values());
                alarmEditInfosBinding.aedpiTagDatetimeBox.setOnItemSelectListener(
                        dateTypeAdapter, dateType.toString(), null,
                        (adapterView, view, position, id) -> {
                            dateType = AlarmTagEditDialog.DateType.values()[position];
                            alarmEditInfosBinding.aedpiTagDatetimeBox.setDateType(dateType);
                        },
                        (v)->dateType.ordinal());

                NotifType subalarm = event.alarmList.getNotifType();
                if (subalarm != null)
                    alarmEditInfosBinding.aedpiTagDatetimeBox.setNotifType(subalarm);
                else
                    alarmEditInfosBinding.aedpiTagDatetimeBox.setVisibility(View.GONE);

                // Tags
                String tags = event.alarmList.getTagsString();
                if (tags != null && !tags.isEmpty())
                    alarmEditInfosBinding.aedpiTagTagsBox.setText(tags);
                else
                    alarmEditInfosBinding.aedpiTagTagsBox.setVisibility(View.GONE);

                break;
            case 1:
                AlarmEditDatesBinding alarmEditDatesBinding = AlarmEditDatesBinding.inflate(li);
                binding.dialogContents.addView(alarmEditDatesBinding.getRoot());

                datePicker = alarmEditDatesBinding.picker;
                datePicker.multiSelectable = true;
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

                datePicker.selected.clear();
                datePicker.selected.addAll(selectedDates);
                datePicker.setOnUpdateListener(
                        (selected)-> {
                            alarmEditDatesBinding.datesSelectedText.setText(DateTimesFormatter.toDate(selected));
                            selectedDates = selected;
                        }
                );
                datePicker.setDate(date, false, true, true);
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
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                event.alarmList.title = editText.getText().toString();
            }
        };
        editText.setTag(textWatcher);
        editText.addTextChangedListener(textWatcher);
    }

    public void setOnItemSelectListener(AutoCompleteTextView spinner, TextInputLayout til,
                                        String text, Drawable drawable,
                                        AdapterView.OnItemClickListener listener) {
        spinner.setText(text);
        til.setStartIconDrawable(drawable);
        spinner.setOnItemClickListener(listener);
    }

    private OnSaveListener onSaveListener;
    public void setOnSaveListener(OnSaveListener onSaveListener) {
        this.onSaveListener = onSaveListener;
    }

    public void onConfirm() {
        /*if (agenda.agenda.title.isEmpty()) {
            Toast.makeText(getContext(), R.string.page_no_title_warning, Toast.LENGTH_SHORT).show();
            return;
        }*/
        saveAlarms();
        dialog.dismiss();
    }
    public void onCancel() {
        dialog.dismiss();
    }

    public void saveAlarms() {
        event.alarmList.importance = importance;
        event.alarmList.type = type;
        event.putDates(selectedTime, selectedDates.toArray(new LocalDate[0]));
        if (onSaveListener != null)
            onSaveListener.save(event);
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

        @Override
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

    interface OnItemSelectedListener extends AdapterView.OnItemSelectedListener {
        @Override
        default void onNothingSelected(AdapterView<?> parent) {}

        static void setOnItemSelectedListener(Spinner spinner, OnItemSelectedListener listener) {
            spinner.setOnItemSelectedListener(listener);
        }
    }

    public interface OnSaveListener {
        void save(Event event);
    }
}

