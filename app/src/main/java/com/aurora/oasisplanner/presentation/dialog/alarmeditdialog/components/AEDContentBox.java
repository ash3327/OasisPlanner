package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.tags.NotifType;
import com.aurora.oasisplanner.databinding.TagTextEditBinding;
import com.aurora.oasisplanner.presentation.util.OnTextChangeListener;
import com.google.android.material.textfield.TextInputLayout;

public class AEDContentBox extends AEDBaseBox {
    private TagTextEditBinding binding;

    public AEDContentBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        handleAttributes(context, attrs);
    }

    private void handleAttributes(Context context, AttributeSet attrs) {
        // attributes handling
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AEDContentBox);

        String textHint = a.getString(R.styleable.AEDContentBox_textHint);
        boolean focusable = a.getBoolean(R.styleable.AEDContentBox_focusable, true);
        boolean multiLine = a.getBoolean(R.styleable.AEDContentBox_multiLine, false);

        if (textHint != null)
            binding.tagContentTil.setHint(textHint);
        binding.tagContentTv.setFocusable(focusable);
        binding.tagContentTv.setInputType(multiLine ? InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT);

        a.recycle();
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initBinding(Context context) {
        binding = TagTextEditBinding.inflate(LayoutInflater.from(context), this, true);
    }

    @Override
    public EditText getEditText() {
        return binding.tagContentTv;
    }
    private TextInputLayout getTil() {
        return binding.tagContentTil;
    }
    @Override
    protected ImageView getIcon() {
        return binding.icon;
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
    public void setOnChangeListener(OnChangeListener ocl) {
        getEditText().addTextChangedListener(new OnTextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                ocl.onChange(s.toString());
            }
        });
    }

    public interface OnChangeListener {
        void onChange(String content);
    }
}
