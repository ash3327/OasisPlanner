package com.aurora.oasisplanner.data.core.use_cases;

import android.os.Bundle;

import androidx.lifecycle.LiveData;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.model.pojo.events.Alarm;
import com.aurora.oasisplanner.data.repository.AgendaRepository;
import com.aurora.oasisplanner.presentation.dialogs.agendaeditdialog.AgendaEditDialog;
import com.aurora.oasisplanner.presentation.dialogs.agendaeditdialog.util.AgendaAccessUtil;
import com.aurora.oasisplanner.presentation.dialogs.alarmeditdialog.util.AlarmQuickEditUtil;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class AgendaUseCases {
    private AgendaRepository repository;

    public AgendaUseCases(
            AgendaRepository repository
    ) {
        this.repository = repository;
    }

    /** -1 indicates a new agenda. */
    public void edit(long agendaId, long activityLId, long eventLId) {
        Bundle bundle = new Bundle();
        bundle.putLong(AgendaEditDialog.EXTRA_AGENDA_ID, agendaId);
        bundle.putLong(AgendaEditDialog.EXTRA_ACTIVL_ID, activityLId);
        bundle.putLong(AgendaEditDialog.EXTRA_EVENT_ID, eventLId);

        if (!AgendaAccessUtil.agendaIdIsPlaceholder(agendaId))
            MainActivity.getNavController().navigate(R.id.navigation_agendaEditDialog, bundle);
        else
            new AlarmQuickEditUtil(agendaId, activityLId, eventLId).quickEdit();
    }

    public Agenda get(long agendaId) {
        return repository.getAgendaFromId(agendaId);
    }

    public void put(Agenda agenda) {
        repository.save(agenda);
    }

    public List<_Activity> getActivities(Agenda agenda) {
        return agenda.activities;
    }

    public List<_Event> getAlarmLists(Activity activity) {
        return activity.alarmLists;
    }
    public Alarm getNextAlarm(_Activity activity) throws ExecutionException, InterruptedException {
        if (activity.hasCache())
            return activity.getCache().getFirstAlarm();
        return AppModule.retrieveAlarmUseCases().getNextAlarmFromActivity(activity.id);
    }

    public LiveData<List<Agenda>> getAgendas() {
        return repository.getAgendas();
    }

    public AgendaRepository getRepository() {
        return repository;
    }
}
