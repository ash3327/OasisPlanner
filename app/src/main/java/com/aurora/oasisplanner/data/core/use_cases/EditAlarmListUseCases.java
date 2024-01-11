package com.aurora.oasisplanner.data.core.use_cases;

import android.content.res.Resources;

import androidx.fragment.app.FragmentManager;

import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.pojo.events.AlarmList;
import com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components.TagEditDialog;
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.AlarmEditDialog;

import java.util.Set;

public class EditAlarmListUseCases {
    private FragmentManager fragmentManager;
    private AlarmList alarmList;

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void invoke(AlarmList alarmList, Activity grp, AlarmEditDialog.OnSaveListener onSaveListener) {
        this.alarmList = alarmList;
        alarmList.contents = grp.activity.descr;//_Doc.getFirst(, "(no content)");

        if (fragmentManager == null)
            throw new Resources.NotFoundException("Fragment Manager is Not Set Properly.");
        AlarmEditDialog dialog = new AlarmEditDialog();
        dialog.setOnSaveListener(onSaveListener);

        dialog.show(fragmentManager, "dialogAlarmEdit");
    }

    public void invokeDialogForTagType(Set<AlarmList> checkedList, Runnable updateUi) {
        TagEditDialog dialog = new TagEditDialog();
        dialog.setUpdateUiFunction(updateUi);
        dialog.setSelectedList(checkedList);
        dialog.show(fragmentManager, "dialogTagType");
    }

    public AlarmList retrieveAlarms() {
        return alarmList;
    }
}
