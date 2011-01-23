package newTeam.player.flying;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.player.BasePlayer;
import newTeam.common.*;
import newTeam.state.exploring.LookForSensorToFollow;

public class FlyingConstructorPlayer extends FlyingPlayer {

    public FlyingConstructorPlayer(BaseState state) {

        super(state);
    }

    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return new LookForSensorToFollow( oldState );
    }

    @Override
    public void initialize() {

    }

    @Override
    public void doSpecificPlayerStatelessActions() {
        super.doSpecificPlayerStatelessActions();

        Message[] messages = myCH.myMSH.getMessages();

        for( Message message : messages )
        {
            if( MessageCoder.getMessageType(message).equals(MessageCoder.FLYING_STATUS_REQUEST) && MessageCoder.getBroadcasterID(message) == myK.squadLeaderID )
            {
                int[] ints = { myK.squadLeaderID };
                String[] strings = { null };
                MapLocation[] locations = {null};

                myCH.myBCH.addToQueue( MessageCoder.encodeMessage( MessageCoder.STATUS_RESPONSE , myK.myRobotID, myK.myLocation, Clock.getRoundNum(), false, strings, ints, locations) );
            }
        }

    }

}
