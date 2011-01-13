package newTeam.player.building.recycler;

import battlecode.common.ComponentType;
import newTeam.player.BasePlayer;
import newTeam.player.building.recycler.RecyclerCommPlayer;
import newTeam.player.building.BuildingPlayer;
import newTeam.state.BaseState;

public class RecyclerPlayer extends BuildingPlayer {
    
    public RecyclerPlayer(BaseState state) {
        super(state);
    }
    
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState state) {
        return state;
    }
    
    public BasePlayer determineSpecificPlayer(ComponentType compType, BaseState state) {

        switch(compType) {
        case ANTENNA:
        case DISH:
        case NETWORK:
            return new RecyclerCommPlayer(state);
        }
        return this;
    
    }

}
