package team039.player.light;

import team039.common.Knowledge;
import team039.common.Prefab;
import team039.common.RobotState;
import team039.player.SpecificPlayer;
import team039.common.util.Logger;
import team039.handler.ComponentsHandler;
import team039.player.SpecificPlayer;

import battlecode.common.*;

public class StartingLightPlayer extends LightConstructorPlayer {
    
    private final RobotController   myRC;
    private final Knowledge         knowledge;
    private final ComponentsHandler compHandler;
        
    public StartingLightPlayer(RobotController rc,
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
        
        switch(knowledge.myState) {
        case SCOUTING_STARTING_LOCATION:
            scoutStartingLocation();
            break;
        case BUILDING_FIRST_MINE:
            buildFirstMine();
            break;
        case BUILDING_SECOND_MINE:
            buildSecondMine();
            break;
        }
    }
    
    @Override
    public void beginningStateSwitches() {
        if (knowledge.myState == RobotState.JUST_BUILT) {
            Logger.debug_print("I called JUST_BUILT at round " + knowledge.roundNum);
            knowledge.myState = RobotState.SCOUTING_STARTING_LOCATION;
        }

        if (knowledge.myState == RobotState.IDLE) {
//          compHandler.pathFinder.setNavigationAlgorithm(NavigationAlgorithm.BUG);
//            compHandler.pathFinder.setGoal(myRC.getLocation().add(Direction.SOUTH_EAST, 100));
//            compHandler.pathFinder.initiateBugNavigation();
//            compHandler.initiateBugNavigation(myRC.getLocation().add(Direction.SOUTH_EAST, 100));
            knowledge.myState = RobotState.EXPLORING;
        }
    }
    
    @Override
    public void doSpecificFirstRoundActions() {
        super.doSpecificFirstRoundActions();
    }
    
    public void scoutStartingLocation() {
        try {
            
            Direction desiredDirection = compHandler.senseStartingLocation();
            if(desiredDirection == Direction.OMNI) {
                knowledge.myState = RobotState.BUILDING_FIRST_MINE;
                buildRecyclerLocation = knowledge.startingUnminedMineLocations[0];
                compHandler.pathFinder.pauseExploration();
                compHandler.pathFinder.setGoal(buildRecyclerLocation);
                compHandler.pathFinder.initiateBugNavigation();
                buildFirstMine();
            }
            else if(desiredDirection != Direction.NONE) {
                compHandler.setDirection(desiredDirection);
            }
            else {
                Logger.debug_printHocho("first round sensing broke");
            }
        }
        catch(Exception e) {
            Logger.debug_printExceptionMessage(e);
        }
    }
    
    public void buildFirstMine() {
        if (compHandler.canBuildBuildingHere(buildRecyclerLocation) && myRC.getTeamResources() > Prefab.commRecycler.getTotalCost() + 1) {
            
            compHandler.build().buildChassisAndThenComponents(Prefab.commRecycler, buildRecyclerLocation, RobotState.BUILDING_SECOND_MINE);
        } else {
            try {
                compHandler.pathFinder.navigateToAdjacent();
            } catch (Exception e) {
                Logger.debug_printExceptionMessage(e);
            }
        }   
    }
    
    public void buildSecondMine() {
        buildRecyclerLocation = knowledge.startingUnminedMineLocations[1];
        compHandler.pathFinder.pauseExploration();
        compHandler.pathFinder.setGoal(buildRecyclerLocation);
        compHandler.pathFinder.initiateBugNavigation();
        knowledge.myState = RobotState.BUILDING_RECYCLER;
        buildRecycler();
    }
    
    @Override
    public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
        
        SpecificPlayer result = this;
        return result;
    }

}
