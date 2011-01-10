package team039.handler;

import java.util.Arrays;

import team039.common.BuildHandler;
import team039.common.Knowledge;
import team039.common.PathFinder;
import team039.common.QuantumConstants;
import team039.common.location.LocationType;
import team039.common.util.Logger;

import battlecode.common.*;

/**
 * Hopefully abstracts some component functionality away from tedium.
 * @author Jason
 *
 */
public class ComponentsHandler {
    
    private static final int BIG_INT = QuantumConstants.BIG_INT;

    private static final boolean ATTACK_DEBRIS = QuantumConstants.ATTACK_DEBRIS;
    private final RobotController myRC;
    private final Knowledge knowledge;
    private final BuildHandler buildHandler;
    
    /*** Controllers ***/
    private MovementController myMC;
    // Some code only uses one sensor... these should be expanded to use either all sensors
    // or the best sensor, depending on the application
    private SensorController[] mySensorSlots = new SensorController[4];
    private SensorController[] mySCs;
    private BuilderController myBC;
    private WeaponController[] myWeaponSlots = new WeaponController[18];
    private WeaponController[] myWCs;
    private BroadcastController myCC;
    
    /*** Handlers ***/
    private SensorHandler mySH;
    
    /*** Navigation ***/
    public         PathFinder           pathFinder;
    
    /*** Controller info ***/
    public int numberOfSensors;
    public boolean hasBuilder = false;
    public int numberOfWeapons;
    public boolean hasComm = false;

    public ComponentsHandler(RobotController rc, Knowledge know) {
        myRC = rc;
        knowledge = know;

        numberOfWeapons = 0;
        numberOfSensors = 0;

        pathFinder = new PathFinder(myRC, this, knowledge);
        buildHandler = new BuildHandler(rc, this, know);

    }

    /******************************* SENSOR METHODS *******************************/
    
    
    
