package com.aurora.oasisplanner.data.datasource.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.entities.events._Agenda;
import com.aurora.oasisplanner.data.model.entities.events._AlarmList;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;

import java.util.List;

@Dao
public interface EventDao {

    // INFO: EVENTS

    @Transaction
    @Query("SELECT * FROM _AlarmList WHERE id = :id")
    _AlarmList getEventById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_AlarmList alarmList);

    @Delete
    void delete(_Agenda agenda);
    @Delete
    void delete(_AlarmList alarmList);

    @Query("DELETE FROM _AlarmList")
    void deleteAllAlarmLists();
}
