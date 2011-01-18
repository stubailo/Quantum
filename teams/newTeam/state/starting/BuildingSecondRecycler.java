package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.idle.Idling;
import newTeam.state.building.MovingToBuildFactory;

public class BuildingSecondRecycler extends BaseState {

    private final MapLocation toBuildLocation;
    
    public BuildingSecondRecycler(BaseState oldState, MapLocation givenToBuildLocation) {
        super(oldState);
        toBuildLocation = givenToBuildLocation;
    }

    @Override
    public void senseAndUpdateKnowledge() {
        if(myK.myLocation.equals(toBuildLocation)) {
            myMH.initializeNavigationToAdjacent(toBuildLocation, NavigatorType.BUG);
        }
    }

    @Override
    public BaseState getNextState() {

        if( myBH.finishedBuilding() )
        {
            return new MovingToBuildFactory( this );
        }

        return this;
    }

    @Override
    public BaseState execute() {

        //add a sensor method that checks if the square is occupied
        if(myK.myLocation.isAdjacentTo(toBuildLocation)) {
            if( !myBH.getCurrentlyBuilding() && myRC.getTeamResources() > Prefab.commRecycler.getTotalCost() + 10 )
            {
                myBH.buildUnit( Prefab.commRecycler , toBuildLocation);
            }

            myBH.step();
        }
        else {
            myMH.step();
        }

        return this;
    }

}
