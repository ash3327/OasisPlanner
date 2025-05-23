package com.aurora.oasisplanner.presentation.dialogs.alarmeditdialog.components.viewargsbox;

import android.content.Context;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.databinding.TagViewTagsBinding;
import com.aurora.oasisplanner.presentation.util.OnTextChangeListener;
import com.aurora.oasisplanner.presentation.widgets.taginputeidittext.TagInputEditText;

public class AEDTagsBox extends AEDBaseBox {
    private TagViewTagsBinding binding;
    private OnTextChangeListener mOnTextChangeListener;

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
    @Override
    protected View getChildContainer() {
        return binding.tagTagsTv;
    }
    protected ImageView getAddRemoveButton() {
        return binding.btnDelete;
    }

    public void setText(String tags) {
        getTagViewText().setTags(tags);
    }
//    public void setOnChangeListener(OnChangeListener ocl) {
//        getTagViewText().addTextChangedListener(new OnTextChangeListener() {
//            @Override
//            public void afterTextChanged(Editable s) {
//                ocl.onChange(s.toString());
//            }
//        });
//    }
    public void setOnClickListener(OnClickListener l) {
        getTagViewText().setOnClickListener(l);
    }
    /** Note: setShowing(false) must be set BEFORE this call - you can ignore if it is setShowing(true). */
    public void setOnChangeListener(OnChangeListener ocl) {
        if (mOnTextChangeListener != null)
            getTagViewText().removeTextChangedListener(mOnTextChangeListener);

        getTagViewText().addTextChangedListener(mOnTextChangeListener = new OnTextChangeListener() {
            @Override
            public void afterTextChanged(Editable s) {
                ocl.onChange(s.toString());
            }
        });
        getAddRemoveButton().setOnClickListener((v)->{
            ocl.onChange(mIsShowing ? null : getText());
            setShowing(!mIsShowing);
        });
    }

    @Override
    public void setShowing(boolean isShowing) {
        super.setShowing(isShowing);
        getAddRemoveButton().setImageResource(mIsShowing ? R.drawable.ic_symb_remove : R.drawable.ic_symb_plus);
    }

    public interface OnChangeListener {
        void onChange(String text);
    }
}
