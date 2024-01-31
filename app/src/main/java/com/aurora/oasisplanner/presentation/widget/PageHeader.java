package com.aurora.oasisplanner.presentation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.databinding.PageHeaderBinding;

public class PageHeader extends LinearLayout {
    public static final int NIL = -1;
    private final TextView tv;
    private final ImageView editButton, detailsButton;
    private final PageHeaderBinding binding;

    public PageHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = PageHeaderBinding.inflate(inflater, this, true);

        tv = binding.pageHeaderTv;
        editButton = binding.pageHeaderIcon;
        detailsButton = binding.pageHeaderIcon2;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PageHeader,
                0, 0);

        CharSequence text = null; int iconSrc = NIL;

        try {
            text = a.getText(R.styleable.PageHeader_text);
            iconSrc = a.getResourceId(R.styleable.PageHeader_icon_src, NIL);
        } finally {
            a.recycle();
        }

        tv.setText(text == null ? "Default Title" : text);
        setEditButtonIcon(iconSrc);
    }

    public void setEditButtonIcon(@DrawableRes int iconSrc) {
        if (iconSrc != NIL) {
            editButton.setVisibility(VISIBLE);
            editButton.setImageResource(iconSrc);
        } else
            editButton.setVisibility(GONE);
    }

    public TextView getTextView() {
        return tv;
    }
    public ImageView getEditButton() {
        return editButton;
    }
    public ImageView getDetailsButton() {
        return detailsButton;
    }

    public interface BooleanFunc { boolean eval(); }
    public interface BranchFunc { void eval(boolean criteria); }
    public void setEditButtonBehavior(BooleanFunc criteria, BranchFunc operator) {
        editButton.setOnClickListener(
                (v)->{
                    if (criteria.eval()) {
                        operator.eval(true);
                        v.setAlpha(.5f);
                    } else {
                        operator.eval(false);
                        v.setAlpha(1);
                    }
                }
        );
    }
    public void setDetailsButtonBehavior(BooleanFunc criteria, BranchFunc operator) {
        detailsButton.setOnClickListener(
                (v)->{
                    if (criteria.eval()) {
                        operator.eval(true);
                        detailsButton.setImageResource(R.drawable.ic_dir_collapse);
                    } else {
                        operator.eval(false);
                        detailsButton.setImageResource(R.drawable.ic_dir_expand);
                    }
                }
        );
    }
}
