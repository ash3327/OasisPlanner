package com.aurora.oasisplanner.data.repository;

import android.os.AsyncTask;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.aurora.oasisplanner.data.datasource.daos.AlarmDao;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmScheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class AlarmRepository {
    private AlarmDao alarmDao;
    private LiveData<List<_Alarm>> alarms;
    private LiveData<List<_SubAlarm>> subalarms;

    public AlarmRepository(AlarmDao alarmDao) {
        this.alarmDao = alarmDao;
        this.alarms = alarmDao.getAlarmsAfter(LocalDateTime.now());
        this.subalarms = alarmDao.getSubAlarmsAfter(LocalDateTime.now());
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
        new InsertAlarmAsyncTask(alarmDao).execute(alarm);
    }

    public void update(_Alarm alarm) {
        new UpdateAlarmAsyncTask(alarmDao).execute(alarm);
    }

    public void delete(_Alarm alarm) {
        new DeleteAlarmAsyncTask(alarmDao).execute(alarm);
    }

    public void deleteAllAlarms() {
        new DeleteAllAlarmsAsyncTask(alarmDao).execute();
    }

    public LiveData<List<_Alarm>> getAlarms() {
        return alarms;
    }

    public LiveData<List<_Alarm>> requestAlarms(String searchEntry) {
        return alarms = alarmDao.getAlarmsAfter(LocalDateTime.now(), searchEntry, new Converters().spannableToString(searchEntry));
    }

    private static class InsertAlarmAsyncTask extends AsyncTask<_Alarm, Void, Void> {
        private AlarmDao alarmDao;

        private InsertAlarmAsyncTask(AlarmDao alarmDao) {
            this.alarmDao = alarmDao;
        }

        @Override
        protected Void doInBackground(_Alarm... alarms) {
            alarmDao.insert(alarms[0]);
            return null;
        }
    }
    private static class UpdateAlarmAsyncTask extends AsyncTask<_Alarm, Void, Void> {
        private AlarmDao alarmDao;

        private UpdateAlarmAsyncTask(AlarmDao alarmDao) {
            this.alarmDao = alarmDao;
        }

        @Override
        protected Void doInBackground(_Alarm... alarms) {
            alarmDao.insert(alarms[0]);
            return null;
        }
    }
    private static class DeleteAlarmAsyncTask extends AsyncTask<_Alarm, Void, Void> {
        private AlarmDao alarmDao;

        private DeleteAlarmAsyncTask(AlarmDao alarmDao) {
            this.alarmDao = alarmDao;
        }

        @Override
        protected Void doInBackground(_Alarm... alarms) {
            alarmDao.delete(alarms[0]);
            return null;
        }
    }
    private static class DeleteAllAlarmsAsyncTask extends AsyncTask<Void, Void, Void> {
        private AlarmDao alarmDao;

        private DeleteAllAlarmsAsyncTask(AlarmDao alarmDao) {
            this.alarmDao = alarmDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            alarmDao.deleteAllAlarms();
            return null;
        }
    }
}
