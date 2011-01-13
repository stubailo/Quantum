package newTeam.player.light;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.player.BasePlayer;

public class LightSoldierPlayer extends LightPlayer {
    
    public LightSoldierPlayer(BaseState state) {
        
        super(state);
    }
    
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState state) {
        return state;
    }
    
    public BasePlayer determineSpecificPlayer(ComponentType compType, BaseState state) {
        
        return this;
    }

}
