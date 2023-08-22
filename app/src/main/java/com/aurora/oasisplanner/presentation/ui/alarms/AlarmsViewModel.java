package com.aurora.oasisplanner.presentation.ui.alarms;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aurora.oasisplanner.data.datasource.AppDatabase;
import com.aurora.oasisplanner.data.model.entities._Alarm;
import com.aurora.oasisplanner.data.repository.AlarmRepository;
import com.aurora.oasisplanner.data.core.AppModule;

import java.util.List;

public class AlarmsViewModel extends AndroidViewModel {
    private AlarmRepository repository;
    public LiveData<List<_Alarm>> alarms;

    public AlarmsViewModel(@NonNull Application application) {
        super(application);
        AppDatabase database = AppModule.provideAppDatabase(application);
        repository = AppModule.provideAlarmRepository(database);
        alarms = repository.getAlarms();
    }

    public void insert(_Alarm alarm) {
        repository.insert(alarm);
    }

    public void update(_Alarm alarm) {
        repository.update(alarm);
    }

    public void delete(_Alarm alarm) {
        repository.delete(alarm);
    }

    public void deleteAllAlarms() {
        repository.deleteAllAlarms();
    }

    public LiveData<List<_Alarm>> getAlarms() {
        return alarms;
    }
}
