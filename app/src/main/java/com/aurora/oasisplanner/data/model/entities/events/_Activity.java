package com.aurora.oasisplanner.data.model.entities.events;

import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.model.entities.__Entity;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.util.styling.Resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity()
public class _Activity extends __Entity {
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
    @ColumnInfo(defaultValue = "")
    public SpannableStringBuilder descr;
    public Map<String,String> args = new HashMap<>();
    public int i = -1;

    @Ignore
    public boolean visible = true;

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

    @Ignore
    public _Activity setI(int i) {
        this.i = i;
        return this;
    }
}
