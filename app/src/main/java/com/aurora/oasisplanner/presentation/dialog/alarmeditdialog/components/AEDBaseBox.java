package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;

import com.google.android.material.textfield.TextInputLayout;

import java.util.function.Function;

public abstract class AEDBaseBox extends LinearLayout {

    public AEDBaseBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initBinding(context);
        init();
    }

    protected abstract void init();
    protected abstract void initBinding(Context context);
}
