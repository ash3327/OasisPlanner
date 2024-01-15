package com.aurora.oasisplanner.data.core.use_cases;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.Navigation;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.model.entities.events._AlarmList;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.model.pojo.events.AlarmList;
import com.aurora.oasisplanner.data.repository.AgendaRepository;
import com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.AgendaEditDialog;

import java.util.List;

public class AgendaUseCases {
    private AgendaRepository repository;
    private FragmentManager fragmentManager;

    public AgendaUseCases(
            AgendaRepository repository
    ) {
        this.repository = repository;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    /** -1 indicates a new agenda. */
    public void edit(long agendaId, long activityLId) {
        if (fragmentManager == null)
            throw new Resources.NotFoundException("Fragment Manager is Not Set Properly.");
        //AgendaEditDialog dialog = new AgendaEditDialog();
        Bundle bundle = new Bundle();
        bundle.putLong(AgendaEditDialog.EXTRA_AGENDA_ID, agendaId);
        bundle.putLong(AgendaEditDialog.EXTRA_ACTIVL_ID, activityLId);
        //dialog.setArguments(bundle);
        Navigation.findNavController(MainActivity.main, R.id.nav_host_fragment).navigate(
                R.id.navigation_agendaEditDialog, bundle);
        //dialog.show(fragmentManager, "myDialog");
    }

    public Agenda get(long agendaId) {
        return repository.getAgendaFromId(agendaId);
    }

    public void put(Agenda agenda) {
        repository.insert(agenda);
    }

    public List<Activity> getActivities(Agenda agenda) {
        return agenda.activities;
    }

    public List<_AlarmList> getAlarmLists(Activity activity) {
        return activity.alarmLists;
    }
}
