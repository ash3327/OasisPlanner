package com.aurora.oasisplanner.data.model.entities.events;

import android.os.Bundle;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aurora.oasisplanner.data.tags.AgendaType;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.util.Converters;

import java.io.Serializable;
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

    @Ignore
    public Bundle packContents() {
        Bundle extras = new Bundle();

        Converters converter = new Converters();

        extras.putLong("id", id);
        extras.putString("title", title);
        extras.putString("types", converter.typeListToString(types));
        extras.putString("type", type.name());
        extras.putSerializable("args", (Serializable) args);

        return extras;
    }

    @Ignore
    public static _Agenda unpackContents(Bundle extras) {
        _Agenda alarm = new _Agenda();

        Converters converter = new Converters();

        alarm.id = extras.getLong("id");
        alarm.title = extras.getString("title");
        alarm.types = converter.typeListFromString(extras.getString("types"));
        alarm.type = AgendaType.valueOf(extras.getString("type"));
        alarm.args = (Map<String,String>) extras.getSerializable("args");

        return alarm;
    }

    @Ignore
    public String toString() {
        return "\n\t\t [ Activity : "+id+" : \n\t\t\t"+type.name()+"\n\t\t\t"+title+"\n\t\t ]";
    }
}
