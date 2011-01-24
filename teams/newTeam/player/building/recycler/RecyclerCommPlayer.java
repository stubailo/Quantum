package newTeam.player.building.recycler;

import battlecode.common.*;

import newTeam.common.QuantumConstants;
import newTeam.handler.BroadcastHandler;
import newTeam.player.BasePlayer;
import newTeam.state.BaseState;

public class RecyclerCommPlayer extends RecyclerPlayer {
    
    private final BroadcastHandler myBCH;
    
    public RecyclerCommPlayer(BaseState state) {
        super(state);
        myBCH = myCH.myBCH;
    }
    
    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return oldState;
    }
    
    @Override
    public void initialize() {
        
    }
    
    @Override
    public void doSpecificPlayerStatelessActions() {
        super.doSpecificPlayerStatelessActions();
        
        if( Clock.getRoundNum() % QuantumConstants.PING_CYCLE_LENGTH == 0)
        {
                myBCH.addToQueue( myRN.generatePing() );
        }
    }
    
    @Override
    public BasePlayer determineSpecificPlayerGivenNewComponent(ComponentType compType,
                                                               BaseState state) {

        return this;
    
    }

}
