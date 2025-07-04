package com.aurora.oasisplanner.data.model.entities.events;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.aurora.oasisplanner.R;
import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.tags.ActivityType;
import com.aurora.oasisplanner.data.tags.Importance;
import com.aurora.oasisplanner.data.util.Converters;
import com.aurora.oasisplanner.util.styling.Resources;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Entity()
public class _Activity extends __Item {
    public enum Type {
        event, task;

        /** [DRAWABLE ICON, STRING BELOW, COLOR PRIMARY, COLOR SECONDARY] */
        public static int[][] typeIds = {
                {R.drawable.ic_agenda_calendar, R.string.activities, R.color.grey_500,         R.color.grey_300, R.drawable.ic_calendar_big},
                {R.drawable.ic_agenda_todo,            R.string.todo,       R.color.grey_500,         R.color.grey_300, R.drawable.ic_assignment_outline_big}
        };

        public Drawable getDrawable() {
            return Resources.getDrawable(typeIds[ordinal()][Resources.SIMPLEDRAWABLE]);
        }
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "activityId")
    public long id;
    public long agendaId;
    @ColumnInfo(name = "activityTypes")
    public List<ActivityType> types = new ArrayList<>();
    @ColumnInfo(name = "activityType")
    public Type type = Type.event;
    @ColumnInfo(name = "activityImportance")
    public Importance importance = Importance.regular;
    @ColumnInfo(name = "activityDescr", defaultValue = "")
    public SpannableStringBuilder title;
    @ColumnInfo(name = "activityArgs")
    public Map<String,String> args = new HashMap<>();
    @ColumnInfo(name = "activityI")
    public int i = -1;

    @Ignore public boolean visible = true;

    public _Activity(){}

    @Ignore
    public _Activity(_Agenda agenda) {
        agendaId = agenda.id;
    }

    @Ignore
    public _Activity(String title) {
        this.title = new SpannableStringBuilder(title);
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
    public CharSequence getTitle() {
        return title;
    }
    @Ignore
    public void setTitle(CharSequence val) {
        title = new SpannableStringBuilder(val);
    }

    @Ignore
    public _Activity setI(int i) {
        this.i = i;
        return this;
    }

    @Ignore
    public Bundle packContents() {
        Bundle extras = new Bundle();

        Converters converter = new Converters();

        extras.putLong("id", id);
        extras.putLong("agendaId", agendaId);
        extras.putString("types", converter.typeListToString(types));
        extras.putString("type", getType().name());
        extras.putString("importance", getImportance().name());
        extras.putString("descr", converter.spannableToString(title));
        extras.putSerializable("args", (Serializable) args);

        return extras;
    }

    @Ignore
    public static _Activity unpackContents(Bundle extras) {
        _Activity alarm = new _Activity();

        Converters converter = new Converters();

        alarm.id = extras.getLong("id");
        alarm.agendaId = extras.getLong("agendaId");
        alarm.types = converter.typeListFromString(extras.getString("types"));
        alarm.type = Type.valueOf(extras.getString("type"));
        alarm.importance = Importance.valueOf(extras.getString("importance"));
        alarm.title = converter.spannableFromString(extras.getString("descr"));
        alarm.args = (Map<String,String>) extras.getSerializable("args");

        return alarm;
    }

    @Ignore
    public String toString() {
        return "\n\t\t [ Activity : "+id+" : \n\t\t\t"+importance.name()+"\n\t\t\t"+type.name()+
                "\n\t\t\t"+ title.toString()+"\n\t\t ]";
    }


    @Ignore
    private Activity cache = null;

    /** Note: This method is NOT thread safe for the UI thread. */
    public Activity getCache() throws ExecutionException, InterruptedException {
        return mergeCache(
                AppModule.retrieveActivityUseCases().getActivityWithChild(id).get(),
                cache
        );
    }
    public boolean hasCache() {
        return cache != null;
    }
    public void setCache(Activity activity) {
        this.cache = activity;
    }
    private Activity mergeCache(Activity activity, Activity cache) {
        if (cache == null)
            cache = activity;
        if (cache == null)
            cache = Activity.empty();
        cache.activity = this;
        this.cache = cache;
        return this.cache;
    }

    @Ignore
    private static long count = 0;
    @Ignore
    private long privateId = 0;
    public long getUniqueReference() {
        if (id != 0)
            return id;
        if (privateId != 0)
            return privateId;
        return privateId = --count;
    }
}
