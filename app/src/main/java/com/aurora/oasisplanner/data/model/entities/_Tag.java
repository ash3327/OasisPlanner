package com.aurora.oasisplanner.data.model.entities;

import android.text.SpannableStringBuilder;

import androidx.annotation.ColorInt;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.presentation.widget.taginputeidittext.TagInputEditText;
import com.aurora.oasisplanner.util.styling.Resources;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
public class _Tag {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    public @ColorInt int color = Resources.getColor(R.color.grey);

    public _Tag() {}
}
