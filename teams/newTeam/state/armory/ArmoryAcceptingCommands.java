package newTeam.state.armory;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.state.idle.Idling;
import newTeam.handler.building.BuildInstructions;

public class ArmoryAcceptingCommands extends BaseState {

    public ArmoryAcceptingCommands(BaseState oldState) {
        super(oldState);

    }

    @Override
    public void senseAndUpdateKnowledge() {

        Message[] messages = myMSH.getMessages();

        for( Message message : messages )
        {
//            if(Clock.getRoundNum() == 501) {
//                Logger.debug_printHocho("got message");
//                Logger.debug_printHocho("currentBuilding? " + myBH.getCurrentlyBuilding() + " messageType? " + MessageCoder.getMessageType(message).equals(MessageCoder.BUILD_UNIT_COMMAND) + " myId? " + (MessageCoder.getIntFromBody(message, 0) == myK.myRobotID) + " isValid? " + MessageCoder.isValid(message));
//            }
            if( !myBH.getCurrentlyBuilding() && MessageCoder.getMessageType(message).equals(MessageCoder.BUILD_UNIT_COMMAND) && MessageCoder.getIntFromBody(message, 0) == myK.myRobotID && MessageCoder.isValid(message) )
            {
                BuildInstructions instructions = Prefab.getInstructionsFromID( MessageCoder.getStringFromBody(message, 0) );
                MapLocation buildLocation = MessageCoder.getLocationFromBody(message, 0);
                RobotLevel height = instructions.getBaseChassis().level;

                Logger.debug_printSashko( "got command to build" );

                if( myBH.chassisBuiltByMe(instructions.getBaseChassis()) )
                {
                    myBH.buildUnit(instructions, buildLocation);
                } else {
                    myBH.waitToBuild(instructions, buildLocation);
                }
            }
        }

    }

    @Override
    public BaseState getNextState() {
        return this;
    }

    @Override
    public BaseState execute() {

        myBH.step();
        return this;
    }

}
