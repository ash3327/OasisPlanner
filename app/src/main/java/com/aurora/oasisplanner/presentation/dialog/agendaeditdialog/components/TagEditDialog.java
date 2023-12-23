package com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components;

import android.app.Dialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.model.entities._Alarm;
import com.aurora.oasisplanner.data.model.pojo.AlarmList;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.databinding.ItemEditTagBinding;
import com.aurora.oasisplanner.databinding.SpinnerElementBinding;
import com.aurora.oasisplanner.databinding.TagTypeSpinnerElementBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Set;

public class TagEditDialog extends AppCompatDialogFragment {
    public static final String EXTRA_ALARM_LISTS = "alarmLists";

    private AlertDialog dialog;
    public TagType type = TagType.LOC;
    private ItemEditTagBinding vbinding;
    private Set<AlarmList> checkedList;
    private Runnable updateUiFunction = ()->{};

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ItemEditTagBinding binding = ItemEditTagBinding.inflate(getLayoutInflater());
        onBind(binding);

        ViewGroup vg = (ViewGroup) binding.getRoot();
        builder.setView(vg);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    public void onBind(ItemEditTagBinding binding) {
        vbinding = binding;
        vbinding.header.setText(R.string.page_overhead_edit_tag);
        //binding.header.setText(agenda.agenda.id <= 0 ? R.string.page_overhead_new_agenda : R.string.page_overhead_edit_agenda);
        vbinding.confirmBtn.setOnClickListener(
                (v)->onConfirm()
        );
        vbinding.cancelButton.setOnClickListener(
                (v)->onCancel()
        );
        SpinAdapter spinAdapter = new SpinAdapter(getLayoutInflater(), TagType.values());
        AutoCompleteTextView spinnerType = vbinding.tagTypeTv;
        TextInputLayout til = vbinding.tagTypeTil;
        spinnerType.setAdapter(spinAdapter);
        setOnItemSelectListener(spinnerType, til,
                type.toString(), type.getDrawable(),
                (AdapterView.OnItemClickListener) (adapterView, view, position, id) -> {
                    type = TagType.values()[position];
                    changeUiToInputType(type);
                });
    }

    public void changeUiToInputType(TagType type) {
        vbinding.tagTypeTil.setStartIconDrawable(type.getDrawable());
        //TODO
        vbinding.tagContentBox.setVisibility(View.GONE);
        vbinding.tagDatetimeBox.setVisibility(View.GONE);
        switch(type) {
            case LOC:
                vbinding.tagContentBox.setVisibility(View.VISIBLE);
                break;
            case ALARM:
                vbinding.tagDatetimeBox.setVisibility(View.VISIBLE);
                break;
        }
        vbinding.tagContentBox.requestLayout();
        vbinding.tagDatetimeBox.requestLayout();
    }

    private void setOnItemSelectListener(AutoCompleteTextView spinner, TextInputLayout til,
                                        String text, Drawable drawable,
                                        AdapterView.OnItemClickListener listener) {
        spinner.setText(text);
        til.setStartIconDrawable(drawable);
        spinner.setOnItemClickListener(listener);
        listener.onItemClick(null, spinner.getRootView(), type.getType(), 0);
    }

    public void scrollTo(int pos, RecyclerView recyclerView) {
        Context rContext = recyclerView.getContext();
        RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(rContext){
            @Override
            protected int getVerticalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };
        smoothScroller.setTargetPosition(pos > 0 ? pos-1 : 0);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager != null)
            layoutManager.startSmoothScroll(smoothScroller);
    }

    public void onConfirm() {
        saveTags();
        dialog.dismiss();
    }
    public void onCancel() {
        updateUiFunction.run();
        dialog.dismiss();
    }

    public void saveTags() {
        TextInputEditText tiet = vbinding.tagContentTv;
        SpannableStringBuilder ssb = new SpannableStringBuilder(tiet.getText());
        ssb.clearSpans();
        assert checkedList != null;
        for (AlarmList checked : checkedList)
            checked.alarmList.putArgs(type.name(), ssb);
        updateUiFunction.run();
    }

    public void setSelectedList(Set<AlarmList> checkedList) {
        this.checkedList = checkedList;
    }

    public void setUpdateUiFunction(Runnable updateUiFunction) {
        this.updateUiFunction = updateUiFunction;
    }

    public static class SpinAdapter extends ArrayAdapter<TagType> {
        private LayoutInflater li;
        private TagType[] vals;

        public SpinAdapter(LayoutInflater li, @NonNull TagType[] typeList) {
            super(li.getContext(), R.layout.tagtype_spinner_element);
            this.li      = li;
            this.vals    = typeList;
        }

        @Override
        public int getCount() {
            return vals.length;
        }

        @Override
        public TagType getItem(int position) {
            return vals[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getDaView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getDaView(position, convertView, parent);
        }

        public View getDaView(int position, View convertView, ViewGroup parent) {
            TagTypeSpinnerElementBinding binding = TagTypeSpinnerElementBinding.inflate(li);
            TagType type = getItem(position);
            binding.text.setText(type.toString());
            binding.icon.setImageDrawable(type.getDrawable());
            return binding.getRoot();
        }
    }
}

