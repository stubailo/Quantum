package newTeam.state.idle;

import battlecode.common.*;

import newTeam.common.Knowledge;
import newTeam.handler.ComponentsHandler;
import newTeam.state.BaseState;
import newTeam.state.exploring.*;

public class Idling extends BaseState {
    
    public Idling(RobotController rc, Knowledge know, ComponentsHandler ch) {
        super(rc, know, ch);
    }
    
    public Idling(BaseState state) {
        super(state);
    }

    @Override
    public BaseState getNextState() {
        
        BaseState newStateFromIdleState = getNewStateFromIdleState();
        newStateFromIdleState.senseAndUpdateKnowledge();
        return newStateFromIdleState.getNextState();
    }
    
    public BaseState getNewStateFromIdleState() {
        return new Exploring(myRC, myK, myCH);
    }
}
