package com.aurora.oasisplanner.data.util;

import android.text.SpannableStringBuilder;

import androidx.room.TypeConverter;

import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.util.styling.Styles;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Converters {
    @TypeConverter
    public Duration durationFromLong(Long value) {
        return value == null ? null : Duration.ofSeconds(value);
    }
    @TypeConverter
    public Long durationToLong(Duration duration) {
        return duration == null ? null : duration.getSeconds();
    }

    @TypeConverter
    public LocalDateTime datetimeFromTimestamp(Long val) {
        return LocalDateTime.ofEpochSecond(val, 0, ZoneOffset.UTC);
    }
    @TypeConverter
    public Long datetimeToTimestamp(LocalDateTime date) {
        return date.toEpochSecond(ZoneOffset.UTC);
    }

    @TypeConverter
    public LocalDate dateFromTimestamp(Long val) {
        return LocalDate.ofEpochDay(val);
    }
    @TypeConverter
    public Long dateToTimestamp(LocalDate date) {
        return date.toEpochDay();
    }

    @TypeConverter
    public SpannableStringBuilder spannableFromString(String val) {
        return Styles.toStyled(val);
    }
    @TypeConverter
    public String spannableToString(SpannableStringBuilder val) {
        return Styles.toHtml(val);
    }

    @TypeConverter
    public List<LocalDate> localDatesFromString(String val) {
        if (val.isEmpty())
            return new ArrayList<>();
        return Arrays.stream(val.split(Styles.SEP)).map((s)->LocalDate.ofEpochDay(Long.parseLong(s))).collect(Collectors.toList());
    }
    @TypeConverter
    public String localDatesToString(List<LocalDate> val) {
        return val.stream().map((s)->s.toEpochDay()+"").reduce("",(a,b)->a+Styles.SEP+b).replaceFirst(Styles.SEP, "");
    }

    @TypeConverter
    public LocalTime localTimeFromTimestamp(Long val) {
        return LocalTime.ofSecondOfDay(val);
    }
    @TypeConverter
    public Long localTimeToTimestamp(LocalTime time) {
        return (long)time.toSecondOfDay();
    }

    @TypeConverter
    public List<ActivityType> typeListFromString(String val) {
        return Arrays.stream(val.split(Styles.SEP)).map(ActivityType::valueOf).collect(Collectors.toList());
    }
    @TypeConverter
    public String typeListToString(List<ActivityType> val) {
        return val.stream().map(ActivityType::toString).reduce("",(a, b)->a+Styles.SEP+b).replaceFirst(Styles.SEP, "");
    }

    @TypeConverter
    public Map<String,String> bundleFromString(String str) {
        try {
            if (str == null) str = "";
            JSONObject jsonObject = new JSONObject(str);
            Map<String,String> map = new HashMap<>();
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                String val = jsonObject.getString(key);
                map.put(key,val);
            }
            return map;
        } catch (JSONException e) {
            return null;
        }
    }
    @TypeConverter
    public String bundleToString(Map<String,String> map) {
        if (map == null) map = new HashMap<>();
        return new JSONObject(map).toString();
    }
}
