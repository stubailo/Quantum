package newTeam.player.building.recycler;

import newTeam.common.QuantumConstants;
import newTeam.handler.BroadcastHandler;
import newTeam.player.BasePlayer;
import newTeam.state.BaseState;
import battlecode.common.Clock;
import battlecode.common.ComponentType;

public class StartingRecyclerCommPlayer extends StartingRecyclerPlayer {
    
    private final BroadcastHandler myBCH;
    
    public StartingRecyclerCommPlayer(BaseState state) {
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
    }
    
    @Override
    public BasePlayer determineSpecificPlayerGivenNewComponent(ComponentType compType,
                                                               BaseState state) {

        return this;
    
    }

}
