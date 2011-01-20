package newTeam.state.recycler;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.state.idle.Idling;

public class WaitingForBaseUpgrade extends BaseState {

    Robot constructor = null;

    boolean hasFactory = false;
    boolean hasArmory = false;

    boolean upgradeComplete = false;

    public WaitingForBaseUpgrade(BaseState oldState) {
        super(oldState);

    }

    @Override
    public void senseAndUpdateKnowledge() {

        Message[] messages = myMSH.getMessages();


        for( Message message : messages )
        {

            if( !hasFactory && MessageCoder.getMessageType(message).equals(MessageCoder.FACTORY_BUILT) && MessageCoder.isValid(message) )
            {
                hasFactory = true;
                myK.myRecyclerNode.factoryID = MessageCoder.getIntFromBody(message, 0);
                myK.myRecyclerNode.factoryLocation = MessageCoder.getLocationFromBody(message, 0);

                if( myK.myRecyclerNode.factoryLocation.isAdjacentTo( myK.myLocation ) )
                {
                    myK.myRecyclerNode.factoryID = mySH.senseAtLocation( myK.myRecyclerNode.factoryLocation , RobotLevel.ON_GROUND ).getID();
                }

            } else if( !hasArmory && MessageCoder.getMessageType(message).equals(MessageCoder.ARMORY_BUILT) )// && MessageCoder.isValid(message) )
            {
                hasArmory = true;
                myK.myRecyclerNode.armoryID = MessageCoder.getIntFromBody(message, 0);
                myK.myRecyclerNode.armoryLocation = MessageCoder.getLocationFromBody(message, 0);

                if( myK.myRecyclerNode.armoryLocation.isAdjacentTo( myK.myLocation ) )
                {
                    myK.myRecyclerNode.armoryID = mySH.senseAtLocation( myK.myRecyclerNode.armoryLocation , RobotLevel.ON_GROUND ).getID();
                } 

            }  else if( MessageCoder.getMessageType(message).equals(MessageCoder.UPGRADE_ABORTED) && MessageCoder.isValid(message) )
            {
                upgradeComplete = true;
            }
        }

        if( hasFactory && hasArmory )
        {
            upgradeComplete = true;
        }

    }

    @Override
    public BaseState getNextState() {

        if( upgradeComplete == true )
        {
            int[] ints = { myK.myRecyclerNode.factoryID };
            String strings[] = { null };
            MapLocation locations[] = { null };

            myBCH.addToQueue( MessageCoder.encodeMessage( MessageCoder.MAKE_FACTORY_PING , myK.myRobotID, myK.myLocation, Clock.getRoundNum(), false, strings, ints, locations) );
            //myK.pinging = false;
            return new UpgradedRecycler( this );
        }

        return this;
    }

    @Override
    public BaseState execute() {
        return this;
    }

}
