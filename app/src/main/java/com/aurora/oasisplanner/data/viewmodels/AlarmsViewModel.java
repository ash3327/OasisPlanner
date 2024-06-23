package com.aurora.oasisplanner.data.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aurora.oasisplanner.data.datasource.AppDatabase;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.pojo.events.Alarm;
import com.aurora.oasisplanner.data.repository.AlarmRepository;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.presentation.widgets.multidatepicker.data.DateRange;

import java.time.LocalDateTime;
import java.util.List;

public class AlarmsViewModel extends AndroidViewModel {
    private AlarmRepository repository;
    public LiveData<List<Alarm>> alarms;

    public AlarmsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppModule.provideAppDatabase(application);
        repository = AppModule.provideAlarmRepository(database, AppModule.provideExecutor());
        alarms = repository.getAlarms();
    }

    public void refreshAlarms(String searchEntry) {
        alarms = repository.requestAlarm(searchEntry);
    }
    public void refreshAlarmsBetween(String searchEntry, DateRange dateRange) {
        alarms = repository.requestAlarm(searchEntry, dateRange.mStart, dateRange.mEnd);
    }
    public void refreshAlarmsBetween(String searchEntry, LocalDateTime start, LocalDateTime end) {
        alarms = repository.requestAlarm(searchEntry, start, end);
    }

    public void insert(_Alarm alarm) {
        repository.insert(alarm);
    }

    public void delete(_Alarm alarm) {
        repository.delete(alarm);
    }

    public LiveData<List<Alarm>> getAlarms() {
        return alarms;
    }
}
