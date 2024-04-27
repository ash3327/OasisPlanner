package com.aurora.oasisplanner.data.core.use_cases;

import android.content.res.Resources;
import android.text.SpannableStringBuilder;

import androidx.fragment.app.FragmentManager;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.model.pojo.events.Event;
import com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components.EventMoveDialog;
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.AlarmTagEditDialog;
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.AlarmEditDialog;

import java.util.Set;

public class EditEventUseCases {
    private FragmentManager fragmentManager;
    private Event event;

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void invoke(_Event alarmList, SpannableStringBuilder activityDescr,
                       AlarmEditDialog.OnSaveListener onSaveListener) {
        AppModule.provideExecutor().submit(
                ()->{
                    this.event = alarmList.getAssociates();
                    this.event.contents = activityDescr;//_Doc.getFirst(, "(no content)");

                    if (fragmentManager == null)
                        throw new Resources.NotFoundException("Fragment Manager is Not Set Properly.");
                    AlarmEditDialog dialog = new AlarmEditDialog();
                    dialog.setOnSaveListener(onSaveListener);

                    dialog.show(fragmentManager, "dialogAlarmEdit");
                }
        );
    }

    public void invokeDialogForTagType(Set<_Event> checkedList, Runnable updateUi) {
        AlarmTagEditDialog dialog = new AlarmTagEditDialog();
        dialog.setUpdateUiFunction(updateUi);
        dialog.setSelectedList(checkedList);
        dialog.show(fragmentManager, "dialogTagType");
    }

    public void invokeDialogForMovingEvent(Set<_Event> checkedList, Agenda agenda, Activity activity, Runnable updateUi) {
        EventMoveDialog dialog = new EventMoveDialog();
        dialog.setUpdateUiFunction(updateUi);
        dialog.setSelectedList(checkedList, agenda, activity);
        dialog.show(fragmentManager, "dialogTagType");
    }

    public Event retrieveAlarms() {
        return event;
    }
}
