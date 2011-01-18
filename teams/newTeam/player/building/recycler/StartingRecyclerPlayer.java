package newTeam.player.building.recycler;

import newTeam.common.util.Logger;
import newTeam.player.BasePlayer;
import newTeam.state.BaseState;
import battlecode.common.ComponentType;
import newTeam.state.starting.ConstructingAntennaOnFirstLight;

public class StartingRecyclerPlayer extends RecyclerPlayer {
    
    public StartingRecyclerPlayer(BaseState state) {
        super(state);
    }
    
    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        
        return new ConstructingAntennaOnFirstLight( oldState );
    }
    
    @Override
    public void initialize() {
        super.initialize();
    }
    
    @Override
    public void doSpecificPlayerStatelessActions() {
        super.doSpecificPlayerStatelessActions();
    }
    
    @Override
    public BasePlayer determineSpecificPlayerGivenNewComponent(ComponentType compType,
                                                               BaseState state) {
        
        switch(compType) {
        
        case ANTENNA:
        case DISH:
        case NETWORK:
            return new StartingRecyclerCommPlayer(state);
        }

        return this;
    
    }

}
