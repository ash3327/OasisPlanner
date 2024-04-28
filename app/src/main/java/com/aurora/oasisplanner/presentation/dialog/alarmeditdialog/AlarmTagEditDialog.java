package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.tags.AlarmType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.tags.NotifType;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.databinding.ItemEditTagBinding;
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components.DateType;
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.components.SpinAdapter;
import com.aurora.oasisplanner.presentation.widget.taginputeidittext.TagInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Set;
import java.util.function.Function;

public class AlarmTagEditDialog extends AppCompatDialogFragment {
    public static final String EXTRA_ALARM_LISTS = "alarmLists";

    private AlertDialog dialog;
    public TagType type = TagType.LOC;
    private DateType dateType = DateType.minutes;
    private ItemEditTagBinding vbinding;
    private Set<_Event> checkedList;
    private Runnable updateUiFunction = ()->{};
    private int idx = 0;

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
        vbinding.deleteButton.setOnClickListener(
                (v)->onDelete()
        );

        SpinAdapter spinAdapter = new SpinAdapter(getLayoutInflater(), TagType.getAvailableValues());
        vbinding.ietdTagChooseTypeBox.setOnItemSelectListener(
                spinAdapter, type.ordinal(), type.getDrawable(),
                (AdapterView.OnItemClickListener) (adapterView, view, position, id) -> {
                    type = TagType.getAvailableValues()[position];
                    changeUiToInputType(type);
                },
                (v)->type.getType());

