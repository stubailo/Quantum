package newTeam.player.light;

import battlecode.common.*;

import newTeam.player.BasePlayer;
import newTeam.state.*;
import newTeam.state.starting.NavigationTester;
import newTeam.state.starting.StartingLightConstructorScouting;
import newTeam.state.building.MovingToBuildFactory;

public class StartingLightConstructorPlayer extends LightConstructorPlayer {
    
    public StartingLightConstructorPlayer(BaseState state) {
        super(state);
    }
    
    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        //return new StartingLightConstructorScouting(oldState);
        //return new MovingToBuildFactory(oldState);

        return new NavigationTester(oldState, 
                myK.myLocation.add(Direction.NORTH, 15).add(Direction.EAST, 23));
//        return new Wait( 3, oldState, new StartingLightConstructorScouting(oldState) );

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
        
        return this;
    }

}
