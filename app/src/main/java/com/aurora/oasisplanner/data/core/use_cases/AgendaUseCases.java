package com.aurora.oasisplanner.data.core.use_cases;

import androidx.fragment.app.FragmentManager;

import com.aurora.oasisplanner.data.model.entities.events._Agenda;
import com.aurora.oasisplanner.data.repository.AgendaRepository;

import java.util.concurrent.Future;

public class AgendaUseCases {
    private AgendaRepository repository;
    private FragmentManager fragmentManager;

    public AgendaUseCases(AgendaRepository repository) {
        this.repository = repository;
    }

    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public Future<_Agenda> get(long agendaId) {
        return repository.getAgenda(agendaId);
    }

    public void put(_Agenda agenda) {
        // NOT IMPEMENTED
        repository.insertAgenda(agenda);
    }

    public void delete(_Agenda agenda) {
        // NOT IMPEMENTED
        repository.deleteAgenda(agenda);
    }
}
