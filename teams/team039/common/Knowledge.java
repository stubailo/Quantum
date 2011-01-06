package team039.common;

import battlecode.common.*;

/**
 * Knowledge class keeps track of information to be used anywhere in relevant Robot's code,
 * i.e. in the RobotPlayer or in the SpecificPlayer or the ComponentHandler or anywhere else
 * it might end up being useful.
 * 
 * Similarly, methods that might want to be used anywhere should be put here (example: the
 * getExceptionMessage method).
 * @author Jason
 *
 */
public class Knowledge {
	
	private final RobotController myRC;
	
	/*** Constants ***/
    public  final  Team                myTeam;
    public  final  MapLocation         myStartLocation;
    public  final  int                 myRobotID;
    
    /*** Round constants ***/
    public         MapLocation         myLocation; 
    public         Direction           myDirection;
    public         double              previousFlux    = 0;
    public         double              deltaFlux       = 0;  
    public         int                 roundNum;
    
    /*** Locations of fixed objects ***/
    // I feel that they should be uncommented as they come into use.
    /***public         MapLocation[]       unminedMineLocations     = new MapLocation[100];
    public         MapLocation[]       ourMineLocations         = new MapLocation[100];
    public         MapLocation[]       theirMineLocations       = new MapLocation[100];
    public         MapLocation[]       minedOutLocations        = new MapLocation[100];
    public         MapLocation[]       debrisLocations          = new MapLocation[100];
    public         MapLocation[]       destroyedDebrisLocations = new MapLocation[100];
    public         MapLocation[]       ourRecyclerLocations     = new MapLocation[100];
    public         MapLocation[]       ourFactoryLocations      = new MapLocation[100];
    public         MapLocation[]       ourArmoryLocations       = new MapLocation[100];
    public         MapLocation[]       ourBuildingLocations     = new MapLocation[100];
    public         MapLocation[]       theirRecyclerLocations   = new MapLocation[100];
    public         MapLocation[]       theirFactoryLocations    = new MapLocation[100];
    public         MapLocation[]       theirArmoryLocations     = new MapLocation[100];
    public         MapLocation[]       theirBuildingLocations   = new MapLocation[100];***/
	
    
    
    /**
     * Sole constructor, initializes final variables.
     * 
     * @param	rc	RobotController associated with this RobotPlayer
     */
	public Knowledge (RobotController rc) {
		myRC            = rc;
		myTeam          = myRC.getTeam();
		myStartLocation = myRC.getLocation();
		myRobotID       = myRC.getRobot().getID();
	}
	
	
	
	/**
	 * Called at the beginning of each round, should update all relevant information.
	 */
	public void update () {
		// Determine delta flux
		// TODO: ignore delta's associated with building, etc.
		// TODO: recognize delta-delta flux that signifies loss of unit, creation of unit,
		//			creation of mine, etc.
    	deltaFlux = myRC.getTeamResources() - previousFlux;
        previousFlux = deltaFlux + previousFlux;
        
        roundNum = Clock.getRoundNum();
        myLocation = myRC.getLocation();
        myDirection = myRC.getDirection();
	}
	
	
	
	/**
	 * Generates string to be printed upon exception
	 * @return the string
	 */
	public String getExceptionMessage () {
		return ("Robot " + myRC.getRobot().getID() + 
                " during round " + Clock.getRoundNum() + 
                " caught exception:");
	}

}
