package newTeam.state.fleeing;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.exploring.FlyingSensorExploring;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.exploring.FlyingSensorExploring;
import newTeam.state.exploring.FlyingSensorExploring;

public class FlyingCheckSquadStatus extends BaseState {

    private int squadSize;
    private int responded;
    private int roundCounter = 0;

    public FlyingCheckSquadStatus(BaseState oldState) {

        super(oldState);

        squadSize = myK.squadLeaderID;
        responded = 0;
    }

    @Override
    public void senseAndUpdateKnowledge() {

        roundCounter++;

        Message[] messages = myCH.myMSH.getMessages();

        for( Message message : messages )
        {
            if( MessageCoder.getMessageType(message).equals(MessageCoder.STATUS_RESPONSE) && MessageCoder.getIntFromBody(message, 0) == myK.myRobotID )
            {
                responded++;
            }
        }

    }

    @Override
    public BaseState getNextState() {

        if( responded == squadSize )
        {
            return new FlyingSensorExploring( this );
        }

        if( roundCounter == QuantumConstants.STATUS_TIMEOUT )
        {
            System.out.println( "crisis mode" );
        }

        return this;
    }

    @Override
    public BaseState execute() {

        return this;
    }

}
