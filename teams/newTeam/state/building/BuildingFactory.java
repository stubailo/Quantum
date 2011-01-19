package newTeam.state.building;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.idle.Idling;

public class BuildingFactory extends BaseState {

    MapLocation toBuildLocation;

    public BuildingFactory(BaseState oldState, MapLocation whereToBuild) {
        super(oldState);

        toBuildLocation = whereToBuild;
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
            return new Idling( this );
        }

        return this;
    }

    @Override
    public BaseState execute() {

        if(myK.myLocation.isAdjacentTo(toBuildLocation)) {
            if( !myBH.getCurrentlyBuilding() && myRC.getTeamResources() > Prefab.factory.getTotalCost() + 10 )
            {
                myBH.buildUnit( Prefab.factory , toBuildLocation);
            }

            myBH.step();
        }
        else {
            myMH.step();
        }

        return this;
    }

}
