package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.databinding.TagOptionsDropdownBinding;
import com.google.android.material.textfield.TextInputLayout;

public class AEDOptionsDropdownBox extends AEDDropdownMenu {
    private TagOptionsDropdownBinding binding;

    public AEDOptionsDropdownBox(Context context, AttributeSet attrs) {
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
    @Override
    protected EditText getEditText() { return getSpinner(); }
    @Override
    protected ImageView getIcon() {
        return binding.icon;
    }
}
