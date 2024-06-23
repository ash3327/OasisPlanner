package com.aurora.oasisplanner.presentation.dialogs.alarmeditdialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.tags.NotifType;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.databinding.ItemEditSubalarmBinding;
import com.aurora.oasisplanner.presentation.dialogs.alarmeditdialog.components.DateType;
import com.aurora.oasisplanner.presentation.dialogs.alarmeditdialog.components.editargsbox.AEDDatetimeBox;

public class AlarmEditSubalarmDialog extends AppCompatDialogFragment {
    private AlertDialog dialog;
    public TagType type = TagType.LOC;
    private DateType dateType = DateType.minutes;
    private ItemEditSubalarmBinding vbinding = null;
    private OnSaveListener onSaveListener = (notifType)->{};
    private NotifType mNotifType;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        ItemEditSubalarmBinding binding = ItemEditSubalarmBinding.inflate(getLayoutInflater());
        onBind(binding);

        ViewGroup vg = (ViewGroup) binding.getRoot();
        builder.setView(vg);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        return dialog;
    }

    public void onBind(ItemEditSubalarmBinding binding) {
        vbinding = binding;
        vbinding.header.setText(R.string.page_overhead_edit_tag);
        vbinding.confirmBtn.setOnClickListener(
                (v)->onConfirm()
        );
        vbinding.cancelButton.setOnClickListener(
                (v)->onCancel()
        );

        ArrayAdapter<DateType> dateTypeAdapter = new ArrayAdapter<DateType>(requireContext(), R.layout.datetype_spinner_element);
        dateTypeAdapter.addAll(DateType.values());
        dateType = mNotifType.dateType;
        getBox().setNotifType(mNotifType);
        getBox().setOnItemSelectListener(
                dateTypeAdapter, dateType.ordinal(), null,
                (AdapterView.OnItemClickListener) (adapterView, view, position, id) -> {
                    dateType = DateType.values()[position];
                    vbinding.ietdTagDatetimeBox.setDateType(dateType);
                },
                (v)->dateType.ordinal());
    }

    public AEDDatetimeBox getBox() {
        return vbinding.ietdTagDatetimeBox;
    }

    public void onConfirm() {
        AppModule.provideExecutor().submit(
                ()->{
                    try {
                        NotifType notifType = getNotifType();
                        Activity a = getActivity();
                        a.runOnUiThread(
                                ()->{
                                    onSaveListener.onSave(notifType);
                                    dialog.dismiss();
                                }
                        );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }
    public void onCancel() {
        dialog.dismiss();
    }

    public NotifType getNotifType() {
        NotifType notifType = vbinding.ietdTagDatetimeBox.getNotifType(dateType);
        if (notifType == null) {
            Toast.makeText(getContext(), R.string.tab_no_content_warning, Toast.LENGTH_SHORT).show();
            return null;
        }
        return notifType;
    }

    public boolean deleteTags() {

        return false;
    }

    public void setNotifType(NotifType notifType) {
        mNotifType = notifType;
        if (vbinding != null)
            getBox().setNotifType(mNotifType);
    }

    public void setOnSaveListener(OnSaveListener onSaveListener) {
        this.onSaveListener = onSaveListener;
    }

    public interface OnSaveListener {
        void onSave(NotifType notifType);
    }
}

