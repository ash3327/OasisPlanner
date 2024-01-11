package com.aurora.oasisplanner.data.core;

import android.app.Application;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import com.aurora.oasisplanner.data.core.use_cases.GetTagUseCases;
import com.aurora.oasisplanner.data.core.use_cases.MemoUseCases;
import com.aurora.oasisplanner.data.datasource.AppDatabase;
import com.aurora.oasisplanner.data.repository.AgendaRepository;
import com.aurora.oasisplanner.data.repository.AlarmRepository;
import com.aurora.oasisplanner.data.core.use_cases.AgendaUseCases;
import com.aurora.oasisplanner.data.core.use_cases.EditAlarmListUseCases;
import com.aurora.oasisplanner.data.repository.MultimediaRepository;
import com.aurora.oasisplanner.data.repository.TagRepository;
import com.aurora.oasisplanner.data.repository.MemoRepository;
import com.aurora.oasisplanner.presentation.dialog.choosetypedialog.ChooseTypeDialog;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmScheduler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppModule {

    private static AgendaUseCases agendaUseCases;
    private static EditAlarmListUseCases editAlarmListUseCases;
    private static MemoUseCases memoUseCases;
    private static GetTagUseCases getTagUseCases;

    public static Executor provideExecutor() {
        return Executors.newFixedThreadPool(2);
    }
    public static AppDatabase provideAppDatabase(Application application) {
        return AppDatabase.getInstance(application);
    }

    public static AgendaRepository provideAgendaRepository(AppDatabase db, AlarmScheduler alarmScheduler) {
        return new AgendaRepository(db.agendaDao(), db.alarmDao(), alarmScheduler);
    }
    public static AlarmRepository provideAlarmRepository(AppDatabase db) {
        return new AlarmRepository(db.alarmDao());
    }
    public static MemoRepository provideMemoRepository(AppDatabase db) {
        return new MemoRepository(db.memoDao());
    }
    public static TagRepository provideGeneralRepository(AppDatabase db) {
        return new TagRepository(db.tagDao());
    }
    public static MultimediaRepository provideMultimediaRepository(AppDatabase db, Executor executor) {
        return new MultimediaRepository(db.multimediaDao(), executor);
    }

    public static AgendaUseCases provideAgendaUseCases(AgendaRepository repository) {
        if (agendaUseCases != null) return agendaUseCases;
        return agendaUseCases = new AgendaUseCases(repository);
    }

    public static EditAlarmListUseCases provideEditAlarmListUseCases() {
        if (editAlarmListUseCases != null) return editAlarmListUseCases;
        return editAlarmListUseCases = new EditAlarmListUseCases();
    }

    public static AgendaUseCases retrieveAgendaUseCases() {
        if (agendaUseCases == null)
            throw new Resources.NotFoundException("The Usecase is Not Defined Yet.");
        return agendaUseCases;
    }

    public static EditAlarmListUseCases retrieveEditAlarmListUseCases() {
        if (editAlarmListUseCases == null)
            throw new Resources.NotFoundException("The Usecase is Not Defined Yet.");
        return editAlarmListUseCases;
    }

    public static MemoUseCases provideMemoUseCases(MemoRepository repository) {
        if (memoUseCases != null) return memoUseCases;
        return memoUseCases = new MemoUseCases(repository);
    }

    public static MemoUseCases retrieveMemoUseCases() {
        if (memoUseCases == null)
            throw new Resources.NotFoundException("The Usecase is Not Defined Yet.");
        return memoUseCases;
    }

    public static GetTagUseCases provideGetTagUseCases(TagRepository repository) {
        if (getTagUseCases != null) return getTagUseCases;
        return getTagUseCases = new GetTagUseCases(repository);
    }

    public static GetTagUseCases retrieveGetTagUseCases() {
        if (getTagUseCases == null)
            throw new Resources.NotFoundException("The Usecase is Not Defined Yet.");
        return getTagUseCases;
    }


    /** setting up the database */
    public static void setupDatabase(Application application, AppCompatActivity activity, CountDownLatch latch) {
        // INFO: setup database and usecases
        AppDatabase db = provideAppDatabase(application);
        Executor executor = provideExecutor();
        AlarmScheduler alarmScheduler = new AlarmScheduler(application);
        provideAgendaUseCases(provideAgendaRepository(db, alarmScheduler));
        provideEditAlarmListUseCases();
        provideMemoUseCases(provideMemoRepository(db));
        provideGetTagUseCases(provideGeneralRepository(db));
        provideMultimediaRepository(db, executor);

        // INFO: setup alarms
        AlarmRepository alarmRepository = provideAlarmRepository(db);
        alarmRepository.schedule(alarmScheduler, activity, latch);
    }


    public static void newAgenda(){
        new ChooseTypeDialog().show();
    }
}
