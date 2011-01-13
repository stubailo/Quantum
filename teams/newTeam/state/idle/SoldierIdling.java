package newTeam.state.idle;

import battlecode.common.*;

import newTeam.common.Knowledge;
import newTeam.handler.ComponentsHandler;
import newTeam.state.BaseState;

public class SoldierIdling extends Idling {
    
    public SoldierIdling(RobotController rc, Knowledge know, ComponentsHandler ch) {
        super(rc, know, ch);
    }
    
    public SoldierIdling(BaseState state) {
        super(state);
    }

}
