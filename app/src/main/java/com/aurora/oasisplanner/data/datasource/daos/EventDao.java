package com.aurora.oasisplanner.data.datasource.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.aurora.oasisplanner.data.model.entities.events._Agenda;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.pojo.events.Event;

@Dao
public interface EventDao {

    // INFO: EVENTS

    @Transaction
    @Query("SELECT * FROM _Event WHERE id = :id")
    _Event getEventById(long id);
    @Transaction
    @Query("SELECT * FROM _Event WHERE id = :id")
    Event getEventWithChildById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_Event alarmList);

    @Delete
    void delete(_Agenda agenda);
    @Delete
    void delete(_Event alarmList);

    @Query("DELETE FROM _Event WHERE activityId = :activityId")
    void deleteEventsByActivity(long activityId);

    @Query("DELETE FROM _Event")
    void deleteAllAlarmLists();
}
