package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import com.aurora.oasisplanner.databinding.TagTagsEditBinding;
import com.aurora.oasisplanner.databinding.TagTextEditBinding;

public class AlarmTagTagsBox extends AlarmTagBaseBox {
    private TagTagsEditBinding binding;

    public AlarmTagTagsBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initBinding(Context context) {
        binding = TagTagsEditBinding.inflate(LayoutInflater.from(context), this, true);
        binding.tagTagsTv.setText("");
    }

    public String getText() {
        try {
            return binding.tagTagsTv.getText().toString();
        } catch (Exception e) {
            return "";
        }
    }
}
