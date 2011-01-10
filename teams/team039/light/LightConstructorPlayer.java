package team039.light;

import team039.common.*;
import battlecode.common.*;

public class LightConstructorPlayer extends LightPlayer {

    private final RobotController myRC;
    private final Knowledge knowledge;
    private final ComponentsHandler compHandler;
    private MapLocation goal;
    private boolean atGoal = true;
    private int goalSqDist;

    public LightConstructorPlayer(RobotController rc,
            Knowledge know,
            ComponentsHandler compHand) {

        super(rc, know, compHand);
        myRC = rc;
        knowledge = know;
        compHandler = compHand;
    }

    @Override
    public void doSpecificActions() {
        super.doSpecificActions();

        switch (knowledge.myState) {
            case EXPLORING:
                explore();
                break;
            case BUILDING_RECYCLER:
                buildRecycler();
                break;
            case ATTACKING:
                attack();
                break;
            case BUILDING:
                compHandler.build().step();
                break;
            case JUST_BUILT:
                break;
            case IDLE:
            	knowledge.myState = RobotState.EXPLORING;
                break;
        }
        
        myRC.setIndicatorString(2, knowledge.myState.toString());
    }

    @Override
    public void beginningStateSwitches() {
        if (knowledge.myState == RobotState.JUST_BUILT) {
//            System.out.println("I called JUST_BUILT at round " + knowledge.roundNum);
            knowledge.myState = RobotState.IDLE;
        }

        if (knowledge.myState == RobotState.IDLE) {
//        	compHandler.pathFinder.setNavigationAlgorithm(NavigationAlgorithm.BUG);
//            compHandler.pathFinder.setGoal(myRC.getLocation().add(Direction.SOUTH_EAST, 100));
//            compHandler.pathFinder.initiateBugNavigation();
//            compHandler.initiateBugNavigation(myRC.getLocation().add(Direction.SOUTH_EAST, 100));
            knowledge.myState = RobotState.EXPLORING;
        }
    }

    @Override
    public void doSpecificFirstRoundActions() {
        super.doSpecificFirstRoundActions();
//        compHandler.pathFinder.setNavigationAlgorithm(NavigationAlgorithm.BUG);
//        compHandler.pathFinder.setGoal(myRC.getLocation().add(Direction.SOUTH, 13));
//        compHandler.pathFinder.initiateBugNavigation();
    }

    @Override
    public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
        SpecificPlayer result = this;
        return result;
    }

    public void attack()
    {
        
        if (compHandler.attackVisible()) {
                System.out.println("Attacking!");
        } else {
            System.out.println("Can't see anyone.");
            knowledge.myState = RobotState.IDLE;
        }
    }

    public void explore() {
        if (compHandler.canIBuild()) {
            Mine[] sensedMines = compHandler.senseEmptyMines();

            if (compHandler.canSenseEnemies() && compHandler.hasWeapons()) {
                knowledge.myState = RobotState.ATTACKING;
                attack();
            }

            // TODO: else statement here?

            if (sensedMines != null) {
                buildRecyclerLocation = sensedMines[0].getLocation();
                compHandler.pathFinder.pauseExploration();
                compHandler.pathFinder.setGoal(buildRecyclerLocation);
                compHandler.pathFinder.initiateBugNavigation();
                knowledge.myState = RobotState.BUILDING_RECYCLER;
            }
        } else {

	        try {
	            compHandler.pathFinder.explore();
	        } catch (Exception e) {
	            System.out.println("Robot " + myRC.getRobot().getID()
	                    + " during round " + Clock.getRoundNum()
	                    + " caught exception:");
	            e.printStackTrace();
	        }
        } 
    }
    
    MapLocation buildRecyclerLocation;
    public void buildRecycler() {
        if (compHandler.canBuildBuildingHere(buildRecyclerLocation) && myRC.getTeamResources() > Prefab.commRecycler.getTotalCost() + 150) {
            
            compHandler.build().buildChassisAndThenComponents(Prefab.commRecycler, buildRecyclerLocation);
        } else {
            try {
                compHandler.pathFinder.navigateToAdjacent();
            } catch (Exception e) {
                System.out.println("Robot " + myRC.getRobot().getID()
                        + " during round " + Clock.getRoundNum()
                        + " caught exception:");
                e.printStackTrace();
            }
        }
    }
}
