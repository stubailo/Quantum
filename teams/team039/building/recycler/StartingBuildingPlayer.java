package team039.building.recycler;

import team039.common.ComponentsHandler;
import team039.common.Knowledge;
import team039.common.SpecificPlayer;
import team039.common.BuildInstructions;
import team039.common.Prefab;
import battlecode.common.*;

public class StartingBuildingPlayer extends RecyclerPlayer {
    
    private final RobotController   myRC;
    private final Knowledge         knowledge;
    private final ComponentsHandler compHandler;
        
    public StartingBuildingPlayer(RobotController rc,
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

        if( myRC.getTeamResources() > Prefab.lightSoldier.getTotalCost()*2 )
        {
            autoBuildRobot( Prefab.lightSoldier );
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

}
