package team039.player.building.recycler;

import team039.common.Knowledge;
import team039.common.RecyclerNode;
import team039.handler.ComponentsHandler;
import team039.player.SpecificPlayer;
import battlecode.common.*;

public class RecyclerCommPlayer extends RecyclerPlayer {
    
    private final RobotController   myRC;
    private final Knowledge         knowledge;
    private final ComponentsHandler compHandler;
        
    public RecyclerCommPlayer(RobotController rc,
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

        if( knowledge.myRecyclerNode == null)
        {
            knowledge.myRecyclerNode = new RecyclerNode();
            knowledge.myRecyclerNode.myLocation = myRC.getLocation();
            knowledge.myRecyclerNode.myRobotID = myRC.getRobot().getID();
            knowledge.myRecyclerNode.parentLocation = null;
            knowledge.myRecyclerNode.parentRobotID = 0;
        }

        knowledge.msg().addToQueue( knowledge.myRecyclerNode.generatePing() );

        myRC.setIndicatorString(1, knowledge.myRecyclerNode.toString() );
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

}
