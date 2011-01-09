package team039.light;

import team039.common.*;
import battlecode.common.*;

public class LightConstructorPlayer extends LightPlayer {
    
    private final RobotController   myRC;
    private final Knowledge         knowledge;
    private final ComponentsHandler compHandler;
    
    private MapLocation goal;
    private boolean atGoal = true;
    private int goalSqDist;
    
        
    public LightConstructorPlayer(RobotController rc,
                                     Knowledge know,
                                     ComponentsHandler compHand) {
        
        super(rc, know, compHand);
        myRC        = rc;
        knowledge   = know;
        compHandler = compHand;
    }
    
    @Override
    public void doSpecificActions() {
        super.doSpecificActions();

        if( knowledge.myState == RobotState.EXPLORING )
        {
            explore();
        }

        if( knowledge.myState == RobotState.BUILDING_RECYCLER )
        {
            buildRecycler();
        }

        if( knowledge.myState == RobotState.BUILDING )
        {
            build();
        }
    }

    @Override
    public void beginningStateSwitches()
    {
        if( knowledge.myState == RobotState.JUST_BUILT )
        {
            System.out.println( "I called JUST_BUILT at round " + knowledge.roundNum  );
            knowledge.myState = RobotState.IDLE;
        }

        if( knowledge.myState == RobotState.IDLE )
        {
            knowledge.myState = RobotState.EXPLORING;
        }
    }

    @Override
    public void doSpecificFirstRoundActions() {
        super.doSpecificFirstRoundActions();
        compHandler.initiateBugNavigation(myRC.getLocation().add(Direction.SOUTH, 13));
    }

    @Override
    public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
        SpecificPlayer result = this;
        return result;
    }

    public void explore()
    {
        Mine[] sensedMines = compHandler.senseEmptyMines();

        if( compHandler.canSenseEnemies() )
        {
            System.out.println( "I see an enemy!" );
        }

        if( sensedMines != null )
        {
            buildRecyclerLocation = sensedMines[0].getLocation();
            compHandler.initiateBugNavigation( buildRecyclerLocation );
            knowledge.myState = RobotState.BUILDING_RECYCLER;
        }

        try {
        	compHandler.navigateBug();
        } catch(Exception e) {
        	System.out.println("Robot " + myRC.getRobot().getID() +
                    " during round " + Clock.getRoundNum() +
                    " caught exception:");
            e.printStackTrace();
        }
    }

    MapLocation buildRecyclerLocation;
    public void buildRecycler()
    {
        if(compHandler.canBuildBuildingHere( buildRecyclerLocation )  && myRC.getTeamResources() > Prefab.commRecycler.getTotalCost() + 150)
        {
            System.out.println("Trying to build here...");
            buildChassisAndThenComponents( Prefab.commRecycler, buildRecyclerLocation );
        }
        try {
        	compHandler.navigateToAdjacent();
        } catch(Exception e) {
        	System.out.println("Robot " + myRC.getRobot().getID() +
                    " during round " + Clock.getRoundNum() +
                    " caught exception:");
            e.printStackTrace();
        }
    }

    private Robot buildTarget = null;
    private MapLocation buildLocation = null;
    private RobotLevel buildHeight = null;
    private BuildInstructions buildInstructions = null;
    private int buildStep = 0;
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
    public void startBuildingComponents( BuildInstructions instructions, MapLocation location, RobotLevel height )
    {
        //first, find the robot
        buildTarget = compHandler.senseARobot( location, height );
        buildStep = 0;
        buildInstructions = instructions;
        buildLocation = location;
        buildHeight = height;

        knowledge.myState = RobotState.BUILDING;
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

        knowledge.myState = RobotState.IDLE;
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

        knowledge.myState = RobotState.IDLE;
    }

}
