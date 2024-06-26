package com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.tags.NotifType;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.databinding.ItemEditTagBinding;
import com.aurora.oasisplanner.databinding.TagTypeSpinnerElementBinding;
import com.aurora.oasisplanner.presentation.widget.taginputeidittext.TagInputEditText;
import com.aurora.oasisplanner.util.styling.Resources;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Set;
import java.util.function.Function;

public class TagEditDialog extends AppCompatDialogFragment {
    public static final String EXTRA_ALARM_LISTS = "alarmLists";

    private AlertDialog dialog;
    public TagType type = TagType.LOC;
    private DateType dateType = DateType.minutes;
    private ItemEditTagBinding vbinding;
    private Set<_Event> checkedList;
    private Runnable updateUiFunction = ()->{};
    private int idx = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ItemEditTagBinding binding = ItemEditTagBinding.inflate(getLayoutInflater());
        onBind(binding);

        ViewGroup vg = (ViewGroup) binding.getRoot();
        builder.setView(vg);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    public void onBind(ItemEditTagBinding binding) {
        vbinding = binding;
        vbinding.header.setText(R.string.page_overhead_edit_tag);
        //binding.header.setText(agenda.agenda.id <= 0 ? R.string.page_overhead_new_agenda : R.string.page_overhead_edit_agenda);
        vbinding.confirmBtn.setOnClickListener(
                (v)->onConfirm()
        );
        vbinding.cancelButton.setOnClickListener(
                (v)->onCancel()
        );
        vbinding.deleteButton.setOnClickListener(
                (v)->onDelete()
        );

        vbinding.tagTypeTv.setInputType(InputType.TYPE_NULL);
        vbinding.tagDateTv.setInputType(InputType.TYPE_CLASS_NUMBER);
        vbinding.tagDateTypeTv.setInputType(InputType.TYPE_NULL);
        vbinding.tagChoiceTv.setInputType(InputType.TYPE_NULL);
        vbinding.tagTypeTv.setKeyListener(null);
        vbinding.tagDateTypeTv.setKeyListener(null);
        vbinding.tagChoiceTv.setKeyListener(null);

        SpinAdapter spinAdapter = new SpinAdapter(getLayoutInflater(), TagType.values());
        AutoCompleteTextView spinnerType = vbinding.tagTypeTv;
        TextInputLayout til = vbinding.tagTypeTil;
        spinnerType.setAdapter(spinAdapter);
        setOnItemSelectListener(spinnerType, til,
                type.toString(), type.getDrawable(),
                (AdapterView.OnItemClickListener) (adapterView, view, position, id) -> {
                    type = TagType.values()[position];
                    changeUiToInputType(type);
                },
                (v)->type.getType());

        AutoCompleteTextView spinnerDateType = vbinding.tagDateTypeTv;
        TextInputLayout dateTypeTil = vbinding.tagTypeTil;
        ArrayAdapter<DateType> dateTypeAdapter = new ArrayAdapter<DateType>(requireContext(), R.layout.datetype_spinner_element);
        setOnItemSelectListener(spinnerDateType, dateTypeTil,
                dateType.toString(), null,
                (AdapterView.OnItemClickListener) (adapterView, view, position, id) -> {
                    dateType = DateType.values()[position];
                    vbinding.tagTimeBox.setVisibility(dateType.hasTime() ? View.VISIBLE : View.GONE);
                    vbinding.tagTimeBox.requestLayout();
                },
                (v)->dateType.ordinal());
        dateTypeAdapter.addAll(DateType.values());
        spinnerDateType.setAdapter(dateTypeAdapter);
        vbinding.tagTimeHourPicker.setMinValue(0);
        vbinding.tagTimeHourPicker.setMaxValue(23);
        vbinding.tagTimeHourPicker.setFormatter((v)->{return String.format("%02d", v);});
        vbinding.tagTimeHourPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        vbinding.tagTimeMinutePicker.setMinValue(0);
        vbinding.tagTimeMinutePicker.setMaxValue(59);
        vbinding.tagTimeMinutePicker.setFormatter((v)->{return String.format("%02d", v);});
        vbinding.tagTimeMinutePicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        vbinding.tagTagsTv.setText("");
    }

