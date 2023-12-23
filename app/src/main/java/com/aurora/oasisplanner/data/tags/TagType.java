package com.aurora.oasisplanner.data.tags;

import android.graphics.drawable.Drawable;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.util.styling.Resources;

import java.util.Arrays;
import java.util.List;

public enum TagType {
    LOC, ALARM;

    /** [DRAWABLE ICON, STRING BELOW, COLOR PRIMARY, COLOR SECONDARY] */
    public static int[][] typeIds = {
            {R.drawable.ic_location,    R.string.tag_loc},
            {R.drawable.ic_agenda_notification,    R.string.tag_subalarm},
            };

    public static boolean contains(String key) {
        try {
            valueOf(key);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public int getType() {
        return this.ordinal();
    }

    public Drawable getDrawable() {
        return Resources.getDrawable(typeIds[getType()][0]);
    }

    public String toString() {
        return Resources.getString(typeIds[getType()][1]);
    }
}
