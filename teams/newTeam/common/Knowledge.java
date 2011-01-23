package newTeam.common;

import newTeam.common.util.Logger;
import battlecode.common.*;

public class Knowledge {
    
    /*** RobotController ***/
    private final   RobotController     myRC;

    /*** Parent Recycler ***/
    public          RecyclerNode        myRecyclerNode;
    public          boolean             pinging;

    /*** Squad Info ***/
    public          int                 squadLeaderID;
    
    /*** Constants ***/
    public  final   Team                myTeam;
    public  final   Team                enemyTeam;
    public  final   MapLocation         myStartLocation;
    public  final   int                 myRobotID;
    public  final   Robot               myRobot;
    public  final   int                 startRound;
    
    private static final int DELTA_FLUX_MEMORY_LENGTH = 10;
    private static final int BIG_INT = QuantumConstants.BIG_INT;
    
    
    /*** Round constants ***/
    public          MapLocation         myLocation;
    public          MapLocation         myPreviousLocation;
    public          Direction           myMovementDirection;
    public          Direction           myDirection;
    public          Direction           myPreviousDirection;
    public          boolean             justMoved;
    public          boolean             justTurned;
    private         double              previousFlux;
    private         double              deltaFlux;
    public          double              totalFlux;
    private         double              deltaFluxes[] = new double[DELTA_FLUX_MEMORY_LENGTH];
    public          double              averageDeltaFlux = BIG_INT;
    private         double              alternativeDeltaFluxes[] = new double[DELTA_FLUX_MEMORY_LENGTH];
    private         int                 majorDeltaFluxShifting = 0;
    private         double              totalDeltaFlux = 0;
    
    /*** SpecificPlayer-specific ***/
    
    /**  StartingLightConstructorPlayer **/
    // This MapLocation is set by the SensorHandler during round 0, 1, or 2,
    //   depending on the orientation of the 
    public          MapLocation         startingTurnedOnRecyclerLocation;
    
    
    
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
        startRound      = Clock.getRoundNum();
        myRobot         = myRC.getRobot();
        myRobotID       = myRobot.getID();
        myLocation      = myStartLocation;
        
        previousFlux    = myRC.getTeamResources();
        myLocation      = myRC.getLocation();

        pinging = false;
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
//        Logger.debug_printHocho("previousFlux: " + previousFlux + ", currentFlux: " + totalFlux);
        deltaFlux = totalFlux - previousFlux;
        previousFlux = totalFlux;
//        Logger.debug_printHocho("deltaFlux: " + deltaFlux);
        
        if(Clock.getRoundNum() - startRound >= DELTA_FLUX_MEMORY_LENGTH + GameConstants.EQUIP_WAKE_DELAY) {
            int mod = Clock.getRoundNum() % DELTA_FLUX_MEMORY_LENGTH;
            if(averageDeltaFlux - deltaFlux < 3) {
                majorDeltaFluxShifting = 0;
                totalDeltaFlux -= deltaFluxes[mod];
                deltaFluxes[mod] = deltaFlux;
                totalDeltaFlux += deltaFlux;
                averageDeltaFlux = totalDeltaFlux / DELTA_FLUX_MEMORY_LENGTH;
            }
            else {
                deltaFluxes[mod] = averageDeltaFlux;
                alternativeDeltaFluxes[mod] = deltaFlux;
                if(++majorDeltaFluxShifting == DELTA_FLUX_MEMORY_LENGTH) {
                    Logger.debug_printHocho("major delta flux shift!");
                    deltaFluxes = alternativeDeltaFluxes;
                    totalDeltaFlux = 0;
                    for(double dFlux : deltaFluxes) {
                        totalDeltaFlux += dFlux;
                    }
                    averageDeltaFlux = totalDeltaFlux / DELTA_FLUX_MEMORY_LENGTH;
                }
            }
        }
        else if(Clock.getRoundNum() - startRound >= GameConstants.EQUIP_WAKE_DELAY) {
            deltaFluxes[Clock.getRoundNum() % DELTA_FLUX_MEMORY_LENGTH] = deltaFlux;
            totalDeltaFlux += deltaFlux;
            averageDeltaFlux = totalDeltaFlux / DELTA_FLUX_MEMORY_LENGTH;
        }
//        Logger.debug_printHocho("averageDeltaFlux: " + averageDeltaFlux);
        
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
    
    public void initializeRecyclerNode() {
        myRecyclerNode = new RecyclerNode(myRobotID, myLocation);
    }

}
