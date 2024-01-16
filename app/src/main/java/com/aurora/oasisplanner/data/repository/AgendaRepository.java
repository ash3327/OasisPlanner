package com.aurora.oasisplanner.data.repository;

import android.os.AsyncTask;
import android.text.SpannableStringBuilder;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.datasource.daos.ActivityDao;
import com.aurora.oasisplanner.data.datasource.daos.AgendaDao;
import com.aurora.oasisplanner.data.datasource.daos.AlarmDao;
import com.aurora.oasisplanner.data.datasource.daos.EventDao;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._AlarmList;
import com.aurora.oasisplanner.data.model.entities.util._Doc;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.model.pojo.events.AlarmList;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.tags.TagType;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmScheduler;

import java.util.HashMap;
import java.util.Map;

public class AgendaRepository {
    private AgendaDao agendaDao;
    private ActivityDao activityDao;
    private EventDao eventDao;
    private AlarmDao alarmDao;
    private AlarmScheduler alarmScheduler;

    public AgendaRepository(
            AgendaDao agendaDao, AlarmDao alarmDao,
            ActivityDao activityDao, EventDao eventDao,
            AlarmScheduler alarmScheduler) {
        this.agendaDao = agendaDao;
        this.activityDao = activityDao;
        this.eventDao = eventDao;
        this.alarmDao = alarmDao;
        this.alarmScheduler = alarmScheduler;
    }

    // INFO: Agenda

    public void insert(Agenda agenda) {
        new InsertAgendaAsyncTask(agendaDao, alarmDao, activityDao, eventDao, alarmScheduler).execute(agenda);
    }

    public void delete(Agenda agenda) {
        new DeleteAgendaAsyncTask(agendaDao, alarmDao, activityDao, eventDao, alarmScheduler).execute(agenda);
    }

    public void deleteAllAlarms() {
        new DeleteAllAgendaAsyncTask(agendaDao, alarmDao, activityDao, eventDao, alarmScheduler).execute();
    }

    public Agenda getAgendaFromId(long id) {
        try {
            return new GetAgendaAsyncTask(agendaDao).execute(id).get();
        } catch (Exception e) {
            throw new RuntimeException("Agenda Not Found Exception: "+id);
        }
    }

    private static class InsertAgendaAsyncTask extends AsyncTask<Agenda, Void, Void> {
        private AgendaDao agendaDao;
        private ActivityDao activityDao;
        private EventDao eventDao;
        private AlarmDao alarmDao;
        private AlarmScheduler alarmScheduler;

        private InsertAgendaAsyncTask(
                AgendaDao agendaDao, AlarmDao alarmDao,
                ActivityDao activityDao, EventDao eventDao,
                AlarmScheduler alarmScheduler) {
            this.agendaDao = agendaDao;
            this.activityDao = activityDao;
            this.eventDao = eventDao;
            this.alarmDao = alarmDao;
            this.alarmScheduler = alarmScheduler;
        }

        @Override
        protected Void doInBackground(Agenda... agendas) {
            insert(agendas[0], agendaDao, alarmDao, activityDao, eventDao, alarmScheduler);
            return null;
        }
    }
    private static class DeleteAgendaAsyncTask extends AsyncTask<Agenda, Void, Void> {
        private AgendaDao agendaDao;
        private AlarmDao alarmDao;
        private ActivityDao activityDao;
        private EventDao eventDao;
        private AlarmScheduler alarmScheduler;

        private DeleteAgendaAsyncTask(AgendaDao agendaDao, AlarmDao alarmDao,
                                      ActivityDao activityDao, EventDao eventDao,
                                      AlarmScheduler alarmScheduler) {
            this.agendaDao = agendaDao;
            this.activityDao = activityDao;
            this.eventDao = eventDao;
            this.alarmDao = alarmDao;
            this.alarmScheduler = alarmScheduler;
        }

