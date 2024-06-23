package com.aurora.oasisplanner.presentation.dialogs.alarmeditdialog.util;

import android.view.View;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.model.pojo.events.Event;
import com.aurora.oasisplanner.presentation.dialogs.agendaeditdialog.util.AgendaAccessUtil;

public class AlarmQuickEditUtil {
    Agenda mAgenda;
    Activity mActivity;
    _Event mEvent;

    public AlarmQuickEditUtil(long agendaId, long activityLId, long eventLId) {
        try {
            mAgenda = AgendaAccessUtil.fetchAgenda(agendaId);
            mActivity = AgendaAccessUtil.fetchActivity(mAgenda, activityLId).getCache();
            mEvent = AgendaAccessUtil.fetchEvent(mActivity, eventLId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void quickEdit() {
        AppModule.retrieveEditEventUseCases().invoke(mEvent, mEvent.activityDescr, this::saveEvent, this::onDestroy);
    }

    public void saveEvent(Event _event) {
        try {
            AppModule.retrieveAgendaUseCases().put(mAgenda);
            onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onDestroy() {
        MainActivity.bottomBar.setVisibility(View.VISIBLE);
        MainActivity activity = MainActivity.main;
        activity.setDrawerLocked(false);
        activity.mDrawerToggle.setDrawerIndicatorEnabled(true);
        activity.refreshToolbar();
    }
}
