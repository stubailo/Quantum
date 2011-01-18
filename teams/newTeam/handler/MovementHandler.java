package newTeam.handler;

import newTeam.common.util.Logger;
import newTeam.common.Knowledge;
import newTeam.handler.navigation.NavigatorType;
import newTeam.handler.navigation.BugNavigator;
import newTeam.handler.navigation.Navigator;
import newTeam.handler.navigation.MoveForwardNavigator;
import newTeam.handler.navigation.MoveBackwardNavigator;
import battlecode.common.*;

public class MovementHandler {
    
    private final RobotController       myRC;
    private final Knowledge             myK;
    private       MovementController    myMC;
    private       Navigator             navigator;
    private       NavigatorType         navigatorType;
    
    
    
    public MovementHandler(RobotController rc, Knowledge know) {
        myRC = rc;
        myK  = know;
    }
    
    
    public void addMC(MovementController mc) {
        myMC = mc;
    }
    
    public void initializeNavigationTo(MapLocation goalLocation, NavigatorType givenNavigatorType) {
        navigatorType = givenNavigatorType;
        
        switch(givenNavigatorType) {
        case BUG:
            navigator = new BugNavigator(myRC, myK, myMC, goalLocation);
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
            switch(navigator.getNextAction()) {
            
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
                //TODO
                switch(navigatorType) {
                
                }
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

}
