package newTeam.player.building.recycler;

import newTeam.player.BasePlayer;
import newTeam.state.BaseState;
import battlecode.common.ComponentType;

public class RecyclerCommPlayer extends RecyclerPlayer {
    
    public RecyclerCommPlayer(BaseState state) {
        super(state);
    }
    
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState state) {
        return state;
    }
    
    public BasePlayer determineSpecificPlayer(ComponentType compType, BaseState state) {

        return this;
    
    }

}