    public void changeUiToInputType(TagType type) {
        vbinding.tagTypeTil.setStartIconDrawable(type.getDrawable());

        vbinding.tagContentBox.setVisibility(View.GONE);
        vbinding.tagDatetimeBox.setVisibility(View.GONE);
        vbinding.tagChoiceBox.setVisibility(View.GONE);
        vbinding.tagTagsBox.setVisibility(View.GONE);
        vbinding.deleteButton.setVisibility(View.VISIBLE);
        switch (type) {
            case LOC:
            case DESCR:
                vbinding.tagContentBox.setVisibility(View.VISIBLE);
                break;
            case ALARM:
                vbinding.tagDatetimeBox.setVisibility(View.VISIBLE);
                break;
            case IMPORTANCE:
            case ALARMTYPE:
                vbinding.deleteButton.setVisibility(View.GONE);
                vbinding.tagChoiceBox.setVisibility(View.VISIBLE);
                break;
            case TAGS:
                vbinding.tagTagsBox.setVisibility(View.VISIBLE);
                break;
        }
        switch (type) {
            case IMPORTANCE:
                AutoCompleteTextView spinnerImportance = vbinding.tagChoiceTv;
                TextInputLayout importanceTil = vbinding.tagChoiceTil;
                ArrayAdapter<Importance> importanceAdapter = new ArrayAdapter<Importance>(requireContext(), R.layout.datetype_spinner_element);
                setOnItemSelectListener(spinnerImportance, importanceTil,
                        Importance.values()[idx].toString(), null,
                        (AdapterView.OnItemClickListener) (adapterView, view, position, id) -> {
                            idx = position;
                        },
                        (v)->Importance.values()[idx].ordinal());
                importanceAdapter.addAll(Importance.values());
                spinnerImportance.setAdapter(importanceAdapter);
                break;
            case ALARMTYPE:
                AutoCompleteTextView spinnerAlarmType = vbinding.tagChoiceTv;
                TextInputLayout alarmTypeTil = vbinding.tagChoiceTil;
                ArrayAdapter<AlarmType> alarmTypeAdapter = new ArrayAdapter<AlarmType>(requireContext(), R.layout.datetype_spinner_element);
                setOnItemSelectListener(spinnerAlarmType, alarmTypeTil,
                        AlarmType.values()[idx].toString(), null,
                        (AdapterView.OnItemClickListener) (adapterView, view, position, id) -> {
                            idx = position;
                        },
                        (v)->AlarmType.values()[idx].ordinal());
                alarmTypeAdapter.addAll(AlarmType.values());
                spinnerAlarmType.setAdapter(alarmTypeAdapter);
                break;
        }
        vbinding.tagContentBox.requestLayout();
        vbinding.tagDatetimeBox.requestLayout();
        vbinding.tagChoiceBox.requestLayout();
        vbinding.tagTagsBox.requestLayout();
    }

