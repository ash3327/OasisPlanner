package com.aurora.oasisplanner.data.util;

public class Switch {
    private boolean state = false;
    private StateObj action;
    public Switch(boolean initState) {
        this.state = initState;
    }
    public boolean getState(){
        return state;
    }
    public boolean setState(boolean state) {
        boolean changed = this.state != state;
        this.state = state;
        if (changed && action != null)
            action.run(state);
        return changed;
    }
    public boolean toggleState() {
        this.state = !state;
        return true;
    }
    public void observe(StateObj action, boolean act) {
        this.action = action;
        if (act) action.run(getState());
    }

    public interface StateObj{
        void run(boolean newState);
    }
}
