package com.aurora.oasisplanner.data.repository;

import android.os.AsyncTask;
import android.text.SpannableStringBuilder;

import androidx.lifecycle.LiveData;
import androidx.room.Transaction;

import com.aurora.oasisplanner.data.datasource.AgendaDao;
import com.aurora.oasisplanner.data.model.entities._Alarm;
import com.aurora.oasisplanner.data.model.entities._Doc;
import com.aurora.oasisplanner.data.model.entities._Period;
import com.aurora.oasisplanner.data.model.pojo.Agenda;
import com.aurora.oasisplanner.data.model.pojo.AlarmList;
import com.aurora.oasisplanner.data.model.pojo.Group;
import com.aurora.oasisplanner.data.model.pojo.Period;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmScheduler;

import java.time.LocalDateTime;
import java.util.List;

public class AgendaRepository {
    private AgendaDao agendaDao;
    private AlarmScheduler alarmScheduler;

    public AgendaRepository(AgendaDao agendaDao, AlarmScheduler alarmScheduler) {
        this.agendaDao = agendaDao;
        this.alarmScheduler = alarmScheduler;
    }

    // INFO: Agenda

    public void insert(Agenda agenda) {
        new InsertAgendaAsyncTask(agendaDao, alarmScheduler).execute(agenda);
    }

    public void delete(Agenda agenda) {
        new DeleteAgendaAsyncTask(agendaDao, alarmScheduler).execute(agenda);
    }

    public void deleteAllAlarms() {
        new DeleteAllAgendaAsyncTask(agendaDao, alarmScheduler).execute();
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
        private AlarmScheduler alarmScheduler;

        private InsertAgendaAsyncTask(AgendaDao agendaDao, AlarmScheduler alarmScheduler) {
            this.agendaDao = agendaDao;
            this.alarmScheduler = alarmScheduler;
        }

        @Override
        protected Void doInBackground(Agenda... agendas) {
            insert(agendas[0], agendaDao, alarmScheduler);
            return null;
        }
    }
    private static class DeleteAgendaAsyncTask extends AsyncTask<Agenda, Void, Void> {
        private AgendaDao agendaDao;
        private AlarmScheduler alarmScheduler;

        private DeleteAgendaAsyncTask(AgendaDao agendaDao, AlarmScheduler alarmScheduler) {
            this.agendaDao = agendaDao;
            this.alarmScheduler = alarmScheduler;
        }

        @Override
        protected Void doInBackground(Agenda... agendas) {
            delete(agendas[0], agendaDao, alarmScheduler);
            return null;
        }
    }
    private static class DeleteAllAgendaAsyncTask extends AsyncTask<Void, Void, Void> {
        private AgendaDao agendaDao;
        private AlarmScheduler alarmScheduler;

