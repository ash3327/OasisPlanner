package com.aurora.oasisplanner.presentation.dialogs.agendaeditdialog.util;

import com.aurora.oasisplanner.data.core.AppModule;
import com.aurora.oasisplanner.data.model.entities.events._Activity;
import com.aurora.oasisplanner.data.model.entities.events._Event;
import com.aurora.oasisplanner.data.model.pojo.events.Activity;
import com.aurora.oasisplanner.data.model.pojo.events.Agenda;

import java.util.List;

public class AgendaAccessUtil {
    public static final long LId_NULL = -1;
    public static final int NIL_VAL = -1;

    public static Agenda fetchAgenda(long agendaId) {
        Agenda _agenda;
        if (agendaId != LId_NULL)
            _agenda = AppModule.retrieveAgendaUseCases().get(agendaId);
        else
            _agenda = Agenda.empty();
        return _agenda;
    }

    public static _Activity fetchActivity(Agenda agenda, long activityLId) {
        for (_Activity actv : agenda.activities) {
            if (actv.id == activityLId) {
                return actv;
            }
        }
        return null;
    }

    public static _Event fetchEvent(Activity activity, long LId) {
        List<Object> list = activity.getObjList(true)[0];
        int _mPinned = AgendaAccessUtil.fetchEventId(list, LId);
        if (_mPinned != AgendaAccessUtil.NIL_VAL)
            return (_Event)list.get(_mPinned);
        return null;
    }
    /**
     * List[] objlist = activity.getObjList(true);
     * List<Object> list = new ArrayList<Object>(objlist[0]);
     * List<ActivityType.Type> types = new ArrayList<>((List<ActivityType.Type>) objlist[1]);
     * int _mPinned = AgendaAccessUtil.fetchEventId(list, LId);
     * if (mPinned == AgendaAccessUtil.NIL_VAL)
     *      mPinned = _mPinned;
     * */
    public static int fetchEventId(List<Object> list, long LId) {
        int i = 0, _mPinned = NIL_VAL;
        for (Object obj : list) {
            if (obj instanceof _Event && ((_Event)obj).id == LId)
                _mPinned = i;
            i++;
        }
        return _mPinned;
    }

    /** Checks if Agenda is just placeholder. */
    public static boolean agendaIdIsPlaceholder(long agendaId) {
        return agendaIsPlaceholder(fetchAgenda(agendaId));
    }

    public static boolean agendaIsPlaceholder(Agenda agenda) {
        return agenda.agenda.args.containsKey("NIL");
    }
}
