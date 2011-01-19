package newTeam.state.building;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.idle.Idling;
import newTeam.state.building.*;

public class ConstructorWaitingForCommand extends BaseState {

    int turnsWaited = 0;

    public ConstructorWaitingForCommand( BaseState oldState ) {
        super( oldState );
    }

    @Override
    public void senseAndUpdateKnowledge() {
        turnsWaited++;
    }

    @Override
    public BaseState getNextState() {

        if( turnsWaited > QuantumConstants.TIME_WAIT_FOR_COMMAND )
        {
            return new Idling( this );
        }

        Message[] messages = myCH.myMSH.getMessages();

        for( Message message : messages )
        {
            if( MessageCoder.getMessageType(message).equals(MessageCoder.BUILD_FACTORY_COMMAND) && MessageCoder.getIntFromBody(message, 0) == myK.myRobotID )
            {
                return new MovingToBuildFactory( this );
            }
        }

        return this;
    }

    @Override
    public BaseState execute() {
        return this;
    }

}
