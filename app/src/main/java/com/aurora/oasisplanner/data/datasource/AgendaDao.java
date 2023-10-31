package com.aurora.oasisplanner.data.datasource;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.aurora.oasisplanner.data.model.entities._Activity;
import com.aurora.oasisplanner.data.model.entities._Period;
import com.aurora.oasisplanner.data.model.entities._Periods;
import com.aurora.oasisplanner.data.model.pojo.Agenda;
import com.aurora.oasisplanner.data.model.entities._Agenda;
import com.aurora.oasisplanner.data.model.entities._Alarm;
import com.aurora.oasisplanner.data.model.entities._AlarmList;
import com.aurora.oasisplanner.data.model.entities._Doc;
import com.aurora.oasisplanner.data.model.pojo.Period;

import java.time.LocalDateTime;
import java.util.List;

@Dao
public interface AgendaDao {

    // INFO: ALARMS

    @Query("SELECT * FROM _Alarm WHERE datetime >= :fromDate ORDER BY datetime ASC")
    LiveData<List<_Alarm>> getAlarmsAfter(LocalDateTime fromDate);

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

    // INFO: PERIODS

    @Transaction
    @Query("SELECT * FROM _periods")
    LiveData<List<Period>> getPeriods();

    @Transaction
    @Query("SELECT * FROM _periods WHERE toDate >= :fromDate ORDER BY fromDate ASC")
    LiveData<List<Period>> getPeriodsAfter(LocalDateTime fromDate);

    @Transaction
    @Query("SELECT * FROM _periods WHERE id = :id")
    Period getPeriodById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_Period period);

    @Delete
    void delete(_Period period);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_Periods period);

    @Delete
    void delete(_Periods period);

    @Query("DELETE FROM _Period")
    void deleteAllPeriod();

    @Query("DELETE FROM _Periods")
    void deleteAllPeriods();

    // INFO: AGENDAS

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
}
