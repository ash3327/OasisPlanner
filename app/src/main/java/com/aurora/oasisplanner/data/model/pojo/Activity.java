package com.aurora.oasisplanner.data.model.pojo;

import static com.aurora.oasisplanner.data.tags.ActivityType.Type.doc;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.aurora.oasisplanner.data.model.entities._AlarmList;
import com.aurora.oasisplanner.data.model.entities._Doc;
import com.aurora.oasisplanner.data.model.entities._Activity;
import com.aurora.oasisplanner.data.tags.ActivityType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Activity {
    @Embedded
    public _Activity activity;

    @Relation(parentColumn = "id", entityColumn = "groupId", entity = _AlarmList.class)
    public List<AlarmList> alarmList = new ArrayList<>();

    @Relation(parentColumn = "id", entityColumn = "groupId", entity = _Doc.class)
    public List<_Doc> docs = new ArrayList<>();

    @Ignore
    public List<AlarmList> invisGroups = new ArrayList<>();
    @Ignore
    public List<_Doc> invisDocs = new ArrayList<>();

    @Ignore
    public boolean visible = true;

    /** This constructor is not for programmer access. Please use Activity(null) instead. */
    public Activity(){}

    /** Please always use this constructor with content NULL. */
    @Ignore
    public Activity(Object obj) {
        this.activity = new _Activity();
    }

    @Ignore
    public Activity putItems(Object... objs) {
        for (Object obj : objs) {
            if (obj instanceof AlarmList) {
                activity.types.add(new ActivityType(ActivityType.Type.activity, alarmList.size()));
                alarmList.add((AlarmList) obj);
            }
            if (obj instanceof _Doc) {
                //activity.types.add(new ActivityType(ActivityType.Type.doc, docs.size()));
                docs.add((_Doc) obj);
            }
        }
        return this;
    }

    @Ignore
    public static Activity empty() {
        return new Activity(null).putItems(
                _Doc.empty()
        );
    }

    @Ignore
    public List[] getObjList(boolean sort) {
        List<Object> list = new ArrayList<>();
        List<ActivityType.Type> types = new ArrayList<>();

        if (sort) {
            alarmList.sort(Comparator.comparingInt(a -> a.alarmList.i));
            docs.sort(Comparator.comparingInt(a -> a.i));
        }

        for (ActivityType gt : activity.types) {
            boolean visible = true;
            Object obj = null;
            switch (gt.type) {
                case activity:
                    obj = alarmList.get(gt.i);
                    visible = ((AlarmList) obj).visible;
                    break;
                case doc:
                    obj = docs.get(gt.i);
                    visible = ((_Doc) obj).visible;
                    break;
            }
            if (visible && obj != null && gt.type != doc) {
                list.add(obj);
                types.add(gt.type);
            }
        }
        for (AlarmList gp : alarmList)
            if (!gp.visible) invisGroups.add(gp);
        for (_Doc doc : docs)
            if (!doc.visible) invisDocs.add(doc);

        return new List[]{list, types};
    }

    @Ignore
    public void update() {
        List<Object> list = getObjList(false)[0];

        alarmList = new ArrayList<>();
        docs = new ArrayList<>();
        activity.types = new ArrayList<>();

        int i = 0;
        for (Object obj : list) {
            if (obj instanceof AlarmList) {
                activity.types.add(new ActivityType(ActivityType.Type.activity, alarmList.size()));
                alarmList.add(((AlarmList) obj).setI(i));
            }
            if (obj instanceof _Doc) {
                activity.types.add(new ActivityType(doc, docs.size()));
                docs.add(((_Doc) obj).setI(i));
            }
            i++;
        }
    }

    public Activity setI(int i) {
        this.activity.i = i;
        return this;
    }

    @Ignore
    public String toString() {
        return "\n\t [ Activity : "+activity.id+" : "+getObjList(false)[0]+"\n\t delete gps: "+invisGroups+"\n\t delete docs: "+invisDocs+"\n\t ]";
    }
}
