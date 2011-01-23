package newTeam.handler.navigation;

import battlecode.common.*;

public class MoveTowardNavigator implements Navigator {
    
    public MoveTowardNavigator(RobotController rc, MovementController mc, MapLocation goal) {
        
    }

    @Override
    public int distanceSquaredToGoal(MapLocation fromHere) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Direction getMovementDirection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public MovementAction getNextAction() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void pauseNavigation() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean reachedGoal() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setGoingToAdjacent(boolean b) {
        // TODO Auto-generated method stub

    }

}
