package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.aurora.oasisplanner.databinding.TagTextEditBinding;

public class AlarmTagContentBox extends AlarmTagBaseBox {
    private TagTextEditBinding binding;

    public AlarmTagContentBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initBinding(Context context) {
        binding = TagTextEditBinding.inflate(LayoutInflater.from(context), this, true);
    }

    public String getText() {
        try {
            return binding.tagContentTv.getText().toString();
        } catch (Exception e) {
            return null;
        }
    }
}
