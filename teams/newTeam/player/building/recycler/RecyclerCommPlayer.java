package newTeam.player.building.recycler;

import newTeam.player.BasePlayer;
import newTeam.state.BaseState;
import battlecode.common.ComponentType;

public class RecyclerCommPlayer extends RecyclerPlayer {
    
    public RecyclerCommPlayer(BaseState state) {
        super(state);
    }
    
    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return oldState;
    }
    
    @Override
    public BasePlayer determineSpecificPlayerGivenNewComponent(ComponentType compType,
                                                               BaseState state) {

        return this;
    
    }

}
