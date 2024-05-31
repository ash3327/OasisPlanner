package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components.viewargsbox;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.databinding.TagViewTextBinding;
import com.aurora.oasisplanner.presentation.util.OnTextChangeListener;

public class AEDContentBox extends AEDBaseBox {
    private TagViewTextBinding binding;
    private OnTextChangeListener mOnTextChangeListener;

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
    @Override
    protected View getChildContainer() {
        return binding.tagContentTv;
    }
    protected ImageView getAddRemoveButton() {
        return binding.btnDelete;
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
    /** Note: setShowing(false) must be set BEFORE this call - you can ignore if it is setShowing(true). */
    public void setOnChangeListener(OnChangeListener ocl) {
        if (mOnTextChangeListener != null)
            getEditText().removeTextChangedListener(mOnTextChangeListener);
        getEditText().addTextChangedListener(mOnTextChangeListener = new OnTextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                ocl.onChange(s.toString());
            }
        });
        getAddRemoveButton().setOnClickListener((v)->{
            ocl.onChange(mIsShowing ? null : getText());
            setShowing(!mIsShowing);
            getAddRemoveButton().setVisibility(mIsShowing ? VISIBLE : GONE);
        });
    }

    public interface OnChangeListener {
        void onChange(String content);
    }
}
