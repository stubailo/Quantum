package newTeam.player.building.recycler;

import battlecode.common.*;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.player.BasePlayer;
import newTeam.player.building.recycler.RecyclerCommPlayer;
import newTeam.player.building.BuildingPlayer;
import newTeam.state.BaseState;
import newTeam.state.idle.Idling;

public class RecyclerPlayer extends BuildingPlayer {
    
    protected final RecyclerNode myRN;
    boolean hasFactory;

    public RecyclerPlayer(BaseState state) {
        super(state);
        if(myK.myRecyclerNode == null) {
            myK.initializeRecyclerNode();
        }
        myRN = myK.myRecyclerNode;

        hasFactory = false;
    }
    
    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return new Idling( oldState );
    }
    
    @Override
    public void initialize() {
        if(!myCH.mySH.amLowestIDRecycler()) {
            myRC.setIndicatorString(0, "turning off...");
            Logger.debug_printHocho("turning off...");
            myRC.turnOff();
        }
    }
    
    @Override
    public void doSpecificPlayerStatelessActions() {
        super.doSpecificPlayerStatelessActions();

        Message[] messages = myCH.myBCH.receiveMessages();

        for( Message message : messages )
        {

            if( !hasFactory && MessageCoder.getMessageType(message).equals(MessageCoder.FACTORY_PING) && MessageCoder.isValid(message) )
            {
                hasFactory = true;
                myRN.hasFactory = true;
                myRN.factoryLocation = MessageCoder.getBroadcasterLocation(message);
                System.out.println("synced with factory ID: " + MessageCoder.getBroadcasterID(message));
            }
        }

        if( Clock.getRoundNum() % QuantumConstants.PING_CYCLE_LENGTH == 0 && myCH.myBCH.canBroadcast() )
        {
                myCH.myBCH.addToQueue( myRN.generatePing() );
        }

        myCH.myBCH.broadcastFromQueue();
    }
    
    @Override
    public BasePlayer determineSpecificPlayerGivenNewComponent(ComponentType compType,
                                                               BaseState state) {

        switch(compType) {
        case ANTENNA:
        case DISH:
        case NETWORK:
            return new RecyclerCommPlayer(state);
        }
        return this;
    
    }

}
