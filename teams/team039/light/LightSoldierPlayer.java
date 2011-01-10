package team039.light;

import team039.common.*;
import battlecode.common.*;

public class LightSoldierPlayer extends LightPlayer {
    
    private final RobotController   myRC;
    private final Knowledge         knowledge;
    private final ComponentsHandler compHandler;
        
    public LightSoldierPlayer(RobotController rc,
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

        switch (knowledge.myState) {
            case EXPLORING:
                explore();
                break;
            case ATTACKING:
                attack();
                break;
            case JUST_BUILT:
                break;
            case IDLE:
                break;
        }
    }

    @Override
    public void beginningStateSwitches() {
        if (knowledge.myState == RobotState.JUST_BUILT) {
            System.out.println("I called JUST_BUILT at round " + knowledge.roundNum);
            knowledge.myState = RobotState.IDLE;
        }

        if (knowledge.myState == RobotState.IDLE) {
//            compHandler.pathFinder.setNavigationAlgorithm(NavigationAlgorithm.BUG);
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
        if (compHandler.canSenseEnemies() && compHandler.hasWeapons()) {
            knowledge.myState = RobotState.ATTACKING;
            compHandler.pathFinder.pauseExploration();
            attack();
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

}
