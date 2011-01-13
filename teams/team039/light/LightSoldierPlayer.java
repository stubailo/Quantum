package team039.light;

import team039.common.*;
import team039.common.util.Logger;
import team039.handler.ComponentsHandler;
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
            Logger.debug_print("I called JUST_BUILT at round " + knowledge.roundNum);
            knowledge.myState = RobotState.IDLE;
        }

        if (knowledge.myState == RobotState.IDLE) {
             compHandler.pathFinder.setNavigationAlgorithm(NavigationAlgorithm.ZIG_ZAG);
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
        } else {
            Logger.debug_print("Can't see anyone.");
            knowledge.myState = RobotState.IDLE;
        }
    }
    private boolean deflecting = false;
    private int numOfTurnsBugged = 0;
    public void explore() {
        if (compHandler.canSenseEnemies() && compHandler.hasWeapons()) {
            Logger.debug_printSashko("Attacking!");
            knowledge.myState = RobotState.ATTACKING;
            compHandler.pathFinder.pauseExploration();
            attack();

        } else {
            /*
            if (knowledge.parentChanged) {
                    Direction newDirection = deflect();
                    if(newDirection != Direction.NONE)
                    {
                        System.out.println("deflecting: " + newDirection);
                        deflecting = true;
                        numOfTurnsBugged = 0;
                        compHandler.pathFinder.initiateBugNavigation(knowledge.myLocation.add(newDirection, QuantumConstants.DEFLECT_BUG_DISTANCE));
                    }
                }

            if( deflecting && (compHandler.pathFinder.reachedGoal() || numOfTurnsBugged >= QuantumConstants.DEFLECT_NUM_TURNS) )
            {
                compHandler.pathFinder.setNavigationAlgorithm(NavigationAlgorithm.ZIG_ZAG);
                deflecting = false;
            }

            if(deflecting)
            {
                numOfTurnsBugged++;
                System.out.println("bugging turn " + numOfTurnsBugged );
            } */

	        try {
	            compHandler.pathFinder.step();
	        } catch (Exception e) {
	        	Logger.debug_print("Robot " + myRC.getRobot().getID()
	                    + " during round " + Clock.getRoundNum()
	                    + " caught exception:");
	            e.printStackTrace();
	        }

                
        }
    }

}
