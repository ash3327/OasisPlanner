package com.aurora.oasisplanner.data.model.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aurora.oasisplanner.data.tags.GroupType;

import java.util.ArrayList;
import java.util.List;

@Entity()
public class _Group {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long agendaId;
    public List<GroupType> types = new ArrayList<>();
    public int i = -1;

    public _Group(){}

    @Ignore
    public _Group(_Agenda agenda) {
        agendaId = agenda.id;
    }
}
