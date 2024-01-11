package com.aurora.oasisplanner.data.datasource.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.aurora.oasisplanner.data.model.entities.memos._Memo;
import com.aurora.oasisplanner.data.model.entities.util._Tag;

import java.util.List;

@Dao
public interface MemoDao {
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
}
