package com.aurora.oasisplanner.presentation.dialogs.agendaeditdialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.presentation.dialogs.agendaeditdialog.components.ActivityAdapter;
import com.aurora.oasisplanner.presentation.util.Switch;
import com.aurora.oasisplanner.databinding.EventMoveDialogBinding;
import com.aurora.oasisplanner.databinding.TagTypeSpinnerElementBinding;

import java.util.Set;

public class EventMoveDialog extends AppCompatDialogFragment {
    public static final String EXTRA_ALARM_LISTS = "alarmLists";

    private AlertDialog dialog;
    private EventMoveDialogBinding vbinding;
    private Set<_Event> checkedList;
    private Agenda agenda;
    private Activity activity;
    private Runnable updateUiFunction = ()->{};
    private int idx = 0;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        vbinding = EventMoveDialogBinding.inflate(getLayoutInflater());
        onBind();

        ViewGroup vg = (ViewGroup) vbinding.getRoot();
        builder.setView(vg);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    public void onBind() {
        vbinding.header.setText(R.string.page_overhead_edit_tag);
        //binding.header.setText(agenda.agenda.id <= 0 ? R.string.page_overhead_new_agenda : R.string.page_overhead_edit_agenda);
        vbinding.confirmBtn.setOnClickListener(
                (v)->onConfirm()
        );
        vbinding.cancelButton.setOnClickListener(
                (v)->onCancel()
        );

        RecyclerView recyclerView = vbinding.emdSelector;
        recyclerView.setLayoutManager(new LinearLayoutManager(vbinding.getRoot().getContext()));
        recyclerView.setHasFixedSize(false);

        Switch tSwitch = new Switch(false);
        final ActivityAdapter adapter = new ActivityAdapter(null, recyclerView, tSwitch, -1);

        recyclerView.setAdapter(adapter);
        adapter.setOnClickListener(this::moveEventsToActivity);
        adapter.setAgenda(agenda);
    }

    private void moveEventsToActivity(_Activity activity) {
        this.activity.removeEvents(checkedList);
        AppModule.provideExecutor().submit(()->{
            try {
                activity.getCache().insertEvents(checkedList);
            } catch (Exception e) {
                e.printStackTrace();
            }
            requireActivity().runOnUiThread(this::onConfirm);
        });
    }

    public void onConfirm() {
        updateUiFunction.run();
        dialog.dismiss();
    }
    public void onCancel() {
        updateUiFunction.run();
        dialog.dismiss();
    }

    public void setSelectedList(Set<_Event> checkedList, Agenda agenda, Activity activity) {
        this.checkedList = checkedList;
        this.agenda = agenda;
        this.activity = activity;
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

