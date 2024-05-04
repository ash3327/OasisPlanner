package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components.viewargsbox;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.CallSuper;

import com.google.android.material.textfield.TextInputLayout;

import java.util.function.Function;

public abstract class AEDDropdownMenu extends AEDBaseBox {

    protected int mPosition = 0;
    private AdapterView.OnItemClickListener ocl_base = null;

    public AEDDropdownMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {
        getSpinner().setInputType(InputType.TYPE_NULL);
        getSpinner().setKeyListener(null);
        getSpinner().setFocusable(false);
    }

    public abstract AutoCompleteTextView getSpinner();
    public abstract TextInputLayout getSpinnerTil();

    @CallSuper
    protected void executeOnClickListener(AdapterView<?> parent, View view, int pos, long id) {
        mPosition = pos;
        if (ocl_base != null)
            ocl_base.onItemClick(parent, view, pos, id);
    }

    public void setOnItemSelectListener(ArrayAdapter spinAdapter, int pos, Drawable drawable,
                                        AdapterView.OnItemClickListener listener,
                                        Function<String, Integer> getType) {
        AutoCompleteTextView spinner = getSpinner();
        TextInputLayout til = getSpinnerTil();
        til.setStartIconDrawable(drawable);
        spinner.setText(spinAdapter.getItem(pos).toString());
        spinner.setOnItemClickListener(ocl_base = (adapterView, view, position, id)->{
            mPosition = position;
            listener.onItemClick(adapterView, view, position, id);
        });
        spinner.setAdapter(spinAdapter);
        mPosition = pos;
        listener.onItemClick(null, spinner.getRootView(), pos, 0);
    }
}
