package newTeam.state.exploring;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.state.idle.Idling;
import newTeam.state.building.FlyingWaitingForConstructor;

public class FlyingSensorExploring extends BaseState {

    public boolean waitingForConstructor;

    public FlyingSensorExploring(BaseState oldState) {
        super(oldState);

        myMH.zigZag();
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

        MapLocation nearestMine = mySH.getNearestEmptyMine();

        if( nearestMine!=null )
        {
            int[] ints = {0 };
            String[] strings = {null};
            MapLocation[] locations = { nearestMine };

            myBCH.addToQueue( MessageCoder.encodeMessage( MessageCoder.FLYER_FOUND_MINE , myRC.getRobot().getID(), myRC.getLocation(), Clock.getRoundNum(), false, strings, ints, locations) );

            return new FlyingWaitingForConstructor( this, nearestMine );
        }

        if( mySH.areEnemiesNearby() )
        {
            return new FlyingSensorFleeing( this );
        }

        myMH.step();

        return this;
    }

}
