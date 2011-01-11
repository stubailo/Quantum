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
                Logger.debug_print("Attacking!");
        } else {
            Logger.debug_print("Can't see anyone.");
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
	            compHandler.pathFinder.zigZag();
	        } catch (Exception e) {
	        	Logger.debug_print("Robot " + myRC.getRobot().getID()
	                    + " during round " + Clock.getRoundNum()
	                    + " caught exception:");
	            e.printStackTrace();
	        }
/*
                if(knowledge.parentChanged)
            {
                Direction prefDirection;

                MapLocation prefVector = knowledge.myRecyclerNode.getVector();
                if( knowledge.oldRecyclerNode != null )
                {
                    MapLocation myMovementVector = knowledge.myLocation.add( -knowledge.oldRecyclerNode.myLocation.x, -knowledge.oldRecyclerNode.myLocation.y );
                    prefVector = prefVector.add(myMovementVector.x, myMovementVector.y);
                }

                MapLocation origin = new MapLocation( 0, 0 );

                prefDirection = origin.directionTo(prefVector);

                int random = Clock.getRoundNum() + myRC.getRobot().getID();

                Direction newDirection;

                switch( random%14 )
                {
                    case 6:
                    case 7:
                    case 8:
                        newDirection = prefDirection.rotateLeft();
                        break;
                    case 9:
                    case 10:
                    case 11:
                        newDirection = prefDirection.rotateRight();
                        break;
                    case 12:
                        newDirection = prefDirection.rotateLeft().rotateLeft();
                        break;
                    case 13:
                        newDirection = prefDirection.rotateRight().rotateRight();
                        break;
                    default:
                        newDirection = prefDirection;
                }

                compHandler.setDirection(newDirection);
            }
 * 
 */

        }
    }

}
