package newTeam.player.light;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.player.BasePlayer;

public class LightPlayer extends BasePlayer {
    
    public LightPlayer(BaseState state) {
        
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
        
        switch(compType) {
        
        case CONSTRUCTOR:
            return new LightConstructorPlayer(state);
        case BLASTER:
            return new LightSoldierPlayer(state);
        }
        
        return this;
    }

}
