package newTeam.handler.navigation;

import battlecode.common.*;

import newTeam.common.util.Logger;
import newTeam.common.Knowledge;

public class ZigZagNavigator implements Navigator {
    
    private final MovementController myMC;
    private final Knowledge          myK;
    private       Direction          movementDirection;
    
    public ZigZagNavigator(MovementController mc, Knowledge know) {
        myMC = mc;
        myK  = know;
    }

    @Override
    public int distanceSquaredToGoal(MapLocation fromHere) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Direction getMovementDirection() {
        return movementDirection;
    }

    @Override
    public MovementAction getNextAction() {
        if(myMC.isActive()) return MovementAction.NONE;
        
        Direction testRDir1 = myK.myDirection;
        Direction testRDir2 = myK.myDirection.rotateRight();
        Direction testLDir1 = myK.myDirection;
        Direction testLDir2 = myK.myDirection.rotateLeft();
        
        try {
            if(myMC.canMove(myK.myDirection)) {
                return MovementAction.MOVE_FORWARD;
            } else {
                //if path is blocked, find a direction to bounce in
                Direction moveDir = Direction.NONE;
                while(moveDir == Direction.NONE) {
                    testRDir1 = testRDir2;
                    testRDir2 = testRDir2.rotateRight();
                    moveDir = moveZigZagDir(testRDir1, testRDir2);
                    if(moveDir == Direction.NONE) {
                        testLDir1 = testLDir2;
                        testLDir2 = testLDir2.rotateLeft();
                        moveDir = moveZigZagDir(testLDir1, testLDir2);
                    }
                    
                    //if surrounded, do nothing.
                    if(testRDir1 == testLDir1)
                        moveDir = testRDir1;
                }
                movementDirection = moveDir;
                return MovementAction.ROTATE;
            }
            
        } catch(Exception e) {
            Logger.debug_printExceptionMessage(e);
            return MovementAction.NONE;
        }
    }
    
    private Direction moveZigZagDir(Direction dir1, Direction dir2) {
        if(myMC.canMove(dir1)) {
            if(dir2 == myK.myDirection.opposite())
                return dir1;
            else if(myMC.canMove(dir2)) 
                return dir2;
            else 
                return dir1;
        } else {
            return Direction.NONE;
        }
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
