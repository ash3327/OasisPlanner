package com.aurora.oasisplanner.data.datasource.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Ignore;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.model.entities.events._Agenda;
import com.aurora.oasisplanner.data.model.pojo.events.Alarm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface AgendaDao {

    // INFO: AGENDAS

    @Transaction
    @Query("SELECT * FROM _agenda")
    LiveData<List<Agenda>> getAgendas();

    @Ignore
    final String searchQuery_start = "SELECT * FROM _Agenda " +
            "JOIN _Agenda ON _Alarm.agendaId = _Agenda.agendaId " +
            "JOIN _Activity ON _Alarm.activityId = _Activity.activityId " +
            "JOIN _Event ON _Alarm.alarmListId = _Event.eventId ";
    @Ignore
    final String searchQuery_end = " AND " +
            "(_Alarm.alarmArgs LIKE '%' || :searchEntry || '%' OR " +
            "_Agenda.agendaTitle LIKE '%' || :searchEntry || '%' OR " +
            "_Activity.activityDescr LIKE '%' || :htmlSearchEntry || '%' OR " +
            "_Event.eventTitle LIKE '%' || :searchEntry || '%') " +
            "ORDER BY _Alarm.alarmDatetime ASC";

    @Transaction
    @Query("SELECT * FROM _Agenda WHERE agendaTitle LIKE '%' || :searchEntry || '%'")
    LiveData<List<Agenda>> getAgendasAfter(String searchEntry);

    @Transaction
    @Query("SELECT * FROM _agenda WHERE agendaId = :id")
    Agenda getAgendaById(long id);
    @Transaction
    @Query("SELECT * FROM _agenda WHERE agendaId = :id")
    _Agenda get_AgendaById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_Agenda agenda);

    @Delete
    void delete(_Agenda agenda);

    @Query("DELETE FROM _agenda")
    void deleteAllAgendas();
}
