package newTeam.handler.navigation;

import battlecode.common.*;

public class CircleNavigator implements Navigator {

    private boolean reachedGoal = false;
    private final MovementController myMC;
    private final RobotController myRC;

    private MapLocation goal;

    private Direction tangent;

    boolean clockwise = true;

    public CircleNavigator(RobotController rc, MovementController mc, MapLocation goalLocation, boolean clock) {
        myMC = mc;
        myRC = rc;

        clockwise = clock;

        goal = goalLocation;
    }

    @Override
    public int distanceSquaredToGoal(MapLocation fromHere) {
        return 0;
    }

    @Override
    public Direction getMovementDirection() {
        return tangent;
    }

    @Override
    public MovementAction getNextAction() {
        if(myMC.isActive()) {
            return MovementAction.NONE;
        }
        else if( clockwise ) {

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
        } else {

            Direction directionToCenter = myRC.getLocation().directionTo(goal);
            Direction directionToMove = null;

            if( directionToCenter.isDiagonal()&&myMC.canMove(directionToCenter.rotateRight()) )
            {
                directionToMove = directionToCenter.rotateRight();
            } else if ( !directionToCenter.isDiagonal()&&myMC.canMove(directionToCenter.rotateRight().rotateRight()) ) {
                directionToMove = directionToCenter.rotateRight().rotateRight();
            } else if ( !directionToCenter.isDiagonal()&&myMC.canMove(directionToCenter.rotateRight()) ) {
                directionToMove = directionToCenter.rotateRight();
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

    }

    @Override
    public boolean reachedGoal() {
        return reachedGoal;
    }

    @Override
    public void setGoingToAdjacent(boolean b) {

    }

}
