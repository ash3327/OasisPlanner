package com.aurora.oasisplanner.data.repository;

import android.text.SpannableStringBuilder;

import androidx.lifecycle.LiveData;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.datasource.daos.ActivityDao;
import com.aurora.oasisplanner.data.datasource.daos.AgendaDao;
import com.aurora.oasisplanner.data.datasource.daos.AlarmDao;
import com.aurora.oasisplanner.data.datasource.daos.EventDao;
import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.model.pojo.events.Alarm;
import com.aurora.oasisplanner.data.model.pojo.events.Event;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmScheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class AgendaRepository {
    private final AgendaDao agendaDao;
    private final ActivityDao activityDao;
    private final EventDao eventDao;
    private final AlarmDao alarmDao;
    private final AlarmScheduler alarmScheduler;
    private final ExecutorService executor;

    public AgendaRepository(
            AgendaDao agendaDao, AlarmDao alarmDao,
            ActivityDao activityDao, EventDao eventDao,
            AlarmScheduler alarmScheduler,
            ExecutorService executor) {
        this.agendaDao = agendaDao;
        this.activityDao = activityDao;
        this.eventDao = eventDao;
        this.alarmDao = alarmDao;
        this.alarmScheduler = alarmScheduler;
        this.executor = executor;
    }

    // INFO: Agenda

    public void save(Agenda agenda) {
        try {
            executor.submit(()-> _save(agenda)).get(1000L, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(Agenda agenda) {
        executor.submit(()-> _delete(agenda));
    }

    public void deleteAllAlarms() {
        executor.submit(this::_deleteAll);
    }

    public Agenda getAgendaFromId(long id) {
        try {
            return executor.submit(()-> agendaDao.getAgendaById(id)).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public LiveData<List<Agenda>> getAgendas() {
        try {
            return executor.submit(()-> agendaDao.getAgendas()).get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private long _save(Agenda agenda) throws ExecutionException, InterruptedException {
        // convert to object list without invisible
        // convert back
        agenda.update();

        for (_Activity gp : agenda.invisGroups)
            _delete(gp);

        long id = agendaDao.insert(agenda.agenda);
        agenda.agenda.id = id;
        
        String title = agenda.agenda.title;

        for (_Activity gp : agenda.activities) {
            gp.agendaId = id;
            _save(gp, title);
        }//*/
        return id;
    }

    private void _save(
            _Activity actv,
            String title
    ) throws ExecutionException, InterruptedException {

        if (!actv.hasCache()) {
            activityDao.insert(actv);
            return;
        }

        Activity activity = actv.getCache();
        activity.update();

        for (_Event gp : activity.invisGroups)
            _delete(gp);

        SpannableStringBuilder alarmDescr = activity.activity.title;

        Importance activityImp = Importance.unimportant;
        for (_Event alarmList : AppModule.retrieveAgendaUseCases().getAlarmLists(activity))
            activityImp = Importance.max(activityImp, alarmList.importance);
        activity.activity.importance = activityImp;

        long id = activityDao.insert(activity.activity);
        activity.activity.id = id;

        for (_Event alarmList : AppModule.retrieveAgendaUseCases().getAlarmLists(activity)) {
            alarmList.activityId = id;
            alarmList.agendaId = activity.activity.agendaId;
            //
            Map<String, String> alarmArgs = new HashMap<>();
            Converters converter = new Converters();
            _save(alarmList, title, alarmDescr, id, alarmArgs);
        }
    }

    private static long _save(
            _Event alarmList,
            String title,
            SpannableStringBuilder alarmDescr,
            long actvId,
            Map<String, String> alarmArgs
    ) {
        long id = AppModule.retrieveEventUseCases().putWithChild(alarmList);
        return id;
    }

    private void _delete(Agenda agenda) {
        agendaDao.delete(agenda.agenda);

        for (_Activity gp : AppModule.retrieveAgendaUseCases().getActivities(agenda))
            _delete(gp);
    }

    private void _delete(_Activity activity) {
        activityDao.delete(activity);
        eventDao.deleteEventsByActivity(activity.id);

        try {
            for (_Event alarmList : activity.getCache().alarmLists)
                _delete(alarmList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void _delete(_Event alarmList) {
        AppModule.retrieveEventUseCases().delete(alarmList);
        Event parent = alarmList.getAssociates();
        for (_Alarm alarm : parent.alarms) {
            if (alarmScheduler != null)
                alarmScheduler.cancel(alarm);
            alarmDao.delete(alarm);
        }
        for (_SubAlarm alarm : parent.subalarms) {
            if (alarmScheduler != null)
                alarmScheduler.cancel(alarm);
            alarmDao.deleteSubAlarm(alarm);
        }
    }

    private void _deleteAll() {
        agendaDao.deleteAllAgendas();
        activityDao.deleteAllGroups();
        eventDao.deleteAllAlarmLists();
        if (alarmScheduler != null) {
            for (_Alarm alarm : alarmDao.getAlarms())
                alarmScheduler.cancel(alarm);
            for (_SubAlarm alarm : alarmDao.getSubAlarms())
                alarmScheduler.cancel(alarm);
        }
        alarmDao.deleteAllAlarms();
        alarmDao.deleteAllSubAlarms();
    }

    public LiveData<List<Agenda>> requestAgenda(String searchEntry) {
        return agendaDao.getAgendasAfter(searchEntry);
    }
//    public LiveData<List<Agenda>> requestAgenda(String searchEntry, LocalDate startDate, LocalDate endDate) {
//        return agendaDao.getAgendasBetween(startDate, endDate, searchEntry, new Converters().spannableToString(searchEntry));
//    }

}
