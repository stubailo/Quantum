package team039.common;

import java.util.Arrays;

//import team039.common.location.LocationType;
import battlecode.common.*;

/**
 * Hopefully abstracts some component functionality away from tedium.
 * @author Jason
 *
 */
public class ComponentsHandler {

    private final RobotController myRC;
    private final Knowledge knowledge;
    private final BuildHandler buildHandler;
    /*** Controllers ***/
    private MovementController myMC;
    // Some code only uses one sensor... these should be expanded to use either all sensors
    // or the best sensor, depending on the application
    private SensorController[] mySCs = new SensorController[4];
    private BuilderController myBC;
    private WeaponController[] myWCs = new WeaponController[18];
    private BroadcastController myCC;
    /*** Controller info ***/
    public         int                  numberOfSensors = 0;
    public         boolean              hasBuilder      = false;
    public         int                  numberOfWeapons = 0;
    public         boolean              hasComm         = false;
    
    /*** Navigation info ***/
    private        boolean              bugNavigating;
    private        boolean              tracking;
    private        boolean              trackingCW;
    private        boolean              moveOnNext;
    private        Direction            trackingDirection;
    private        Direction            trackingRefDirection;
    private        MapLocation          bugGoal;
    private        MapLocation          bugStart;
    private        MapLocation []       bugPrevLocations;
    private        Direction []         bugPrevDirections;
    private        int                  bugStep;
   
    public ComponentsHandler(RobotController rc, Knowledge know) {
        myRC = rc;
        knowledge = know;
        
        buildHandler = new BuildHandler( rc, this, know );
    }

    /******************************* SENSOR METHODS *******************************/
    
    
    
    public void sense() {
        if(numberOfSensors == 0) return;
        
        if(knowledge.justTurned) {
            for(SensorController sensor : mySCs) {
                switch(sensor.type()) {
                
                }
            }
        }
    }
    
    /**
     * Optimizes void-checking.  Considers off_map to be void.
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

    public Mine[] senseMines()
    {
        if (numberOfSensors == 0) {
            return null;
        }
        return mySCs[0].senseNearbyGameObjects(Mine.class);
    }

    public boolean canSenseEnemies()
    {
        if (numberOfSensors == 0) {
            return false;
        }
        Robot[] sensedRobots = mySCs[0].senseNearbyGameObjects(Robot.class);

        for( Robot sensedRobot : sensedRobots )
        {
            if( !sensedRobot.getTeam().equals(myRC.getTeam()) )
                return true;
        }

        return false;
    }

    /*
     * Gets a list of mines that don't have anything on them.
     */

