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

    public RecyclerPlayer(BaseState state) {
        super(state);
        if(myK.myRecyclerNode == null) {
            myK.initializeRecyclerNode();
        }
        myRN = myK.myRecyclerNode;
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

        myK.pinging = true;
    }
    
    @Override
    public void doSpecificPlayerStatelessActions() {
        super.doSpecificPlayerStatelessActions();

        Message[] messages = myCH.myMSH.getMessages();


        for( Message message : messages )
        {
            if( MessageCoder.getMessageType(message).equals(MessageCoder.FACTORY_FOUND_ARMORY) && MessageCoder.isValid(message) )
            {
                
                myK.myRecyclerNode.armoryID = MessageCoder.getIntFromBody(message, 0);
                System.out.println( "found armory! " + myK.myRecyclerNode.armoryID );
            }
        }

        if( myK.pinging && Clock.getRoundNum() > 5 && Clock.getRoundNum() % QuantumConstants.PING_CYCLE_LENGTH == 0 && myCH.myBCH.canBroadcast() )
        {
                myCH.myBCH.addToQueue( myRN.generatePing() );
        }
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
