package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputLayout;

import java.util.function.Function;

public abstract class AEDDropdownMenu extends AEDBaseBox {

    public AEDDropdownMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        getSpinner().setInputType(InputType.TYPE_NULL);
        getSpinner().setKeyListener(null);
    }

    public abstract AutoCompleteTextView getSpinner();
    public abstract TextInputLayout getSpinnerTil();

    public void setOnItemSelectListener(ArrayAdapter spinAdapter, String text, Drawable drawable,
                                        AdapterView.OnItemClickListener listener,
                                        Function<String, Integer> getType) {
        AutoCompleteTextView spinner = getSpinner();
        TextInputLayout til = getSpinnerTil();
        spinner.setText(text);
        til.setStartIconDrawable(drawable);
        spinner.setOnItemClickListener(listener);
        spinner.setAdapter(spinAdapter);
        listener.onItemClick(null, spinner.getRootView(), getType.apply(null), 0);
    }
}
