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

    // INFO: AGENDAS

    @Transaction
    @Query("SELECT * FROM _agenda")
    LiveData<List<Agenda>> getAgendas();

    @Transaction
    @Query("SELECT * FROM _agenda WHERE id = :id")
    Agenda getAgendaById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_Agenda agenda);

    @Delete
    void delete(_Agenda agenda);

    @Query("DELETE FROM _agenda")
    void deleteAllAgendas();
}
