package com.aurora.oasisplanner.data.core.use_cases;

import androidx.fragment.app.FragmentManager;

import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;
import com.aurora.oasisplanner.data.repository.AlarmRepository;

import java.util.List;

public class AlarmUseCases {
    private AlarmRepository repository;
    private FragmentManager fragmentManager;

    public AlarmUseCases(
            AlarmRepository repository
    ) {
        this.repository = repository;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public _Alarm get(long alarmId) {
        return repository.requestAlarm(alarmId);
    }

    public void put(_Alarm alarm) {
        repository.insert(alarm);
    }
    public void put(List<_Alarm> alarms) {
        repository.insert(alarms);
    }

    public void delete(_Alarm alarm) {
        repository.delete(alarm);
    }
    public void delete(List<_Alarm> alarms) {
        repository.delete(alarms);
    }
    public void deleteSubAlarms(List<_SubAlarm> subAlarms) {
        repository.deleteSubAlarms(subAlarms);
    }
}
