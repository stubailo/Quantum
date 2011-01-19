package newTeam.handler;

import battlecode.common.*;
import newTeam.common.Knowledge;
import newTeam.common.QuantumConstants;
import newTeam.common.util.Logger;

public class SensorHandler {
    
    
    private final RobotController    myRC;
    private final Knowledge          myK;
    private final MovementController myMC;
    
    
    /*** Controller ***/
    private final SensorController[] mySCs           = new SensorController[4];
    private       int                numberOfSensors = 0;
    
    /*** Constants ***/
    private static final int MAX_TOTAL_NUMBER_OF_ROBOTS  = QuantumConstants.MAX_TOTAL_NUMBER_OF_ROBOTS;
    private static final int MAX_NUMBER_OF_SENSED_THINGS = QuantumConstants.MAX_NUMBER_OF_SENSED_THINGS;
    
    /*** 
     * REDUNDANCY PREVENTERS
     * 
     * These booleans prevent unnecessary redundant computation and are reset by the "refresh" method.
     */
    private int             lastRoundRefreshed      = -1;
    private boolean         robotsSensed            = false;
    private boolean         minesSensed             = false;
    private boolean         buildingsSensed         = false;
    //private final boolean[] sensableRobotsIdHash    = new boolean[MAX_TOTAL_NUMBER_OF_ROBOTS];
    
    /***
     * INFO
     * 
     * 
     */
    private final   Robot[]     sensableRobots              = new Robot[MAX_NUMBER_OF_SENSED_THINGS];
    private         int         numberOfSensableRobots      = 0;
    private final   Mine[]      sensableMines               = new Mine[MAX_NUMBER_OF_SENSED_THINGS];
    private         int         numberOfSensableMines       = 0;
    
    public          MapLocation startingTurnedOnRecyclerLocation,
                                startingFirstMineToBeBuiltLocation,
                                startingSecondMineToBeBuiltLocation,
                                startingIdealBuildingLocation;
    public          boolean     standardWayClear;
    
    
    public          RobotInfo   startingLightInfo;
    
    
    

    
    
    public SensorHandler(RobotController rc, Knowledge know) {
        
        myRC = rc;
        myK  = know;
        myMC = (MovementController) myRC.components()[0];
    }
    
    
    
    /**
     * Adds a SensorController to the Handler
     * @param sc    SensorController to be added
     */
    public void addSC(SensorController sc) {
        mySCs[numberOfSensors] = sc;
        numberOfSensors++;
    }
    
    /**
     * Uses some basic knowledge to reset appropriate redundancy preventers
     */
    public void refresh() {
        if(lastRoundRefreshed == Clock.getRoundNum()) return;
        lastRoundRefreshed = Clock.getRoundNum();
        
        robotsSensed = false;
        if(myK.justMoved || myK.justTurned) {
            minesSensed = false;
            buildingsSensed = false;
        }
    }

    public Robot senseAtLocation( MapLocation location, RobotLevel height)
    {
        GameObject obj;
        try {
        obj = mySCs[0].senseObjectAtLocation(location, height);
        } catch ( Exception e ) {
            return null;
        }
        if( obj instanceof Robot )
        {
            return (Robot) obj;
        } else {
            return null;
        }
    }

    private void senseRobots() {
        if(robotsSensed) return;
        robotsSensed = true;
        numberOfSensableRobots = 0;
        boolean[] sensableRobotsIdHash = new boolean[MAX_TOTAL_NUMBER_OF_ROBOTS];
        
        for(int index = 0; index < numberOfSensors; index ++) {
            for(Robot sensableRobot : mySCs[index].senseNearbyGameObjects(Robot.class)) {
                int id = sensableRobot.getID();
                if(!sensableRobotsIdHash[id]) {
                    sensableRobots[numberOfSensableRobots++] = sensableRobot;
                    sensableRobotsIdHash[id] = true;
                }
            }
        }
    }
    
    public Robot[] getSensableRobots() {
        if(!robotsSensed) {
            senseRobots();
        }
        return sensableRobots;
    }
    
    public int getNumberOfSensableRobots() {
        if(!robotsSensed) {
            senseRobots();
        }
        return numberOfSensableRobots;
    }
    
    
    
    private void senseMines() {
        if(minesSensed) return;
        minesSensed = true;
        boolean[] sensableMinesIdHash = new boolean[MAX_TOTAL_NUMBER_OF_ROBOTS];
        
        for(int index = 0; index < numberOfSensors; index ++) {
            for(Mine sensableMine : mySCs[index].senseNearbyGameObjects(Mine.class)) {
                int id = sensableMine.getID();
                if(!sensableMinesIdHash[id]) {
                    sensableMines[numberOfSensableMines++] = sensableMine;
                    sensableMinesIdHash[id] = true;
                }
            }
        }
    }
    
    public Mine[] getSensableMines() {
        if(!minesSensed) {
            senseMines();
        }
        return sensableMines;
    }
    
    public int getNumberOfSensableMines() {
        if(!minesSensed) {
            senseMines();
        }
        return numberOfSensableMines;
    }