    private void setOnItemSelectListener(AutoCompleteTextView spinner, TextInputLayout til,
                                         String text, Drawable drawable,
                                         AdapterView.OnItemClickListener listener,
                                         Function<String, Integer> getType) {
        spinner.setText(text);
        til.setStartIconDrawable(drawable);
        spinner.setOnItemClickListener(listener);
        listener.onItemClick(null, spinner.getRootView(), getType.apply(null), 0);
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

    public void onConfirm() {
        AppModule.provideExecutor().submit(
                ()->{
                    Log.d("test3", "STARTED CONFIRM");
                    try {
                        if (saveTags()) {
                            Activity a = getActivity();
                            a.runOnUiThread(
                                    ()->{
                                        updateUiFunction.run();
                                        dialog.dismiss();
                                    }
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }
    public void onCancel() {
        updateUiFunction.run();
        dialog.dismiss();
    }
    public void onDelete() {
        if (deleteTags()) {
            updateUiFunction.run();
            dialog.dismiss();
        }
    }

    public boolean saveTags() {
        SpannableStringBuilder ssb = null;
        switch (type) {
            case LOC:
            case DESCR:
                TextInputEditText tiet = vbinding.tagContentTv;
                ssb = new SpannableStringBuilder(tiet.getText());
                ssb.clearSpans();
                if (ssb.length() == 0) {
                    Toast.makeText(getContext(), R.string.tab_no_content_warning, Toast.LENGTH_SHORT).show();
                    return false;
                }
                break;
            case ALARM:
                TextInputEditText tietD = vbinding.tagDateTv;
                AutoCompleteTextView tietDT = vbinding.tagDateTypeTv;
                int val;
                try {
                    val = Integer.parseInt(tietD.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(getContext(), R.string.tab_no_content_warning, Toast.LENGTH_SHORT).show();
                    return false;
                }
                NotifType notifType;
                DateType dt = dateType;
                if (dt.hasTime())
                    notifType = new NotifType(val, dt, vbinding.tagTimeHourPicker.getValue(), vbinding.tagTimeMinutePicker.getValue());
                else
                    notifType = new NotifType(val, dt);
                ssb = new SpannableStringBuilder(notifType.toString());
                break;
            case IMPORTANCE:
            case ALARMTYPE:
                break;
            case TAGS:
                ssb = new SpannableStringBuilder(vbinding.tagTagsTv.getText());
                break;
        }

        assert checkedList != null;
        switch (type) {
            case IMPORTANCE:
                for (_Event checked : checkedList)
                    checked.importance = Importance.values()[idx];
                break;
            case ALARMTYPE:
                for (_Event checked : checkedList)
                    checked.type = AlarmType.values()[idx];
                break;
            case TAGS:
                for (_Event checked : checkedList) {
                    SpannableStringBuilder ssb2 = checked.getArgSpannable(type);
                    checked.putArgs(type.name(), TagInputEditText.combine(ssb2, ssb));
                }
                break;
            case ALARM:
                for (_Event checked : checkedList) {
                    checked.putArgs(type.name(), ssb);
                    checked.getAssociates().setSubalarms();
                }
                break;
            case DESCR:
                for (_Event checked : checkedList)
                    checked.setTitle(ssb);
                break;
            default:
                for (_Event checked : checkedList)
                    checked.putArgs(type.name(), ssb);
        }

        return true;
    }

    public boolean deleteTags() {
        assert checkedList != null;
        if (type != TagType.IMPORTANCE && type != TagType.ALARMTYPE) {
            for (_Event checked : checkedList)
                checked.removeKey(type.name());
            return true;
        }
        return false;
    }

    public void setSelectedList(Set<_Event> checkedList) {
        this.checkedList = checkedList;
    }

    public void setUpdateUiFunction(Runnable updateUiFunction) {
        this.updateUiFunction = updateUiFunction;
    }

    public static class SpinAdapter extends ArrayAdapter<TagType> {
        private LayoutInflater li;
        private TagType[] vals;

        public SpinAdapter(LayoutInflater li, @NonNull TagType[] typeList) {
            super(li.getContext(), R.layout.tagtype_spinner_element);
            this.li      = li;
            this.vals    = typeList;
        }

        @Override
        public int getCount() {
            return vals.length;
        }

        @Override
        public TagType getItem(int position) {
            return vals[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getDaView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getDaView(position, convertView, parent);
        }

        public View getDaView(int position, View convertView, ViewGroup parent) {
            TagTypeSpinnerElementBinding binding = TagTypeSpinnerElementBinding.inflate(li);
            TagType type = getItem(position);
            binding.text.setText(type.toString());
            binding.icon.setImageDrawable(type.getDrawable());
            return binding.getRoot();
        }
    }

    public enum DateType {
        minutes, hours, days, weeks, months;

        private static String[] dateTypeStrings = Resources.getStringArr(R.array.date_types);

        @NonNull
        @Override
        public String toString() {
            if (dateTypeStrings == null) dateTypeStrings = Resources.getStringArr(R.array.date_types);
            return dateTypeStrings[ordinal()];
        }

        public boolean hasTime() {
            switch (this) {
                case minutes:
                case hours:
                    return false;
                default:
                    return true;
            }
        }

        private static final TemporalUnit[] units = new TemporalUnit[]{
                ChronoUnit.MINUTES, ChronoUnit.HOURS, ChronoUnit.DAYS,
                ChronoUnit.WEEKS, ChronoUnit.MONTHS
        };

        public TemporalUnit getTemporalUnit() {
            return units[ordinal()];
        }
    };
}

