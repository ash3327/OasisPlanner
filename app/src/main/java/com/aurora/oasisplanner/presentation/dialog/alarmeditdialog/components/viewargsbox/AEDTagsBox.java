package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components.viewargsbox;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurora.oasisplanner.databinding.TagViewTagsBinding;
import com.aurora.oasisplanner.presentation.util.OnTextChangeListener;
import com.aurora.oasisplanner.presentation.widget.taginputeidittext.TagInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AEDTagsBox extends AEDBaseBox {
    private TagViewTagsBinding binding;

    public AEDTagsBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initBinding(Context context) {
        binding = TagViewTagsBinding.inflate(LayoutInflater.from(context), this, true);
        getEditText().setText("");
    }

    public String getText() {
        try {
            return getEditText().getText().toString();
        } catch (Exception e) {
            return "";
        }
    }

    private TagInputEditText getTagViewText() { return binding.tagTagsTv; }
    @Override
    protected TextView getTitleView() {
        return binding.tagTitleTv;
    }
    @Override
    protected EditText getEditText() { return getTagViewText(); }
    @Override
    protected ImageView getIcon() {
        return binding.icon;
    }

    public void setText(String tags) {
        getTagViewText().setTags(tags);
    }
    public void setOnChangeListener(OnChangeListener ocl) {
        getTagViewText().addTextChangedListener(new OnTextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                ocl.onChange(s.toString());
            }
        });
    }

    public interface OnChangeListener {
        void onChange(String text);
    }
}
