package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.Knowledge;
import newTeam.handler.ComponentsHandler;

public class StartingScoutSurroundings extends BaseState {
    
    public StartingScoutSurroundings(RobotController rc, Knowledge know, ComponentsHandler ch) {
        super(rc, know, ch);
    }
    
    public StartingScoutSurroundings(BaseState state) {
        super(state);
    }

}
