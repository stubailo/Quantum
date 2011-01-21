package newTeam.handler;

import battlecode.common.*;
import newTeam.common.Knowledge;
import newTeam.common.QuantumConstants;
import newTeam.common.util.Logger;
import newTeam.handler.sensor.TerrainStatus;

public class SensorHandler {
    
    
    private final RobotController    myRC;
    private final Knowledge          myK;
    
    
    /*** Controller ***/
    private final SensorController[] mySCs           = new SensorController[4];
    private       int                numberOfSensors = 0;
    private final MovementController myMC;

    /*** Life Constants ***/
    private final Team               myTeam;
    private final Team               enemyTeam;
    private final int                myID;
    
    /*** Round Constants ***/
    private       MapLocation        myLocation;
    
    /*** Constants ***/
    private static final int MAX_TOTAL_NUMBER_OF_ROBOTS    = QuantumConstants.MAX_TOTAL_NUMBER_OF_ROBOTS;
    private static final int MAX_NUMBER_OF_SENSABLE_THINGS = QuantumConstants.MAX_NUMBER_OF_SENSABLE_THINGS;
    private static final int LOCATION_HASH_SIZE            = QuantumConstants.LOCATION_HASH_SIZE;
    private static final int BIG_INT                       = QuantumConstants.BIG_INT;
    
    /*** 
     * REDUNDANCY PREVENTERS
     * 
     * These booleans prevent unnecessary redundant computation and are reset by the "refresh" method.
     */
    private int             lastRoundRefreshed      = -1;
    private boolean         robotsSensed            = false;
    private boolean         robotInfosSensed        = false;
    private boolean         robotsDeepSensed        = false;
    private boolean         minesSensed             = false;
    private boolean         blockersSensed          = false;
    private boolean         emptyMinesSensed        = false;
    //private final boolean[] sensableRobotsIdHash    = new boolean[MAX_TOTAL_NUMBER_OF_ROBOTS];
    
    /***
     * INFO
     * 
     * 
     */
    private final   Robot[]             sensableRobots                  = new Robot[MAX_NUMBER_OF_SENSABLE_THINGS];
    private final   int[]               sensableRobotsSensorHash        = new int[MAX_NUMBER_OF_SENSABLE_THINGS];
    private final   RobotInfo[]         sensableRobotInfos              = new RobotInfo[MAX_NUMBER_OF_SENSABLE_THINGS];
    private         int                 numberOfSensableRobots          = 0;
    private final   Robot[]             sensableAlliedRobots            = new Robot[MAX_NUMBER_OF_SENSABLE_THINGS];
    private final   RobotInfo[]         sensableAlliedRobotInfos        = new RobotInfo[MAX_NUMBER_OF_SENSABLE_THINGS];
    private         int                 numberOfSensableAlliedRobots    = 0;
    private final   Robot[]             sensableEnemyRobots             = new Robot[MAX_NUMBER_OF_SENSABLE_THINGS];
    private final   RobotInfo[]         sensableEnemyRobotInfos         = new RobotInfo[MAX_NUMBER_OF_SENSABLE_THINGS];
    private         int                 numberOfSensableEnemyRobots     = 0;
    private final   Robot[]             sensableAlliedBuildings         = new Robot[MAX_NUMBER_OF_SENSABLE_THINGS];
    private final   RobotInfo[]         sensableAlliedBuildingInfos     = new RobotInfo[MAX_NUMBER_OF_SENSABLE_THINGS];
    private         int                 numberOfSensableAlliedBuildings = 0;
    private final   Robot[]             sensableEnemyBuildings          = new Robot[MAX_NUMBER_OF_SENSABLE_THINGS];
    private final   RobotInfo[]         sensableEnemyBuildingInfos      = new RobotInfo[MAX_NUMBER_OF_SENSABLE_THINGS];
    private         int                 numberOfSensableEnemyBuildings  = 0;
    private final   Robot[]             sensableDebris                  = new Robot[MAX_NUMBER_OF_SENSABLE_THINGS];
    private final   RobotInfo[]         sensableDebrisInfos             = new RobotInfo[MAX_NUMBER_OF_SENSABLE_THINGS];
    private         int                 numberOfSensableDebris          = 0;
    private final   Mine[]              sensableMines                   = new Mine[MAX_NUMBER_OF_SENSABLE_THINGS];
//    private final   MineInfo[]          sensableMineInfos               = new MineInfo[MAX_NUMBER_OF_SENSED_THINGS];
    private         int                 numberOfSensableMines           = 0;
    private final   Mine[]              sensableEmptyMines              = new Mine[MAX_NUMBER_OF_SENSABLE_THINGS];
    private         int                 numberOfSensableEmptyMines      = 0;
    
