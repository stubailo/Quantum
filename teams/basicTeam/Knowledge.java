package basicTeam;

import battlecode.common.*;

public class Knowledge {
	
	private final RobotController myRC;
	
	// Constants
    public final   Team                team;
    public final   MapLocation         startLocation;
    
    // Round constants
    public         MapLocation         myLocation; 
    public         Direction           myDirection;
    public         double              previousFlux    = 0;
    public         double              deltaFlux       = 0;  
    public         int                 roundNum;
    
    // Locations of fixed objects
    public         MapLocation[]       mineLocations;
    public         MapLocation[]       minedOutLocations;
    public         MapLocation[]       debrisLocations;
    public         MapLocation[]       destroyedDebrisLocations;
    public         MapLocation[]       ourRecyclerLocations;
    public         MapLocation[]       ourFactoryLocations;
    public         MapLocation[]       ourArmoryLocations;
    public         MapLocation[]       ourBuildingLocations;
    public         MapLocation[]       theirRecyclerLocations;
    public         MapLocation[]       theirFactoryLocations;
    public         MapLocation[]       theirArmoryLocations;
    public         MapLocation[]       theirBuildingLocations;
	
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
