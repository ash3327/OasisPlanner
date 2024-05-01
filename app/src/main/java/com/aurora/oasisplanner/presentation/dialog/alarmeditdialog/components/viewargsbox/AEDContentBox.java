package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components.viewargsbox;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.databinding.TagViewTextBinding;
import com.aurora.oasisplanner.presentation.util.OnTextChangeListener;

public class AEDContentBox extends AEDBaseBox {
    private TagViewTextBinding binding;

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
        binding = TagViewTextBinding.inflate(LayoutInflater.from(context), this, true);
    }

    @Override
    public EditText getEditText() {
        return binding.tagContentTv;
    }
    @Override
    protected ImageView getIcon() {
        return binding.icon;
    }
    @Override
    protected TextView getTitleView() {
        return binding.tagTitleTv;
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

    public void setOnClickListener(OnClickListener l) {
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
