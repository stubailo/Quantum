package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.starting.StartingMovingToBuild;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;

public class TurningOnRecycler extends BaseState {
    
    private final MapLocation turnOnLocation;
    private final MapLocation factoryLocation;
    private final MapLocation armoryLocation;
    
    private boolean reachedGoal;
    
    public TurningOnRecycler(BaseState oldState, MapLocation givenTurnOnLocation, MapLocation givenFactoryLocation, MapLocation givenArmoryLocation) {
        super(oldState);
        
        turnOnLocation = givenTurnOnLocation;
        factoryLocation = givenFactoryLocation;
        armoryLocation = givenArmoryLocation;
        
        myMH.initializeNavigationToAdjacent(turnOnLocation, NavigatorType.BUG);
    }
    
    @Override
    public BaseState getNextState() {
        if(reachedGoal) {
            try {
                myRC.turnOn(turnOnLocation, RobotLevel.ON_GROUND);
            }
            catch(Exception e) {
                Logger.debug_printExceptionMessage(e);
            }
            BaseState result = new StartingMovingToBuild(this, factoryLocation, armoryLocation, Clock.getRoundNum() + GameConstants.POWER_WAKE_DELAY);
            result.senseAndUpdateKnowledge();
            return result.getNextState();
        }
        return this;
    }
    
    @Override
    public BaseState execute() {
        if(!reachedGoal) {
            reachedGoal = myMH.step();
        }
        return this;
    }

}
