package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;

import com.aurora.oasisplanner.databinding.TagTagsEditBinding;
import com.aurora.oasisplanner.presentation.util.OnTextChangeListener;
import com.aurora.oasisplanner.presentation.widget.taginputeidittext.TagInputEditText;

public class AEDTagsBox extends AEDBaseBox {
    private TagTagsEditBinding binding;

    public AEDTagsBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initBinding(Context context) {
        binding = TagTagsEditBinding.inflate(LayoutInflater.from(context), this, true);
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