    public void senseStartingLightPlayer ()
    {
        if(!robotsSensed) {
            senseRobots();
        }

        for(int index = 0; index < numberOfSensableRobots; index ++) {
            try {
                RobotInfo info = mySCs[0].senseRobotInfo(sensableRobots[index]);
                if( info.chassis.equals(Chassis.LIGHT) ) {
                    startingLightInfo = info;
                    break;
                }
            } catch ( Exception e ) {
                Logger.debug_printExceptionMessage(e);
            }
        }
    }
    
    
    /**
    * Designed for use by starting light on round 0, 1, or 2, records locations of empty mines
    * and other useful opening information
    * @return      direction to turn in, OMNI to proceed, NONE if error occurs
    */
    public Direction senseStartingLightConstructorSurroundings() {
        try {
            SensorController sensor      = mySCs[0];
            MapLocation      myLocation  = myK.myLocation;
            Direction        myDirection = myK.myDirection;
              
            Robot[] sensedRobots = sensor.senseNearbyGameObjects(Robot.class);
              
            Direction otherDirection          = Direction.NONE,
                      usefulDirection1        = Direction.NONE,
                      usefulDirection2        = Direction.NONE;
            int       numberOfSensedRecyclers = 0,
                      lowestRecyclerID        = QuantumConstants.BIG_INT;
            for(Robot sensedRobot : sensedRobots) {
                if(sensedRobot.getTeam() != Team.NEUTRAL) { // We must check that it's not debris.
                    if(numberOfSensedRecyclers == 0) {
                        startingTurnedOnRecyclerLocation = sensor.senseLocationOf(sensedRobot);
                        lowestRecyclerID = sensedRobot.getID();
                        otherDirection = myLocation.directionTo(startingTurnedOnRecyclerLocation);
                        numberOfSensedRecyclers++;
                    }
                    else {
                        if(sensedRobot.getID() < lowestRecyclerID) {
                            startingTurnedOnRecyclerLocation = sensor.senseLocationOf(sensedRobot);
                            if(myDirection == otherDirection) {
                                otherDirection = myLocation.directionTo(startingTurnedOnRecyclerLocation);
                            }
                        }
                        else {
                            if(myDirection == otherDirection) {
                                otherDirection = myLocation.directionTo(sensor.senseLocationOf(sensedRobot));
                            }
                        }
                        numberOfSensedRecyclers++;
                        break;
                    }
                }
            }
          
            if(numberOfSensedRecyclers == 0) return myDirection.opposite();
            
            boolean otherIsRight = (myDirection.rotateRight() == otherDirection);
            if(otherIsRight) {
                usefulDirection1 = myDirection.rotateRight();
                usefulDirection2 = usefulDirection1.rotateRight();
            }
            else {
                usefulDirection1 = myDirection.rotateLeft();
                usefulDirection2 = usefulDirection1.rotateLeft();
            }
          
            Direction standardWayDirection = Direction.NONE;
            if(myDirection.ordinal() % 2 == 1) { // myDirection is diagonal
                if(numberOfSensedRecyclers == 1) {
                    standardWayDirection                = myDirection;
                    startingSecondMineToBeBuiltLocation = myLocation.add(usefulDirection1, 2);
                    startingFirstMineToBeBuiltLocation  = myLocation.add(usefulDirection1).add(usefulDirection2);
                    startingIdealBuildingLocation       = myLocation.add(usefulDirection1, 3);
                }
                else {
                    standardWayDirection                = usefulDirection2; 
                    startingSecondMineToBeBuiltLocation = myLocation.add(otherDirection, 2);
                    startingFirstMineToBeBuiltLocation  = myLocation.add(otherDirection).add(myDirection);
                    startingIdealBuildingLocation       = myLocation.add(otherDirection, 3);
                }
            }
            else { // myDirection is not diagonal
                if(numberOfSensedRecyclers == 1) {
                    standardWayDirection                = otherIsRight ? usefulDirection2.rotateRight() : usefulDirection2.rotateLeft();
                    startingSecondMineToBeBuiltLocation = myLocation.add(usefulDirection2, 2);
                    startingFirstMineToBeBuiltLocation  = myLocation.add(usefulDirection1).add(usefulDirection2);
                    startingIdealBuildingLocation       = myLocation.add(usefulDirection2, 3);
                }
                else {
                    standardWayDirection                = otherIsRight ? myDirection.rotateLeft() : myDirection.rotateRight();
                    startingSecondMineToBeBuiltLocation = myLocation.add(myDirection, 2);
                    startingFirstMineToBeBuiltLocation  = myLocation.add(otherDirection).add(myDirection);
                    startingIdealBuildingLocation       = myLocation.add(myDirection, 3);
                }
            }
          
            standardWayClear = myMC.canMove(standardWayDirection);
          
            return Direction.OMNI;
        }
        catch(Exception e) {
            Logger.debug_printExceptionMessage(e);
            return Direction.NONE;
        }
    }
    
    
    
    /**
     * Finds adjacent location in which building is possible
     * @return      empty adjacent square, myLocation if such a location does not exist
     */
    public MapLocation findEmptyLocationToBuild() {
        Direction testDirection = Direction.EAST;
        for(int garbage = 0; garbage < 8; garbage++) {
            if(myMC.canMove(testDirection)) {
                return myK.myLocation.add(testDirection);
            }
            testDirection = testDirection.rotateRight();
        }
        return myK.myLocation;
    }
    
    
    
    /**
     * Designed by first-round use of recycler, finds lowest ID adjacent recycler.
     * 
     * (So that it can turn itself off if it's not the lowest)
     */
    public Boolean amLowestIDRecycler() {
        try {
            SensorController sensor = mySCs[0];
            int myID = myK.myRobotID;
            for (Robot robot : sensor.senseNearbyGameObjects(Robot.class)) {
                RobotInfo robotInfo = sensor.senseRobotInfo(robot);
                Team team = robot.getTeam();
                if (team == myK.myTeam) {
                    ComponentType[] compTypes = robotInfo.components;
                    switch (robotInfo.chassis) {
                        case BUILDING:
                            for (ComponentType compType : compTypes) {
                                if (compType == ComponentType.RECYCLER) {
                                    if (robot.getID() < myID) {
                                        return false;
                                    }
                                    break;
                                }
                            }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            Logger.debug_printExceptionMessage(e);
            return true;
        }
    }
    

}
