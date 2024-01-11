package com.aurora.oasisplanner.data.model.entities;

import android.text.SpannableStringBuilder;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aurora.oasisplanner.data.tags.AgendaType;
import com.aurora.oasisplanner.data.tags.ActivityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
public class _Agenda {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String title;
    public AgendaType type;
    public List<ActivityType> types = new ArrayList<>();
    public Map<String,String> args = new HashMap<>();

    public _Agenda(){}

    @Ignore
    public _Agenda(AgendaType type, String title) {
        this.type = type; this.title = title;
    }
}
