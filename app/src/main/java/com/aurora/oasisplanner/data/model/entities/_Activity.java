package com.aurora.oasisplanner.data.model.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aurora.oasisplanner.data.tags.ActivityType;

import java.util.ArrayList;
import java.util.List;

@Entity()
public class _Activity {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public long agendaId;
    public List<ActivityType> types = new ArrayList<>();
    public int i = -1;

    public _Activity(){}

    @Ignore
    public _Activity(_Agenda agenda) {
        agendaId = agenda.id;
    }
}
