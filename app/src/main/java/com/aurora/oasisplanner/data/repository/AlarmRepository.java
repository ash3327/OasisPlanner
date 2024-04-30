package com.aurora.oasisplanner.data.repository;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.activities.OasisApp;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.datasource.daos.AlarmDao;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;
import com.aurora.oasisplanner.data.model.pojo.events.Alarm;
import com.aurora.oasisplanner.data.model.pojo.events.SubAlarm;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmScheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

public class AlarmRepository {
    private AlarmDao alarmDao;
    private LiveData<List<Alarm>> alarms;
    private LiveData<List<SubAlarm>> subalarms;
    private ExecutorService executor;

    public AlarmRepository(AlarmDao alarmDao, ExecutorService executor) {
        this.alarmDao = alarmDao;
        this.alarms = alarmDao.getAlarmsInfoAfter(LocalDateTime.now());
        this.subalarms = alarmDao.getSubAlarmsInfoAfter(LocalDateTime.now());
        this.executor = executor;
    }

    private boolean firstTime = true, firstTimeSubAlarm = true;
    public void scheduleAlarms(AlarmScheduler alarmScheduler, List<Alarm> _alarms) {
        try {
            for (Alarm alarm : _alarms)
                alarmScheduler.schedule(alarm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void scheduleSubAlarms(AlarmScheduler alarmScheduler, List<SubAlarm> _subalarms) {
        try {
            for (SubAlarm alarm : _subalarms)
                alarmScheduler.schedule(alarm);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void schedule(AlarmScheduler alarmScheduler, LifecycleOwner obs) {
        firstTime = true; firstTimeSubAlarm = true;
        List<Alarm> lia = alarms.getValue();
        if (lia == null || lia.isEmpty()) {
            alarms.observe(obs, (_alarms) -> {
                if (!firstTime) return;
                firstTime = false;
                scheduleAlarms(alarmScheduler, _alarms);
            });
        } else {
            scheduleAlarms(alarmScheduler, lia);
        }

        List<SubAlarm> lisa = subalarms.getValue();
        if (lisa == null || lisa.isEmpty()) {
            subalarms.observe(obs, (_subalarms) -> {
                if (!firstTimeSubAlarm) return;
                firstTimeSubAlarm = false;
                scheduleSubAlarms(alarmScheduler, _subalarms);
            });
        } else {
            scheduleSubAlarms(alarmScheduler, lisa);
        }
    }

    public void insert(_Alarm alarm) {
        executor.execute(()->{
            long id = alarmDao.insert(alarm);
            AlarmScheduler.scheduleMany(AppModule.retrieveAlarmScheduler(), AppModule.retrieveAlarmUseCases().get(id));
        });
    }

    public void insertSubAlarm(_SubAlarm alarm) {
        executor.execute(()->{
            long id = alarmDao.insertSubAlarm(alarm);
            AlarmScheduler.scheduleMany(AppModule.retrieveAlarmScheduler(), AppModule.retrieveAlarmUseCases().getSubAlarmInfo(id));
        });
    }

    public void insertWith(Alarm alarm) {
        executor.execute(()->{
            _Alarm _alarm = alarm.getAlarm();

            _alarm.alarmListId = alarm.getEventId();
            _alarm.activityId = alarm.getActivityId();
            _alarm.agendaId = alarm.getAgendaId();

            AppModule.retrieveAlarmUseCases().put(_alarm);
        });
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

    public LiveData<List<Alarm>> getAlarms() {
        return alarms;
    }

    public LiveData<List<Alarm>> requestAlarm(String searchEntry) {
        return alarms = alarmDao.getAlarmsInfoAfter(LocalDateTime.now(), searchEntry, new Converters().spannableToString(searchEntry));
    }
    public LiveData<List<Alarm>> requestAlarm(String searchEntry, LocalDate startDate, LocalDate endDate) {
        return alarms = alarmDao.getAlarmsInfoBetween(startDate, endDate, searchEntry, new Converters().spannableToString(searchEntry));
    }
    public Alarm requestAlarm(long id) {
        return alarmDao.getAlarmInfoById(id);
    }
    public SubAlarm requestSubAlarm(long id) {
        return alarmDao.getSubAlarmInfoById(id);
    }

    public Alarm requestFirstAlarmFromActivity(long activityId) {
        return alarmDao.getFirstAlarmInfoFromActivityAfter(activityId, LocalDateTime.now());
    }
}
