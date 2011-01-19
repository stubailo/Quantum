package newTeam.state.building;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.idle.Idling;
import newTeam.state.building.GoToNextBuilding;

public class WaitUntilDone extends BaseState {


    public WaitUntilDone( BaseState oldState ) {
        super( oldState );
    }

    @Override
    public void senseAndUpdateKnowledge() {
    }

    @Override
    public BaseState getNextState() {

        Message[] messages = myCH.myMSH.getMessages();

        for( Message message : messages )
        {
            if( MessageCoder.getMessageType(message).equals(MessageCoder.JUST_BUILT_UNIT_DESIGNATION) && MessageCoder.getIntFromBody(message, 0) == myK.myRobotID )
            {
                String designation = MessageCoder.getStringFromBody(message, 0);

                myK.unitType = designation;

                if( designation.equals(Prefab.mediumSoldier.instructionsID) )
                {
                    return new Idling(  this );
                }
            }
        }

        return this;
    }

    @Override
    public BaseState execute() {
        return this;
    }

}
