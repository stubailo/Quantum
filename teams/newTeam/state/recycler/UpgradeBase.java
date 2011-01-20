package newTeam.state.recycler;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.state.idle.Idling;

public class UpgradeBase extends BaseState {

    boolean builtConstructor = false;
    Robot constructor = null;

    public UpgradeBase(BaseState oldState) {
        super(oldState);

    }

    @Override
    public void senseAndUpdateKnowledge() {
    }

    @Override
    public BaseState getNextState() {

        if( myBH.finishedBuilding() )
        {
            builtConstructor = true;

            int[] ints = { constructor.getID() };
            String[] strings = { null };
            MapLocation[] locations = { null };
            myBCH.addToQueue( MessageCoder.encodeMessage( MessageCoder.BUILD_FACTORY_COMMAND, myK.myRobotID, myK.myLocation, Clock.getRoundNum(), false, strings, ints, locations));

            return new WaitingForBaseUpgrade( this );
        }

        return this;
    }

    @Override
    public BaseState execute() {



        //add a sensor method that checks if the square is occupied
        if( !builtConstructor && !myBH.getCurrentlyBuilding() && myRC.getTeamResources() > Prefab.lightConstructor.getTotalCost() + 100 )
        {
            MapLocation buildLocation = mySH.findEmptyLocationToBuild();
            if( !(buildLocation==null) )
            {
                myBH.buildUnit( Prefab.lightConstructor , buildLocation);
                constructor = myBH.getBuildTarget();
            }
        }

        myBH.step();

        return this;
    }

}
