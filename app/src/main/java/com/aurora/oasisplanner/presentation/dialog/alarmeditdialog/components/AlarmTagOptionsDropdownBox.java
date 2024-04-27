package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AutoCompleteTextView;

import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.databinding.TagChooseTypeBinding;
import com.aurora.oasisplanner.databinding.TagOptionsDropdownBinding;
import com.google.android.material.textfield.TextInputLayout;

public class AlarmTagOptionsDropdownBox extends AlarmTagDropdownMenu {
    private TagOptionsDropdownBinding binding;

    public AlarmTagOptionsDropdownBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initBinding(Context context) {
        binding = TagOptionsDropdownBinding.inflate(LayoutInflater.from(context), this, true);
    }
    @Override
    public AutoCompleteTextView getSpinner() {
        return binding.tagChoiceTv;
    }
    @Override
    public TextInputLayout getSpinnerTil() {
        return binding.tagChoiceTil;
    }

    public void changeType(TagType type) {
        getSpinnerTil().setStartIconDrawable(type.getDrawable());
    }
}
