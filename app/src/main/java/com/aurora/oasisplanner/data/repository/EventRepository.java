package com.aurora.oasisplanner.data.repository;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.datasource.daos.EventDao;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;
import com.aurora.oasisplanner.data.model.pojo.events.Event;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class EventRepository {
    private final EventDao eventDao;
    private final ExecutorService executor;

    public EventRepository(EventDao eventDao, ExecutorService executor) {
        this.eventDao = eventDao;
        this.executor = executor;
    }

    public Future<Long> insertEvent(final _Event alarmList) {
        return executor.submit(()->{
            if (!alarmList.hasAssociates())
                return eventDao.insert(alarmList);
            else
                return insertEventWithChild(alarmList.getAssociates()).get();
        });
    }

    public Future<_Event> getEvent(final long id) {
        return executor.submit(()->eventDao.getEventById(id));
    }

    public void deleteEvent(final _Event alarmList) {
        executor.execute(()->{
            Event parent = alarmList.getAssociates();
            AppModule.retrieveAlarmUseCases().delete(parent.alarms);
            AppModule.retrieveAlarmUseCases().deleteSubAlarms(parent.subalarms);
            eventDao.delete(alarmList);
        });
    }

    
    public Future<Long> insertEventWithChild(final Event event) {
        return executor.submit(()->{
            long id = eventDao.insert(event.alarmList);
            for (_Alarm alarm : event.alarms) {
                alarm.alarmListId = id;
                alarm.activityId = event.alarmList.activityId;
                alarm.agendaId = event.alarmList.agendaId;
            }
            for (_SubAlarm alarm : event.subalarms) {
                alarm.alarmListId = id;
                alarm.activityId = event.alarmList.activityId;
                alarm.agendaId = event.alarmList.agendaId;
            }
            AppModule.retrieveAlarmUseCases().put(event.alarms);
            AppModule.retrieveAlarmUseCases().putSubAlarms(event.subalarms);
            return id;
        });
    }

    public Future<Event> getEventWithChild(final long id) {
        return executor.submit(()->eventDao.getEventWithChildById(id));
    }

    public void deleteEventWithChild(final Event event) {
        executor.execute(()->{
            eventDao.delete(event.alarmList);
            AppModule.retrieveAlarmUseCases().delete(event.alarms);
        });
    }
}
