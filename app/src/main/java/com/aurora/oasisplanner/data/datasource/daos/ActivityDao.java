package com.aurora.oasisplanner.data.datasource.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;

@Dao
public interface ActivityDao {

    // INFO: ACTIVITIES

    @Transaction
    @Query("SELECT * FROM _Activity WHERE id = :id")
    _Activity getActivityById(long id);
    @Transaction
    @Query("SELECT * FROM _Activity WHERE id = :id")
    Activity getActivityWithChildById(long id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_Activity activity);

    @Delete
    void delete(_Activity activity);

    @Query("DELETE FROM _Activity")
    void deleteAllGroups();
}
