package com.aurora.oasisplanner.data.core.use_cases;

import android.content.res.Resources;

import androidx.fragment.app.FragmentManager;

import com.aurora.oasisplanner.data.model.pojo.Activity;
import com.aurora.oasisplanner.data.model.pojo.AlarmList;
import com.aurora.oasisplanner.data.model.entities._Doc;
import com.aurora.oasisplanner.data.repository.AgendaRepository;
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.AlarmEditDialog;

public class EditAlarmListUseCase {
    private AgendaRepository repository;
    private FragmentManager fragmentManager;
    private AlarmList alarmList;

    public EditAlarmListUseCase(AgendaRepository repository) {
        this.repository = repository;
    }

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

        dialog.show(fragmentManager, "myDialog");
    }

    public AlarmList retrieveAlarms() {
        return alarmList;
    }
}
