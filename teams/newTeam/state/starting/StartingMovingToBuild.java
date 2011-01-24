package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.handler.navigation.NavigatorType;

public class StartingMovingToBuild extends BaseState {
    
    private final MapLocation factoryLocation;
    private final MapLocation armoryLocation;
    private       boolean     reachedGoal;
    private       int         roundUntilWhichToWait;
    
    public StartingMovingToBuild(BaseState oldState, MapLocation givenFactoryLocation, MapLocation givenArmoryLocation, int givenRoundUntilWhichToWait) {
        super(oldState);
        factoryLocation       = givenFactoryLocation;
        armoryLocation        = givenArmoryLocation;
        roundUntilWhichToWait = givenRoundUntilWhichToWait;
        
        myMH.initializeNavigationToAdjacent(factoryLocation, NavigatorType.BUG);
    }
    
    public StartingMovingToBuild(BaseState oldState, MapLocation givenArmoryLocation) {
        super(oldState);
        factoryLocation = null;
        armoryLocation  = givenArmoryLocation;
        
        myMH.initializeNavigationToAdjacent(armoryLocation, NavigatorType.BUG);
    }
    
    @Override
    public BaseState getNextState() {
        if(reachedGoal && Clock.getRoundNum() >= roundUntilWhichToWait) {
            BaseState result = null;
            if(factoryLocation == null) {
                result = new BuildingFirstArmory(this, armoryLocation);
            }
            else {
                result = new BuildingFirstFactory(this, factoryLocation, armoryLocation);
            }
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
