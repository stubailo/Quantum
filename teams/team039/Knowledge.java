package team039;

import battlecode.common.*;

public class Knowledge {
	
	private final RobotController myRC;
	
	// Constants
    public  final  Team                team;
    public  final  MapLocation         startLocation;
    
    // Round constants
    public         MapLocation         myLocation; 
    public         Direction           myDirection;
    public         double              previousFlux    = 0;
    public         double              deltaFlux       = 0;  
    public         int                 roundNum;
    
    // Locations of fixed objects
    public         MapLocation[]       unminedMineLocations     = new MapLocation[100];
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
    public         MapLocation[]       theirBuildingLocations   = new MapLocation[100];
	
	public Knowledge (RobotController rc) {
		myRC = rc;
		team = myRC.getTeam();
		startLocation = myRC.getLocation();
	}
	
	public void update () {
		// Determine delta flux
    	deltaFlux = myRC.getTeamResources() - previousFlux;
        previousFlux = deltaFlux + previousFlux;
        
        roundNum = Clock.getRoundNum();
        myLocation = myRC.getLocation();
        myDirection = myRC.getDirection();
	}

}
