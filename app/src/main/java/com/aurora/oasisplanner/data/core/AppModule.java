package com.aurora.oasisplanner.data.core;

import android.app.Application;
import android.content.res.Resources;

import androidx.appcompat.app.AppCompatActivity;

import com.aurora.oasisplanner.data.core.use_cases.AlarmUseCases;
import com.aurora.oasisplanner.data.core.use_cases.EventUseCases;
import com.aurora.oasisplanner.data.core.use_cases.GetTagUseCases;
import com.aurora.oasisplanner.data.core.use_cases.MemoUseCases;
import com.aurora.oasisplanner.data.datasource.AppDatabase;
import com.aurora.oasisplanner.data.repository.AgendaRepository;
import com.aurora.oasisplanner.data.repository.AlarmRepository;
import com.aurora.oasisplanner.data.core.use_cases.AgendaUseCases;
import com.aurora.oasisplanner.data.core.use_cases.EditAlarmListUseCases;
import com.aurora.oasisplanner.data.repository.EventRepository;
import com.aurora.oasisplanner.data.repository.MultimediaRepository;
import com.aurora.oasisplanner.data.repository.TagRepository;
import com.aurora.oasisplanner.data.repository.MemoRepository;
import com.aurora.oasisplanner.presentation.dialog.choosetypedialog.ChooseTypeDialog;
import com.aurora.oasisplanner.util.notificationfeatures.AlarmScheduler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppModule {

    private static AgendaUseCases agendaUseCases;
    private static EditAlarmListUseCases editAlarmListUseCases;
    private static EventUseCases eventUseCases;
    private static AlarmUseCases alarmUseCases;
    private static MemoUseCases memoUseCases;
    private static GetTagUseCases getTagUseCases;

    private static ExecutorService executor;

    public static ExecutorService provideExecutor() {
        if (executor == null)
            executor = Executors.newFixedThreadPool(2);
        return executor;
    }

    public static AppDatabase provideAppDatabase(Application application) {
        return AppDatabase.getInstance(application);
    }

    public static AgendaRepository provideAgendaRepository(AppDatabase db, AlarmScheduler alarmScheduler) {
        return new AgendaRepository(
                db.agendaDao(), db.alarmDao(),
                db.activityDao(), db.eventDao(),
                alarmScheduler);
    }
    public static EventRepository provideEventRepository(AppDatabase db, ExecutorService executor) {
        return new EventRepository(db.eventDao(), executor);
    }
    public static AlarmRepository provideAlarmRepository(AppDatabase db, ExecutorService executor) {
        return new AlarmRepository(db.alarmDao(), executor);
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
    public static AgendaUseCases retrieveAgendaUseCases() {
        if (agendaUseCases == null)
            throw new Resources.NotFoundException("The Usecase is Not Defined Yet.");
        return agendaUseCases;
    }

    public static EventUseCases provideEventUseCases(EventRepository repository) {
        if (eventUseCases != null) return eventUseCases;
        return eventUseCases = new EventUseCases(repository);
    }
    public static EventUseCases retrieveEventUseCases() {
        if (eventUseCases == null)
            throw new Resources.NotFoundException("The Usecase is Not Defined Yet.");
        return eventUseCases;
    }

    public static AlarmUseCases provideAlarmUseCases(AlarmRepository repository) {
        if (alarmUseCases != null) return alarmUseCases;
        return alarmUseCases = new AlarmUseCases(repository);
    }
    public static AlarmUseCases retrieveAlarmUseCases() {
        if (alarmUseCases == null)
            throw new Resources.NotFoundException("The Usecase is Not Defined Yet.");
        return alarmUseCases;
    }

    public static EditAlarmListUseCases provideEditAlarmListUseCases() {
        if (editAlarmListUseCases != null) return editAlarmListUseCases;
        return editAlarmListUseCases = new EditAlarmListUseCases();
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
        ExecutorService executor = provideExecutor();
        AlarmScheduler alarmScheduler = new AlarmScheduler(application);
        provideAgendaUseCases(provideAgendaRepository(db, alarmScheduler));
        provideEventUseCases(provideEventRepository(db, executor));
        provideAlarmUseCases(provideAlarmRepository(db, executor));
        provideEditAlarmListUseCases();
        provideMemoUseCases(provideMemoRepository(db));
        provideGetTagUseCases(provideGeneralRepository(db));
        provideMultimediaRepository(db, executor);

        // INFO: setup alarms
        AlarmRepository alarmRepository = provideAlarmRepository(db, executor);
        alarmRepository.schedule(alarmScheduler, activity, latch);
    }


    public static void newAgenda(){
        new ChooseTypeDialog().show();
    }
}
