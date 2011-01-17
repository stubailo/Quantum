package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.handler.navigation.NavigatorType;
import newTeam.common.util.Logger;

public class StartingMovingToBuild extends BaseState {
    
    private final MapLocation mineToBeBuiltLocation;
    
    public StartingMovingToBuild(BaseState oldState, MapLocation startingMineToBeBuiltLocation) {
        super(oldState);
        mineToBeBuiltLocation = startingMineToBeBuiltLocation;
        myMH.initializeNavigationToAdjacent(mineToBeBuiltLocation, NavigatorType.BUG);
    }
    
    @Override
    public void senseAndUpdateKnowledge() {
        
    }
    
    @Override
    public BaseState getNextState() {

        Logger.debug_printSashko("distance: " + myRC.getLocation().distanceSquaredTo(idealBuildingLocation));

        if(myRC.getLocation().distanceSquaredTo(idealBuildingLocation) == 0)
        {
            Logger.debug_printSashko("switching to BFR: " + myRC.getLocation().distanceSquaredTo(idealBuildingLocation));
            return new BuildingFirstRecycler( this );
        }

        return this;
    }
    
    @Override
    public BaseState execute() {
        if(myRC.getLocation().distanceSquaredTo(idealBuildingLocation) > 0)
        {
            myMH.step();
        }
        return this;
    }

}
