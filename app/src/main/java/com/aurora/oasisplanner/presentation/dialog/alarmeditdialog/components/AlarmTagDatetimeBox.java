package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.tags.NotifType;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.databinding.TagSubalarmDatetimePickBinding;
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.AlarmTagEditDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AlarmTagDatetimeBox extends AlarmTagDropdownMenu {
    private TagSubalarmDatetimePickBinding binding;

    public AlarmTagDatetimeBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initBinding(Context context) {
        binding = TagSubalarmDatetimePickBinding.inflate(LayoutInflater.from(context), this, true);
        binding.tagDateTv.setInputType(InputType.TYPE_CLASS_NUMBER);
        binding.tagTimeHourPicker.setMinValue(0);
        binding.tagTimeHourPicker.setMaxValue(23);
        binding.tagTimeHourPicker.setFormatter((v)->{return String.format("%02d", v);});
        binding.tagTimeHourPicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        binding.tagTimeMinutePicker.setMinValue(0);
        binding.tagTimeMinutePicker.setMaxValue(59);
        binding.tagTimeMinutePicker.setFormatter((v)->{return String.format("%02d", v);});
        binding.tagTimeMinutePicker.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }
    @Override
    public AutoCompleteTextView getSpinner() {
        return binding.tagDateTypeTv;
    }
    @Override
    public TextInputLayout getSpinnerTil() {
        return binding.tagDateTypeTil;
    }

    public void setDateType(AlarmTagEditDialog.DateType dateType) {
        binding.tagTimeBox.setVisibility(dateType.hasTime() ? View.VISIBLE : View.GONE);
        binding.tagTimeBox.requestLayout();
    }

    public NotifType getNotifType(AlarmTagEditDialog.DateType dt) {
        TextInputEditText tietD = binding.tagDateTv;
        int val;
        try {
            val = Integer.parseInt(tietD.getText().toString());
        } catch (Exception e) {
            return null;
        }
        NotifType notifType;
        if (dt.hasTime())
            notifType = new NotifType(val, dt, binding.tagTimeHourPicker.getValue(), binding.tagTimeMinutePicker.getValue());
        else
            notifType = new NotifType(val, dt);
        return notifType;
    }
}
