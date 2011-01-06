package team039.building;

import team039.building.recycler.RecyclerPlayer;
import team039.common.ComponentsHandler;
import team039.common.Knowledge;
import team039.common.SpecificPlayer;
import team039.common.BuildInstructions;
import battlecode.common.*;

public class BuildingPlayer implements SpecificPlayer {
    
    private final RobotController   myRC;
    private final Knowledge         knowledge;
    private final ComponentsHandler compHandler;
        
    public BuildingPlayer(RobotController rc,
                          Knowledge know,
                          ComponentsHandler compHand) {
        
        myRC = rc;
        knowledge = know;
        compHandler = compHand;
    }
    
    public void doSpecificActions() {
        
    }
    
    public void doSpecificFirstRoundActions() {
        
    }
    
    public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
        SpecificPlayer result = this;
        
        switch(compType) {
        case RECYCLER:
            result = new RecyclerPlayer(myRC, knowledge, compHandler);
            break;
        }
        return result;
    }

    public void startBuildingComponents( BuildInstructions instructions, MapLocation location, RobotLevel height )
    {
        //first, find the robot ID to make sure you're building on the right robot


    }

}
