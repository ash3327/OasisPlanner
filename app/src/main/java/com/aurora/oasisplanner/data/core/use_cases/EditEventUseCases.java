package com.aurora.oasisplanner.data.core.use_cases;

import androidx.fragment.app.FragmentManager;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.model.pojo.events.Event;
import com.aurora.oasisplanner.data.tags.NotifType;
import com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.EventMoveDialog;
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.AlarmEditSubalarmDialog;
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.AlarmTagEditDialog;
import com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.AlarmEditDialog;

import java.util.Set;

public class EditEventUseCases {
    private FragmentManager fragmentManager;
    private Event event;
    private AlarmEditDialog.OnSaveListener onSaveListener;
    private Runnable onDestroyListener;

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void invoke(_Event event, String activityDescr, AlarmEditDialog.OnSaveListener onSaveListener, Runnable onDestroyListener) {
        this.event = event.getAssociates();
        this.event.contents = activityDescr;
        this.onSaveListener = onSaveListener;
        this.onDestroyListener = onDestroyListener;

        MainActivity.getNavController().navigate(R.id.navigation_alarmEditDialog);
    }

    public void invokeDialogForTagType(Set<_Event> checkedList, Runnable updateUi) {
        AlarmTagEditDialog dialog = new AlarmTagEditDialog();
        dialog.setUpdateUiFunction(updateUi);
        dialog.setSelectedList(checkedList);
        dialog.show(fragmentManager, "dialogTagType");
    }
    public void invokeDialogForSubalarm(NotifType notifType, AlarmEditSubalarmDialog.OnSaveListener updateUi) {
        AlarmEditSubalarmDialog dialog = new AlarmEditSubalarmDialog();
        dialog.setOnSaveListener(updateUi);
        dialog.setNotifType(notifType);
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
    public AlarmEditDialog.OnSaveListener getOnSaveListener() { return onSaveListener; }
    public Runnable getOnDestroyListener() { return onDestroyListener; }
}
