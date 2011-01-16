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
    
    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return oldState;
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
