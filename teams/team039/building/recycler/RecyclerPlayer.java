package team039.building.recycler;

import team039.building.BuildingPlayer;
import team039.common.ComponentsHandler;
import team039.common.Knowledge;
import team039.common.SpecificPlayer;
import battlecode.common.*;

public class RecyclerPlayer extends BuildingPlayer {
    
    private final RobotController   myRC;
    private final Knowledge         knowledge;
    private final ComponentsHandler compHandler;
        
    public RecyclerPlayer(RobotController rc,
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
    }
    
    @Override
    public void doSpecificFirstRoundActions() {
        super.doSpecificFirstRoundActions();
        compHandler.updateAlliedRecyclerInformation();
        if(knowledge.lowestAlliedRecyclerID < knowledge.myRobotID) {
            myRC.setIndicatorString(2, "turning off");
            myRC.turnOff();
        }
    }
    
    @Override
    public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
        SpecificPlayer result = this;
        
        switch(compType) {
        case ANTENNA:
        case DISH:
        case NETWORK:
            result = new RecyclerCommPlayer(myRC, knowledge, compHandler);
            break;
        }
        return result;
    }

    @Override
    public void beginningStateSwitches() {
        super.beginningStateSwitches();

    }

}
