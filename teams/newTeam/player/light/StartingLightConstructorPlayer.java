package newTeam.player.light;

import battlecode.common.*;

import newTeam.player.BasePlayer;
import newTeam.state.BaseState;
import newTeam.state.starting.StartingScoutSurroundings;

public class StartingLightConstructorPlayer extends LightConstructorPlayer {
    
    public StartingLightConstructorPlayer(BaseState state) {
        super(state);
    }
    
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState state) {
        return new StartingScoutSurroundings(state);
    }
    
    public BasePlayer determineSpecificPlayer(ComponentType compType, BaseState state) {
        
        return this;
    }

}