    /**
     * Looks for nearby mines, but only if just turned or just moved.
     * @return      returns location of nearest unmined mine.
     */
    public MapLocation senseNearbyMines() {
        if(numberOfSensors == 0) return null;
        
        if(knowledge.justTurned || knowledge.justMoved) {
            Logger.debug_printHocho("sensing :)");
            // Bytecode count: at least 100 + 25 * (number of sensed mines)
            //            (plus another 25 * (number of sensed mines) if a robot is on it)
            try {
                MapLocation nearestMine                = null;
                int         nearestMineDistanceSquared = BIG_INT;
                
                for(SensorController sensor : mySCs) {
                    Mine[] nearbyMines = sensor.senseNearbyGameObjects(Mine.class);
                    
                    for(Mine nearbyMine : nearbyMines) {
                        MapLocation mineLocation = nearbyMine.getLocation();
                        Robot potentialBuilding = (Robot) sensor.senseObjectAtLocation(mineLocation, RobotLevel.ON_GROUND);
                        
                        if(potentialBuilding == null ||
                           sensor.senseRobotInfo(potentialBuilding).chassis != Chassis.BUILDING) {
                            
                            knowledge.locationMemory.setType(mineLocation,
                                                             LocationType.UNMINED_MINE,
                                                             knowledge.roundNum);
                            
                            int mineDistanceSquared = knowledge.myLocation.distanceSquaredTo(mineLocation);
                            if(mineDistanceSquared < nearestMineDistanceSquared) {
                                
                                nearestMineDistanceSquared = mineDistanceSquared;
                                nearestMine                = mineLocation;
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
                }
                return nearestMine;
            }
            catch(Exception e) {
                Logger.debug_printExceptionMessage(e);
                return null;
            }
        }
        Logger.debug_printHocho("not sensing...");
        return null;
    }

    /**
     * Caches information related to ground-traversability.
     * @param mySCs     robot's sensor controllers
     * @param location  location to be checked
     * @return          an integer, 0 = no, 1 = yes, -1 = dunno (will be convention)
     */
    /*    public int isGroundTraversable(MapLocation location) {

    LocationType locationType = knowledge.locationMemory.getType(location);
    if(locationType == null) {
    TerrainTile locationTile = myRC.senseTerrainTile(location);
    if(locationTile == null) return -1;
    switch(locationTile) {

    case LAND:
    knowledge.locationMemory.setType(location,
    LocationType.LAND,
    knowledge.roundNum);
    break;

    case OFF_MAP:
    knowledge.locationMemory.setType(location,
    LocationType.LAND,
    knowledge.roundNum);
    break;

    case VOID:
    knowledge.locationMemory.setType(location,
    LocationType.LAND,
    knowledge.roundNum);
    break;
    }
    }

    if(locationType == LocationType.VOID || locationType == LocationType.OFF_MAP)
    return 0;
    return 1;
    }
     */
    public Mine[] senseMines() {
        if (numberOfSensors == 0) {
            return null;
        }
        return mySCs[0].senseNearbyGameObjects(Mine.class);
    }

    public boolean canSenseEnemies() {
        if (numberOfSensors == 0) {
            return false;
        }
        Robot[] sensedRobots = mySCs[0].senseNearbyGameObjects(Robot.class);

        if( QuantumConstants.ATTACK_DEBRIS )
        {

            for (Robot sensedRobot : sensedRobots) {
                if (!sensedRobot.getTeam().equals(myRC.getTeam())) {
                    return true;
                }
            }
        } else {
            for (Robot sensedRobot : sensedRobots) {
                if (sensedRobot.getTeam().equals(myRC.getTeam().opponent())) {
                    return true;
                }
            }
        }

        return false;
    }

    /*
     * Gets a list of mines that don't have anything on them.
     */
    public Mine[] senseEmptyMines() {
        if (numberOfSensors == 0) {
            return null;
        }

        Mine[] sensedMines = senseMines();

        int len = sensedMines.length;

        boolean[] empty = new boolean[len];
        Mine[] output = new Mine[len];

        Mine sensedMine;
        int numEmptyMines = 0;

        for (int i = 0; i < len; i++) {
            sensedMine = sensedMines[i];

            try {
                GameObject obj = mySCs[0].senseObjectAtLocation(sensedMine.getLocation(), RobotLevel.ON_GROUND);

                if (obj == null) {
                    empty[i] = true;
                    output[numEmptyMines] = sensedMine;
                    numEmptyMines++;

                } else if (obj instanceof Robot) {
                    //not sure how to tell the difference between our team's recyclers and anything else.. :/
                } else {
                    empty[i] = false;
                }
            } catch (Exception e) {
                Logger.debug_printExceptionMessage(e);
                return null;
            }
        }

        if (numEmptyMines == 0) {
            return null;
        } else {
            return output;
        }
    }

    /**
     * Returns an array of Robots that can be sensed
     * @return        an array of all nearby robots, null if there are no robots or no sensors
     */
    public Robot[] getSensedRobots() {
        if (numberOfSensors == 0) {
            return null;
        }
        return mySCs[0].senseNearbyGameObjects(Robot.class);
    }

    /**
     * Returns the first empty location that is sensed at the appropriate height.
     * @return        One empty location
     */
    public MapLocation getAdjacentEmptySpot(RobotLevel height) {
        MapLocation myLocation = knowledge.myLocation;

        if (height.equals(RobotLevel.IN_AIR)) {
            if (senseARobot(myLocation, height) == null) {
                return myLocation;
            }
        }

        Direction direction = Direction.EAST;
        int counter = 0;

        while (senseARobot(myLocation.add(direction), height) != null && counter < 8) {
            counter++;
            direction = direction.rotateRight();
        }



        if (senseARobot(myLocation.add(direction), height) == null) {
            return myLocation.add(direction);
        } else {
            return null;
        }
    }

    /**
     * Returns the robot in a certain square and height
     * @return        the robot in that square at that height,
     *                null if there is anything else there
     */
    public Robot senseARobot(MapLocation location, RobotLevel height) {
        if (numberOfSensors == 0) {
            return null;
        }

        for (SensorController currSensor : mySCs) {
            try {
                if (currSensor.canSenseSquare(location)) {
                    GameObject obj = currSensor.senseObjectAtLocation(location, height);
                    if (obj instanceof Robot) {
                        return (Robot) obj;
                    } else {
                        return null;
                    }
                }
            } catch (Exception e) {
                Logger.debug_printExceptionMessage(e);
            }
        }

        return null;
    }

    /**
     * Designed by first-round use of recycler, finds lowest ID adjacent recycler.
     * 
     * (So that it can turn itself off if it's not the lowest)
     */
    public Boolean updateAlliedRecyclerInformation() {
        if (numberOfSensors == 0) {
            return false;
        }
        try {
            SensorController sensor = mySCs[0];
            int lowest = knowledge.myRobotID;
            MapLocation lowestLoc = knowledge.myLocation;
            for (Robot robot : sensor.senseNearbyGameObjects(Robot.class)) {
                RobotInfo robotInfo = sensor.senseRobotInfo(robot);
                Team team = robot.getTeam();
                if (team == knowledge.myTeam) {
                    ComponentType[] compTypes = robotInfo.components;
                    switch (robotInfo.chassis) {
                        case BUILDING:
                            for (ComponentType compType : compTypes) {
                                if (compType == ComponentType.RECYCLER) {
                                    int id = robot.getID();
                                    Logger.debug_print("found recycler with id "
                                            + String.valueOf(id));
                                    if (id < lowest) {
                                        lowest = id;
                                        lowestLoc = robotInfo.location;
                                    }
                                    break;
                                }
                            }
                    }
                } else {
                    knowledge.numberOfSensedEnemies += 1;
                }
            }
            knowledge.lowestAlliedRecyclerID = lowest;
            knowledge.lowestAlliedRecyclerIDLocation = lowestLoc;
            return true;
        } catch (Exception e) {
            Logger.debug_printExceptionMessage(e);
            return false;
        }
    }

    /**
     * Designed for use by starting light, records locations of recyclers and mines
     * @return      direction to turn in, OMNI if it sees everything, NONE upon error.
     */
    public Direction senseStartingLocation() {
        if (numberOfSensors == 0) {
            return Direction.NONE;
        }

        try {
            SensorController sensor = mySCs[0];

            Robot[] sensedRobots = sensor.senseNearbyGameObjects(Robot.class);
            Mine[] sensedMines = sensor.senseNearbyGameObjects(Mine.class);

            boolean overcorrect = false;
            GameObject correctingObject = knowledge.myRobot;
            
            int numberOfUnminedMines = sensedMines.length - sensedRobots.length;

            // If we see all robots/mines, we record them.
            // Otherwise, depending on number of seen robots/mines, we cleverly turn
            //    towards them.
            switch (sensedRobots.length) {

                case 0:
                    switch (numberOfUnminedMines) {

                        case 0:
                            overcorrect = true;
                            break;
                        case 1:
                            overcorrect = true;
                            correctingObject = sensedMines[0];
                            break;
                        case 2:
                            correctingObject = sensedMines[0];
                            break;
                    }
                    break;

                case 1:
                    correctingObject = sensedRobots[0];
                    if (numberOfUnminedMines == 0) {
                        overcorrect = true;
                    }

                case 2:
                    switch (numberOfUnminedMines) {

                        case 0:
                            correctingObject = sensedRobots[0];
                            break;
                        case 1:
                            correctingObject = sensedMines[0];
                            break;
                    }
                    break;
            }
            // We can sense some objects but not all... so we turn towards correcting object
            if (!correctingObject.equals(knowledge.myRobot)) {
                MapLocation sensedLoc = sensor.senseLocationOf(sensedRobots[0]);
                Direction sensedDir = knowledge.myLocation.directionTo(sensedLoc);

                // If overcorrect is true then we turn twice in the direction of the object
                if (overcorrect) {
                    if (knowledge.myDirection.rotateRight().equals(sensedDir)) {
                        return sensedDir.rotateRight();
                    } else {
                        return sensedDir.rotateLeft();
                    }
                }
                return sensedDir;
            }

            // If overcorrect is true we sense NO objects, so we might as well do a 180.
            if (overcorrect) {
                return knowledge.myDirection.opposite();
            }

            // Now we know we can sense everything, so we quickly jot it down.
            Robot recycler1 = sensedRobots[0], recycler2 = sensedRobots[1];
            MapLocation recycler1Location = sensor.senseLocationOf(recycler1);
            MapLocation recycler2Location = sensor.senseLocationOf(recycler2);
            if (recycler1.getID() < recycler2.getID()) {
                knowledge.startingTurnedOnRecyclerLocation = recycler1Location;
            } else {
                knowledge.startingTurnedOnRecyclerLocation = recycler2Location;
            }

            int unminedMinesFound = 0;
            int badSquares = 0;
            
            for(Mine mine : sensedMines) {
                MapLocation mineLoc = mine.getLocation();
                if(!(mineLoc.equals(recycler1Location) || mineLoc.equals(recycler2Location))) {
                    if(unminedMinesFound == 0) {
                        Direction addDirection = Direction.EAST;
                        int potBadSquares = 0;
                        for(int index = 0; index < 8; index ++) {
                            MapLocation addLoc = mineLoc.add(addDirection);
                            if((!addLoc.equals(recycler2Location)) &&
                               (!addLoc.equals(recycler1Location)) &&
                               (!addLoc.equals(knowledge.myLocation)) &&
                               sensor.canSenseSquare(addLoc)) {
                                TerrainTile tt = myRC.senseTerrainTile(addLoc);
                                if(tt.equals(TerrainTile.LAND)) {
                                    Robot potRob = (Robot) sensor.senseObjectAtLocation(addLoc, RobotLevel.ON_GROUND);
                                    if(potRob == null) {
                                        knowledge.startingUnminedMineLocations[0] = mineLoc;
                                        unminedMinesFound = 1;
                                    }
                                    else {
                                        potBadSquares++;
                                    }
                                }
                                else {
                                    potBadSquares++;
                                }
                            }
                            addDirection = addDirection.rotateLeft();
                        }
                        knowledge.startingUnminedMineLocations[1] = mineLoc;
                        unminedMinesFound = -1;
                        badSquares = potBadSquares;
                    }
                    else if(unminedMinesFound == 1) {
                        knowledge.startingUnminedMineLocations[1] = mineLoc;
                        break;
                    }
                    else if(unminedMinesFound == -1) {
                        Direction addDirection = Direction.EAST;
                        int potBadSquares = 0;
                        for(int index = 0; index < 8; index ++) {
                            MapLocation addLoc = mineLoc.add(addDirection);
                            if((!addLoc.equals(recycler2Location)) &&
                               (!addLoc.equals(recycler1Location)) &&
                               (!addLoc.equals(knowledge.myLocation)) &&
                               sensor.canSenseSquare(addLoc)) {
                                TerrainTile tt = myRC.senseTerrainTile(addLoc);
                                if(tt.equals(TerrainTile.LAND)) {
                                    Robot potRob = (Robot) sensor.senseObjectAtLocation(addLoc, RobotLevel.ON_GROUND);
                                    if(potRob == null) {
                                        knowledge.startingUnminedMineLocations[0] = mineLoc;
                                        unminedMinesFound = 1;
                                    }
                                    else {
                                        potBadSquares++;
                                    }
                                }
                                else {
                                    potBadSquares++;
                                }
                            }
                            addDirection = addDirection.rotateLeft();
                        }
                        if(potBadSquares > badSquares) {
                            knowledge.startingUnminedMineLocations[0] = knowledge.startingUnminedMineLocations[1];
                            knowledge.startingUnminedMineLocations[1] = mineLoc;
                        } else {
                            knowledge.startingUnminedMineLocations[0] = mineLoc;
                        }
                        break;
                    }
                }
            }

            return Direction.OMNI;
        } catch (Exception e) {
            Logger.debug_printExceptionMessage(e);
            return Direction.NONE;
        }
    }

    /***************************** MOVEMENT METHODS *******************************/

    public boolean setDirection(Direction direction) {
        try {
            if (myMC.isActive()) {
                return false;
            }
            myMC.setDirection(direction);
            return true;
        } catch (Exception e) {
            Logger.debug_printExceptionMessage(e);
            return false;
        }
    }
    
    public boolean motorActive () {
    	return myMC.isActive();
    }
    
    public boolean canMove(Direction dir) {
    	return myMC.canMove(dir);
    }
    
    public void moveForward() throws GameActionException {
    	if(!myMC.canMove(knowledge.myDirection))
    		return;
    	
    	myMC.moveForward();
    }
    
    public void moveBackward() throws GameActionException {
    	if(!myMC.canMove(knowledge.myDirection.opposite()))
    		return;
    	
    	myMC.moveBackward();
    }
    
    
    
    /***************************** BROADCAST METHODS *******************************/
    //this method is called by doCommonEndTurnActions() in RobotPlayer
    public boolean broadcast(Message composedMessage) {



        if (composedMessage == null || myCC == null) {
            return false;
        }

        try {
            if (myCC.isActive()) {
                return false;
            }
            myCC.broadcast(composedMessage);
            return true;
        } catch (Exception e) {
            Logger.debug_printExceptionMessage(e);
            return false;
        }
    }

    /***************************** BUILDING METHODS *******************************/
    //TODO:  really?
    public BuildHandler build() {
        return buildHandler;
    }

    public boolean canIBuild() {
        return !(myBC == null);
    }

    public boolean builderActive() {
        return myBC.isActive();
    }

    public boolean canIBuildThis(ComponentType component) {
        return BuildMappings.canBuild(myBC.type(), component);
    }

    public boolean canBuildBuildingHere(MapLocation location) {
        return myMC.canMove(myRC.getLocation().directionTo(location)) && myRC.getLocation().distanceSquaredTo(location) <= 2;
    }

    public boolean buildComponent(ComponentType component,
            MapLocation location,
            RobotLevel height) {
        if (myBC.isActive()) {
            return false;
        }

        try {
            myBC.build(component, location, height);
            return true;
        } catch (Exception e) {
            Logger.debug_printExceptionMessage(e);
            return false;
        }
    }

    public Boolean buildChassis(Chassis chassis, MapLocation location) {
        if (myBC.isActive()) {
            return false;
        }

        try {
            myBC.build(chassis, location);
            return true;
        } catch (Exception e) {
            Logger.debug_printExceptionMessage(e);
            return false;
        }
    }

    /******************************** ATTACKING METHODS *******************************/
    public boolean attackVisible() {

        if (numberOfSensors == 0 || numberOfWeapons == 0) {
            return false;
        }
        Robot[] sensedRobots = mySCs[0].senseNearbyGameObjects(Robot.class);

        int weaponsFired = 0;

        Robot foundEnemy = null;
        Robot foundDebris = null;

        Logger.debug_print("see " + sensedRobots.length + " robots");

        for (Robot sensedRobot : sensedRobots) {
            if( sensedRobot.getTeam()==myRC.getRobot().getTeam().opponent() )
            {
                foundEnemy = sensedRobot;
            } else if ( QuantumConstants.ATTACK_DEBRIS && sensedRobot.getTeam() == Team.NEUTRAL )
            {
                foundDebris = sensedRobot;
                Logger.debug_print("found debris");
            }
        }

        if(sensedRobots.length == 0)
        {
            return false;

        }
        
        for (int index = 0; index < numberOfWeapons; index++) {
            WeaponController weapon = myWCs[index];
            if (weapon.isActive()) {
            } else {

                Robot robotToShoot = (foundEnemy==null)?foundDebris:foundEnemy;

                try {
                    MapLocation enemyLocation = mySCs[0].senseLocationOf(robotToShoot);

                    if (weapon.withinRange( enemyLocation ) ) {
                        weapon.attackSquare( enemyLocation , robotToShoot.getRobotLevel() );
                        weaponsFired++;
                    } else {
                    }
                } catch (Exception e) {
                    Logger.debug_printExceptionMessage(e);
                    return false;
                }
            }
        }

        return weaponsFired > 0;
    }

    public boolean hasWeapons() {
        Logger.debug_print( "teehee weapons: " + numberOfWeapons );
        return numberOfWeapons > 0;
    }

    /******************************** OTHER METHODS *******************************/
    /**
     * Looks at new components, sorts them, and returns them
     * 
     * @return        returns new components
     */
    // TODO: Allow returning of more than one component-type...
    public ComponentType[] updateComponents() {
        ComponentController[] newComps = myRC.newComponents();

        int length = newComps.length;

        if (length == 0) {
            return null;
        }
        ComponentType[] newCompTypes = new ComponentType[length];

        int index = 0;

        for ( ComponentController newComp : newComps ) {
            ComponentType newCompType = newComp.type();
            newCompTypes[index] = newCompType;
            index++;

            

            switch (newCompType) { // TODO: handle IRON, JUMP, DROPSHIP, BUG, DUMMY
                // TODO: handle passives

                case BUILDING_MOTOR:
                case SMALL_MOTOR:
                case MEDIUM_MOTOR:
                case LARGE_MOTOR:
                case FLYING_MOTOR:
                    myMC = (MovementController) newComp;
                    break;

                case ANTENNA:
                case DISH:
                case NETWORK:
                    myCC = (BroadcastController) newComp;
                    hasComm = true;
                    break;

                case SATELLITE:
                case TELESCOPE:
                case SIGHT:
                case RADAR:
                case BUILDING_SENSOR:
                    //mySH.addSensor((SensorController) newComp);
                    mySensorSlots[numberOfSensors] = (SensorController) newComp;
                    numberOfSensors += 1;
                    mySCs = new SensorController[numberOfSensors];
                    System.arraycopy(mySensorSlots, 0, mySCs, 0, numberOfSensors);
                    break;

                case BLASTER:
                case SMG:
                case RAILGUN:
                case HAMMER:
                case BEAM:
                case MEDIC: // Should MEDIC be under weapons?
                    
                    myWeaponSlots[numberOfWeapons] = (WeaponController) newComp;
                    numberOfWeapons += 1;
                    myWCs = new WeaponController[numberOfWeapons];
                    System.arraycopy(myWeaponSlots, 0, myWCs, 0, numberOfWeapons);
                    break;

                case CONSTRUCTOR:
                case RECYCLER:
                case FACTORY:
                case ARMORY:
                    myBC = (BuilderController) newComp;
                    break;
            }
        }

        
        return newCompTypes;
    }
}
