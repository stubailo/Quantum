package newTeam.player.building;

import battlecode.common.*;

import newTeam.player.BasePlayer;
import newTeam.player.building.recycler.RecyclerPlayer;
import newTeam.player.building.factory.FactoryPlayer;
import newTeam.state.BaseState;

public class BuildingPlayer extends BasePlayer {
    
    public BuildingPlayer(BaseState state) {
        
        super(state);
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
        

        switch (compType) {
            case RECYCLER:
                return new RecyclerPlayer(state);
            case FACTORY:
                return new FactoryPlayer(state);
        }
        return this;
    }

}
