package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components.viewargsbox;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.tags.NotifType;
import com.aurora.oasisplanner.databinding.TagViewSubalarmDatetimeBinding;

public class AEDDatetimeBox extends AEDBaseBox {
    private TagViewSubalarmDatetimeBinding binding;
    private OnChangeListener ocl;
    private NotifType mNotifType = null;

    public AEDDatetimeBox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init() {

    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void initBinding(Context context) {
        binding = TagViewSubalarmDatetimeBinding.inflate(LayoutInflater.from(context), this, true);
    }
    @Override
    protected TextView getTitleView() {
        return binding.tagTitleTv;
    }

    @Override
    protected ImageView getIcon() {
        return binding.icon;
    }

    @Override
    protected TextView getEditText() {
        return getTextView();
    }

    protected TextView getTextView() { return binding.tagContentTv; }

    @Override
    protected View getChildContainer() {
        return binding.tagContentTv;
    }

    @SuppressLint("SetTextI18n")
    public void setNotifType(NotifType notifType) {
        mNotifType = notifType;
        getTextView().setText(notifType.getDescription());
        if (ocl != null)
            ocl.onChange(notifType);
    }
    public void setOnChangeListener(OnChangeListener ocl) {
        this.ocl = ocl;
        getTextView().setOnClickListener((v)->{
            // open up new dialog for input
            AppModule.retrieveEditEventUseCases().invokeDialogForSubalarm(
                    mNotifType,
                    this::setNotifType
            );
        });
    }

    public interface OnChangeListener {
        void onChange(NotifType notifType);
    }
}
