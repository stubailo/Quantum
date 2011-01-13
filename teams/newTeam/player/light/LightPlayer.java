package newTeam.player.light;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.player.BasePlayer;

public class LightPlayer extends BasePlayer {
    
    public LightPlayer(BaseState state) {
        
        super(state);
    }
    
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState state) {
        return state;
    }
    
    public BasePlayer determineSpecificPlayer(ComponentType compType, BaseState state) {
        
        switch(compType) {
        
        case CONSTRUCTOR:
            return new LightConstructorPlayer(state);
        case BLASTER:
            return new LightSoldierPlayer(state);
        }
        
        return this;
    }

}
