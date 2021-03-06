package newTeam.player;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.util.Logger;
import newTeam.common.Knowledge;
import newTeam.handler.ComponentsHandler;
import newTeam.player.light.StartingLightConstructorPlayer;
import newTeam.player.light.LightPlayer;
import newTeam.player.medium.MediumPlayer;
import newTeam.player.flying.FlyingPlayer;
import newTeam.player.building.BuildingPlayer;
import newTeam.player.building.recycler.StartingRecyclerPlayer;

public class BasePlayer {
    
    private   final BaseState         newStateBasedOnNewSpecificPlayer;
    private         boolean           newStateBasedOnNewSpecificPlayerRetrieved = false;
    protected final RobotController   myRC;
    protected final Knowledge         myK;
    protected final ComponentsHandler myCH;
    
    public BasePlayer(BaseState state) {
        myRC = state.myRC;
        myK  = state.myK;
        myCH = state.myCH;
        newStateBasedOnNewSpecificPlayer = determineNewStateBasedOnNewSpecificPlayer(state);
        initialize();
    }
    
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return oldState;
    }
    
    public final BaseState getNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        if(newStateBasedOnNewSpecificPlayerRetrieved) {
            return oldState;
        }
        else {
            newStateBasedOnNewSpecificPlayerRetrieved = true;
            return newStateBasedOnNewSpecificPlayer;
        }
    }
    
    public void initialize() {
        
    }
    
    public void doSpecificPlayerStatelessActions() {
        
    }
    
    public BasePlayer determineSpecificPlayerGivenNewComponent(ComponentType componentType,
                                                               BaseState state) {
        
        return this;
    }
    
    public static final BasePlayer determineInitialSpecificPlayer(RobotController rc,
                                                                  BaseState state) {
        
        switch(rc.getChassis()) {
        
        case LIGHT:
            if(Clock.getRoundNum() == 0) {
                return new StartingLightConstructorPlayer(state);
            }
            else {
                return new LightPlayer(state);
            }
            
        case BUILDING:
            if(Clock.getRoundNum() == 0) {
                return new StartingRecyclerPlayer(state);
            }
            else {
                return new BuildingPlayer(state);
            }

        case MEDIUM:
                return new MediumPlayer(state);

        case FLYING:
                return new FlyingPlayer(state);
        }


        
        Logger.debug_printCustomErrorMessage("forgot to include case in BasePlayer's" +
        		                             "determineInitialSpecificPlayer", "Hocho");
        return new BasePlayer(state);
    }

}