        @Override
        protected Void doInBackground(Agenda... agendas) {
            delete(agendas[0], agendaDao, alarmDao, activityDao, eventDao, alarmScheduler);
            return null;
        }
    }
    private static class DeleteAllAgendaAsyncTask extends AsyncTask<Void, Void, Void> {
        private AgendaDao agendaDao;
        private AlarmDao alarmDao;
        private ActivityDao activityDao;
        private EventDao eventDao;
        private AlarmScheduler alarmScheduler;

        private DeleteAllAgendaAsyncTask(AgendaDao agendaDao, AlarmDao alarmDao,
                                         ActivityDao activityDao, EventDao eventDao,
                                         AlarmScheduler alarmScheduler) {
            this.agendaDao = agendaDao;
            this.alarmDao = alarmDao;
            this.activityDao = activityDao;
            this.eventDao = eventDao;
            this.alarmScheduler = alarmScheduler;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            deleteAll(agendaDao, alarmDao, activityDao, eventDao, alarmScheduler);
            return null;
        }
    }
    private static class GetAgendaAsyncTask extends AsyncTask<Long, Void, Agenda> {
        private AgendaDao agendaDao;

        private GetAgendaAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected Agenda doInBackground(Long... agendaIds) {
            return agendaDao.getAgendaById(agendaIds[0]);
        }
    }

    private static long insert(Agenda agenda,
                               AgendaDao agendaDao, AlarmDao alarmDao,
                               ActivityDao activityDao, EventDao eventDao,
                               AlarmScheduler alarmScheduler) {
        // convert to object list without invisible
        // convert back
        agenda.update();

        /*for (_Doc doc : agenda.invisDocs)
            agendaDao.delete(doc);*/
        for (Activity gp : agenda.invisGroups)
            delete(gp, agendaDao, alarmDao, activityDao, eventDao, alarmScheduler);

        long id = agendaDao.insert(agenda.agenda);
        agenda.agenda.id = id;
        /*for (_Doc doc : agenda.docs) {
            doc.setAgendaId(id);
            agendaDao.insert(doc);
        }*/
        String title = agenda.agenda.title;
        SpannableStringBuilder content = _Doc.getFirst(agenda.docs, "");
        for (Activity gp : AppModule.retrieveAgendaUseCases().getActivities(agenda)) {
            gp.activity.agendaId = id;
            insert(gp, title, content, agendaDao, activityDao, eventDao, alarmDao, alarmScheduler);
        }
        return id;
    }

    private static long insert(
            Activity activity,
            String title,
            SpannableStringBuilder agendaDescr,
            AgendaDao agendaDao,
            ActivityDao activityDao,
            EventDao eventDao,
            AlarmDao alarmDao,
            AlarmScheduler alarmScheduler
    ) {

        activity.update();

        /*for (_Doc doc : activity.invisDocs)
            agendaDao.delete(doc);*/
        for (_AlarmList gp : activity.invisGroups)
            delete(gp, agendaDao, alarmDao, eventDao, alarmScheduler);

        SpannableStringBuilder alarmDescr = activity.activity.descr;

        // TODO: Agenda Importance = Max (Alarm Importance, temporarily)
        Importance activityImp = Importance.unimportant;
        for (_AlarmList alarmList : AppModule.retrieveAgendaUseCases().getAlarmLists(activity))
            activityImp = Importance.max(activityImp, alarmList.importance);
        activity.activity.importance = activityImp;

        long id = activityDao.insert(activity.activity);
        activity.activity.id = id;
        /*for (_Doc doc : activity.docs) {
            doc.setGroupId(id);
            agendaDao.insert(doc);
        }*/
        for (_AlarmList alarmList : AppModule.retrieveAgendaUseCases().getAlarmLists(activity)) {
            alarmList.activityId = id;
            alarmList.agendaId = activity.activity.agendaId;
            //
            Map<String, String> alarmArgs = new HashMap<>();
            Converters converter = new Converters();
            insert(alarmList, title, agendaDescr, alarmDescr, id, agendaDao, alarmDao, eventDao, alarmScheduler, alarmArgs);
        }
        return id;
    }

