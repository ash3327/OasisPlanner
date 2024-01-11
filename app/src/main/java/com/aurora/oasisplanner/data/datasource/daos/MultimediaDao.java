package com.aurora.oasisplanner.data.datasource.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.aurora.oasisplanner.data.model.entities.memos._Memo;
import com.aurora.oasisplanner.data.model.entities.util._Doc;

import java.util.List;

@Dao
public interface MultimediaDao {
    // INFO: Multimedia Elements

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(_Doc doc);
    @Delete
    void delete(_Doc doc);
}
