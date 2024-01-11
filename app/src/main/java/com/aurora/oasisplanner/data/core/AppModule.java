package com.aurora.oasisplanner.data.core;

import android.app.Activity;
import android.app.Application;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import com.aurora.oasisplanner.data.core.use_cases.general_usecases.GeneralUseCases;
import com.aurora.oasisplanner.data.core.use_cases.general_usecases.GetTagUseCase;
import com.aurora.oasisplanner.data.core.use_cases.memo_usecases.DeleteMemoUseCase;
import com.aurora.oasisplanner.data.core.use_cases.memo_usecases.EditMemoUseCase;
import com.aurora.oasisplanner.data.core.use_cases.memo_usecases.GetMemoUseCase;
import com.aurora.oasisplanner.data.core.use_cases.memo_usecases.MemoUseCases;
import com.aurora.oasisplanner.data.core.use_cases.memo_usecases.PutMemoUseCase;
import com.aurora.oasisplanner.data.datasource.AppDatabase;
import com.aurora.oasisplanner.data.repository.AgendaRepository;
import com.aurora.oasisplanner.data.repository.AlarmRepository;
import com.aurora.oasisplanner.data.core.use_cases.agenda_usecases.AgendaUseCases;
import com.aurora.oasisplanner.data.core.use_cases.agenda_usecases.EditAgendaUseCase;
import com.aurora.oasisplanner.data.core.use_cases.agenda_usecases.EditAlarmListUseCase;
import com.aurora.oasisplanner.data.core.use_cases.agenda_usecases.GetAgendaUseCase;
import com.aurora.oasisplanner.data.core.use_cases.agenda_usecases.PutAgendaUseCase;
import com.aurora.oasisplanner.data.repository.GeneralRepository;
import com.aurora.oasisplanner.data.repository.MemoRepository;
import com.aurora.oasisplanner.presentation.dialog.choosetypedialog.ChooseTypeDialog;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmScheduler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppModule {

    private static AgendaUseCases agendaUseCases;
    private static MemoUseCases memoUseCases;
    private static GeneralUseCases generalUseCases;

    public static Executor provideExecutor() {
        return Executors.newFixedThreadPool(2);
    }
    public static AppDatabase provideAppDatabase(Application application) {
        return AppDatabase.getInstance(application);
    }

    public static AgendaRepository provideAgendaRepository(AppDatabase db, AlarmScheduler alarmScheduler) {
        return new AgendaRepository(db.agendaDao(), alarmScheduler);
    }
    public static AlarmRepository provideAlarmRepository(AppDatabase db) {
        return new AlarmRepository(db.agendaDao());
    }
    public static MemoRepository provideMemoRepository(AppDatabase db) {
        return new MemoRepository(db.agendaDao());
    }
    public static GeneralRepository provideGeneralRepository(AppDatabase db) {
        return new GeneralRepository(db.agendaDao());
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

    public static MemoUseCases provideMemoUseCases(MemoRepository repository) {
        if (memoUseCases != null) return memoUseCases;
        return memoUseCases = new MemoUseCases(
                new GetMemoUseCase(repository),
                new EditMemoUseCase(repository),
                new PutMemoUseCase(repository),
                new DeleteMemoUseCase(repository)
        );
    }

    public static MemoUseCases retrieveMemoUseCases() {
        if (memoUseCases == null)
            throw new Resources.NotFoundException("The Usecase is Not Defined Yet.");
        return memoUseCases;
    }

    public static GeneralUseCases provideGeneralUseCases(GeneralRepository repository) {
        if (generalUseCases != null) return generalUseCases;
        return generalUseCases = new GeneralUseCases(
                new GetTagUseCase(repository)
        );
    }

    public static GeneralUseCases retrieveGeneralUseCases() {
        if (generalUseCases == null)
            throw new Resources.NotFoundException("The Usecase is Not Defined Yet.");
        return generalUseCases;
    }


    /** setting up the database */
    public static void setupDatabase(Application application, AppCompatActivity activity, CountDownLatch latch) {
        // INFO: setup database and usecases
        AppDatabase db = provideAppDatabase(application);
        Executor executor = provideExecutor();
        AlarmScheduler alarmScheduler = new AlarmScheduler(application);
        provideAgendaUseCases(provideAgendaRepository(db, alarmScheduler));
        provideMemoUseCases(provideMemoRepository(db));
        provideGeneralUseCases(provideGeneralRepository(db));

        // INFO: setup alarms
        AlarmRepository alarmRepository = provideAlarmRepository(db);
        alarmRepository.schedule(alarmScheduler, activity, latch);
    }


    public static void newAgenda(){
        new ChooseTypeDialog().show();
    }
}
