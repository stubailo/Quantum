package newTeam.player.building.recycler;

import battlecode.common.*;
import newTeam.common.util.Logger;
import newTeam.common.MessageCoder;
import newTeam.common.RecyclerNode;
import newTeam.player.BasePlayer;
import newTeam.player.building.recycler.RecyclerCommPlayer;
import newTeam.player.building.BuildingPlayer;
import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.state.recycler.ConstructingAntennaOnSelf;

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
        if(!myCH.mySH.amLowestIDRecycler()) {
            return new ConstructingAntennaOnSelf( oldState );
        }
        else {
            return new Idling(oldState);
        }
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
            if( MessageCoder.getMessageType(message).equals(MessageCoder.FACTORY_FOUND_ARMORY) && MessageCoder.isValid(message) )
            {
                
                myK.myRecyclerNode.armoryID = MessageCoder.getIntFromBody(message, 0);
                Logger.debug_printSashko( "found armory! " + myK.myRecyclerNode.armoryID );
            }
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
