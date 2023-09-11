package com.aurora.oasisplanner.data.model.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class _Periods {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public LocalDate fromDate, toDate;
    // TODO: when the Period is updated, this should also be updated (bounds)

    @Ignore
    public boolean visible = true;

    public _Periods() {}

    @Override
    public String toString() {
        return "<"+id+","+ name+">";
    }
}
