package newTeam.player.building.factory;

import battlecode.common.*;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.player.BasePlayer;
import newTeam.player.building.recycler.RecyclerCommPlayer;
import newTeam.player.building.BuildingPlayer;
import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.state.factory.*;

public class FactoryPlayer extends BuildingPlayer {

    boolean hasRecycler;

    boolean pinging;

    public FactoryPlayer(BaseState state) {
        super(state);

        hasRecycler = false;
        pinging = false;
    }

    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return new BuildingDishOnSelf( oldState );
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
            
            if( MessageCoder.getMessageType(message).equals(MessageCoder.RECYCLER_PING) && MessageCoder.isValid(message) )
            {
                myK.myRecyclerNode = RecyclerNode.getFromPing(message);
                hasRecycler = true;
            } else if ( MessageCoder.getMessageType(message).equals(MessageCoder.MAKE_FACTORY_PING) && MessageCoder.isValid(message) )
            {
                myK.pinging = true;
            }
        }

        if( hasRecycler && Clock.getRoundNum() % QuantumConstants.PING_CYCLE_LENGTH == 1 && myK.myRecyclerNode.armoryID == 0 && myK.myRecyclerNode.armoryLocation!=null && myK.myRecyclerNode.armoryLocation.isAdjacentTo( myK.myLocation ) )
        {
            int newArmoryID = myCH.mySH.senseAtLocation( myK.myRecyclerNode.armoryLocation , RobotLevel.ON_GROUND ).getID();

            int[] ints = { newArmoryID };
            String[] strings = { null };
            MapLocation[] locations = { null };

            myCH.myBCH.addToQueue( MessageCoder.encodeMessage( MessageCoder.FACTORY_FOUND_ARMORY, myK.myRobotID, myK.myLocation, Clock.getRoundNum(), false, strings, ints, locations) );
        }

        if( myK.pinging && Clock.getRoundNum() > 5 && Clock.getRoundNum() % QuantumConstants.PING_CYCLE_LENGTH == 0 && myCH.myBCH.canBroadcast() )
        {
                myCH.myBCH.addToQueue( myK.myRecyclerNode.generatePing() );
        }

    }

    @Override
    public BasePlayer determineSpecificPlayerGivenNewComponent(ComponentType compType,
                                                               BaseState state) {

        return this;

    }

    public Message generateFactoryPing()
    {
        return MessageCoder.encodeMessage(MessageCoder.FACTORY_PING, myRC.getRobot().getID(), myRC.getLocation(), Clock.getRoundNum(), false, null, null, null);
    }

}
