package newTeam.state.exploring;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.idle.Idling;

public class LookForSensorToFollow extends BaseState {

    private MapLocation theMine;

    public LookForSensorToFollow(BaseState oldState) {
        super(oldState);

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
            if( MessageCoder.getMessageType(message).equals(MessageCoder.FLYING_SENSOR_PING) && MessageCoder.getIntFromBody(message, 0) == 0 )
            {
                

                myK.squadLeaderID = MessageCoder.getBroadcasterID(message);

                Logger.debug_printSashko( "found sensor!id: " + myK.squadLeaderID );

                int[] ints = { myK.squadLeaderID };
                String[] strings = { null };
                MapLocation[] locations = {null};

                myBCH.addToQueue( MessageCoder.encodeMessage( MessageCoder.JOINING_SQUAD, myK.myRobotID, myK.myLocation, Clock.getRoundNum(), false, strings, ints, locations) );
                return new FlyingFollowingSensor( this );
            }
        }

        return this;
    }

}
