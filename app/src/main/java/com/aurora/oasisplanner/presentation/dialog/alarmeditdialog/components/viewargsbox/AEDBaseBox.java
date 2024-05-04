package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components.viewargsbox;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aurora.oasisplanner.R;
import com.google.android.material.textfield.TextInputLayout;

public abstract class AEDBaseBox extends LinearLayout {

    protected String mTextHint;
    protected Drawable mIconDrawable;
    protected float mTextSize;

    public AEDBaseBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBinding(context);
        handleAttributes(context, attrs);
        init();
    }

    private void handleAttributes(Context context, AttributeSet attrs) {
        // attributes handling
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AEDBaseBox);

        mTextHint = a.getString(R.styleable.AEDBaseBox_textHint);
        mIconDrawable = a.getDrawable(R.styleable.AEDBaseBox_mainIcon);
        mTextSize = a.getDimensionPixelSize(R.styleable.AEDBaseBox_textSize, -1);

        getTitleView().setText(mTextHint);
        if (mIconDrawable != null)
            getIcon().setImageDrawable(mIconDrawable);
        if (mTextSize != -1 && getEditText() != null)
            getEditText().setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);

        a.recycle();
    }

    public void setShowing(boolean isShowing) {
        getChildContainer().setVisibility(isShowing ? VISIBLE : GONE);
    }

    protected abstract void init();
    protected abstract void initBinding(Context context);
    protected abstract TextView getTitleView();
    protected abstract ImageView getIcon();
    protected abstract TextView getEditText();
    protected abstract View getChildContainer();
}
