package newTeam.common;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.Team;

public class Knowledge {
    
    /*** RobotController ***/
    private final  RobotController      myRC;
    
    /*** Constants ***/
    public  final   Team                myTeam;
    public  final   Team                enemyTeam;
    public  final   MapLocation         myStartLocation;
    public  final   int                 myRobotID;
    public  final   Robot               myRobot;
    
    /*** Round constants ***/
    public          MapLocation         myLocation;
    public          MapLocation         myPreviousLocation;
    public          Direction           myMovementDirection;
    public          Direction           myDirection;
    public          Direction           myPreviousDirection;
    public          boolean             justMoved;
    public          boolean             justTurned;
    public          double              previousFlux;
    public          double              deltaFlux;
    public          double              totalFlux;
    
    
    
    /**
     * Sole constructor, initializes final variables.
     * 
     * @param    rc    RobotController associated with this RobotPlayer
     */
    public Knowledge (RobotController rc) {
        myRC            = rc;
        myTeam          = myRC.getTeam();
        enemyTeam       = myTeam.opponent();
        myStartLocation = myRC.getLocation();
        myRobot         = myRC.getRobot();
        myRobotID       = myRobot.getID();
        myLocation      = myStartLocation;
        
        previousFlux = 0;
        myLocation = myRC.getLocation();
    }
    
    
    
    /**
     * Called at the beginning of each round, should update all relevant
     * information that is needed irrespective of state.
     */
    public void doStatelessUpdate() {
        // Determine delta flux
        // TODO: ignore delta's associated with building, etc.
        // TODO: recognize delta-delta flux that signifies loss of unit, creation of unit,
        //            creation of mine, etc.
        totalFlux = myRC.getTeamResources();
        deltaFlux = totalFlux - previousFlux;
        previousFlux = totalFlux;
        
        
        MapLocation myNewLocation = myRC.getLocation();
        if(myNewLocation != myLocation) {
            myPreviousLocation = myLocation;
            myLocation = myNewLocation;
            myMovementDirection = myPreviousLocation.directionTo(myNewLocation);
            justMoved = true;
        }
        else justMoved = false;
        
        Direction myNewDirection = myRC.getDirection();
        if(myNewDirection != myDirection) {
            myPreviousDirection = myDirection;
            myDirection = myNewDirection;
            justTurned = true;
        }
        else justTurned = false;
    }

}