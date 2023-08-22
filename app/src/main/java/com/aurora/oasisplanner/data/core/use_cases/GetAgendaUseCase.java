package com.aurora.oasisplanner.data.core.use_cases;

import com.aurora.oasisplanner.data.model.pojo.Agenda;
import com.aurora.oasisplanner.data.repository.AgendaRepository;

public class GetAgendaUseCase {
    private AgendaRepository repository;

    public GetAgendaUseCase(AgendaRepository repository) {
        this.repository = repository;
    }

    public Agenda invoke(long agendaId) {
        return repository.getAgendaFromId(agendaId);
    }
}
