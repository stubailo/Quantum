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

    /*** Enemy Info ***/
    public int                          enemyTimeStamp;
    public MapLocation                  lastKnownEnemyLocation;
    
    /*** Constants ***/
    public  final   Team                myTeam;
    public  final   Team                enemyTeam;
    public  final   MapLocation         myStartLocation;
    public  final   int                 myRobotID;
    public  final   Robot               myRobot;
    public          int                 startRound;
    
    private static final int DELTA_FLUX_MEMORY_LENGTH = 20;
    private static final int RATIONAL_DELTA_FLUX_LOWER_BOUND = -20;
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
    public          double              averageDeltaFlux = 0;
    private         double              alternativeDeltaFluxes[] = new double[DELTA_FLUX_MEMORY_LENGTH];
    private         int                 majorDeltaFluxShifting = 0;
    private         double              totalDeltaFlux = 0;
    
    
    /*** TURN OFF? ***/
    public          boolean             turnOff = false;
    public          boolean             turnedOn = false;
    
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
        
        turnedOn = false;
        
        if(turnOff) {
            myRC.setIndicatorString(0, "turning off...");
            Logger.debug_printHocho("turning off...");
            myRC.turnOff();
            turnOff = false;
            turnedOn = true;
            startRound = Clock.getRoundNum();
        }
        
        totalFlux = myRC.getTeamResources();
//        Logger.debug_printHocho("previousFlux: " + previousFlux + ", currentFlux: " + totalFlux);
        deltaFlux = totalFlux - previousFlux;
        previousFlux = totalFlux;
//        Logger.debug_printHocho("deltaFlux: " + deltaFlux);
        
        if(Clock.getRoundNum() - startRound >= DELTA_FLUX_MEMORY_LENGTH + GameConstants.EQUIP_WAKE_DELAY) {
            int mod = Clock.getRoundNum() % DELTA_FLUX_MEMORY_LENGTH;
            if(averageDeltaFlux - deltaFlux < 3) {
//                Logger.debug_printHocho("deltaFlux normal at " + deltaFlux + ", averageDeltaFlux: " + averageDeltaFlux);
                majorDeltaFluxShifting = 0;
                totalDeltaFlux -= deltaFluxes[mod];
                deltaFluxes[mod] = deltaFlux;
                totalDeltaFlux += deltaFlux;
                averageDeltaFlux = totalDeltaFlux / DELTA_FLUX_MEMORY_LENGTH;
            }
            else {
//                Logger.debug_printHocho("deltaFlux abnormal at " + deltaFlux + ", averageDeltaFlux: " + averageDeltaFlux);
                totalDeltaFlux -= deltaFluxes[mod];
                deltaFluxes[mod] = averageDeltaFlux;
                totalDeltaFlux += averageDeltaFlux;
                averageDeltaFlux = totalDeltaFlux / DELTA_FLUX_MEMORY_LENGTH;
                alternativeDeltaFluxes[mod] = deltaFlux;
                if(++majorDeltaFluxShifting == DELTA_FLUX_MEMORY_LENGTH) {
                    Logger.debug_printHocho("major delta flux shift!");
                    deltaFluxes = alternativeDeltaFluxes;
                    totalDeltaFlux = 0;
                    for(double dFlux : deltaFluxes) {
                        totalDeltaFlux += dFlux;
                    }
                    Logger.debug_printHocho("old average deltaFlux: " + averageDeltaFlux + ", new average deltaFlux: " + totalDeltaFlux / DELTA_FLUX_MEMORY_LENGTH);
                    averageDeltaFlux = totalDeltaFlux / DELTA_FLUX_MEMORY_LENGTH;
                }
            }
        }
        else if(Clock.getRoundNum() - startRound >= GameConstants.EQUIP_WAKE_DELAY) {
            if(deltaFlux >= RATIONAL_DELTA_FLUX_LOWER_BOUND) {
//                Logger.debug_printHocho("early deltaFlux normal at " + deltaFlux);
                deltaFluxes[Clock.getRoundNum() % DELTA_FLUX_MEMORY_LENGTH] = deltaFlux;
                totalDeltaFlux += deltaFlux;
                averageDeltaFlux = totalDeltaFlux / DELTA_FLUX_MEMORY_LENGTH;
            }
            else {
//                Logger.debug_printHocho("early deltaFlux abnormal at " + deltaFlux);
                double idealDeltaFlux = averageDeltaFlux  * DELTA_FLUX_MEMORY_LENGTH / (Clock.getRoundNum() - startRound - 1);
                deltaFluxes[Clock.getRoundNum() % DELTA_FLUX_MEMORY_LENGTH] = idealDeltaFlux;
                totalDeltaFlux += idealDeltaFlux;
                averageDeltaFlux = totalDeltaFlux / DELTA_FLUX_MEMORY_LENGTH;
            }
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
