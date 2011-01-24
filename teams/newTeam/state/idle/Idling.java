package newTeam.state.idle;

import battlecode.common.*;

import newTeam.common.Knowledge;
import newTeam.handler.ComponentsHandler;
import newTeam.state.BaseState;
import newTeam.state.exploring.*;

public class Idling extends BaseState {
    
    public Idling(BaseState state) {
        super(state);
    }

    @Override
    public BaseState getNextState() {
        
        return this;
    }
}