    private static long insert(
            _AlarmList alarmList,
            String title,
            SpannableStringBuilder agendaDescr,
            SpannableStringBuilder alarmDescr,
            long actvId,
            AgendaDao agendaDao,
            AlarmDao alarmDao,
            EventDao eventDao,
            AlarmScheduler alarmScheduler,
            Map<String, String> alarmArgs
    ) {

        long id = AppModule.retrieveEventUseCases().put(alarmList);
        /*alarmList.id = id;
        AlarmList parent = alarmList.getAssociates();
        for (_Alarm alarm : parent.alarms) {
            if (alarm.visible) {
                alarm.alarmListId = id;
                alarm.activityId = actvId;
                alarm.agendaId = alarmList.agendaId;
                //alarm.setAgendaData(title, agendaDescr, alarmDescr);
                //alarm.setAlarmData(alarmList.type, alarmList.importance);
                if (alarmArgs != null)
                    alarm.getArgs().putAll(alarmArgs);
                alarm.getArgs().putAll(alarmList.getArgs());
                alarm.id = alarmDao.insert(alarm);
                if (alarmScheduler != null)
                    alarmScheduler.schedule(alarm);
            } else {
                if (alarmScheduler != null)
                    alarmScheduler.cancel(alarm);
                alarmDao.delete(alarm);
            }
        }
        for (_SubAlarm alarm : parent.subalarms) {
            if (alarm.visible) {
                alarm.alarmListId = id;
                alarm.activityId = actvId;
                alarm.agendaId = alarmList.agendaId;
                alarm.setAgendaData(title, agendaDescr, alarmDescr);
                alarm.setAlarmData(alarmList.type, alarmList.importance);
                if (alarmArgs != null)
                    alarm.getArgs().putAll(alarmArgs);
                alarm.getArgs().putAll(alarmList.getArgs());
                alarm.id = alarmDao.insertSubAlarm(alarm);
                if (alarmScheduler != null)
                    alarmScheduler.schedule(alarm);
            } else {
                if (alarmScheduler != null)
                    alarmScheduler.cancel(alarm);
                alarmDao.deleteSubAlarm(alarm);
            }
        }//*/
        return id;
    }

    private static void delete(Agenda agenda,
                               AgendaDao agendaDao, AlarmDao alarmDao,
                               ActivityDao activityDao, EventDao eventDao,
                               AlarmScheduler alarmScheduler) {
        agendaDao.delete(agenda.agenda);
        for (Activity gp : AppModule.retrieveAgendaUseCases().getActivities(agenda))
            delete(gp, agendaDao, alarmDao, activityDao, eventDao, alarmScheduler);
        /*for (_Doc doc : agenda.docs)
            agendaDao.delete(doc);*/
    }

    private static void delete(Activity activity,
                               AgendaDao agendaDao, AlarmDao alarmDao,
                               ActivityDao activityDao, EventDao eventDao,
                               AlarmScheduler alarmScheduler) {
        activityDao.delete(activity.activity);
        for (_AlarmList alarmList : AppModule.retrieveAgendaUseCases().getAlarmLists(activity))
            delete(alarmList, agendaDao, alarmDao, eventDao, alarmScheduler);
        /*for (_Doc doc : activity.docs)
            agendaDao.delete(doc);*/
    }

    private static void delete(_AlarmList alarmList, AgendaDao agendaDao, AlarmDao alarmDao,
                               EventDao eventDao, AlarmScheduler alarmScheduler) {
        AppModule.retrieveEventUseCases().delete(alarmList);
        AlarmList parent = alarmList.getAssociates();
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

    private static void deleteAll(AgendaDao agendaDao, AlarmDao alarmDao,
                                  ActivityDao activityDao, EventDao eventDao,
                                  AlarmScheduler alarmScheduler) {
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
    }

}
