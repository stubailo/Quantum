package newTeam.player.light;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.player.BasePlayer;

public class LightConstructorPlayer extends LightPlayer {
    
    public LightConstructorPlayer(BaseState state) {
        
        super(state);
    }
    
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState state) {
        return state;
    }
    
    public BasePlayer determineSpecificPlayer(ComponentType compType, BaseState state) {
        
        return this;
    }

}
