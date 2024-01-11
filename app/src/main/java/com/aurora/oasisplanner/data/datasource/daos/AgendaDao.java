package com.aurora.oasisplanner.data.datasource.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.entities.memos._Memo;
import com.aurora.oasisplanner.data.model.entities.events._SubAlarm;
import com.aurora.oasisplanner.data.model.entities.util._Tag;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;
import com.aurora.oasisplanner.data.model.entities.events._Agenda;
import com.aurora.oasisplanner.data.model.entities.events._Alarm;
import com.aurora.oasisplanner.data.model.entities.events._AlarmList;
import com.aurora.oasisplanner.data.model.entities.util._Doc;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface AgendaDao {

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
    _Alarm getAlarmById(int id);

    @Query("SELECT * FROM _Alarm")
    List<_Alarm> getAlarms();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_Alarm alarm);

    @Delete
    void delete(_Alarm alarm);

    @Query("DELETE FROM _Alarm")
    void deleteAllAlarms();

    // INFO: AGENDAS

    // INFO: GENERAL __ENTITY


    // INFO: GENERAL _ENTITY

    // INFO: OLD AGENDAS

    @Transaction
    @Query("SELECT * FROM _agenda")
    LiveData<List<Agenda>> getAgendas();

    @Transaction
    @Query("SELECT * FROM _agenda WHERE id = :id")
    Agenda getAgendaById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_Agenda agenda);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_Activity activity);
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_AlarmList alarmList);

    @Delete
    void delete(_Agenda agenda);
    @Delete
    void delete(_Activity activity);
    @Delete
    void delete(_AlarmList alarmList);

    @Query("DELETE FROM _agenda")
    void deleteAllAgendas();
    @Query("DELETE FROM _Activity")
    void deleteAllGroups();
    @Query("DELETE FROM _alarmlist")
    void deleteAllAlarmLists();

    // INFO: Multimedia Elements

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_Doc doc);
    @Delete
    void delete(_Doc doc);
    
    // INFO: Memos

    @Query("SELECT * FROM _Memo WHERE id = :id")
    _Memo getMemoById(long id);

    @Query("SELECT * FROM _Memo")
    LiveData<List<_Memo>> getMemos();

    @Query("SELECT * FROM _Memo WHERE tags LIKE '%' || :searchEntry || '%' " +
            "OR title LIKE '%' || :htmlSearchEntry || '%' OR contents LIKE '%' || :htmlSearchEntry || '%'")
    LiveData<List<_Memo>> getMemos(String searchEntry, String htmlSearchEntry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_Memo Memo);

    @Delete
    void delete(_Memo Memo);

    @Query("DELETE FROM _Memo")
    void deleteAllMemos();

    // INFO: Tags
    @Query("SELECT * FROM _Tag WHERE name = :name")
    _Tag getTagByName(String name);

    @Query("SELECT * FROM _Tag")
    LiveData<List<_Tag>> getTags();

    @Query("SELECT * FROM _Tag WHERE name LIKE '%' || :searchEntry || '%' ")
    LiveData<List<_Tag>> getTags(String searchEntry);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_Tag tag);

    @Delete
    void delete(_Tag tag);

    @Query("DELETE FROM _Tag")
    void deleteAllTags();
}
