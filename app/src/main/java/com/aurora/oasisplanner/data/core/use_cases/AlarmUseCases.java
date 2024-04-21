package com.aurora.oasisplanner.data.core.use_cases;

import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LifecycleOwner;

import com.aurora.oasisplanner.activities.MainActivity;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;
import com.aurora.oasisplanner.data.model.pojo.events.Alarm;
import com.aurora.oasisplanner.data.model.pojo.events.Event;
import com.aurora.oasisplanner.data.repository.AlarmRepository;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmScheduler;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class AlarmUseCases {
    private AlarmRepository repository;

    public AlarmUseCases(
            AlarmRepository repository
    ) {
        this.repository = repository;
    }

    public Alarm get(long alarmId) {
        return repository.requestAlarm(alarmId);
    }
    public Alarm getSubAlarmInfo(long alarmId) {
        return repository.requestSubAlarm(alarmId);
    }

    public Alarm getNextAlarmFromActivity(long activityId) {
        return repository.requestFirstAlarmFromActivity(activityId);
    }

    public void put(_Alarm alarm) {
        repository.insert(alarm);
    }
    public void put(List<_Alarm> alarms) {
        for (_Alarm alarm : alarms) {
            if (alarm.visible)
                put(alarm);
            else
                delete(alarm);
        }
    }
    public void putWith(Alarm alarm) {
        repository.insertWith(alarm);
    }
    public void putSubAlarm(_SubAlarm alarm) {
        repository.insertSubAlarm(alarm);
    }
    public void putSubAlarms(List<_SubAlarm> alarms) {
        for (_SubAlarm alarm : alarms) {
            if (alarm.visible)
                putSubAlarm(alarm);
            else
                deleteSubAlarm(alarm);
        }
    }

    public void delete(_Alarm alarm) {
        repository.delete(alarm);
    }
    public void delete(List<_Alarm> alarms) {
        repository.delete(alarms);
    }
    public void deleteSubAlarm(_SubAlarm alarm) {
        repository.deleteSubAlarm(alarm);
    }
    public void deleteSubAlarms(List<_SubAlarm> subAlarms) {
        repository.deleteSubAlarms(subAlarms);
    }

    public void forceUpdateAlarms(AlarmScheduler alarmScheduler, LifecycleOwner lifecycleOwner) {
        repository.schedule(alarmScheduler, lifecycleOwner);
    }
}
