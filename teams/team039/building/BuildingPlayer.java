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

    private Robot buildTarget = null;
    private MapLocation buildLocation = null;
    private RobotLevel buildHeight = null;
    private BuildInstructions buildInstructions = null;
    private int buildStep = 0;


    public BuildingPlayer(RobotController rc,
                          Knowledge know,
                          ComponentsHandler compHand) {
        
        myRC = rc;
        knowledge = know;
        compHandler = compHand;
    }
    
    public void doSpecificActions() {

        build();
    }

    public void build()
    {
        //build actions
        if( !compHandler.builderActive() && buildTarget != null )
        {
            //skip components that this building can't build
            while (buildStep != buildInstructions.getNumSteps() && !compHandler.canIBuild(buildInstructions.getComponent(buildStep)))
            {
                buildStep++;
            }

            if(buildStep < buildInstructions.getNumSteps())
            {
                if(!compHandler.buildComponent(buildInstructions.getComponent(buildStep), buildLocation, buildHeight))
                {
                    abortBuilding();
                }
                else
                {
                    buildStep++;
                }
            }
            else
            {
                finishBuilding();
            }
        }
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

    /**
     * First looks for an empty space, then builds a chassis and consequently components there.
     * The building no longer needs to waste turns rotating, so this can be done in one turn
     * unless there is no space around the building.
     */
    public Boolean autoBuildRobot( BuildInstructions instructions )
    {
        if(myRC.getTeamResources() < instructions.getTotalCost()) return false;
        if( buildTarget==null )
        {
            Chassis chassis = instructions.getBaseChassis();
            MapLocation location = compHandler.getAdjacentEmptySpot(chassis.level);
            if(location==null)
            {
                System.out.print("no location found to build something");
               return false;
            } else {
                buildChassisAndThenComponents( instructions, location );
                return true;
            }
        }else {
            return false;
        }
    }

    /**
     * Builds a chassis in the designated location, then begins building components
     * on it with startBuildingComponents
     */
    private Boolean buildChassisAndThenComponents( BuildInstructions instructions, MapLocation location )
    {
        Chassis chassis = instructions.getBaseChassis();
        if(compHandler.buildChassis( chassis, location ))
        {
            startBuildingComponents( instructions, location, chassis.level );
            return true;
        }
        else
        return false;
    }

    /**
     * Initiates component build process by setting all of the required variables
     */
    private void startBuildingComponents( BuildInstructions instructions, MapLocation location, RobotLevel height )
    {
        //first, find the robot
        buildTarget = compHandler.senseARobot( location, height );
        buildStep = 0;
        buildInstructions = instructions;
        buildLocation = location;
        buildHeight = height;
    }

    /**
     * Called after a successful build process... anything to activate the just-built robot should go here
     */
    private void finishBuilding()
    {
        buildTarget = null;
        buildStep = 0;
        buildInstructions = null;
        buildLocation = null;
        buildHeight = null;
    }

    /**
     * Only called if something went wrong - erase all evidence of target
     */
    private void abortBuilding()
    {
        buildTarget = null;
        buildStep = 0;
        buildInstructions = null;
        buildLocation = null;
        buildHeight = null;
    }
}
