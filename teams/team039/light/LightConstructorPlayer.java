package team039.light;

import team039.common.ComponentsHandler;
import team039.common.Knowledge;
import team039.common.SpecificPlayer;
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
        try {
        	compHandler.navigateBug();
        } catch(Exception e) {
        	System.out.println("Robot " + myRC.getRobot().getID() + 
                    " during round " + Clock.getRoundNum() + 
                    " caught exception:");
            e.printStackTrace();
        }
    }
    
    @Override
    public void doSpecificFirstRoundActions() {
        super.doSpecificFirstRoundActions();
        compHandler.initiateBugNavigation(myRC.getLocation().add(Direction.SOUTH, 23));
    }
    
    @Override
    public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
        SpecificPlayer result = this;
        return result;
    }

}