        private DeleteAllAgendaAsyncTask(AgendaDao agendaDao, AlarmScheduler alarmScheduler) {
            this.agendaDao = agendaDao;
            this.alarmScheduler = alarmScheduler;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            deleteAll(agendaDao, alarmScheduler);
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

    private static long insert(Agenda agenda, AgendaDao agendaDao, AlarmScheduler alarmScheduler) {
        // convert to object list without invisible
        // convert back
        agenda.update();

        for (_Doc doc : agenda.invisDocs)
            agendaDao.delete(doc);
        for (Group gp : agenda.invisGroups)
            delete(gp, agendaDao, alarmScheduler);

        long id = agendaDao.insert(agenda.agenda);
        agenda.agenda.id = id;
        for (_Doc doc : agenda.docs) {
            doc.setAgendaId(id);
            agendaDao.insert(doc);
        }
        String title = agenda.agenda.title;
        SpannableStringBuilder content = _Doc.getFirst(agenda.docs, "");
        for (Group gp : agenda.groups) {
            gp.group.agendaId = id;
            insert(gp, title, content, agendaDao, alarmScheduler);
        }
        return id;
    }

    private static long insert(
            Group group,
            String title,
            SpannableStringBuilder agendaDescr,
            AgendaDao agendaDao,
            AlarmScheduler alarmScheduler
    ) {

        group.update();

        for (_Doc doc : group.invisDocs)
            agendaDao.delete(doc);
        for (AlarmList gp : group.invisGroups)
            delete(gp, agendaDao, alarmScheduler);

        long id = agendaDao.insert(group.group);
        group.group.id = id;
        for (_Doc doc : group.docs) {
            doc.setGroupId(id);
            agendaDao.insert(doc);
        }
        SpannableStringBuilder alarmDescr = _Doc.getFirst(group.docs, "(no content)");
        for (AlarmList alarmList : group.alarmList) {
            alarmList.alarmList.groupId = id;
            alarmList.alarmList.agendaId = group.group.agendaId;
            insert(alarmList, title, agendaDescr, alarmDescr, agendaDao, alarmScheduler);
        }
        return id;
    }

    private static long insert(
            AlarmList alarmList,
            String title,
            SpannableStringBuilder agendaDescr,
            SpannableStringBuilder alarmDescr,
            AgendaDao agendaDao,
            AlarmScheduler alarmScheduler
    ) {

        long id = agendaDao.insert(alarmList.alarmList);
        alarmList.alarmList.id = id;
        for (_Alarm alarm : alarmList.alarms) {
            if (alarm.visible) {
                alarm.alarmListId = id;
                alarm.agendaId = alarmList.alarmList.agendaId;
                alarm.setAgendaData(title, agendaDescr, alarmDescr);
                alarm.setAlarmData(alarmList.alarmList.type, alarmList.alarmList.importance);
                alarm.id = agendaDao.insert(alarm);
                if (alarmScheduler != null)
                    alarmScheduler.schedule(alarm);
            } else {
                if (alarmScheduler != null)
                    alarmScheduler.cancel(alarm);
                agendaDao.delete(alarm);
            }
        }
        return id;
    }

    private static void delete(Agenda agenda, AgendaDao agendaDao, AlarmScheduler alarmScheduler) {
        agendaDao.delete(agenda.agenda);
        for (Group gp : agenda.groups)
            delete(gp, agendaDao, alarmScheduler);
        for (_Doc doc : agenda.docs)
            agendaDao.delete(doc);
    }

    private static void delete(Group group, AgendaDao agendaDao, AlarmScheduler alarmScheduler) {
        agendaDao.delete(group.group);
        for (AlarmList alarmList : group.alarmList)
            delete(alarmList, agendaDao, alarmScheduler);
        for (_Doc doc : group.docs)
            agendaDao.delete(doc);
    }

    private static void delete(AlarmList alarmList, AgendaDao agendaDao, AlarmScheduler alarmScheduler) {
        agendaDao.delete(alarmList.alarmList);
        for (_Alarm alarm : alarmList.alarms) {
            if (alarmScheduler != null)
                alarmScheduler.cancel(alarm);
            agendaDao.delete(alarm);
        }
    }

    private static void deleteAll(AgendaDao agendaDao, AlarmScheduler alarmScheduler) {
        agendaDao.deleteAllAgendas();
        agendaDao.deleteAllGroups();
        agendaDao.deleteAllAlarmLists();
        if (alarmScheduler != null)
            for (_Alarm alarm : agendaDao.getAlarms())
                alarmScheduler.cancel(alarm);
        agendaDao.deleteAllAlarms();
    }

    // INFO: Period

    public LiveData<List<Period>> getPeriodsAfter(LocalDateTime time) {
        try {
            return new GetPeriodsAsyncTask(agendaDao).execute(time).get();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Period Not Found Exception: "+e.getMessage());
        }
    }

    private static class GetPeriodsAsyncTask extends AsyncTask<LocalDateTime, Void, LiveData<List<Period>>> {
        private AgendaDao agendaDao;

        private GetPeriodsAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected LiveData<List<Period>> doInBackground(LocalDateTime... times) {
            return agendaDao.getPeriodsAfter(times[0]);
        }
    }

    // insert & delete

    public void insert(Period period) {
        new InsertPeriodAsyncTask(agendaDao).execute(period);
    }

    public void delete(Period period) {
        new DeletePeriodAsyncTask(agendaDao).execute(period);
    }

    private static class InsertPeriodAsyncTask extends AsyncTask<Period, Void, Void> {
        private AgendaDao agendaDao;

        private InsertPeriodAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected Void doInBackground(Period... periods) {
            insert(periods[0], agendaDao);
            return null;
        }
    }
    private static class DeletePeriodAsyncTask extends AsyncTask<Period, Void, Void> {
        private AgendaDao agendaDao;

        private DeletePeriodAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected Void doInBackground(Period... periods) {
            delete(periods[0], agendaDao);
            return null;
        }
    }

    private static long insert(Period period, AgendaDao agendaDao) {
        // convert to object list without invisible
        // convert back

        long id = agendaDao.insert(period.period);
        period.period.id = id;
        for (_Period p : period.periods) {
            p.parentPeriodId = id;
            if (!p.visible)
                agendaDao.delete(p);
            else agendaDao.insert(p);
        }

        return id;
    }

    private static void delete(Period period, AgendaDao agendaDao) {
        agendaDao.delete(period.period);
        for (_Period p : period.periods)
            agendaDao.delete(p);
    }
}
