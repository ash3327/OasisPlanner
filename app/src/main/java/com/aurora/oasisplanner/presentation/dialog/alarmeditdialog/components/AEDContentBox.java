package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.databinding.TagTextEditBinding;
import com.google.android.material.textfield.TextInputLayout;

public class AEDContentBox extends AEDBaseBox {
    private TagTextEditBinding binding;

    public AEDContentBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        // attributes handling
        @SuppressLint("CustomViewStyleable")
        TypedArray a1 = context.obtainStyledAttributes(attrs, R.styleable.AEDBaseBox);
        TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.AEDContentBox);

        String textHint = a2.getString(R.styleable.AEDContentBox_textHint);
        Drawable iconDrawable = a1.getDrawable(R.styleable.AEDBaseBox_mainIcon);
        boolean focusable = a2.getBoolean(R.styleable.AEDContentBox_focusable, true);
        float textSize = a2.getDimensionPixelSize(R.styleable.AEDContentBox_textSize, -1);

        if (textHint != null)
            binding.tagContentTil.setHint(textHint);
        if (iconDrawable != null)
            binding.icon.setImageDrawable(iconDrawable);
        binding.tagContentTv.setFocusable(focusable);
        if (textSize != -1)
            binding.tagContentTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        a1.recycle();
        a2.recycle();
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initBinding(Context context) {
        binding = TagTextEditBinding.inflate(LayoutInflater.from(context), this, true);
    }

    public EditText getEditText() {
        return binding.tagContentTv;
    }
    private TextInputLayout getTil() {
        return binding.tagContentTil;
    }

    public String getText() {
        try {
            return getEditText().getText().toString();
        } catch (Exception e) {
            return null;
        }
    }
    public void setText(String text) {
        getEditText().setText(text);
    }

    public void setOnClickListener(View.OnClickListener l) {
        getTil().setOnClickListener(l);
        getEditText().setOnClickListener(l);
    }
}
