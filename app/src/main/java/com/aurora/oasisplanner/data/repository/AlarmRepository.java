package com.aurora.oasisplanner.data.repository;

import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.datasource.daos.AlarmDao;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmScheduler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class AlarmRepository {
    private AlarmDao alarmDao;
    private LiveData<List<_Alarm>> alarms;
    private LiveData<List<_SubAlarm>> subalarms;
    private ExecutorService executor;

    public AlarmRepository(AlarmDao alarmDao, ExecutorService executor) {
        this.alarmDao = alarmDao;
        this.alarms = alarmDao.getAlarmsAfter(LocalDateTime.now());
        this.subalarms = alarmDao.getSubAlarmsAfter(LocalDateTime.now());
        this.executor = executor;
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
        executor.execute(()->alarmDao.insert(alarm));
        AlarmScheduler.scheduleMany(AppModule.retrieveAlarmScheduler(), alarm);
    }
    public void insert(List<_Alarm> alarms) {
        executor.execute(()->alarmDao.insert(alarms));
        AlarmScheduler.scheduleMany(AppModule.retrieveAlarmScheduler(), alarms.toArray(new _Alarm[0]));
    }

    public void insertSubAlarm(_SubAlarm alarm) {
        executor.execute(()->alarmDao.insertSubAlarm(alarm));
        AlarmScheduler.scheduleMany(AppModule.retrieveAlarmScheduler(), alarm);
    }
    public void insertSubAlarms(List<_SubAlarm> alarms) {
        executor.execute(()->alarmDao.insertSubAlarms(alarms));
        AlarmScheduler.scheduleMany(AppModule.retrieveAlarmScheduler(), alarms.toArray(new _Alarm[0]));
    }

    public void delete(_Alarm alarm) {
        executor.execute(()->alarmDao.delete(alarm));
        AlarmScheduler.cancelMany(AppModule.retrieveAlarmScheduler(), alarm);
    }
    public void delete(List<_Alarm> alarms) {
        executor.execute(()->alarmDao.delete(alarms));
        AlarmScheduler.cancelMany(AppModule.retrieveAlarmScheduler(), alarms.toArray(new _Alarm[0]));
    }
    public void deleteSubAlarm(_SubAlarm alarm) {
        executor.execute(()->alarmDao.deleteSubAlarm(alarm));
        AlarmScheduler.cancelMany(AppModule.retrieveAlarmScheduler(), alarm);
    }
    public void deleteSubAlarms(List<_SubAlarm> subAlarms) {
        executor.execute(()->alarmDao.deleteSubAlarms(subAlarms));
        AlarmScheduler.cancelMany(AppModule.retrieveAlarmScheduler(), subAlarms.toArray(new _Alarm[0]));
    }

    public LiveData<List<_Alarm>> getAlarms() {
        return alarms;
    }

    public LiveData<List<_Alarm>> requestAlarm(String searchEntry) {
        return alarms = alarmDao.getAlarmsAfter(LocalDateTime.now(), searchEntry, new Converters().spannableToString(searchEntry));
    }
    public _Alarm requestAlarm(long id) {
        return alarmDao.getAlarmById(id);
    }
}
