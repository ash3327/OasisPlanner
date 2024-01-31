package com.aurora.oasisplanner.data.tags;

import android.graphics.drawable.Drawable;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.util.styling.Resources;

public enum AlarmType {
    notif, agenda, todo;

    /** [DRAWABLE ICON, STRING BELOW, COLOR PRIMARY, COLOR SECONDARY] */
    public static int[][] typeIds = {
            {R.drawable.ic_agenda_notification, R.string.notif, R.color.grey_500, R.color.grey_300,
                    R.drawable.menuic_notification, R.drawable.ic_notif},
            {R.drawable.ic_agenda_calendar, R.string.activities, R.color.grey_500, R.color.grey_300,
                    R.drawable.ic_agendatype_calendar, R.drawable.ic_calendar},
            {R.drawable.ic_agenda_todo, R.string.todo, R.color.grey_500, R.color.grey_300,
                    R.drawable.ic_todo, R.drawable.ic_assignment_outline}};

    public int getType() {
        return this.ordinal();
    }
    public Drawable getDrawable() {
        return Resources.getDrawable(typeIds[getType()][Resources.DRAWABLE]);
    }
    public Drawable getSimpleDrawable(){
        return Resources.getDrawable(typeIds[getType()][Resources.SIMPLEDRAWABLE]);
    }
    public Drawable getOutlineDrawable() {
        return Resources.getDrawable(typeIds[getType()][Resources.EXTRA_1]);
    }
    public String toString() {
        return Resources.getString(typeIds[getType()][Resources.STRING]);
    }
}
