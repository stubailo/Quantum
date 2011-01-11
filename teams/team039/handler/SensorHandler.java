package team039.handler;

import battlecode.common.*;
import team039.common.Knowledge;
import team039.common.QuantumConstants;
import team039.common.location.LocationType;
import team039.common.util.Logger;

public class SensorHandler {
    
    
    private final RobotController myRC;
    private final Knowledge       knowledge;
    
    
    /*** Controller ***/
    private final SensorController[] mySensorSlots   = new SensorController[4];
    private       SensorController[] mySCs;
    private       int                numberOfSensors = 0;

    public SensorHandler(RobotController rc, Knowledge know) {
        
        myRC      = rc;
        knowledge = know;
        
    }
    
    
    
    /**
     * Adds a SensorController to the Handler
     * @param sc    SensorController to be added
     */
    public void addSensor(SensorController sc) {
        mySensorSlots[numberOfSensors] = sc;
        numberOfSensors++;
        mySCs = new SensorController[numberOfSensors];
        System.arraycopy(mySensorSlots, 0, mySCs, 0, numberOfSensors);
    }
    
    
    
    /**
     * Senses nearby mines, updating knowledge appropriately.
     */
    public void senseNearbyMines() {
        if(numberOfSensors == 0) return;
        
        if(knowledge.justTurned || knowledge.justMoved) {
            Logger.debug_printHocho("sensing :)");
            // Bytecode count: at least 100 + 25 * (number of sensed mines)
            //            (plus another 25 * (number of sensed mines) if a robot is on it)
            try {
                if(knowledge.justMoved) {
                    knowledge.nearestUnminedMineLocation        = null;
                    knowledge.nearestUnminedMineDistanceSquared = QuantumConstants.BIG_INT;
                }
                
                for(SensorController sensor : mySCs) {
                    Mine[] nearbyMines = sensor.senseNearbyGameObjects(Mine.class);
                    
                    for(Mine nearbyMine : nearbyMines) {
                        MapLocation  mineLocation     = nearbyMine.getLocation();
                        LocationType mineLocationType = knowledge.locationMemory.getType(mineLocation);
                        
                        if(mineLocationType == null) {
                            Robot potentialBuilding = (Robot) sensor.senseObjectAtLocation(mineLocation, RobotLevel.ON_GROUND);
                            
                            if(potentialBuilding == null ||
                               sensor.senseRobotInfo(potentialBuilding).chassis != Chassis.BUILDING) {
                                
                                knowledge.locationMemory.setType(mineLocation,
                                                                 LocationType.UNMINED_MINE,
                                                                 knowledge.roundNum);
                                
                                int mineDistanceSquared = knowledge.myLocation.distanceSquaredTo(mineLocation);
                                if(mineDistanceSquared < knowledge.nearestUnminedMineDistanceSquared) {
                                    
                                    knowledge.nearestUnminedMineDistanceSquared = mineDistanceSquared;
                                    knowledge.nearestUnminedMineLocation        = mineLocation;
                                }
                            }
                            else {
                                if(potentialBuilding.getTeam() == knowledge.myTeam) {
                                    knowledge.locationMemory.setType(mineLocation,
                                                                     LocationType.OUR_MINE,
                                                                     knowledge.roundNum);
                                }
                                else {
                                    knowledge.locationMemory.setType(mineLocation,
                                                                     LocationType.OUR_MINE,
                                                                     knowledge.roundNum);
                                }
                            }
                        }
                        else {
                            switch()
                        }
                    }
                }
            }
            catch(Exception e) {
                Logger.debug_printExceptionMessage(e);
            }
        }
        Logger.debug_printHocho("not sensing...");
    }
    
    
    
    /**
     * Caches information related to ground-traversability.
     * @param mySCs     robot's sensor controllers
     * @param location  location to be checked
     * @return          an integer, 0 = no, 1 = yes, -1 = dunno (will be convention)
     */
    public int isGroundTraversable(MapLocation location) {

        LocationType locationType = knowledge.locationMemory.getType(location);
        if(locationType == null) {
            TerrainTile locationTile = myRC.senseTerrainTile(location);
            if(locationTile == null) return -1;
            switch(locationTile) {

            case LAND:
                knowledge.locationMemory.setType(location, LocationType.LAND);
                break;

            case OFF_MAP:
                knowledge.locationMemory.setType(location, LocationType.LAND);
                break;

            case VOID:
                knowledge.locationMemory.setType(location, LocationType.LAND);
                break;
            }
        }

        if(locationType == LocationType.VOID || locationType == LocationType.OFF_MAP) return 0;
        return 1;
    }
    
    
    
    public void senseNearbyRobots() {
        if(numberOfSensors == 0) return;
        
        try {
            knowledge.numberOfSensedEnemies = 0;
            knowledge.weakestNearbyDebrisHP = (double) QuantumConstants.BIG_INT;
            
            for(SensorController sensor : mySCs) {
                Robot[] nearbyRobots = sensor.senseNearbyGameObjects(Robot.class);
                
                for(Robot robot : nearbyRobots) {
                    RobotInfo   robotInfo     = sensor.senseRobotInfo(robot);
                    MapLocation robotLocation = robotInfo.location;
                    Team        robotTeam     = robot.getTeam();
                    
                    if(robotTeam == knowledge.myTeam) {
                        switch(robotInfo.chassis) {
                        
                        }
                    }
                    else if(robotTeam == knowledge.enemyTeam) {
                        switch(robotInfo.chassis) {
                        
                        case BUILDING:
                            knowledge.locationMemory.setType(robotLocation, LocationType.ENEMY_BUILDING);
                            break;
                            
                        case LIGHT:
                        case MEDIUM:
                        case HEAVY:
                            knowledge.locationMemory.setType(robotLocation, LocationType.LAND);
                            break;
                            
                        case FLYING:
                            break;
                        }
                        
                        
                        knowledge.numberOfSensedEnemies++;                        
                    }
                    else {
                        // debris
                        
                        double nearbyDebrisHP = robotInfo.hitpoints;
                        if(nearbyDebrisHP < knowledge.weakestNearbyDebrisHP) {
                            knowledge.weakestNearbyDebrisHP       = nearbyDebrisHP;
                            knowledge.weakestNearbyDebrisLocation = robotLocation;
                        }
                        knowledge.locationMemory.setType(robotLocation, LocationType.DEBRIS);
                    }
                }
            }
        }
        catch(Exception e) {
            Logger.debug_printExceptionMessage(e);
        }
    }
    
}