    private final   TerrainStatus[][]   terrainStatusHash           = new TerrainStatus[LOCATION_HASH_SIZE][LOCATION_HASH_SIZE];
    
    private         boolean             enemiesNearby;
    
    public          MapLocation         startingTurnedOnRecyclerLocation,
                                        startingFirstMineToBeBuiltLocation,
                                            startingSecondMineToBeBuiltLocation,
                                                startingIdealBuildingLocation;
    public          boolean             standardWayClear;
    
    
    public          RobotInfo           startingLightInfo;
    
    
    

    
    
    public SensorHandler(RobotController rc, Knowledge know) {
        
        myRC = rc;
        myK  = know;
        myMC = (MovementController) myRC.components()[0];
        
        myTeam = myK.myTeam;
        enemyTeam = myTeam.opponent();
        myID   = myK.myRobotID;
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
        robotInfosSensed = false;
        robotsDeepSensed = false;
        enemiesNearby = false;
        myLocation = myK.myLocation;
        //if(myK.justMoved || myK.justTurned) {
        if(myK.justMoved || myK.justTurned) {
            minesSensed = false;
            emptyMinesSensed = false;
            blockersSensed = false;
        }
    }
    
    
    
    public boolean areEnemiesNearby() {
        if(!robotsSensed) senseRobots();
        return enemiesNearby;
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
        if(robotsSensed || robotsDeepSensed) return;
        numberOfSensableRobots = 0;
        boolean[] sensableRobotsIdHash = new boolean[MAX_TOTAL_NUMBER_OF_ROBOTS];
        
        for(int index = 0; index < numberOfSensors; index ++) {
            for(Robot sensableRobot : mySCs[index].senseNearbyGameObjects(Robot.class)) {
                int id = sensableRobot.getID();
                if(!sensableRobotsIdHash[id]) {
                    
                    sensableRobots[numberOfSensableRobots++] = sensableRobot;
                    sensableRobotsIdHash[id] = true;
                    sensableRobotsSensorHash[id] = index;
                }
                if(sensableRobot.getTeam() == enemyTeam) {
                    enemiesNearby = true;
                }
            }
        }
        robotsSensed = true;
    }
    
