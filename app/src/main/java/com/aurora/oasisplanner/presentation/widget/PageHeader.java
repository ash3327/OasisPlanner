package com.aurora.oasisplanner.presentation.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.databinding.AddItemEditTextBinding;
import com.aurora.oasisplanner.databinding.PageHeaderBinding;

import org.w3c.dom.Text;

public class PageHeader extends LinearLayout {
    public static final int NIL = -1;
    private final TextView tv;
    private final ImageView icon;
    private final PageHeaderBinding binding;

    public PageHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = PageHeaderBinding.inflate(inflater, this, true);

        tv = binding.pageHeaderTv;
        icon = binding.pageHeaderIcon;

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
        setIconDrawable(iconSrc);
    }

    public void setIconDrawable(@DrawableRes int iconSrc) {
        if (iconSrc != NIL) {
            icon.setVisibility(VISIBLE);
            icon.setImageResource(iconSrc);
        } else
            icon.setVisibility(GONE);
    }

    public TextView getTextView() {
        return tv;
    }
    public ImageView getImgButton() {
        return icon;
    }
}
