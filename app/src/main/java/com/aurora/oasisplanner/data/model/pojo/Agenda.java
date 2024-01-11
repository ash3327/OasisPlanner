package com.aurora.oasisplanner.data.model.pojo;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

import com.aurora.oasisplanner.data.model.entities._Agenda;
import com.aurora.oasisplanner.data.model.entities._Doc;
import com.aurora.oasisplanner.data.model.entities._Activity;
import com.aurora.oasisplanner.data.tags.AgendaType;
import com.aurora.oasisplanner.data.tags.ActivityType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Agenda extends _Entity {
    @Embedded
    public _Agenda agenda;

    @Relation(parentColumn = "id", entityColumn = "agendaId", entity = _Activity.class)
    public List<Activity> activities = new ArrayList<>();

    @Relation(parentColumn = "id", entityColumn = "agendaId", entity = _Doc.class)
    public List<_Doc> docs = new ArrayList<>();

    @Ignore
    public List<Activity> invisGroups = new ArrayList<>();
    @Ignore
    public List<_Doc> invisDocs = new ArrayList<>();

    public Agenda(){}

    @Ignore
    public Agenda(AgendaType type, String title) {
        this.agenda = new _Agenda(type, title);
    }

    public static Agenda empty() {
        return new Agenda(
                AgendaType.agenda,
                ""
        );
    }

    @Ignore
    public List[] getObjList(boolean sort) {
        List<Object> list = new ArrayList<>();
        List<ActivityType.Type> types = new ArrayList<>();

        if (sort) {
            activities.sort(Comparator.comparingInt(a -> a.activity.i));
            docs.sort(Comparator.comparingInt(a -> a.i));
        }

        for (ActivityType gt : agenda.types) {
            boolean visible = true;
            Object obj = null;
            switch (gt.type) {
                case activity:
                    obj = activities.get(gt.i);
                    visible = ((Activity) obj).visible;
                    break;
                case doc:
                    obj = docs.get(gt.i);
                    visible = ((_Doc) obj).visible;
                    break;
            }
            if (visible && obj != null) {
                list.add(obj);
                types.add(gt.type);
            }
        }
        for (Activity gp : activities)
            if (!gp.visible) invisGroups.add(gp);
        for (_Doc doc : docs)
            if (!doc.visible) invisDocs.add(doc);

        return new List[]{list, types};
    }

    @Ignore
    public void update() {
        List<Object> list = getObjList(false)[0];

        activities = new ArrayList<>();
        docs = new ArrayList<>();
        agenda.types = new ArrayList<>();

        int i = 0;
        for (Object obj : list) {
            if (obj instanceof Activity) {
                agenda.types.add(new ActivityType(ActivityType.Type.activity, activities.size()));
                activities.add(((Activity) obj).setI(i));
            }
            if (obj instanceof _Doc) {
                agenda.types.add(new ActivityType(ActivityType.Type.doc, docs.size()));
                docs.add(((_Doc) obj).setI(i));
            }
            i++;
        }
    }

    @Ignore
    public String toString() {
        return "\n [ Agenda : "+agenda.id+" : "+agenda.title+" : "+getObjList(false)[0]+"\n delete gps: "+ invisGroups +"\n delete docs: "+invisDocs+"\n ]";
    }
}
