package newTeam.handler.navigation;

import battlecode.common.*;

public class FlyingExploreNavigator implements Navigator {

    private final MovementController myMC;
    private final RobotController myRC;

    private MapLocation originLocation;

    private Direction tangent;

    private boolean clockwise;

    private int stepCounter;

    public FlyingExploreNavigator(RobotController rc, MovementController mc, MapLocation loc, boolean isClockwise) {
        myMC = mc;
        myRC = rc;

        originLocation = loc;

        clockwise = isClockwise;

        tangent = myRC.getDirection();

        stepCounter = 0;
    }

    @Override
    public int distanceSquaredToGoal(MapLocation fromHere) {
        // TODO Auto-generated method stub
        return myRC.getLocation().distanceSquaredTo(originLocation);
    }

    @Override
    public Direction getMovementDirection() {
        return null;
    }

    @Override
    public MovementAction getNextAction() {
        // TODO Auto-generated method stub
        if(myMC.isActive()) {
            return MovementAction.NONE;
        }

        Direction directionToCenter = myRC.getLocation().directionTo(originLocation);
        Direction directionToMove = null;

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
