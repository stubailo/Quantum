package newTeam.state.attacking;

import battlecode.common.*;

import newTeam.common.Knowledge;
import newTeam.handler.ComponentsHandler;
import newTeam.state.BaseState;

public class Attacking extends BaseState {
    
    public Attacking(RobotController rc, Knowledge know, ComponentsHandler ch) {
        super(rc, know, ch);
    }
    
    public Attacking(BaseState state) {
        super(state);
    }
    
}
