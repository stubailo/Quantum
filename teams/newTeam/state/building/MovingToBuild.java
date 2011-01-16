package newTeam.state.building;

import battlecode.common.*;

import newTeam.state.BaseState;

public class MovingToBuild extends BaseState {
    
    private final MapLocation givenLocationAtWhichToBuild;
    
    public MovingToBuild(BaseState oldState, MapLocation locationAtWhichToBuild) {
        super(oldState);
        givenLocationAtWhichToBuild = locationAtWhichToBuild;
    }
    
    
    

}
