package newTeam.state.idle;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.idle.Idling;
import newTeam.state.exploring.*;

public class WaitingForConstructor extends BaseState {

    int turnsWaited = 0;

    public WaitingForConstructor( BaseState oldState ) {
        super( oldState );
    }

    @Override
    public void senseAndUpdateKnowledge() {
        turnsWaited++;
    }

    @Override
    public BaseState getNextState() {

        Message[] messages = myCH.myMSH.getMessages();

        for( Message message : messages )
        {
            if( MessageCoder.getMessageType(message).equals(MessageCoder.JOINING_SQUAD) && MessageCoder.getIntFromBody(message, 0) == myK.myRobotID )
            {
                myK.squadLeaderID = 1;
                return new FlyingSensorExploring( this );
            }
        }

        return this;
    }

    @Override
    public BaseState execute() {
        return this;
    }

}
