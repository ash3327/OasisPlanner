package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components.editargsbox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aurora.oasisplanner.R;
import com.google.android.material.textfield.TextInputLayout;

import java.util.function.Function;

public abstract class AEDBaseBox extends LinearLayout {

    public AEDBaseBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBinding(context);
        handleAttributes(context, attrs);
        init();
    }

    private void handleAttributes(Context context, AttributeSet attrs) {
        // attributes handling
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AEDBaseBox);

        String textHint = a.getString(R.styleable.AEDBaseBox_textHint);
        Drawable iconDrawable = a.getDrawable(R.styleable.AEDBaseBox_mainIcon);
        float textSize = a.getDimensionPixelSize(R.styleable.AEDBaseBox_textSize, -1);

        if (textHint != null)
            getTil().setHint(textHint);
        if (iconDrawable != null)
            getIcon().setImageDrawable(iconDrawable);
        if (textSize != -1)
            getEditText().setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

        a.recycle();
    }

    protected abstract void init();
    protected abstract void initBinding(Context context);
    protected abstract ImageView getIcon();
    protected abstract EditText getEditText();
    protected abstract TextInputLayout getTil();
}
