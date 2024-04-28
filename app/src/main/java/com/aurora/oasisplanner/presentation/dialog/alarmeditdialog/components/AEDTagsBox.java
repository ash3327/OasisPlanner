package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;

import com.aurora.oasisplanner.databinding.TagTagsEditBinding;
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
    public void setText(String tags) {
        getTagEditText().setTags(tags);
    }

    private TagInputEditText getTagEditText() { return binding.tagTagsTv; }
    @Override
    protected EditText getEditText() { return getTagEditText(); }
    @Override
    protected ImageView getIcon() {
        return binding.icon;
    }
}
