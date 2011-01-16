package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.handler.navigation.NavigatorType;

public class StartingMovingToBuild extends BaseState {
    
    private final MapLocation idealBuildingLocation;
    
    public StartingMovingToBuild(BaseState oldState, MapLocation startingIdealBuildingLocation) {
        super(oldState);
        idealBuildingLocation = startingIdealBuildingLocation;
        myMH.initializeNavigationTo(idealBuildingLocation, NavigatorType.BUG);
    }
    
    @Override
    public void senseAndUpdateKnowledge() {
        
    }
    
    @Override
    public BaseState getNextState() {
        return this;
    }
    
    @Override
    public BaseState execute() {
        myMH.step();
        return this;
    }

}