    public Robot[] getSensableRobots() {
        if(!(robotsSensed || robotsDeepSensed)) {
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
    
    
    
    
    private void senseRobotInfo() {
        if(robotInfosSensed) return;
        if(!robotsSensed) senseRobots();
        
        try {
            for(int index = 0; index < numberOfSensableRobots; index++) {
                Robot sensableRobot = sensableRobots[index];
                sensableRobotInfos[index] = mySCs[sensableRobotsSensorHash[sensableRobot.getID()]].senseRobotInfo(sensableRobot);
            }
        }
        catch(Exception e) {
            Logger.debug_printExceptionMessage(e);
        }
    }
    
    
    
    /*private void deepSenseRobotInfo() {
        
        if(robotsDeepSensed) return;
        if(!robotsSensed) senseRobots();
        
        try {
            
            numberOfSensableAlliedRobots = 0;
            numberOfSensableEnemyRobots  = 0;
            
            if(!blockersSensed) {
                
                numberOfSensableAlliedBuildings = 0;
                numberOfSensableEnemyBuildings  = 0;
                numberOfSensableDebris          = 0;
            }
            
            for(int index = 0; index < numberOfSensableRobots; index++) {
                Robot sensableRobot = sensableRobots[index];
                
            }
            
            boolean[] sensableRobotsIdHash = new boolean[MAX_TOTAL_NUMBER_OF_ROBOTS];
            
            for(int index = 0; index < numberOfSensors; index ++) {
                SensorController mySC = mySCs[index];
                for(Robot sensableRobot : mySC.senseNearbyGameObjects(Robot.class)) {
                    int id = sensableRobot.getID();
                    if(!sensableRobotsIdHash[id]) {
                        
                        RobotInfo sensableRobotInfo = mySC.senseRobotInfo(sensableRobot);
                        
                        sensableRobots    [numberOfSensableRobots]   = sensableRobot;
                        sensableRobotInfos[numberOfSensableRobots++] = sensableRobotInfo;
                        sensableRobotsIdHash[id] = true;
                        
                        if(blockersSensed) {
                            
                            Team sensableRobotTeam = sensableRobot.getTeam();
                            
                            if(sensableRobotTeam != Team.NEUTRAL) {
                                
                                if(sensableRobotInfo.chassis != Chassis.BUILDING) {
                                    if(sensableRobotTeam == myTeam) {
                                        sensableAlliedRobots    [numberOfSensableAlliedRobots]   = sensableRobot;
                                        sensableAlliedRobotInfos[numberOfSensableAlliedRobots++] = sensableRobotInfo;
                                    }
                                    else {
                                        enemiesNearby = true;
                                        sensableEnemyRobots     [numberOfSensableEnemyRobots]    = sensableRobot;
                                        sensableEnemyRobotInfos [numberOfSensableEnemyRobots++]  = sensableRobotInfo;
                                    }
                                }
                            }
                        }
                        
                        else {
                            
                            Team sensableRobotTeam = sensableRobot.getTeam();
                            
                            if(sensableRobotTeam != Team.NEUTRAL) {
                                
                                if(sensableRobotInfo.chassis == Chassis.BUILDING) {
                                    
                                    MapLocation buildingLocation = sensableRobotInfo.location;
                                    terrainStatusHash[buildingLocation.x % LOCATION_HASH_SIZE][buildingLocation.y % LOCATION_HASH_SIZE] = TerrainStatus.BLOCKER;
                                    
                                    if(sensableRobotTeam == myTeam) {
                                        sensableAlliedBuildings    [numberOfSensableAlliedBuildings]   = sensableRobot;
                                        sensableAlliedBuildingInfos[numberOfSensableAlliedBuildings++] = sensableRobotInfo;
                                    }
                                    else {
                                        sensableEnemyBuildings     [numberOfSensableEnemyBuildings]    = sensableRobot;
                                        sensableEnemyBuildingInfos [numberOfSensableEnemyBuildings++]  = sensableRobotInfo;
                                    }
                                }
                                
                                else {
                                    if(sensableRobotTeam == myTeam) {
                                        sensableAlliedRobots    [numberOfSensableAlliedRobots]   = sensableRobot;
                                        sensableAlliedRobotInfos[numberOfSensableAlliedRobots++] = sensableRobotInfo;
                                    }
                                    else {
                                        sensableEnemyRobots     [numberOfSensableEnemyRobots]    = sensableRobot;
                                        sensableEnemyRobotInfos [numberOfSensableEnemyRobots++]  = sensableRobotInfo;
                                    }
                                }
                            }
                            
                            else {
                                
                                MapLocation debrisLocation = sensableRobotInfo.location;
                                terrainStatusHash[debrisLocation.x % LOCATION_HASH_SIZE][debrisLocation.y % LOCATION_HASH_SIZE] = TerrainStatus.BLOCKER;
                                
                                sensableDebris     [numberOfSensableDebris]   = sensableRobot;
                                sensableDebrisInfos[numberOfSensableDebris++] = sensableRobotInfo;
                            }
                        }
                    }
                }
            }
            robotsDeepSensed = true;
            blockersSensed = true;
        
        }
        catch(Exception e) {
            Logger.debug_printExceptionMessage(e);
        }
    }*/
    
    
    
    private void senseBlockers() {
        
        if(blockersSensed) return;
        if(!robotInfosSensed) senseRobotInfo();
        
        try {
                
            numberOfSensableAlliedBuildings = 0;
            numberOfSensableEnemyBuildings  = 0;
            numberOfSensableDebris          = 0;
            
            for(int index = 0; index < numberOfSensableRobots; index++) {
                Robot sensableRobot = sensableRobots[index];
                RobotInfo sensableRobotInfo = sensableRobotInfos[index];
                            
                Team sensableRobotTeam = sensableRobot.getTeam();
                
                if(sensableRobotTeam != Team.NEUTRAL) {
                    
                    if(sensableRobotInfo.chassis == Chassis.BUILDING) {
                        
                        MapLocation buildingLocation = sensableRobotInfo.location;
                        terrainStatusHash[buildingLocation.x % LOCATION_HASH_SIZE][buildingLocation.y % LOCATION_HASH_SIZE] = TerrainStatus.BLOCKER;
                        
                        if(sensableRobotTeam == myTeam) {
                            sensableAlliedBuildings    [numberOfSensableAlliedBuildings]   = sensableRobot;
                            sensableAlliedBuildingInfos[numberOfSensableAlliedBuildings++] = sensableRobotInfo;
                        }
                        else {
                            sensableEnemyBuildings     [numberOfSensableEnemyBuildings]    = sensableRobot;
                            sensableEnemyBuildingInfos [numberOfSensableEnemyBuildings++]  = sensableRobotInfo;
                        }
                    }
                    
                    else if(sensableRobotTeam != myTeam) {
                        enemiesNearby = true;
                    }
                }
                
                else {
                    
                    MapLocation debrisLocation = sensableRobotInfo.location;
                    terrainStatusHash[debrisLocation.x % LOCATION_HASH_SIZE][debrisLocation.y % LOCATION_HASH_SIZE] = TerrainStatus.BLOCKER;
                    
                    sensableDebris     [numberOfSensableDebris]   = sensableRobot;
                    sensableDebrisInfos[numberOfSensableDebris++] = sensableRobotInfo;
                }
            }
            blockersSensed = true;
        }
        catch(Exception e) {
            Logger.debug_printExceptionMessage(e);
        }
    }
    
    
    
    private void senseMines() {
        if(minesSensed) return;
        
        try {
            
            numberOfSensableMines = 0;
            boolean[] sensableMinesIdHash = new boolean[MAX_TOTAL_NUMBER_OF_ROBOTS];
            
            for(int index = 0; index < numberOfSensors; index++) {
                SensorController mySC = mySCs[index];
                for(Mine sensableMine : mySC.senseNearbyGameObjects(Mine.class)) {
                    int id = sensableMine.getID();
                    if(!sensableMinesIdHash[id]) {
                        //sensableMineInfos[numberOfSensableMines] = mySC.senseMineInfo(sensableMine);
                        sensableMines[numberOfSensableMines++] = sensableMine;
                        sensableMinesIdHash[id] = true;
                    }
                }
            }
            minesSensed = true;
        }
        catch(Exception e) {
            Logger.debug_printExceptionMessage(e);
        }
    }
    
    private void senseEmptyMines() {
        if(emptyMinesSensed) return;
        
        if(!minesSensed) senseMines();
        if(!blockersSensed) senseBlockers();
        
        numberOfSensableEmptyMines = 0;
        
        for(int index = 0; index < numberOfSensableMines; index++) {
            Mine sensableMine = sensableMines[index];
            MapLocation mineLocation = sensableMine.getLocation();
            if(terrainStatusHash[mineLocation.x % LOCATION_HASH_SIZE][mineLocation.y % LOCATION_HASH_SIZE] != TerrainStatus.BLOCKER) {
                sensableEmptyMines[numberOfSensableEmptyMines++] = sensableMine;
            }
        }
        
        emptyMinesSensed = true;
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
    
    public MapLocation getNearestEmptyMine() {
        if(!emptyMinesSensed) {
            senseEmptyMines();
        }
        
        int nearestMineDistance = BIG_INT;
        MapLocation nearestMineLocation = myLocation;
        
        for(int index = 0; index < numberOfSensableEmptyMines; index++) {
            MapLocation mineLocation = sensableEmptyMines[index].getLocation();
            
            int distance = myLocation.distanceSquaredTo(mineLocation);
            if(distance < nearestMineDistance) {
                nearestMineDistance = distance;
                nearestMineLocation = mineLocation;
            }
        }
        
        if(nearestMineLocation.equals(myLocation)) return null;
        return nearestMineLocation;
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
    
    
    
    public TerrainStatus getTerrainStatus(MapLocation location) {
        if(!blockersSensed) senseBlockers();
        int hashX = location.x % LOCATION_HASH_SIZE;
        int hashY = location.y % LOCATION_HASH_SIZE;
        TerrainStatus terrainStatus = terrainStatusHash[hashX][hashY];
        if(terrainStatus != null) {
            return terrainStatus;
        }
        else {
            TerrainTile terrainTile = myRC.senseTerrainTile(location);
            if(terrainTile == null) return null;
            
            switch(terrainTile) {
            
            case LAND:
                terrainStatusHash[hashX][hashY] = TerrainStatus.LAND;
                return TerrainStatus.LAND;
                
            case VOID:
                terrainStatusHash[hashX][hashY] = TerrainStatus.VOID;
                return TerrainStatus.VOID;
                
            case OFF_MAP:
                terrainStatusHash[hashX][hashY] = TerrainStatus.OFF_MAP;
                return TerrainStatus.OFF_MAP;
            }
            
            Logger.debug_printCustomErrorMessage("getTerrainStatus() should not reach this point", "Hocho");
            return null;
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
            Direction        myDirection = myK.myDirection;
              
            Robot[] sensedRobots = sensor.senseNearbyGameObjects(Robot.class);
              
            Direction otherDirection          = Direction.NONE,
                      usefulDirection1        = Direction.NONE,
                      usefulDirection2        = Direction.NONE;
            int       numberOfSensedRecyclers = 0,
                      lowestRecyclerID        = BIG_INT;
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
     * @return      empty adjacent square, null if such a location does not exist
     */
    public MapLocation findEmptyLocationToBuild() {

        Direction testDirection = Direction.EAST;
        for(int garbage = 0; garbage < 8; garbage++) {
            if(!myMC.canMove(testDirection)) {
                break;
            }
            testDirection = testDirection.rotateRight();
        }

        for(int garbage = 0; garbage < 8; garbage++) {
            if(myMC.canMove(testDirection)) {
                return myLocation.add(testDirection);
            }
            testDirection = testDirection.rotateRight();
        }
        return null;
    }
    
    
    
    /**
     * Designed by first-round use of recycler, finds lowest ID adjacent recycler.
     * 
     * (So that it can turn itself off if it's not the lowest)
     */
    public Boolean amLowestIDRecycler() {
        try {
            SensorController sensor = mySCs[0];
            for (Robot robot : sensor.senseNearbyGameObjects(Robot.class)) {
                RobotInfo robotInfo = sensor.senseRobotInfo(robot);
                Team team = robot.getTeam();
                if (team == myTeam) {
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
