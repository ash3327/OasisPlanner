package com.aurora.oasisplanner.presentation.dialog.alarmeditdialog.util;

import android.util.Log;
import android.view.View;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.model.pojo.events.Event;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components.ActivityAdapter;
import com.aurora.oasisplanner.presentation.dialog.agendaeditdialog.components.EventAdapter;

public class AlarmQuickAddUtil {
    public static void quickAdd() {
        _Event event = _Event.empty();
        AppModule.retrieveEditEventUseCases().invoke(event, event.activityDescr, AlarmQuickAddUtil::saveEvent, AlarmQuickAddUtil::onDestroy);
    }

    public static void saveEvent(Event _event) {
        try {
            Agenda agenda = Agenda.empty();
            agenda.agenda.args.put("NIL", null);
            ActivityAdapter._insert(agenda, ActivityType.Type.activity, 0, "");
            Activity activity = agenda.activities.get(0).getCache();
            EventAdapter._insert(activity, ActivityType.Type.activity, 0, null, _event.alarmList);

            AppModule.retrieveAgendaUseCases().put(agenda);
            onDestroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void onDestroy() {
        MainActivity.bottomBar.setVisibility(View.VISIBLE);
        MainActivity activity = MainActivity.main;
        activity.setDrawerLocked(false);
        activity.mDrawerToggle.setDrawerIndicatorEnabled(true);
        activity.refreshToolbar();
    }
}
