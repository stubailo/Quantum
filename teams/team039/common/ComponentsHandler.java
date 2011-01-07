package team039.common;

import java.util.Arrays;

import battlecode.common.*;

/**
 * Hopefully abstracts some component functionality away from tedium.
 * @author Jason
 *
 */
public class ComponentsHandler {

    private final RobotController myRC;
    private final Knowledge       knowledge;
    
    /*** Controllers ***/
    public         MovementController   myMC;
    public         SensorController[]   mySCs = new SensorController[4];
    public         BuilderController    myBC;
    public         WeaponController[]   myWCs = new WeaponController[18];
    public         BroadcastController  myCC;

    /*** Controller info ***/
    public         int                  numberOfSensors = 0;
    public         boolean              hasBuilder      = false;
    public         int                  numberOfWeapons = 0;
    public         boolean              hasComm         = false;
    
    /*** Navigation info ***/
    private        boolean              bugNavigating;
    private        boolean              tracking;
    private        Direction            trackingDirection;
    private        MapLocation          bugGoal;
    private        MapLocation          bugStart;
    private        MapLocation []       bugPrevLocations;
    private        Direction []         bugPrevDirections;
    private        int                  bugStep;
    
    
    public ComponentsHandler(RobotController rc, Knowledge know) {
        myRC = rc;
        knowledge = know;
    }
    
    
    
    /**
     * Returns an array of Robots that can be sensed
     * @return        an array of all nearby robots, empty if there are no robots or no sensors
     */
    public Robot[] getSensedRobots() {
        if(numberOfSensors == 0) return new Robot[0];
        else return mySCs[0].senseNearbyGameObjects(Robot.class);
    }
    
    
    
    /**
     * Looks at new components, sorts them, and returns the most interesting
     * 
     * @return        returns any component that might change the SpecificPlayer
     */
    // TODO: Allow returning of more than one component-type...
    public ComponentType updateComponents() {
        ComponentController[] newComps = myRC.newComponents();
        
        if(newComps.length == 0) return null;
        
        ComponentType result = null;
        
        for(ComponentController newComp: newComps) {
            switch(newComp.type()) { // TODO: handle IRON, JUMP, DROPSHIP, BUG, DUMMY
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
    
    public void initiateBugNavigation(MapLocation goal) {
    	bugNavigating = true;
    	tracking = false;
    	bugGoal = goal;
    	bugStart = myRC.getLocation();
		bugPrevLocations = new MapLocation [QuantumConstants.BUG_MEMORY_LENGTH];
		bugPrevDirections = new Direction [QuantumConstants.BUG_MEMORY_LENGTH];
		bugStep = 0;
    }
    
    public void navigateBug() throws GameActionException {
    	if(numberOfSensors == 0) 
    		return; 
    	if(myMC.isActive())
    		return;
    	
    	MapLocation location = myRC.getLocation();
    	Direction directionToGoal = location.directionTo(bugGoal);
    	Direction myDirection = myRC.getDirection();
    	
    	if(tracking){
    		
    	} else {
    		if(myMC.canMove(directionToGoal)) {
    			if(myDirection == directionToGoal) 
    				myMC.moveForward();
    			else if(myDirection == directionToGoal.opposite())
    				myMC.moveBackward();
    			else
    				myMC.setDirection(directionToGoal);
    			
    			return;
    		} else {
    			tracking = true;
    			bugPrevLocations[bugStep % QuantumConstants.BUG_MEMORY_LENGTH] = location;
    			
    			//TODO: This seems like information we could store somewhere permanent...
    			Direction [] open = findAdjacentOpenDirections(location);
    			//findBestMove(open);
    			
    			//bugPrevDirections[bugStep % QuantumConstants.BUG_MEMORY_LENGTH] = dir;
    		}
    	}

//    	SensorController sensor = mySCs[0];

    	
    	myRC.yield();
    	
	
    }
    
    private Direction [] findAdjacentOpenDirections(MapLocation location) throws GameActionException {
    	
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
