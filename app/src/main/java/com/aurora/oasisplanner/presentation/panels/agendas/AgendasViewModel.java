package com.aurora.oasisplanner.presentation.panels.agendas;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.core.use_cases.AgendaUseCases;
import com.aurora.oasisplanner.data.datasource.AppDatabase;
import com.aurora.oasisplanner.data.model.entities.events._Agenda;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.repository.AgendaRepository;
import com.aurora.oasisplanner.presentation.widgets.multidatepicker.data.DateRange;

import java.util.List;

public class AgendasViewModel extends AndroidViewModel {
    private AgendaRepository repository;
    public LiveData<List<Agenda>> agendas;

    public AgendasViewModel(@NonNull Application application) {
        super(application);
        repository = AppModule.retrieveAgendaUseCases().getRepository();
        agendas = repository.getAgendas();
    }

    public void refreshAgendas(String searchEntry) {
        agendas = repository.requestAgenda(searchEntry);
    }
//    public void refreshAgendasBetween(String searchEntry, DateRange dateRange) {
//        agendas = repository.requestAgenda(searchEntry, dateRange.mStart, dateRange.mEnd);
//    }

    /*public void insert(_Agenda agenda) {
        repository.insert(agenda);
    }

    public void delete(_Agenda agenda) {
        repository.delete(agenda);
    }*/

    public LiveData<List<Agenda>> getAgendas() {
        return agendas;
    }
}
