package com.aurora.oasisplanner.data.repository;

import com.aurora.oasisplanner.data.datasource.daos.AgendaDao;
import com.aurora.oasisplanner.data.model.entities.events._Agenda;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class AgendaRepository {
    private final AgendaDao agendaDao;
    private final ExecutorService executor;

    public AgendaRepository(AgendaDao agendaDao, ExecutorService executor) {
        this.agendaDao = agendaDao;
        this.executor = executor;
    }

    public Future<Long> insertAgenda(final _Agenda agenda) {
        /*return executor.submit(()->{
            if (!agenda.hasAssociates())
                return agendaDao.insert(agenda);
            else
                return insertAgendaWithChild(agenda.getAssociates()).get();
        });*/
        assert false;
        return null;
    }

    public Future<_Agenda> getAgenda(final long id) {
        return executor.submit(()->agendaDao.get_AgendaById(id));
    }

    public void deleteAgenda(final _Agenda agenda) {
        /*executor.execute(()->{
            Agenda parent = agenda.getAssociates();
            AppModule.retrieveAlarmUseCases().delete(parent.alarms);
            AppModule.retrieveAlarmUseCases().deleteSubAlarms(parent.subalarms);
            agendaDao.delete(agenda);
        });*/
        assert false;
    }

    
    public Future<Long> insertAgendaWithChild(final Agenda agenda) {
        /*return executor.submit(()->{
            long id = agendaDao.insert(agenda.agenda);
            for (_Alarm alarm : agenda.alarms) {
                alarm.agendaId = id;
                alarm.agendaId = agenda.agenda.agendaId;
                alarm.agendaId = agenda.agenda.agendaId;
            }
            for (_SubAlarm alarm : agenda.subalarms) {
                alarm.agendaId = id;
                alarm.agendaId = agenda.agenda.agendaId;
                alarm.agendaId = agenda.agenda.agendaId;
            }
            AppModule.retrieveAlarmUseCases().put(agenda.alarms);
            AppModule.retrieveAlarmUseCases().putSubAlarms(agenda.subalarms);
            return id;
        });*/
        assert false;
        return null;
    }

    public Future<Agenda> getAgendaWithChild(final long id) {
        //return executor.submit(()->agendaDao.getAgendaWithChildById(id));
        assert false;
        return null;
    }

    public void deleteAgendaWithChild(final Agenda agenda) {
        /*executor.execute(()->{
            agendaDao.delete(agenda.agenda);
            AppModule.retrieveAlarmUseCases().delete(agenda.alarms);
        });*/
        assert false;
    }
}
