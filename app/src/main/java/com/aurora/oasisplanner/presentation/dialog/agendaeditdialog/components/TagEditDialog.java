package com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components;

import android.app.Dialog;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.databinding.ItemEditTagBinding;
import com.aurora.oasisplanner.databinding.SpinnerElementBinding;
import com.aurora.oasisplanner.databinding.TagTypeSpinnerElementBinding;

import java.util.List;

public class TagEditDialog extends AppCompatDialogFragment {
    public static final String EXTRA_ALARM_LISTS = "alarmLists";

    private AlertDialog dialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //assert getArguments() != null;

        //Bundle alarmLists = getArguments().getBundle(EXTRA_ALARM_LISTS);

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
        binding.header.setText(R.string.page_overhead_edit_tag);
        //binding.header.setText(agenda.agenda.id <= 0 ? R.string.page_overhead_new_agenda : R.string.page_overhead_edit_agenda);
        binding.confirmBtn.setOnClickListener(
                (v)->onConfirm()
        );
        binding.cancelButton.setOnClickListener(
                (v)->onCancel()
        );
        SpinAdapter spinAdapter = new SpinAdapter(getLayoutInflater(), TagType.values());
        binding.itemEditTagDiagSpinner.setAdapter(spinAdapter);
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

    public void associateTitle(EditText editText) {
        //editText.setText(agenda.agenda.title);
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //agenda.agenda.title = editText.getText().toString();
            }
        };
        editText.setTag(textWatcher);
        editText.addTextChangedListener(textWatcher);
    }

    public void onConfirm() {
        saveTags();
        dialog.dismiss();
    }
    public void onCancel() {
        dialog.dismiss();
    }

    public void saveTags() {
        // TODO: Edit Tags
    }

    public static class SpinAdapter extends BaseAdapter {
        private LayoutInflater li;
        private TagType[] vals;

        public SpinAdapter(LayoutInflater li, @NonNull TagType[] typeList) {
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

