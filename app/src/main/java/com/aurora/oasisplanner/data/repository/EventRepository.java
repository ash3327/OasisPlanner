package com.aurora.oasisplanner.data.repository;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.datasource.daos.EventDao;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._AlarmList;
import com.aurora.oasisplanner.data.model.pojo.events.AlarmList;
import com.aurora.oasisplanner.data.util.Converters;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class EventRepository {
    private final EventDao eventDao;
    private final ExecutorService executor;

    public EventRepository(EventDao eventDao, ExecutorService executor) {
        this.eventDao = eventDao;
        this.executor = executor;
    }

    public Future<Long> insertEvent(final _AlarmList alarmList) {
        return executor.submit(()->{
            if (!alarmList.hasAssociates())
                return eventDao.insert(alarmList);
            else
                return insertEventWithChild(alarmList.getAssociates()).get();
        });
    }

    public Future<_AlarmList> getEvent(final long id) {
        return executor.submit(()->eventDao.getEventById(id));
    }

    public void deleteEvent(final _AlarmList alarmList) {
        executor.execute(()->{
            AlarmList parent = alarmList.getAssociates();
            AppModule.retrieveAlarmUseCases().delete(parent.alarms);
            AppModule.retrieveAlarmUseCases().deleteSubAlarms(parent.subalarms);
            eventDao.delete(alarmList);
        });
    }

    
    public Future<Long> insertEventWithChild(final AlarmList alarmList) {
        return executor.submit(()->{
            long id = eventDao.insert(alarmList.alarmList);
            AppModule.retrieveAlarmUseCases().put(alarmList.alarms);
            return id;
        });
    }

    public Future<AlarmList> getEventWithChild(final long id) {
        return executor.submit(()->eventDao.getEventWithChildById(id));
    }

    public void deleteEventWithChild(final AlarmList alarmList) {
        executor.execute(()->{
            eventDao.delete(alarmList.alarmList);
            AppModule.retrieveAlarmUseCases().delete(alarmList.alarms);
        });
    }
}
