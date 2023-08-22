package com.aurora.oasisplanner.data.core;

import android.app.Application;
import android.content.res.Resources;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.aurora.oasisplanner.data.datasource.AppDatabase;
import com.aurora.oasisplanner.data.repository.AgendaRepository;
import com.aurora.oasisplanner.data.repository.AlarmRepository;
import com.aurora.oasisplanner.data.core.use_cases.AgendaUseCases;
import com.aurora.oasisplanner.data.core.use_cases.EditAgendaUseCase;
import com.aurora.oasisplanner.data.core.use_cases.EditAlarmListUseCase;
import com.aurora.oasisplanner.data.core.use_cases.GetAgendaUseCase;
import com.aurora.oasisplanner.data.core.use_cases.PutAgendaUseCase;
import com.aurora.oasisplanner.presentation.dialog.choosetypedialog.ChooseTypeDialog;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmScheduler;

public class AppModule {

    private static AgendaUseCases agendaUseCases;

    public static AppDatabase provideAppDatabase(Application application) {
        return AppDatabase.getInstance(application);
    }

    public static AgendaRepository provideAgendaRepository(AppDatabase db, AlarmScheduler alarmScheduler) {
        return new AgendaRepository(db.agendaDao(), alarmScheduler);
    }
    public static AlarmRepository provideAlarmRepository(AppDatabase db) {
        return new AlarmRepository(db.agendaDao());
    }

    public static AgendaUseCases provideAgendaUseCases(AgendaRepository repository) {
        if (agendaUseCases != null) return agendaUseCases;
        return agendaUseCases = new AgendaUseCases(
                new GetAgendaUseCase(repository),
                new EditAgendaUseCase(repository),
                new PutAgendaUseCase(repository),
                new EditAlarmListUseCase(repository)
        );
    }

    public static AgendaUseCases retrieveAgendaUseCases() {
        if (agendaUseCases == null)
            throw new Resources.NotFoundException("The Usecase is Not Defined Yet.");
        return agendaUseCases;
    }

    public static void newAgenda(){
        new ChooseTypeDialog().show();
    }
}
