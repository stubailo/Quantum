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
    private final Robot              myRobot;
    
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
    private boolean         gottenNearestEnemy      = false;
    private boolean         minesSensed             = false;
    private boolean         blockersSensed          = false;
    private boolean         emptyMinesSensed        = false;
    private boolean         gottenNearestEmptyMine  = false;
    private boolean         ownIncomeSensed         = false;
    //private final boolean[] sensableRobotsIdHash    = new boolean[MAX_TOTAL_NUMBER_OF_ROBOTS];
    
    /***
     * INFO
     * 
     * 
     */
    public          int                 horizontalSightRange            = 0,
                                        forwardSightRange               = 0;
    
    private final   Robot[]             sensableRobots                  = new Robot[MAX_NUMBER_OF_SENSABLE_THINGS];
    private final   int[]               sensableRobotsSensorHash        = new int[MAX_TOTAL_NUMBER_OF_ROBOTS];
    private final   RobotInfo[]         sensableRobotInfos              = new RobotInfo[MAX_NUMBER_OF_SENSABLE_THINGS];
    //private final   Team[]              sensableRobotTeams              = new Team[MAX_TOTAL_NUMBER_OF_ROBOTS];
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
    private         int                 numberOfSensableMines           = 0;
    private final   Mine[]              sensableEmptyMines              = new Mine[MAX_NUMBER_OF_SENSABLE_THINGS];
    private         int                 numberOfSensableEmptyMines      = 0;
    
    private final   TerrainStatus[][]   terrainStatusHash               = new TerrainStatus[LOCATION_HASH_SIZE][LOCATION_HASH_SIZE];
    
    private final   int[]               edgeCoordinates                 = new int[4];
    private final   boolean[]           edgesFound                      = new boolean[4];
    
    private         MapLocation         nearestEmptyMineLocation;
    private         boolean             enemiesNearby;
    private         MapLocation         nearestEnemyLocation;
    
    private         double              ownIncome;
    
    public          MapLocation         startingTurnedOnRecyclerLocation,
                                        startingMine1Location,
                                        startingMine2Location,
                                        startingFirstMineToBeBuiltLocation,
                                        startingSecondMineToBeBuiltLocation,
                                        startingIdealBuildingLocation;
    public          boolean             standardWayClear;
    
    
    public          RobotInfo           startingLightInfo;
    
    
    

    
    
    public SensorHandler(RobotController rc, Knowledge know) {
        
        myRC = rc;
        myK  = know;
        myMC = (MovementController) myRC.components()[0];
        
        myTeam    = myK.myTeam;
        enemyTeam = myTeam.opponent();
        myID      = myK.myRobotID;
        myRobot   = myK.myRobot;
    }
    
    
    
    /**
     * Adds a SensorController to the Handler
     * @param sc    SensorController to be added
     */
    public void addSC(SensorController sc) {
        
        switch(sc.type()) {
        
        case SIGHT:
            if(horizontalSightRange < 2) horizontalSightRange = 2;
            if(forwardSightRange    < 3) forwardSightRange    = 3;
            break;
            
        case RADAR:
            if(horizontalSightRange < 6) horizontalSightRange = 6;
            if(forwardSightRange    < 6) forwardSightRange    = 6;
            break;
            
        case SATELLITE:
            horizontalSightRange = 10;
            forwardSightRange    = 10;
            break;
        }
        
        mySCs[numberOfSensors] = sc;
        numberOfSensors++;
    }
    
    /**
     * Uses some basic knowledge to reset appropriate redundancy preventers
     */
    public void refresh() {
        if(lastRoundRefreshed == Clock.getRoundNum()) return;
        
        robotsSensed        = false;
        robotInfosSensed    = false;
        robotsDeepSensed    = false;
        enemiesNearby       = false;
        gottenNearestEnemy  = false;
        ownIncomeSensed     = false;
        
        myLocation = myK.myLocation;
        //if(myK.justMoved || myK.justTurned) {
        if(myK.justMoved || myK.justTurned) {
            minesSensed            = false;
            emptyMinesSensed       = false;
            gottenNearestEmptyMine = false;
            blockersSensed         = false;
        }
        
        lastRoundRefreshed = Clock.getRoundNum();
    }
    
    public void thoroughRefresh() {
        robotsSensed        = false;
        robotInfosSensed    = false;
        robotsDeepSensed    = false;
        enemiesNearby       = false;
        gottenNearestEnemy  = false;
        ownIncomeSensed     = false;
        
        myLocation = myK.myLocation;
        
        minesSensed            = false;
        emptyMinesSensed       = false;
        gottenNearestEmptyMine = false;
        blockersSensed         = false;
        
        lastRoundRefreshed = Clock.getRoundNum();
    }
    
    
    
    public void senseEdges() {
        
        if(myK.justMoved) {
        
            Direction myDirection = myK.myDirection;
            
            if(myDirection.ordinal() % 2 == 1) {//diagonal
                Direction   dir1      = myDirection.rotateLeft(),
                            dir2      = myDirection.rotateRight();
                int         dir1Num   = dir1.ordinal()/2;
                int         dir2Num   = dir2.ordinal()/2;
                if(!edgesFound[dir1Num]) {
                    edgesFound[dir1Num] = senseEdgeJustMoved(myLocation, dir1);
                } 
                if(!edgesFound[dir2Num]) {
                    edgesFound[dir2Num] = senseEdgeJustMoved(myLocation, dir2);
                }
            }
            else {
                
                int         myDirNum  = myDirection.ordinal()/2;
                
                if(!edgesFound[myDirNum]) {
                    edgesFound[myDirNum] = senseEdgeJustMoved(myLocation, myDirection);
                }
            }
        }
        
        else if(myK.justMoved) {
            Direction myDirection = myK.myDirection;
            
            if(myDirection.ordinal() % 2 == 1) {//diagonal
                
                Direction   dir1      = myDirection.rotateLeft(),
                            dir2      = myDirection.rotateRight();
                int         dir1Num   = dir1.ordinal()/2;
                int         dir2Num   = dir2.ordinal()/2;
                
                if(!edgesFound[dir1Num]) {
                    edgesFound[dir1Num] = senseEdgeJustTurned(myLocation, dir1);
                } 
                if(!edgesFound[dir2Num]) {
                    edgesFound[dir2Num] = senseEdgeJustTurned(myLocation, dir2);
                }
            }
            else {
                
                int         myDirNum  = myDirection.ordinal()/2;
                
                if(!edgesFound[myDirNum]) {
                    edgesFound[myDirNum] = senseEdgeJustTurned(myLocation, myDirection);
                    Logger.debug_printHocho("direction: " + myDirection.toString() + ", found? " + edgesFound[myDirNum]);
                }
            }
        }
    }
    
    
    
    private boolean senseEdgeJustMoved(MapLocation myLocation, Direction dir) {
        
        MapLocation senseLocation = null;
        
        switch(dir) {
        
        case NORTH:
            senseLocation = new MapLocation(myLocation.x, myLocation.y - forwardSightRange);
            break;
        case EAST:
            senseLocation = new MapLocation(myLocation.x + forwardSightRange, myLocation.y);
            break;
        case SOUTH:
            senseLocation = new MapLocation(myLocation.x, myLocation.y + forwardSightRange);
            break;
        case WEST:
            senseLocation = new MapLocation(myLocation.x - forwardSightRange, myLocation.y);
            break;
        }
        
        TerrainTile senseTile = myRC.senseTerrainTile(senseLocation);
        
        if(senseTile == TerrainTile.OFF_MAP) {
            
            switch(dir) {
            
            case NORTH:
                edgeCoordinates[0] = senseLocation.y;
                return true;
            case EAST:
                edgeCoordinates[1] = senseLocation.x;
                return true;
            case SOUTH:
                edgeCoordinates[2] = senseLocation.y;
                return true;
            case WEST:
                edgeCoordinates[3] = senseLocation.x;
                return true;
            }
        }
        return false;
    }
    
    private boolean senseEdgeJustTurned(MapLocation myLocation, Direction dir) {

        MapLocation senseLocation = null;
        Direction   addDirection  = dir.opposite();
        
        switch(dir) {
        
        case NORTH:
            senseLocation = new MapLocation(myLocation.x, myLocation.y - forwardSightRange);
            break;
        case EAST:
            senseLocation = new MapLocation(myLocation.x + forwardSightRange, myLocation.y);
            break;
        case SOUTH:
            senseLocation = new MapLocation(myLocation.x, myLocation.y + forwardSightRange);
            break;
        case WEST:
            senseLocation = new MapLocation(myLocation.x - forwardSightRange, myLocation.y);
            break;
        }
        
        for(int garbage = 0; garbage < forwardSightRange; garbage++) {
            
            if(myRC.senseTerrainTile(senseLocation) == TerrainTile.OFF_MAP) {
                
                switch(dir) {
                
                case NORTH:
                    edgeCoordinates[0] = senseLocation.y;
                    return true;
                case EAST:
                    edgeCoordinates[1] = senseLocation.x;
                    return true;
                case SOUTH:
                    edgeCoordinates[2] = senseLocation.y;
                    return true;
                case WEST:
                    edgeCoordinates[3] = senseLocation.x;
                    return true;
                }
            }
            
            senseLocation = senseLocation.add(addDirection);
        }
        return false;
    }
    
    
    
    public Direction getDirectionTowardUndiscoveredBoundaries() {
        if(edgesFound[0]) {
            if(edgesFound[1]) {
                if(edgesFound[2]) {
                    if(edgesFound[3]) {
                        return Direction.OMNI;
                    }
                    else {
                        return Direction.WEST;
                    }
                }
                else {
                    if(edgesFound[3]) {
                        return Direction.SOUTH;
                    }
                    else {
                        return Direction.SOUTH_WEST;
                    }
                }
            }
            else {
                if(edgesFound[2]) {
                    if(edgesFound[3]) {
                        return Direction.EAST;
                    }
                    else {
                        if(myK.myRobotID % 2 == 0) {
                            return Direction.WEST;
                        }
                        else {
                            return Direction.EAST;
                        }
                    }
                }
                else {
                    if(edgesFound[3]) {
                        return Direction.SOUTH_EAST;
                    }
                    else {
                        return Direction.SOUTH;
                    }
                }
            }
        }
        else {
            if(edgesFound[1]) {
                if(edgesFound[2]) {
                    if(edgesFound[3]) {
                        return Direction.NORTH;
                    }
                    else {
                        return Direction.NORTH_WEST;
                    }
                }
                else {
                    if(edgesFound[3]) {
                        if(myK.myRobotID % 2 == 0) {
                            return Direction.NORTH;
                        }
                        else {
                            return Direction.SOUTH;
                        }
                    }
                    else {
                        return Direction.WEST;
                    }
                }
            }
            else {
                if(edgesFound[2]) {
                    if(edgesFound[3]) {
                        return Direction.NORTH_EAST;
                    }
                    else {
                        return Direction.NORTH;
                    }
                }
                else {
                    if(edgesFound[3]) {
                        return Direction.EAST;
                    }
                    else {
                        switch(myK.myRobotID % 4) {
                        case 0:
                            return Direction.NORTH;
                        case 1:
                            return Direction.EAST;
                        case 2:
                            return Direction.SOUTH;
                        case 3:
                            return Direction.WEST;
                        }
                    }
                }
            }
        }
        Logger.debug_printCustomErrorMessage("should return a direction earlier", "Hocho");
        return Direction.NONE;
    }
    
    
    
    public boolean areEnemiesNearby() {
        senseRobots();
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
        if(robotsSensed) return;
        
        numberOfSensableRobots = 0;
        boolean[] sensableRobotsIdHash = new boolean[MAX_TOTAL_NUMBER_OF_ROBOTS];
        
        for(int index = 0; index < numberOfSensors; index ++) {
            for(Robot sensableRobot : mySCs[index].senseNearbyGameObjects(Robot.class)) {
                int id = sensableRobot.getID();
//                Logger.debug_printHocho("id: " + id);
                if(!sensableRobotsIdHash[id]) {
                    
                    sensableRobots[numberOfSensableRobots++] = sensableRobot;
                    sensableRobotsIdHash[id] = true;
//                    sensableRobotsSensorHash[id] = index;
                    
//                    Team sensableRobotTeam = sensableRobot.getTeam();
//                    sensableRobotTeams[id] = sensableRobotTeam;
                    
                    if(sensableRobot.getTeam() == enemyTeam) {
                        enemiesNearby = true;
                    }
                }
            }
        }
        robotsSensed = true;
    }
    
    public Robot[] getSensableRobots() {
        senseRobots();
        return sensableRobots;
    }
    
    public int getNumberOfSensableRobots() {
        senseRobots();
        return numberOfSensableRobots;
    }
    
    
    
    
    private void senseRobotInfos() {
        if(robotInfosSensed) return;
        senseRobots();
        
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
    
    
    
    private void deepSenseRobotInfo() {
        
        if(robotsDeepSensed) return;
        senseRobotInfos();
        
        try {
            
            numberOfSensableAlliedRobots = 0;
            numberOfSensableEnemyRobots  = 0;
            
            if(!blockersSensed) {
                
                numberOfSensableAlliedBuildings = 0;
                numberOfSensableEnemyBuildings  = 0;
                numberOfSensableDebris          = 0;
            }
            
            if(blockersSensed) {
            
                for(int index = 0; index < numberOfSensableRobots; index++) {
                    Robot sensableRobot = sensableRobots[index];
                    RobotInfo sensableRobotInfo = sensableRobotInfos[index]; 
                
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
            }
                
            else {
                
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
            
            
            robotsDeepSensed = true;
            blockersSensed = true;
        
        }
        catch(Exception e) {
            Logger.debug_printExceptionMessage(e);
        }
    }
    
    public RobotInfo[] getEnemyRobotInfos() {
        deepSenseRobotInfo();
        if(numberOfSensableEnemyRobots == 0) {
            if(numberOfSensableEnemyBuildings == 0) {
                return null;
            }
            return sensableEnemyBuildingInfos;
        }
        return sensableEnemyRobotInfos;
    }
    
    public int getNumberOfSensableEnemyRobots() {
        deepSenseRobotInfo();
        if(numberOfSensableEnemyRobots == 0) {
            return numberOfSensableEnemyBuildings;
        }
        return numberOfSensableEnemyRobots;
    }
    
    public MapLocation getNearestEnemyLocation() {
        if(gottenNearestEnemy) return nearestEnemyLocation;
        deepSenseRobotInfo();
        
        int nearestEnemyDistance = BIG_INT;
        nearestEnemyLocation = null;
        
        if(numberOfSensableEnemyRobots > 0) {
        
            for(int index = 0; index < numberOfSensableEnemyRobots; index++) {
                MapLocation enemyLocation = sensableEnemyRobotInfos[index].location;
                
                int distance = myLocation.distanceSquaredTo(enemyLocation);
                if(distance < nearestEnemyDistance) {
                    nearestEnemyDistance = distance;
                    nearestEnemyLocation = enemyLocation;
                }
            }
        }
        
        else {
        
            for(int index = 0; index < numberOfSensableEnemyBuildings; index++) {
                MapLocation enemyLocation = sensableEnemyBuildingInfos[index].location;
                
                int distance = myLocation.distanceSquaredTo(enemyLocation);
                if(distance < nearestEnemyDistance) {
                    nearestEnemyDistance = distance;
                    nearestEnemyLocation = enemyLocation;
                }
            }
        }
        
        gottenNearestEmptyMine = true;
        
        return nearestEnemyLocation;
    }
    
    public int getNumberOfSensableAlliedRobots() {
        deepSenseRobotInfo();
        return numberOfSensableAlliedRobots;
    }
    
    
    private void senseBlockers() {
        
        if(blockersSensed) return;
        senseRobotInfos();
        
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
        
        senseMines();
        senseBlockers();
        
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
        senseMines();
        return sensableMines;
    }
    
    public int getNumberOfSensableMines() {
        senseMines();
        return numberOfSensableMines;
    }
    
    public MapLocation getNearestEmptyMine() {
        
        if(gottenNearestEmptyMine) return nearestEmptyMineLocation;
        senseEmptyMines();
                
        int nearestEmptyMineDistance = BIG_INT;
        nearestEmptyMineLocation = null;
        
        for(int index = 0; index < numberOfSensableEmptyMines; index++) {
            MapLocation mineLocation = sensableEmptyMines[index].getLocation();
            
            int distance = myLocation.distanceSquaredTo(mineLocation);
            if(distance < nearestEmptyMineDistance) {
                nearestEmptyMineDistance = distance;
                nearestEmptyMineLocation = mineLocation;
            }
        }
        
        gottenNearestEmptyMine = true;
        return nearestEmptyMineLocation;
    }

    
    
    
    public void senseStartingLightPlayer ()
    {
        senseRobots();

        for(int index = 0; index < numberOfSensableRobots; index ++) {
            try {
                RobotInfo info = mySCs[0].senseRobotInfo(sensableRobots[index]);
                if( info.chassis == Chassis.LIGHT ) {
                    startingLightInfo = info;
                    break;
                }
            } catch ( Exception e ) {
                Logger.debug_printExceptionMessage(e);
            }
        }
    }
    
    
    
    public TerrainStatus getTerrainStatus(MapLocation location) {
        senseBlockers();
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
    
    
    
    public double getOwnIncome() {
        if(ownIncomeSensed) return ownIncome;
        
        ownIncomeSensed = true;
        
        try {
            return mySCs[0].senseIncome(myRobot);
        }
        catch(Exception e) {
            Logger.debug_printExceptionMessage(e);
            return 0;
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
                    startingMine1Location               = myLocation.add(usefulDirection1);
                    startingMine2Location               = myLocation.add(usefulDirection2);
                    startingSecondMineToBeBuiltLocation = myLocation.add(usefulDirection1, 2);
                    startingFirstMineToBeBuiltLocation  = myLocation.add(usefulDirection1).add(usefulDirection2);
                    startingIdealBuildingLocation       = myLocation.add(usefulDirection1, 3);
                }
                else {
                    standardWayDirection                = usefulDirection2; 
                    startingMine1Location               = myLocation.add(otherDirection);
                    startingMine2Location               = myLocation.add(myDirection);
                    startingSecondMineToBeBuiltLocation = myLocation.add(otherDirection, 2);
                    startingFirstMineToBeBuiltLocation  = myLocation.add(otherDirection).add(myDirection);
                    startingIdealBuildingLocation       = myLocation.add(otherDirection, 3);
                }
            }
            else { // myDirection is not diagonal
                if(numberOfSensedRecyclers == 1) {
                    standardWayDirection                = otherIsRight ? usefulDirection2.rotateRight() : usefulDirection2.rotateLeft();
                    startingMine1Location               = myLocation.add(usefulDirection2);
                    startingMine2Location               = myLocation.add(usefulDirection1);
                    startingSecondMineToBeBuiltLocation = myLocation.add(usefulDirection2, 2);
                    startingFirstMineToBeBuiltLocation  = myLocation.add(usefulDirection1).add(usefulDirection2);
                    startingIdealBuildingLocation       = myLocation.add(usefulDirection2, 3);
                }
                else {
                    standardWayDirection                = otherIsRight ? myDirection.rotateLeft() : myDirection.rotateRight();
                    startingMine1Location               = myLocation.add(myDirection);
                    startingMine2Location               = myLocation.add(otherDirection);
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
     * Gets recycler location to turn on.
     */
    public MapLocation[] getRecyclerLocationToTurnOn(MapLocation justBuilt) {
        
        MapLocation[]       result                          = new MapLocation[2];
        Direction           directionToJustBuiltRecycler    = myLocation.directionTo(justBuilt);
        Direction           testDirection                   = myK.myDirection.rotateLeft();
        MovementController  motor                           = (MovementController) myRC.components()[0];
        
        if(motor.canMove(testDirection)) {
            result[0] = justBuilt;
            result[1] = myLocation.add(testDirection);
            return result;
        }
        
        testDirection = testDirection.rotateRight().rotateRight();
        
        if(motor.canMove(testDirection)) {
             result[0] = justBuilt;
             result[1] = myLocation.add(testDirection);
             return result;
        }
        
        return null;
    }
    
    /**
     * Gets orthogonally adjacent of the original four mines
     */
    public MapLocation getOrthogonallyAdjacentStartingRecycler(MapLocation location) {
        MapLocation[] startingRecyclers = {startingMine1Location, startingMine2Location, startingFirstMineToBeBuiltLocation, startingSecondMineToBeBuiltLocation};
        for(MapLocation startingRecycler : startingRecyclers) {
            if(location.distanceSquaredTo(startingRecycler) == 1) {
                return startingRecycler;
            }
        }
        return null;
    }
    
    
    
    public MapLocation turnOnRecyclerLocation(MapLocation factoryLocation) {
        MapLocation result = getOrthogonallyAdjacentStartingRecycler(factoryLocation);
        if(result != null) return result;
        MapLocation[] startingRecyclers   = {startingMine1Location, startingMine2Location, startingFirstMineToBeBuiltLocation, startingSecondMineToBeBuiltLocation};
        for(int index = 0; index < 4; index++) {
            MapLocation startingRecycler = startingRecyclers[index];
            if(startingRecycler.isAdjacentTo(factoryLocation)) return startingRecycler;
        }
        return null;
    }
    
    
    
    /**
     * Designed by first-round use of recycler, finds lowest ID adjacent recycler.
     * 
     * (So that it can turn itself off if it's not the lowest)
     */
    public boolean amLowestIDRecycler() {
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
