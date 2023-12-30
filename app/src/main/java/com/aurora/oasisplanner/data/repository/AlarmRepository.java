package com.aurora.oasisplanner.data.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.datasource.AgendaDao;
import com.aurora.oasisplanner.data.model.entities._Alarm;
import com.aurora.oasisplanner.data.model.entities._SubAlarm;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmScheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AlarmRepository {
    private AgendaDao agendaDao;
    private LiveData<List<_Alarm>> alarms;
    private LiveData<List<_SubAlarm>> subalarms;

    public AlarmRepository(AgendaDao agendaDao) {
        this.agendaDao = agendaDao;
        this.alarms = agendaDao.getAlarmsAfter(LocalDateTime.now());
        this.subalarms = agendaDao.getSubAlarmsAfter(LocalDateTime.now());
    }

    private boolean firstTime = true, firstTimeSubAlarm = true;
    public void schedule(AlarmScheduler alarmScheduler, LifecycleOwner obs, CountDownLatch latch) {
        firstTime = true;
        alarms.observe(obs, (_alarms)->{
            if (!firstTime) return;
            firstTime = false;
            try {
                for (_Alarm alarm : _alarms)
                    alarmScheduler.schedule(alarm);
            } catch (Exception e) {
                e.printStackTrace();
            }
            latch.countDown();
            //Log.d("test3", "SCHEDULED ALARMS: "+_alarms);
        });
        subalarms.observe(obs, (_subalarms)->{
            if (!firstTimeSubAlarm) return;
            firstTimeSubAlarm = false;
            try {
                for (_SubAlarm alarm : _subalarms)
                    alarmScheduler.schedule(alarm);
            } catch (Exception e) {
                e.printStackTrace();
            }
            latch.countDown();
            //Log.d("test3", "SCHEDULED ALARMS: "+_alarms);
        });
    }

    public void insert(_Alarm alarm) {
        new InsertAlarmAsyncTask(agendaDao).execute(alarm);
    }

    public void update(_Alarm alarm) {
        new UpdateAlarmAsyncTask(agendaDao).execute(alarm);
    }

    public void delete(_Alarm alarm) {
        new DeleteAlarmAsyncTask(agendaDao).execute(alarm);
    }

    public void deleteAllAlarms() {
        new DeleteAllAlarmsAsyncTask(agendaDao).execute();
    }

    public LiveData<List<_Alarm>> getAlarms() {
        return alarms;
    }

    public LiveData<List<_Alarm>> requestAlarms(String searchEntry) {
        return alarms = agendaDao.getAlarmsAfter(LocalDateTime.now(), searchEntry, new Converters().spannableToString(searchEntry));
    }

    private static class InsertAlarmAsyncTask extends AsyncTask<_Alarm, Void, Void> {
        private AgendaDao agendaDao;

        private InsertAlarmAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected Void doInBackground(_Alarm... alarms) {
            agendaDao.insert(alarms[0]);
            return null;
        }
    }
    private static class UpdateAlarmAsyncTask extends AsyncTask<_Alarm, Void, Void> {
        private AgendaDao agendaDao;

        private UpdateAlarmAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected Void doInBackground(_Alarm... alarms) {
            agendaDao.insert(alarms[0]);
            return null;
        }
    }
    private static class DeleteAlarmAsyncTask extends AsyncTask<_Alarm, Void, Void> {
        private AgendaDao agendaDao;

        private DeleteAlarmAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected Void doInBackground(_Alarm... alarms) {
            agendaDao.delete(alarms[0]);
            return null;
        }
    }
    private static class DeleteAllAlarmsAsyncTask extends AsyncTask<Void, Void, Void> {
        private AgendaDao agendaDao;

        private DeleteAllAlarmsAsyncTask(AgendaDao agendaDao) {
            this.agendaDao = agendaDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            agendaDao.deleteAllAlarms();
            return null;
        }
    }
}