        ArrayAdapter<DateType> dateTypeAdapter = new ArrayAdapter<DateType>(requireContext(), R.layout.datetype_spinner_element);
        dateTypeAdapter.addAll(DateType.values());
        vbinding.ietdTagDatetimeBox.setOnItemSelectListener(
                dateTypeAdapter, dateType.ordinal(), null,
                (AdapterView.OnItemClickListener) (adapterView, view, position, id) -> {
                    dateType = DateType.values()[position];
                    vbinding.ietdTagDatetimeBox.setDateType(dateType);
                },
                (v)->dateType.ordinal());
    }

    public void changeUiToInputType(TagType type) {
        vbinding.ietdTagChooseTypeBox.getSpinnerTil().setStartIconDrawable(type.getDrawable());

        vbinding.ietdTagContentBox.setVisibility(View.GONE);
        vbinding.ietdTagDatetimeBox.setVisibility(View.GONE);
        vbinding.ietdTagChoiceBox.setVisibility(View.GONE);
        vbinding.ietdTagTagsBox.setVisibility(View.GONE);
        vbinding.deleteButton.setVisibility(View.VISIBLE);
        switch (type) {
            case LOC:
            case DESCR:
                vbinding.ietdTagContentBox.setVisibility(View.VISIBLE);
                break;
            case ALARM:
                vbinding.ietdTagDatetimeBox.setVisibility(View.VISIBLE);
                break;
            case IMPORTANCE:
            case ALARMTYPE:
                vbinding.deleteButton.setVisibility(View.GONE);
                vbinding.ietdTagChoiceBox.setVisibility(View.VISIBLE);
                break;
            case TAGS:
                vbinding.ietdTagTagsBox.setVisibility(View.VISIBLE);
                break;
        }
        switch (type) {
            case IMPORTANCE:
                ArrayAdapter<Importance> importanceAdapter = new ArrayAdapter<Importance>(requireContext(), R.layout.datetype_spinner_element);
                importanceAdapter.addAll(Importance.values());
                vbinding.ietdTagChoiceBox.setOnItemSelectListener(
                        importanceAdapter, idx, null,
                        (AdapterView.OnItemClickListener) (adapterView, view, position, id) -> {
                            idx = position;
                        },
                        (v)->Importance.values()[idx].ordinal());
                break;
            case ALARMTYPE:
                ArrayAdapter<AlarmType> alarmTypeAdapter = new ArrayAdapter<AlarmType>(requireContext(), R.layout.datetype_spinner_element);
                alarmTypeAdapter.addAll(AlarmType.values());
                vbinding.ietdTagChoiceBox.setOnItemSelectListener(
                        alarmTypeAdapter, idx, null,
                        (AdapterView.OnItemClickListener) (adapterView, view, position, id) -> {
                            idx = position;
                        },
                        (v)->AlarmType.values()[idx].ordinal());
                break;
        }
        vbinding.ietdTagContentBox.requestLayout();
        vbinding.ietdTagDatetimeBox.requestLayout();
        vbinding.ietdTagChoiceBox.requestLayout();
        vbinding.ietdTagTagsBox.requestLayout();
    }

    private void setOnItemSelectListener(AutoCompleteTextView spinner, TextInputLayout til,
                                         String text, Drawable drawable,
                                         AdapterView.OnItemClickListener listener,
                                         Function<String, Integer> getType) {
        spinner.setText(text);
        til.setStartIconDrawable(drawable);
        spinner.setOnItemClickListener(listener);
        listener.onItemClick(null, spinner.getRootView(), getType.apply(null), 0);
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
        AppModule.provideExecutor().submit(
                ()->{
                    try {
                        if (saveArgs()) {
                            Activity a = getActivity();
                            a.runOnUiThread(
                                    ()->{
                                        updateUiFunction.run();
                                        dialog.dismiss();
                                    }
                            );
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }
    public void onCancel() {
        updateUiFunction.run();
        dialog.dismiss();
    }
    public void onDelete() {
        if (deleteTags()) {
            updateUiFunction.run();
            dialog.dismiss();
        }
    }

    public boolean saveArgs() {
        SpannableStringBuilder ssb = null;
        switch (type) {
            case LOC:
            case DESCR:
                String text = vbinding.ietdTagContentBox.getText();
                if (text == null || text.length() == 0) {
                    Toast.makeText(getContext(), R.string.tab_no_content_warning, Toast.LENGTH_SHORT).show();
                    return false;
                }
                ssb = new SpannableStringBuilder(text);
                break;
            case ALARM:
                NotifType notifType = vbinding.ietdTagDatetimeBox.getNotifType(dateType);
                if (notifType == null) {
                    Toast.makeText(getContext(), R.string.tab_no_content_warning, Toast.LENGTH_SHORT).show();
                    return false;
                }
                ssb = new SpannableStringBuilder(notifType.toString());
                break;
            case IMPORTANCE:
            case ALARMTYPE:
                break;
            case TAGS:
                ssb = new SpannableStringBuilder(vbinding.ietdTagTagsBox.getText());
                break;
        }

        assert checkedList != null;
        switch (type) {
            case IMPORTANCE:
                for (_Event checked : checkedList)
                    checked.importance = Importance.values()[idx];
                break;
            case ALARMTYPE:
                for (_Event checked : checkedList)
                    checked.type = AlarmType.values()[idx];
                break;
            case TAGS:
                for (_Event checked : checkedList) {
                    SpannableStringBuilder ssb2 = checked.getArgSpannable(type);
                    checked.putArgs(type.name(), TagInputEditText.combine(ssb2, ssb));
                }
                break;
            case ALARM:
                for (_Event checked : checkedList) {
                    checked.putArgs(type.name(), ssb);
                    checked.getAssociates().generateSubalarms();
                }
                break;
            case DESCR:
                for (_Event checked : checkedList)
                    checked.setTitle(ssb);
                break;
            default:
                for (_Event checked : checkedList)
                    checked.putArgs(type.name(), ssb);
        }

        return true;
    }

    public boolean deleteTags() {
        assert checkedList != null;
        if (type != TagType.IMPORTANCE && type != TagType.ALARMTYPE) {
            for (_Event checked : checkedList)
                checked.removeKey(type.name());
            return true;
        }
        return false;
    }

    public void setSelectedList(Set<_Event> checkedList) {
        this.checkedList = checkedList;
    }

    public void setUpdateUiFunction(Runnable updateUiFunction) {
        this.updateUiFunction = updateUiFunction;
    }
}

