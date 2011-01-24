package newTeam.player.building.recycler;

import newTeam.common.util.Logger;
import newTeam.player.BasePlayer;
import newTeam.state.BaseState;
import battlecode.common.ComponentType;
import newTeam.state.starting.ConstructingAntennaOnFirstLight;
import newTeam.state.recycler.ConstructingAntennaOnSelf;

public class StartingRecyclerPlayer extends RecyclerPlayer {
    
    public StartingRecyclerPlayer(BaseState state) {
        super(state);
    }
    
    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        
        if(!myCH.mySH.amLowestIDRecycler()) {
            return new ConstructingAntennaOnSelf(oldState);
        }
        else {
            return new ConstructingAntennaOnFirstLight( oldState );
        }
    }
    
    @Override
    public void initialize() {
        // If code after this point is read then the constructor has turned this back on
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