    public Mine[] senseEmptyMines()
    {
        if (numberOfSensors == 0) {
            return null;
        }

        Mine[] sensedMines = senseMines();

        int len = sensedMines.length;

        boolean[] empty = new boolean[ len ];
        Mine [] output = new Mine[len];

        Mine sensedMine;
        int numEmptyMines = 0;

        for( int i=0; i < len; i++ )
        {
            sensedMine = sensedMines[i];

            try {
            GameObject obj = mySCs[0].senseObjectAtLocation( sensedMine.getLocation() , RobotLevel.ON_GROUND );

            if( obj == null )
            {
                empty[i] = true;
                output[numEmptyMines] = sensedMine;
                numEmptyMines ++;

            } else if ( obj instanceof Robot )
            {
                //not sure how to tell the difference between our team's recyclers and anything else.. :/
            } else {
                empty[i] = false;
            }
            } catch (Exception e) {
                knowledge.printExceptionMessage(e);
                return null;
            }
        }

        if( numEmptyMines == 0 )
        {
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

        while (senseARobot(myLocation.add(direction), height) != null && counter<8) {
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
                knowledge.printExceptionMessage(e);
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
                                    System.out.println("found recycler with id "
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
            knowledge.printExceptionMessage(e);
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

            // If we see all robots/mines, we record them.
            // Otherwise, depending on number of seen robots/mines, we cleverly turn
            //    towards them.
            switch (sensedRobots.length) {

                case 0:
                    switch (sensedMines.length) {

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
                    if (sensedMines.length == 0) {
                        overcorrect = true;
                    }

                case 2:
                    switch (sensedMines.length) {

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
            if (recycler1.getID() < recycler2.getID()) {
                knowledge.startingTurnedOnRecyclerLocation =
                        sensor.senseLocationOf(recycler1);
            } else {
                knowledge.startingTurnedOnRecyclerLocation =
                        sensor.senseLocationOf(recycler2);
            }

            MapLocation mine1Loc = sensor.senseLocationOf(sensedMines[0]),
                    mine2Loc = sensor.senseLocationOf(sensedMines[1]);

            if (knowledge.myLocation.distanceSquaredTo(mine1Loc)
                    < knowledge.myLocation.distanceSquaredTo(mine2Loc)) {
                knowledge.startingUnminedMineLocations[0] = mine1Loc;
                knowledge.startingUnminedMineLocations[1] = mine2Loc;
            } else {
                knowledge.startingUnminedMineLocations[0] = mine2Loc;
                knowledge.startingUnminedMineLocations[1] = mine1Loc;
            }
            return Direction.OMNI;
        } catch (Exception e) {
            knowledge.printExceptionMessage(e);
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
            knowledge.printExceptionMessage(e);
            return false;
        }
    }
    
    public void initiateBugNavigation(MapLocation goal) {
    	bugNavigating = true;
    	tracking = false;
    	moveOnNext = false;
    	bugGoal = goal;
    	bugStart = myRC.getLocation();
		bugPrevLocations = new MapLocation [QuantumConstants.BUG_MEMORY_LENGTH];
		bugPrevDirections = new Direction [QuantumConstants.BUG_MEMORY_LENGTH];
		bugStep = 0;
    }
    
    public void navigateBug() throws GameActionException {
//    	if(numberOfSensors == 0) 
//    		return; 
    	//do nothing if the motor is active or you are not navigating
    	if(!bugNavigating)
    		return;
    	if(myMC.isActive())
    		return;
 	
    	MapLocation location = myRC.getLocation();
    	Direction directionToGoal = location.directionTo(bugGoal);
    	Direction myDirection = myRC.getDirection();
    	int bugPos = bugStep % QuantumConstants.BUG_MEMORY_LENGTH;
    	
    	//stop navigating once you have reached your goal
    	if(location == bugGoal) {
    		bugNavigating = false;
    		return;
    	}
    	
    	myRC.yield();
    	
    	//check to see if the robot is tracking
    	if(tracking){
    		//set the initial reference direction to begin testing from.
    		Direction testDirection = trackingRefDirection;
            
    		//avoids moving back and forth, which can arise in some cases.
    		if(trackingDirection == trackingRefDirection.opposite()){
    			if(trackingCW)
    				testDirection = testDirection.rotateLeft();
    			else
    				testDirection = testDirection.rotateRight();
    		}
    		
    		//check directions beginning with the reference direction, in the order depending on 
    		//if you are tracking clockwise or counterclockwise
    		boolean pathBlocked = true;
    		while(pathBlocked) {
    			if(myMC.canMove(testDirection)){
    				trackingDirection = testDirection;
    				pathBlocked = false;
    			}
    			
    			//increment direction
    			if(trackingCW)
    				testDirection = testDirection.rotateLeft();
    			else
    				testDirection = testDirection.rotateRight();
    			
    			//stop checking if you are surrounded
    			if(testDirection == trackingRefDirection)
    				break;
    		}
    		
    		//move or change direction as necessary
    		if(myDirection == trackingDirection)
    			myMC.moveForward();
    		else if(myDirection == trackingDirection.opposite())
    			myMC.moveBackward();
    		else
    			myMC.setDirection(trackingDirection);
  
    		//check if you are done tracking
    		if(trackingDirection == trackingRefDirection) {
    			tracking = false;
    			moveOnNext = true;
    		}	
    	} else if(moveOnNext) {
    		if(myDirection == trackingDirection) {
    			myMC.moveForward();
    			moveOnNext = false;
    		} else if(myDirection == trackingDirection.opposite()) {
    			myMC.moveBackward();
    			moveOnNext = false;
    		} else
    			myMC.setDirection(trackingDirection);
    		
    	} else {
    		//if you are not tracking, check if you can move toward the goal
    		if(myMC.canMove(directionToGoal)) {
    			if(myDirection == directionToGoal) 
    				myMC.moveForward();
    			else if(myDirection == directionToGoal.opposite())
    				myMC.moveBackward();
    			else
    				myMC.setDirection(directionToGoal);
    			
    			return;
    		} else {
    			//the path is blocked, so begin tracking.
    			tracking = true;
    			bugPos = (bugPos + 1) % QuantumConstants.BUG_MEMORY_LENGTH;
    			bugPrevLocations[bugPos] = location;
    			trackingRefDirection = directionToGoal;
    		
    			//determine if you should track around the obstacle clockwise or counterclockwise
    			Direction ccwDir = directionToGoal;
    			Direction cwDir = directionToGoal;
    			boolean searching = true;
    			while(searching) {
    				ccwDir = ccwDir.rotateRight();
    				cwDir = cwDir.rotateLeft();
    				if(myMC.canMove(ccwDir)) {
    					bugPrevDirections[bugPos] = ccwDir;
    					trackingCW = false;
    					searching = false;
    					if(myMC.canMove(cwDir) && location.add(cwDir).distanceSquaredTo(bugGoal) 
    							< location.add(ccwDir).distanceSquaredTo(bugGoal)) {
    						bugPrevDirections[bugPos] = cwDir;
    						trackingCW = true;
    					}
    				} else if(myMC.canMove(cwDir)) {
    					bugPrevDirections[bugPos] = cwDir;
    					trackingCW = true;
    					searching = false;
    				}
    					
    				//stop searching if you are surrounded
    				if(ccwDir == cwDir) {
    					bugPrevDirections[bugPos] = ccwDir;
    					break;
    				}
    			}
    			
    			trackingDirection = bugPrevDirections[bugPos];
    			//myMC.setDirection(trackingDirection);
    			
    		}
    	}
    	
    	//debug indicator strings
    	myRC.setIndicatorString(0, "reference " + trackingRefDirection +
    			" tracking " + trackingDirection);
    	myRC.setIndicatorString(1,"tracking: " + tracking + 
    			"; orientation is clockwise: " + trackingCW);
    	
	
    }

    public void navigateToAdjacent() throws GameActionException {
    	//must initiate bug navigation before calling.
    	if(!bugNavigating)
    		return;
    	
    	MapLocation location = myRC.getLocation();
    	
    	if(location.distanceSquaredTo(bugGoal) <= 2) {
    		if(myMC.isActive()) {
    			return;
    		} else {
    			bugNavigating = false;
    			myMC.setDirection(location.directionTo(bugGoal));
    			return;
    		}
    	}
    		
    	navigateBug();
    }
    /***************************** BROADCAST METHODS *******************************/

    //this method is called by doCommonEndTurnActions() in RobotPlayer
    public boolean broadcast(Message composedMessage) {

        

        if( composedMessage == null || myCC == null )
        {
            return false;
        }

        try {
            if (myCC.isActive()) {
                return false;
            }
            myCC.broadcast(composedMessage);
            return true;
        } catch (Exception e) {
            knowledge.printExceptionMessage(e);
            return false;
        }
    }

    /***************************** BUILDING METHODS *******************************/

    public void buildStep()
    {
        buildHandler.build();
    }

    public boolean autoBuildRobot( BuildInstructions instructions )
    {
        return buildHandler.autoBuildRobot(instructions);
    }

    public boolean buildAtLocation( BuildInstructions instructions, MapLocation location )
    {
        return buildHandler.buildChassisAndThenComponents( instructions, location  );
    }

    public void buildComponents( BuildInstructions instructions, MapLocation location )
    {
        buildHandler.startBuildingComponents(instructions, location, instructions.getBaseChassis().level);
    }

    public void abortBuilding()
    {
        buildHandler.abortBuilding();
    }

    public boolean builderActive() {
        return myBC.isActive();
    }

    public boolean canIBuild(ComponentType component) {
        return BuildMappings.canBuild(myBC.type(), component);
    }

    public boolean canBuildBuildingHere( MapLocation location )
    {
        return myMC.canMove( myRC.getLocation().directionTo(location) );
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
            knowledge.printExceptionMessage(e);
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
            knowledge.printExceptionMessage(e);
            return false;
        }
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

        for (int index = 0; index < length; index++) {
            ComponentController newComp = newComps[index];
            ComponentType newCompType = newComp.type();
            newCompTypes[index] = newCompType;
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
                    mySCs[numberOfSensors] = (SensorController) newComp;
                    numberOfSensors += 1;
                    break;

                case BLASTER:
                case SMG:
                case RAILGUN:
                case HAMMER:
                case BEAM:
                case MEDIC: // Should MEDIC be under weapons?
                    myWCs[numberOfWeapons] = (WeaponController) newComp;
                    numberOfWeapons += 1;
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

    /**
     * Looks at new components, sorts them, and returns the most interesting
     * 
     * @return        returns any component that might change the SpecificPlayer
     */
    public ComponentType oldUpdateComponents() {
        ComponentController[] newComps = myRC.newComponents();

        if (newComps.length == 0) {
            return null;
        }

        ComponentType result = null;

        for (ComponentController newComp : newComps) {
            switch (newComp.type()) {

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
                    mySCs[numberOfSensors] = (SensorController) newComp;
                    numberOfSensors += 1;
                    break;

                case BLASTER:
                    result = ComponentType.BLASTER;
                case SMG:
                case RAILGUN:
                case HAMMER:
                case BEAM:
                case MEDIC: // Should MEDIC be under weapons?
                    myWCs[numberOfWeapons] = (WeaponController) newComp;
                    numberOfWeapons += 1;
                    break;

                case CONSTRUCTOR:
                    result = ComponentType.CONSTRUCTOR;
                    myBC = (BuilderController) newComp;
                    break;

                case RECYCLER:
                    result = ComponentType.RECYCLER;
                    myBC = (BuilderController) newComp;
                    break;

                case FACTORY:
                    result = ComponentType.FACTORY;
                    myBC = (BuilderController) newComp;
                    break;

                case ARMORY:
                    result = ComponentType.ARMORY;
                    myBC = (BuilderController) newComp;
                    break;
            }
        }
        return result;
    }
    

    /**
     * This doesn't really seem useful.  Just keeping it around until we have a better 
     * sensor method.  
     * @param location
     * @throws GameActionException
     */
    
    private Direction [] findAdjacentOpenDirections(MapLocation location) throws GameActionException {
    	
    	/** easier to just use the canMove() method.  */
    	SensorController sensor = mySCs[0];
    	Direction [] open = new Direction [8];
    	boolean [] blocked = new boolean [8];
    	Arrays.fill(blocked, false);
    	int i = 0;

    	//sense nearby robots
    	Robot [] nearBots = getSensedRobots();

    	//find adjacent robots and record their direction as blocked
    	RobotInfo nearBotInfo;
    	for(Robot r : nearBots){
    		if(r.getRobotLevel() == RobotLevel.ON_GROUND) {
	    		nearBotInfo = sensor.senseRobotInfo(r);
	    		if(location.distanceSquaredTo(nearBotInfo.location) <= 2) {
	    			blocked[location.directionTo(nearBotInfo.location).ordinal()] = true;
	    		}
    		}
    	}
    	  	
    	//sense the adjacent terrain tiles, and record the open ones.
    	for(Direction d : Direction.values()){
    		if(!blocked[d.ordinal()] && myRC.senseTerrainTile(location.add(d)) == TerrainTile.LAND) {
    			open[i] = d;
    			i++;
    		}
    	}  
    	   	
    	// TODO:  not sure if this is the best way to trim the array...
    	return (Direction [])Arrays.asList(open).subList(0, i-1).toArray();
    }
    
}
