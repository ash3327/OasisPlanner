package com.aurora.oasisplanner.data.model.entities;

import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.util.styling.Resources;

import java.util.ArrayList;
import java.util.List;

@Entity()
public class _Activity {
    public enum Type {
        event, task;

        /** [DRAWABLE ICON, STRING BELOW, COLOR PRIMARY, COLOR SECONDARY] */
        public static int[][] typeIds = {
                {R.drawable.ic_agenda_calendar, R.string.activities, R.color.grey_500,         R.color.grey_300, R.drawable.ic_agendatype_calendar},
                {R.drawable.ic_agenda_todo,            R.string.todo,       R.color.grey_500,         R.color.grey_300, R.drawable.ic_todo}
        };

        public Drawable getDrawable() {
            return Resources.getDrawable(typeIds[ordinal()][Resources.SIMPLEDRAWABLE]);
        }
    }

    @PrimaryKey(autoGenerate = true)
    public long id;
    public long agendaId;
    public List<ActivityType> types = new ArrayList<>();
    public Type type;
    public Importance importance;
    public int i = -1;

    public _Activity(){}

    @Ignore
    public _Activity(_Agenda agenda) {
        agendaId = agenda.id;
    }

    public Type getType() {
        if (type == null)
            type = Type.event;
        return type;
    }

    public Importance getImportance() {
        if (importance == null)
            importance = Importance.regular;
        return importance;
    }//*/
}
