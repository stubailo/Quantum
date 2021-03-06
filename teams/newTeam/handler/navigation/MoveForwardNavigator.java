package newTeam.handler.navigation;

import battlecode.common.*;

public class MoveForwardNavigator implements Navigator {
    
    private boolean reachedGoal = false;
    private final MovementController myMC;
    private final RobotController myRC;
    
    public MoveForwardNavigator(RobotController rc, MovementController mc) {
        myMC = mc;
        myRC = rc;
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
        if(myMC.isActive()) {
            return MovementAction.NONE;
        }
        else {
            if(myMC.canMove(myRC.getDirection())) {
                reachedGoal = true;
                return MovementAction.MOVE_FORWARD;
            }
            else {
                return MovementAction.PATH_BLOCKED;
            }
        }
    }

    @Override
    public void pauseNavigation() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean reachedGoal() {
        // TODO Auto-generated method stub
        return reachedGoal;
    }

    @Override
    public void setGoingToAdjacent(boolean b) {
        // TODO Auto-generated method stub

    }

}
