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

import androidx.annotation.Nullable;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.databinding.AddItemEditTextBinding;
import com.aurora.oasisplanner.databinding.PageHeaderBinding;

import org.w3c.dom.Text;

public class PageHeader extends LinearLayout {
    private final TextView tv;
    private final PageHeaderBinding binding;

    public PageHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = PageHeaderBinding.inflate(inflater, this, true);

        tv = binding.pageHeader0;

        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PageHeader,
                0, 0);
        try {
            CharSequence text = a.getText(R.styleable.PageHeader_text);
            if (text == null || text.toString().isEmpty()) text = "Default Title";
            tv.setText(text);
        } finally {
            a.recycle();
        }
    }

    public TextView getTextView() {
        return tv;
    }
}
