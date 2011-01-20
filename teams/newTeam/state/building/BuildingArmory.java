package newTeam.state.building;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.idle.Idling;

public class BuildingArmory extends BaseState {

    MapLocation toBuildLocation;

    MapLocation moveLocation;

    public BuildingArmory(BaseState oldState) {
        super(oldState);

        System.out.println( "building armory!" );

        toBuildLocation = myRC.getLocation();
        moveLocation = mySH.findEmptyLocationToBuild();

        myMH.initializeNavigationTo(moveLocation, NavigatorType.BUG);
    }

    @Override
    public void senseAndUpdateKnowledge() {
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
                myBH.buildUnit( Prefab.armory , toBuildLocation);
            }

            myBH.step();
        }
        else {
            myMH.step();
        }

        return this;
    }

}
