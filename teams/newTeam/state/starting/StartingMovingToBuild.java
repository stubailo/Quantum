package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.handler.navigation.NavigatorType;
import newTeam.common.util.Logger;

public class StartingMovingToBuild extends BaseState {
    
    private final boolean     needToCheck;
    private       boolean     reachedGoal;
    private       boolean     switchBuildOrder = false;
    
    public StartingMovingToBuild(BaseState oldState, boolean toAdjacent) {
        super(oldState);
        //mineToBeBuiltLocation = startingMineToBeBuiltLocation;
        needToCheck = toAdjacent;
        if(toAdjacent) {
            myMH.initializeNavigationToAdjacent(mySH.startingFirstMineToBeBuiltLocation, NavigatorType.BUG);
        }
        else {
            myMH.initializeNavigationTo(mySH.startingSecondMineToBeBuiltLocation, NavigatorType.BUG);
        }
    }
    
    @Override
    public void senseAndUpdateKnowledge() {
    }
    
    @Override
    public BaseState getNextState() {

        BaseState result = this;
        
        if(reachedGoal) {
            if(needToCheck) {
                MapLocation myLocation = myK.myLocation;
                if(myLocation.distanceSquaredTo(mySH.startingFirstMineToBeBuiltLocation) <= 2 &&
                   myLocation.distanceSquaredTo(mySH.startingSecondMineToBeBuiltLocation) <= 2) {
                    
                    result = new BuildingFirstRecycler(this, switchBuildOrder);
                }
                else {
                    myMH.moveForward();
                    switchBuildOrder = true;
                    reachedGoal = false;
                }
            }
            else {
                result = new BuildingFirstRecycler(this, switchBuildOrder);
            }
            
            result.senseAndUpdateKnowledge();
            result = result.getNextState();
            return result;
        }

        return result;
    }
    
    @Override
    public BaseState execute() {
//        if(myRC.getLocation().distanceSquaredTo(idealBuildingLocation) > 0)
//        {
            reachedGoal = myMH.step();
//        }
        return this;
    }

}
