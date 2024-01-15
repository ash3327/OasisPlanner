package com.aurora.oasisplanner.data.datasource.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;
import com.aurora.oasisplanner.data.model.entities.memos._Memo;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface AlarmDao {
    // INFO: SUBALARMS

    @Query("SELECT * FROM _SubAlarm WHERE datetime >= :fromDate ORDER BY datetime ASC")
    LiveData<List<_SubAlarm>> getSubAlarmsAfter(LocalDateTime fromDate);

    @Query("SELECT * FROM _SubAlarm WHERE id = :id")
    _Alarm getSubAlarmById(int id);

    @Query("SELECT * FROM _SubAlarm")
    List<_SubAlarm> getSubAlarms();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_SubAlarm alarm);

    @Delete
    void delete(_SubAlarm alarm);
    @Delete
    void deleteSubAlarms(List<_SubAlarm> subAlarms);

    @Query("DELETE FROM _SubAlarm")
    void deleteAllSubAlarms();

    // INFO: ALARMS

    @Query("SELECT * FROM _Alarm WHERE datetime >= :fromDate ORDER BY datetime ASC")
    LiveData<List<_Alarm>> getAlarmsAfter(LocalDateTime fromDate);

    @Query("SELECT * FROM _Alarm WHERE datetime >= :fromDate AND " +
            "(title LIKE '%' || :searchEntry || '%' OR alarmDescr LIKE '%' || :htmlSearchEntry || '%'" +
            "OR agendaDescr LIKE '%' || :htmlSearchEntry || '%' OR args LIKE '%' || :searchEntry || '%') " +
            "ORDER BY datetime ASC")
    LiveData<List<_Alarm>> getAlarmsAfter(LocalDateTime fromDate, String searchEntry, String htmlSearchEntry);

    @Query("SELECT * FROM _Alarm WHERE id = :id")
    _Alarm getAlarmById(long id);

    @Query("SELECT * FROM _Alarm")
    List<_Alarm> getAlarms();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_Alarm alarm);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<_Alarm> alarms);

    @Delete
    void delete(_Alarm alarm);
    @Delete
    void delete(List<_Alarm> alarm);

    @Query("DELETE FROM _Alarm")
    void deleteAllAlarms();
}
