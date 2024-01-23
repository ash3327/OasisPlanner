package com.aurora.oasisplanner.data.repository;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.datasource.daos.ActivityDao;
import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ActivityRepository {
    private final ActivityDao activityDao;
    private final ExecutorService executor;

    public ActivityRepository(ActivityDao activityDao, ExecutorService executor) {
        this.activityDao = activityDao;
        this.executor = executor;
    }

    public Future<Long> insertActivity(final _Activity activity) {
        /*return executor.submit(()->{
            if (!activity.hasAssociates())
                return activityDao.save(activity);
            else
                return insertActivityWithChild(activity.getAssociates()).get();
        });*/
        assert false;
        return null;
    }

    public Future<_Activity> getActivity(final long id) {
        return executor.submit(()->activityDao.getActivityById(id));
    }

    public void deleteActivity(final _Activity activity) {
        /*executor.execute(()->{
            Activity parent = activity.getAssociates();
            AppModule.retrieveAlarmUseCases().delete(parent.alarms);
            AppModule.retrieveAlarmUseCases().deleteSubAlarms(parent.subalarms);
            activityDao.delete(activity);
        });*/
        assert false;
    }

    
    public Future<Long> insertActivityWithChild(final Activity activity) {
        /*return executor.submit(()->{
            long id = activityDao.save(activity.activity);
            for (_Alarm alarm : activity.alarms) {
                alarm.activityId = id;
                alarm.activityId = activity.activity.activityId;
                alarm.agendaId = activity.activity.agendaId;
            }
            for (_SubAlarm alarm : activity.subalarms) {
                alarm.activityId = id;
                alarm.activityId = activity.activity.activityId;
                alarm.agendaId = activity.activity.agendaId;
            }
            AppModule.retrieveAlarmUseCases().put(activity.alarms);
            AppModule.retrieveAlarmUseCases().putSubAlarms(activity.subalarms);
            return id;
        });*/
        assert false;
        return null;
    }

    public Future<Activity> getActivityWithChild(final long id) {
        return executor.submit(()->activityDao.getActivityWithChildById(id));
        //return null;
    }

    public void deleteActivityWithChild(final Activity activity) {
        /*executor.execute(()->{
            activityDao.delete(activity.activity);
            AppModule.retrieveAlarmUseCases().delete(activity.alarms);
        });*/
        assert false;
    }
}
