package newTeam.state.building;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.exploring.FlyingSensorExploring;

public class FlyingWaitingForConstructor extends BaseState {

    private MapLocation theMine;

    boolean mineOccupied = false;
    int timeOut = QuantumConstants.BUILD_RECYCLER_TIMEOUT;

    public FlyingWaitingForConstructor(BaseState oldState, MapLocation mine) {
        super(oldState);

        theMine = mine;

        myMH.initializeNavigationToAdjacent(theMine, NavigatorType.BUG);
    }

    @Override
    public void senseAndUpdateKnowledge() {

    }

    @Override
    public BaseState getNextState() {

        Message[] messages = myCH.myMSH.getMessages();

        for( Message message : messages )
        {
            if( MessageCoder.getMessageType(message).equals(MessageCoder.BUILT_RECYCLER) && MessageCoder.getIntFromBody(message, 0) == myK.myRobotID )
            {
                return new FlyingSensorExploring( this );
            }
        }

        if( mySH.senseAtLocation(theMine, RobotLevel.ON_GROUND) != null )
        {
            mineOccupied = true;
        }

        if( mineOccupied )
        {
            timeOut--;
        }

        if( mineOccupied && timeOut == 0 )
        {
            myBCH.addToQueue( MessageCoder.encodeMessage( MessageCoder.ABORT_BUILDING , myK.myRobotID, myK.myLocation, Clock.getRoundNum(), false, null, null, null) );
            
            System.out.println("sending abort command");

            return new FlyingSensorExploring( this );
        }

        return this;
    }

    @Override
    public BaseState execute() {

        
        if( Clock.getRoundNum() % QuantumConstants.PING_CYCLE_LENGTH == 1 )
        {
            int[] ints = {0 };
            String[] strings = {null};
            MapLocation[] locations = { theMine };

            myBCH.addToQueue( MessageCoder.encodeMessage( MessageCoder.FLYER_FOUND_MINE , myRC.getRobot().getID(), myRC.getLocation(), Clock.getRoundNum(), false, strings, ints, locations) );
        }

        myMH.step();

        return this;
    }

}
