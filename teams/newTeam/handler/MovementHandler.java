package newTeam.handler;

import newTeam.common.util.Logger;
import newTeam.common.Knowledge;
import newTeam.handler.navigation.*;
import battlecode.common.*;

public class MovementHandler {
    
    private final RobotController       myRC;
    private final Knowledge             myK;
    private       MovementController    myMC;
    private       SensorHandler         mySH;
    private       Navigator             navigator;
    private       NavigatorType         navigatorType;
    
    private boolean pathBlocked = false;
    
    public MovementHandler(RobotController rc, Knowledge know, SensorHandler sh) {
        myRC = rc;
        myK  = know;
        mySH = sh;
    }
    
    
    public void addMC(MovementController mc) {
        myMC = mc;
    }

    public boolean canMove( Direction dir )
    {
        if( myMC!=null )
        {
            return myMC.canMove( dir );
        } else {
            return false;
        }
    }
    
    public void initializeNavigationTo(MapLocation goalLocation, NavigatorType givenNavigatorType) {
        navigatorType = givenNavigatorType;
        
        switch(givenNavigatorType) {
        case BUG:
            navigator = new BugNavigator(myRC, myK, myMC, goalLocation);
            break;
            
        case TANGENT_BUG:
            navigator = new TangentBug(myRC, myK, myMC, mySH, goalLocation);
            break;
            //TODO: add TANGENT_BUG
        }
    }
    
    public void initializeNavigationToAdjacent(MapLocation goalLocation,
                                               NavigatorType navigatorType) {
        switch(navigatorType) {
        case BUG:
            navigator = new BugNavigator(myRC, myK, myMC, goalLocation, true);
            break;
        case TANGENT_BUG:
            Logger.debug_printCustomErrorMessage("TangentBug can't navigate to adjacent", "Hocho");
        }
    }
    
    public boolean step() {
        try {
            MovementAction nextAction = navigator.getNextAction();
//            Logger.debug_printHocho(nextAction.toString());
            myRC.setIndicatorString(2, nextAction.toString());

            switch(nextAction) {
//            switch(navigator.getNextAction()) {
            
            case MOVE_FORWARD:
                myMC.moveForward();
                
                if(navigatorType == NavigatorType.MOVE_FORWARD) {
                    return true;
                }
                return false;
                
            case MOVE_BACKWARD:
                myMC.moveBackward();
                
                if(navigatorType == NavigatorType.MOVE_BACKWARD) {
                    return true;
                }
                return false;
                
            case ROTATE:
                myMC.setDirection(navigator.getMovementDirection());
                return false;
                
            case AT_GOAL:
                return true;
                
            case PATH_BLOCKED:
                pathBlocked = true;
                switch(navigatorType) {
                
                }
                
            case GOAL_INACCESSIBLE:
                //TODO
            }
            
            return false;
        }
        catch(Exception e) {
            Logger.debug_printExceptionMessage(e);
            return false;
        }
    }
    
    public boolean reachedGoal() {
        return navigator.reachedGoal();
    }

    public boolean getPathBlocked()
    {
        if( pathBlocked )
        {
            pathBlocked = false;
            return true;
        } else {
            return false;
        }
    }
    
    public void setDirection(Direction direction) {
        try {
            if(!myMC.isActive()) {
                myMC.setDirection(direction);
            }
        }
        catch(Exception e) {
            Logger.debug_printExceptionMessage(e);
        }
    }
    
    public void moveForward() {
        navigatorType = NavigatorType.MOVE_FORWARD;
        navigator = new MoveForwardNavigator(myRC, myMC);
    }
    
    public void moveBackward() {
        navigatorType = NavigatorType.MOVE_BACKWARD;
        navigator = new MoveBackwardNavigator(myRC, myMC);
    }
    
    public void moveToward(MapLocation location) {
        navigatorType = NavigatorType.MOVE_TOWARD;
        navigator = new MoveTowardNavigator(myRC, myMC, location);
        step();
    }
    
    public void zigZag() {
        navigatorType = NavigatorType.ZIG_ZAG;
        navigator = new ZigZagNavigator(myMC, myK);
    }

    public void circle( MapLocation location, boolean clockwise )
    {
        navigatorType = NavigatorType.CIRCLE;
        navigator = new CircleNavigator(myRC, myMC, location, clockwise);

    }

}
