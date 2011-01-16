package newTeam.player.light;

import battlecode.common.*;

import newTeam.player.BasePlayer;
import newTeam.state.BaseState;
import newTeam.state.starting.StartingLightConstructorScouting;

public class StartingLightConstructorPlayer extends LightConstructorPlayer {
    
    public StartingLightConstructorPlayer(BaseState state) {
        super(state);
    }
    
    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return new StartingLightConstructorScouting(oldState);
    }
    
    @Override
    public BasePlayer determineSpecificPlayerGivenNewComponent(ComponentType compType,
                                                               BaseState state) {
        
        return this;
    }

}
