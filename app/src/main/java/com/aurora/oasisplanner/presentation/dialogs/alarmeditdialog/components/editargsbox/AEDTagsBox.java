package com.aurora.oasisplanner.presentation.dialogs.alarmeditdialog.components.editargsbox;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;

import com.aurora.oasisplanner.databinding.TagEditTagsBinding;
import com.aurora.oasisplanner.presentation.util.OnTextChangeListener;
import com.aurora.oasisplanner.presentation.widgets.taginputeidittext.TagInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AEDTagsBox extends AEDBaseBox {
    private TagEditTagsBinding binding;

    public AEDTagsBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initBinding(Context context) {
        binding = TagEditTagsBinding.inflate(LayoutInflater.from(context), this, true);
        getEditText().setText("");
    }

    public String getText() {
        try {
            return getEditText().getText().toString();
        } catch (Exception e) {
            return "";
        }
    }

    private TagInputEditText getTagEditText() { return binding.tagTagsTv; }
    @Override
    protected EditText getEditText() { return getTagEditText(); }
    @Override
    protected ImageView getIcon() {
        return binding.icon;
    }
    @Override
    protected TextInputLayout getTil() {
        return binding.tagTagsTil;
    }

    public void setText(String tags) {
        getTagEditText().setTags(tags);
    }
    public void setOnChangeListener(OnChangeListener ocl) {
        getTagEditText().addTextChangedListener(new OnTextChangeListener() {
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
