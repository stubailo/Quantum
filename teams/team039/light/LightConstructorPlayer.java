package team039.light;

import team039.common.*;
import team039.common.util.Logger;
import team039.handler.ComponentsHandler;
import battlecode.common.*;

public class LightConstructorPlayer extends LightPlayer {

    private final RobotController myRC;
    private final Knowledge knowledge;
    private final ComponentsHandler compHandler;

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

        Logger.debug_printAntony("testing....");
        switch (knowledge.myState) {
            case EXPLORING:
                explore();
                break;
            case BUILDING_RECYCLER:
                buildRecycler();
                break;
            case FLEEING:
                flee();
                break;
            case BUILDING:
                build();
                break;
            case JUST_BUILT:
                break;
            case IDLE:
                knowledge.myState = RobotState.EXPLORING;
                break;
            case BUILDING_FACTORY:
                buildFactory();
                break;
        }

        myRC.setIndicatorString(0, knowledge.myState.toString());
    }

    @Override
    public void beginningStateSwitches() {
        if (knowledge.myState == RobotState.JUST_BUILT) {
            Logger.debug_print("I called JUST_BUILT at round " + knowledge.roundNum);
            knowledge.myState = RobotState.IDLE;
        }

        if (knowledge.myState == RobotState.JUST_BUILT_FACTORY)
        {
            buildFactoryLocation = null;
            knowledge.myState = RobotState.IDLE;
        }

        if (knowledge.myState == RobotState.IDLE) {
//            compHandler.pathFinder.setNavigationAlgorithm(NavigationAlgorithm.BUG);
//            compHandler.pathFinder.setGoal(myRC.getLocation().add(Direction.SOUTH_EAST, 100));
//            compHandler.pathFinder.initiateBugNavigation();
//            compHandler.initiateBugNavigation(myRC.getLocation().add(Direction.SOUTH_EAST, 100));
            compHandler.pathFinder.setNavigationAlgorithm(NavigationAlgorithm.ZIG_ZAG);
            knowledge.myState = RobotState.EXPLORING;
        }
    }

    @Override
    public void initialize() {
        knowledge.amIALightConstructor = true;
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
    private MapLocation fleeTarget = null;

    public void flee() {
        try {
            compHandler.pathFinder.navigateBug();
        } catch (Exception e) {
        }
        ;
        if (myRC.getLocation().distanceSquaredTo(fleeTarget) < 3) {
            knowledge.myState = RobotState.EXPLORING;
        }
    }

    public void explore() {

            Mine[] sensedMines = compHandler.senseEmptyMines();
            //MapLocation nearestMine = compHandler.senseNearbyMines();

            // TODO: else statement here?

            if (sensedMines != null) {
            //if(nearestMine != null) {
                buildRecyclerLocation = sensedMines[0].getLocation();

//                compHandler.pathFinder.pauseExploration();
                compHandler.pathFinder.setGoal(buildRecyclerLocation);
                compHandler.pathFinder.initiateBugNavigation();
                knowledge.myState = RobotState.BUILDING_RECYCLER;
                buildRecycler();
            } else {
                if (knowledge.parentChanged) {
                    Direction newDirection = deflect();
                }

<<<<<<< HEAD
		        try {
		            compHandler.pathFinder.step();
		        } catch (Exception e) {
		            Logger.debug_printExceptionMessage(e);
		        }
=======
                try {
                    compHandler.pathFinder.zigZag();
                } catch (Exception e) {
                    Logger.debug_printExceptionMessage(e);
                }
>>>>>>> bf62e3e964c3e8901f06ecd19c648f0706c9cee4
            }

        

    }
    


    MapLocation buildRecyclerLocation;

    public void buildRecycler() {
        int distanceToLocation = knowledge.myLocation.distanceSquaredTo(buildRecyclerLocation);
        if(distanceToLocation == 0) {
            compHandler.pathFinder.navigateToAdjacent();
        } else if(distanceToLocation <= 2 && 
                !compHandler.canMove(knowledge.myLocation.directionTo(buildRecyclerLocation))) {
            knowledge.myState = RobotState.IDLE;
        }
        
        if (compHandler.canBuildBuildingHere(buildRecyclerLocation) && 
                myRC.getTeamResources() > Prefab.commRecycler.getTotalCost() + 1) {

            //changes to state BUILDING if the chassis is successfully built.
            compHandler.build().buildChassisAndThenComponents(Prefab.commRecycler, buildRecyclerLocation);
        } else {
            compHandler.pathFinder.navigateToAdjacent();
        }
    }

    MapLocation buildFactoryLocation;

    public void buildFactory() {

        if(buildFactoryLocation == null)
        {
            buildFactoryLocation = knowledge.myRecyclerNode.myLocation.add(Direction.NORTH, 3);
        }

    	int distanceToLocation = knowledge.myLocation.distanceSquaredTo(buildFactoryLocation);
    	if(distanceToLocation == 0) {
    		compHandler.pathFinder.navigateToAdjacent();
    	} else if(distanceToLocation <= 2 &&
        		!compHandler.canMove(knowledge.myLocation.directionTo(buildFactoryLocation))) {
        	knowledge.myState = RobotState.IDLE;
        }

        if (compHandler.canBuildBuildingHere(buildFactoryLocation) &&
        		myRC.getTeamResources() > Prefab.commRecycler.getTotalCost() + 1) {

        	//changes to state BUILDING if the chassis is successfully built.
            compHandler.build().buildChassisAndThenComponents(Prefab.commRecycler, buildFactoryLocation, RobotState.JUST_BUILT_FACTORY);
        } else {
            compHandler.pathFinder.navigateToAdjacent();
        }
    }
    
    private void build() {
        compHandler.build().step();
    }
}
