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
//        Logger.debug_printHocho(myK.myLocation.toString());
//        Logger.debug_printHocho(givenToBuildLocation.toString());
        toBuildLocation = givenToBuildLocation;
        if(myK.myLocation.equals(toBuildLocation)) {
//            Logger.debug_printHocho("initializing navigation");
            myMH.initializeNavigationToAdjacent(toBuildLocation, NavigatorType.BUG);
        }
    }

    @Override
    public void senseAndUpdateKnowledge() {
        mySH.senseEdges();
    }

    @Override
    public BaseState getNextState() {

        if( myBH.finishedBuilding() )
        {
            return new DeterminingFactoryAndArmoryLocations(this);
        }

        return this;
    }

    @Override
    public BaseState execute() {

        if(myK.myLocation.isAdjacentTo(toBuildLocation)) {
//            Logger.debug_printHocho("working on building");
            if( !myBH.getCurrentlyBuilding() && myRC.getTeamResources() > Prefab.commRecycler.getTotalCost())
            {
                myBH.buildUnit( Prefab.commRecycler , toBuildLocation);
            }

            myBH.step();
        }
        else {
//            Logger.debug_printHocho("working on moving");
            myMH.step();
        }

        return this;
    }

}
