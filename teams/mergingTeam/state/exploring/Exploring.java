package newTeam.state.exploring;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.Knowledge;
import newTeam.handler.ComponentsHandler;

public class Exploring extends BaseState {
    
    public Exploring(RobotController rc, Knowledge know, ComponentsHandler ch) {
        super(rc, know, ch);
    }
    
    public Exploring(BaseState state) {
        super(state);
    }

}
