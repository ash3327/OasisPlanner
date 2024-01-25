package com.aurora.oasisplanner.data.datasource.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.aurora.oasisplanner.data.model.entities.util._Tag;

import java.util.List;

@Dao
public interface TagDao {
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
