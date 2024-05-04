package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components.editargsbox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.aurora.oasisplanner.data.tags.NotifType;
import com.aurora.oasisplanner.databinding.TagEditSubalarmDatetimeBinding;
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components.DateType;
import com.aurora.oasisplanner.presentation.util.OnTextChangeListener;
import com.google.android.material.textfield.TextInputLayout;

public class AEDDatetimeBox extends AEDDropdownMenu {
    private TagEditSubalarmDatetimeBinding binding;
    private OnChangeListener ocl;

    public AEDDatetimeBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void initBinding(Context context) {
        binding = TagEditSubalarmDatetimeBinding.inflate(LayoutInflater.from(context), this, true);
        getNumEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        getHourPicker().setMinValue(0);
        getHourPicker().setMaxValue(23);
        getHourPicker().setFormatter((v)-> String.format("%02d", v));
        getHourPicker().setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        getMinutePicker().setMinValue(0);
        getMinutePicker().setMaxValue(59);
        getMinutePicker().setFormatter((v)-> String.format("%02d", v));
        getMinutePicker().setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
    }
    @Override
    public AutoCompleteTextView getSpinner() {
        return binding.tagDateTypeTv;
    }
    @Override
    public TextInputLayout getSpinnerTil() {
        return binding.tagDateTypeTil;
    }
    @Override
    protected EditText getEditText() { return getSpinner(); }
    public EditText getNumEditText() { return binding.tagDateTv; }
    public NumberPicker getHourPicker() { return binding.tagTimeHourPicker; }
    public NumberPicker getMinutePicker() { return binding.tagTimeMinutePicker; }
    public LinearLayout getTimeBox() { return binding.tagTimeBox; }

    public void setDateType(DateType dateType) {
        getTimeBox().setVisibility(dateType.hasTime() ? View.VISIBLE : View.GONE);
        getTimeBox().requestLayout();
    }

    public NotifType getNotifType(DateType dt) {
        int val;
        try {
            val = Integer.parseInt(getNumEditText().getText().toString());
        } catch (Exception e) {
            return null;
        }
        NotifType notifType;
        if (dt.hasTime())
            notifType = new NotifType(val, dt, getHourPicker().getValue(), getMinutePicker().getValue());
        else
            notifType = new NotifType(val, dt);
        return notifType;
    }
    @Override
    protected ImageView getIcon() {
        return binding.iconTop;
    }

    @SuppressLint("SetTextI18n")
    public void setNotifType(NotifType notifType) {
        setDateType(notifType.dateType);
        getNumEditText().setText(notifType.val+"");
        getSpinner().setListSelection(notifType.dateType.ordinal());
        if (notifType.dateType.hasTime()) {
            getHourPicker().setValue(notifType.hour);
            getMinutePicker().setValue(notifType.minute);
        }
    }
    public void setOnChangeListener(OnChangeListener ocl) {
        this.ocl = ocl;
        getNumEditText().addTextChangedListener(new OnTextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                onChange();
            }
        });
        getSpinner().setOnItemClickListener(this::executeOnClickListener);
        getHourPicker().setOnValueChangedListener((v,oldVal,newVal)->{
            onChange();
        });
        getMinutePicker().setOnValueChangedListener((v,oldVal,newVal)->{
            onChange();
        });
    }

    @Override
    protected void executeOnClickListener(AdapterView<?> parent, View view, int pos, long id) {
        super.executeOnClickListener(parent, view, pos, id);
        onChange();
    }
    private void onChange() {
        try {
            int position = mPosition;
            DateType dateType = DateType.values()[position];
            NotifType notifType = getNotifType(dateType);
            ocl.onChange(notifType);
        } catch (Exception e) {} // only set NotifType if is valid.
    }
    public interface OnChangeListener {
        void onChange(NotifType notifType);
    }
}
