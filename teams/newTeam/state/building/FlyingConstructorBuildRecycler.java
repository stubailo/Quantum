package newTeam.state.building;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.exploring.*;

public class FlyingConstructorBuildRecycler extends BaseState {

    private MapLocation theMine;
    private boolean builtRecycler = false;

    public FlyingConstructorBuildRecycler(BaseState oldState, MapLocation mine) {
        super(oldState);

        theMine = mine;

        myMH.initializeNavigationToAdjacent(theMine, NavigatorType.BUG);
    }

    @Override
    public void senseAndUpdateKnowledge() {

    }

    @Override
    public BaseState getNextState() {

        if( myBH.finishedBuilding() )
        {
            int[] ints = { myK.squadLeaderID };
            String[] strings = { null };
            MapLocation[] locations = {null};

            myBCH.addToQueue( MessageCoder.encodeMessage( MessageCoder.BUILT_RECYCLER , myK.myRobotID, myK.myLocation, Clock.getRoundNum(), false, null, null, null) );
            return new FlyingFollowingSensor( this );
        }

        return this;
    }

    @Override
    public BaseState execute() {

        Message[] messages = myCH.myMSH.getMessages();

        for( Message message : messages )
        {
            if( MessageCoder.getMessageType(message).equals(MessageCoder.ABORT_BUILDING) && MessageCoder.getBroadcasterID(message) == myK.squadLeaderID )
            {
                myBH.abortBuilding();
                return new FlyingFollowingSensor( this );
            }
        }

        if( !builtRecycler && myMH.reachedGoal() )
        {
            myBH.buildUnit( Prefab.commRecycler , theMine);
            builtRecycler = true;
        }

        myMH.step();

        myBH.step();

        return this;
    }

}
