package newTeam.state.exploring;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.building.FlyingConstructorBuildRecycler;

public class FlyingFollowingSensor extends BaseState {

    private MapLocation lastKnownLocation;


    public FlyingFollowingSensor(BaseState oldState) {
        super(oldState);

        lastKnownLocation = null;

        myMH.initializeNavigationTo( myK.myLocation, NavigatorType.BUG);
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

        Message[] messages = myCH.myMSH.getMessages();

        for( Message message : messages )
        {
            if( MessageCoder.getMessageType(message).equals(MessageCoder.FLYING_SENSOR_PING) && MessageCoder.getBroadcasterID(message) == myK.squadLeaderID )
            {
                lastKnownLocation = MessageCoder.getBroadcasterLocation(message);
                myMH.initializeNavigationTo( lastKnownLocation , NavigatorType.BUG);
            } else if ( MessageCoder.getMessageType(message).equals(MessageCoder.FLYER_FOUND_MINE) ) {
                return new FlyingConstructorBuildRecycler( this, MessageCoder.getLocationFromBody(message, 0) );
            }
        }

        myMH.step();

        return this;
    }

}
