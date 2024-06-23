package com.aurora.oasisplanner.data.model.entities.util;

import android.graphics.Color;

import androidx.annotation.ColorInt;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.util.styling.Resources;

@Entity
public class _Tag {
    @Ignore
    public static @ColorInt int defaultCol = Resources.getColor(R.color.grey_100);
    public static @ColorInt int
            black = Resources.getColor(R.color.black),
            white = Resources.getColor(R.color.white);
    @Ignore
    public boolean isNew = false;

    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public @ColorInt int color = defaultCol;

    public _Tag() {}

    @Ignore
    public @ColorInt int getTextColor() {
        return Color.valueOf(color).luminance() < .5 ? white : black;
    }
}
