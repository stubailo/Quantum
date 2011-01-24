package newTeam.state.exploring;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.handler.navigation.NavigatorType;
import newTeam.common.QuantumConstants;
import newTeam.common.util.Logger;

public class Exploring extends BaseState {
    
    private static final int EXPLORE_GOAL_DISTANCE = QuantumConstants.EXPLORE_GOAL_DISTANCE;
    private static final int EXPLORE_TIME          = QuantumConstants.EXPLORE_TIME;
    
    private Direction   exploreDirection;
    private Direction   prevExploreDirection = Direction.NONE;
    private Boolean     reachedGoal = false;
    private MapLocation goalLocation;
    private int         roundLastSetGoal = 0;
    
    public Exploring(BaseState state) {
        super(state);
    }
    
    @Override
    public void senseAndUpdateKnowledge() {
        
        mySH.senseEdges();
        exploreDirection = mySH.getDirectionTowardUndiscoveredBoundaries();
        if(exploreDirection != prevExploreDirection) {
            prevExploreDirection = exploreDirection;
            setNewGoal();
        }
        
        else if(reachedGoal == null || reachedGoal || Clock.getRoundNum() - roundLastSetGoal > EXPLORE_TIME) {
            setNewGoal(reachedGoal != null && Clock.getRoundNum() - roundLastSetGoal <= EXPLORE_TIME);
        }
    }
    
    private void setNewGoal() {
        setNewGoal(true);
    }
    
    private void setNewGoal(boolean succeeded) {
        roundLastSetGoal = Clock.getRoundNum();
        if(exploreDirection != Direction.OMNI && succeeded) {
            goalLocation = myK.myLocation.add(exploreDirection, EXPLORE_GOAL_DISTANCE);
        }
        else {
            switch(Clock.getRoundNum() % 4) {
            case 0:
                goalLocation = myK.myLocation.add(Direction.NORTH, EXPLORE_GOAL_DISTANCE);
                break;
            case 1:
                goalLocation = myK.myLocation.add(Direction.EAST, EXPLORE_GOAL_DISTANCE);
                break;
            case 2:
                goalLocation = myK.myLocation.add(Direction.SOUTH, EXPLORE_GOAL_DISTANCE);
                break;
            case 3:
                goalLocation = myK.myLocation.add(Direction.WEST, EXPLORE_GOAL_DISTANCE);
                break;
            }
        }
        myMH.initializeNavigationTo(goalLocation, NavigatorType.TANGENT_BUG);
    }
    
    @Override
    public BaseState getNextState() {
        
        return this;
    }
    
    @Override
    public BaseState execute() {
        
        if(myMH == null) Logger.debug_printHocho("WTF?!");
        else reachedGoal = myMH.step();
        return this;
    }

}
