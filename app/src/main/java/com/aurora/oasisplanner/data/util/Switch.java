package com.aurora.oasisplanner.data.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Switch {
    private boolean state = false;
    private List<StateObj> actions;
    private HashMap<Long, StateObj> idxActions;
    public Switch(boolean initState) {
        this.state = initState;
        this.actions = new ArrayList<>();
        this.idxActions = new HashMap<>();
    }
    public boolean getState(){
        return state;
    }
    public boolean setState(boolean state) {
        return setState(state, false);
    }
    public boolean setState(boolean state, boolean forceRefresh) {
        boolean changed = this.state != state;
        this.state = state;
        if ((changed || forceRefresh) && actions != null) {
            for (StateObj action : actions)
                action.run(state);
            for (StateObj action : idxActions.values())
                action.run(state);
        }
        return changed;
    }
    public boolean toggleState() {
        setState(!state, true);
        return state;
    }
    public void observe(StateObj action, boolean act) {
        if (actions == null) actions = new ArrayList<>();
        this.actions.add(action);
        if (act) action.run(getState());
    }
    public void observe(StateObj action, boolean act, long id) {
        if (idxActions == null) idxActions = new HashMap<>();
        this.idxActions.put(id, action);
        if (act) action.run(getState());
    }

    public interface StateObj{
        void run(boolean newState);
    }
}
