package newTeam.handler.navigation;

import battlecode.common.*;

public class CircleNavigator implements Navigator {

    private boolean reachedGoal = false;
    private final MovementController myMC;
    private final RobotController myRC;

    private MapLocation goal;

    private Direction tangent;

    public CircleNavigator(RobotController rc, MovementController mc, MapLocation goalLocation) {
        myMC = mc;
        myRC = rc;

        goal = goalLocation;
    }

    @Override
    public int distanceSquaredToGoal(MapLocation fromHere) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Direction getMovementDirection() {
        return tangent;
    }

    @Override
    public MovementAction getNextAction() {
        // TODO Auto-generated method stub
        if(myMC.isActive()) {
            return MovementAction.NONE;
        }
        else {

            Direction directionToCenter = myRC.getLocation().directionTo(goal);
            Direction directionToMove = null;

            if( directionToCenter.isDiagonal()&&myMC.canMove(directionToCenter.rotateLeft()) )
            {
                directionToMove = directionToCenter.rotateLeft();
            } else if ( !directionToCenter.isDiagonal()&&myMC.canMove(directionToCenter.rotateLeft().rotateLeft()) ) {
                directionToMove = directionToCenter.rotateLeft().rotateLeft();
            } else if ( !directionToCenter.isDiagonal()&&myMC.canMove(directionToCenter.rotateLeft()) ) {
                directionToMove = directionToCenter.rotateLeft();
            } else {
                directionToMove = null;
            }

            if( directionToMove == null)
            {
                return MovementAction.PATH_BLOCKED;
            } else if(myRC.getDirection().equals(directionToMove))
            {
                return MovementAction.MOVE_FORWARD;
            } else {
                tangent = directionToMove;
                return MovementAction.ROTATE;
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
