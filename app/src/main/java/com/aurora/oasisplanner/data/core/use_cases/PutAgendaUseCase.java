package com.aurora.oasisplanner.data.core.use_cases;

import com.aurora.oasisplanner.data.model.pojo.Agenda;
import com.aurora.oasisplanner.data.repository.AgendaRepository;

public class PutAgendaUseCase {
    private AgendaRepository repository;

    public PutAgendaUseCase(AgendaRepository repository) {
        this.repository = repository;
    }

    public void invoke(Agenda agenda) {
        repository.insert(agenda);
    }
}
