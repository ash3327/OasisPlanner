package com.aurora.oasisplanner.presentation.dialogs.alarmeditdialog.components.editargsbox;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.databinding.TagEditTextBinding;
import com.aurora.oasisplanner.presentation.util.OnTextChangeListener;
import com.google.android.material.textfield.TextInputLayout;

public class AEDContentBox extends AEDBaseBox {
    private TagEditTextBinding binding;

    public AEDContentBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        handleAttributes(context, attrs);
    }

    private void handleAttributes(Context context, AttributeSet attrs) {
        // attributes handling
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AEDContentBox);

        boolean focusable = a.getBoolean(R.styleable.AEDContentBox_focusable, true);
        boolean multiLine = a.getBoolean(R.styleable.AEDContentBox_multiLine, false);

        getEditText().setFocusable(focusable);
        getEditText().setInputType(multiLine ? InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_CLASS_TEXT : InputType.TYPE_CLASS_TEXT);

        a.recycle();
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initBinding(Context context) {
        binding = TagEditTextBinding.inflate(LayoutInflater.from(context), this, true);
    }

    @Override
    public EditText getEditText() {
        return binding.tagContentTv;
    }
    @Override
    protected TextInputLayout getTil() {
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
