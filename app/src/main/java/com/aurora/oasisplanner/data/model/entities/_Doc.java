package com.aurora.oasisplanner.data.model.entities;

import android.text.SpannableStringBuilder;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class _Doc {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public SpannableStringBuilder contents;
    public long agendaId = -1; // agenda can contain a _Doc, but a _Doc is not necessarily contained in an Agenda.
    public long groupId = -1;
    public int i = -1;

    @Ignore
    public boolean visible = true;

    public _Doc() {}

    @Ignore
    public _Doc(CharSequence contents) {
        this.contents = new SpannableStringBuilder(contents);
    }

    public void clearAllIds() {
        agendaId = -1;
        groupId = -1;
    }
    public void setAgendaId(long agendaId) {
        clearAllIds();
        this.agendaId = agendaId;
    }
    public void setGroupId(long groupId) {
        clearAllIds();
        this.groupId = groupId;
    }

    @Ignore
    public static _Doc empty() {
        return new _Doc("");
    }

    @Ignore
    public _Doc setI(int i) {
        this.i = i;
        return this;
    }

    @Ignore
    public static SpannableStringBuilder getFirst(List<_Doc> docs, String ifNull) {
        return docs.size() > 0 ?
                docs.get(0).contents :
                new SpannableStringBuilder(ifNull);
    }

    @Ignore
    public String toString() {
        return " [_Doc : "+id+" : "+contents.toString()+"] ";
    }
}
