package newTeam.state.building;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.idle.Idling;

public class BuildingFactory extends BaseState {

    MapLocation toBuildLocation;

    MapLocation moveLocation;

    public BuildingFactory(BaseState oldState) {
        super(oldState);

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
            int[] ints = { 0 };
            String[] strings = { null };
            MapLocation[] locations = { toBuildLocation };

            myBCH.addToQueue( MessageCoder.encodeMessage( MessageCoder.FACTORY_BUILT, myK.myRobotID, myK.myLocation, Clock.getRoundNum(), false, strings, ints, locations) );

            return new MovingToBuildArmory( this, toBuildLocation );
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
