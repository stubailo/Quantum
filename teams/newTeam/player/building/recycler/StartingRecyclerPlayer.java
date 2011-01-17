package newTeam.player.building.recycler;

import newTeam.player.BasePlayer;
import newTeam.state.BaseState;
import battlecode.common.ComponentType;
import newTeam.state.building.*;

public class StartingRecyclerPlayer extends RecyclerPlayer {
    
    public StartingRecyclerPlayer(BaseState state) {
        super(state);
    }
    
    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return new ConstructingAntennaOnFirstLight( oldState );
    }
    
    @Override
    public BasePlayer determineSpecificPlayerGivenNewComponent(ComponentType compType,
                                                               BaseState state) {

        return this;
    
    }

}
