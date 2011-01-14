package newTeam.player;

import battlecode.common.*;

import newTeam.state.BaseState;

public class BasePlayer {
    
    private final BaseState newStateBasedOnNewSpecificPlayer;
    private       boolean   newStateBasedOnNewSpecificPlayerRetrieved = false;
    
    public BasePlayer(BaseState state) {
        newStateBasedOnNewSpecificPlayer = determineNewStateBasedOnNewSpecificPlayer(state);
    }
    
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState state) {
        return state;
    }
    
    public final BaseState getNewStateBasedOnNewSpecificPlayer(BaseState state) {
        if(newStateBasedOnNewSpecificPlayerRetrieved) {
            return state;
        }
        else {
            newStateBasedOnNewSpecificPlayerRetrieved = true;
            return newStateBasedOnNewSpecificPlayer;
        }
    }
    
    
    public BasePlayer determineSpecificPlayerGivenNewComponent(ComponentType componentType, BaseState state) {
        return this;
    }

}
