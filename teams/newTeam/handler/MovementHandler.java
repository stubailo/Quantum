package newTeam.handler;

import newTeam.common.util.Logger;
import newTeam.common.Knowledge;
import newTeam.handler.navigation.NavigatorType;
import newTeam.handler.navigation.BugNavigator;
import newTeam.handler.navigation.Navigator;
import battlecode.common.*;

public class MovementHandler {
    
    private final RobotController       myRC;
    private final Knowledge             myK;
    private       MovementController    myMC;
    private       Navigator             navigator;
    
    
    
    public MovementHandler(RobotController rc, Knowledge know) {
        myRC = rc;
        myK  = know;
    }
    
    
    public void addMC(MovementController mc) {
        myMC = mc;
    }
    
    public void initializeNavigationTo(MapLocation goalLocation, NavigatorType navigatorType) {
        switch(navigatorType) {
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
    
    public void step() {
        try {
            switch(navigator.getNextAction()) {
            
            case MOVE_FORWARD:
                myMC.moveForward();
                break;
            case MOVE_BACKWARD:
                myMC.moveBackward();
                break;
                
            case ROTATE:
                myMC.setDirection(navigator.getMovementDirection());
                break;
            }
        }
        catch(Exception e) {
            Logger.debug_printExceptionMessage(e);
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

}
