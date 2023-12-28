package com.aurora.oasisplanner.data.core.use_cases.agenda_usecases;

public class AgendaUseCases {
    public GetAgendaUseCase getAgendaUseCase;
    public EditAgendaUseCase editAgendaUseCase;
    public PutAgendaUseCase putAgendaUseCase;
    public EditAlarmListUseCase editAlarmListUseCase;

    public AgendaUseCases(
            GetAgendaUseCase getAgendaUseCase,
            EditAgendaUseCase editAgendaUseCase,
            PutAgendaUseCase putAgendaUseCase,
            EditAlarmListUseCase editAlarmListUseCase
    ) {
        this.getAgendaUseCase = getAgendaUseCase;
        this.editAgendaUseCase = editAgendaUseCase;
        this.putAgendaUseCase = putAgendaUseCase;
        this.editAlarmListUseCase = editAlarmListUseCase;
    }
}
