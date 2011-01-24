package newTeam.player.building.recycler;

import battlecode.common.*;

import newTeam.common.QuantumConstants;
import newTeam.player.BasePlayer;
import newTeam.handler.BroadcastHandler;
import newTeam.state.BaseState;
import newTeam.state.recycler.WaitingForBaseUpgrade;

public class StartingRecyclerCommPlayer extends StartingRecyclerPlayer {
    
    private final BroadcastHandler myBCH;
    
    public StartingRecyclerCommPlayer(BaseState state) {
        super(state);
        myBCH = myCH.myBCH;
    }
    
    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        
        return new WaitingForBaseUpgrade(oldState);
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
