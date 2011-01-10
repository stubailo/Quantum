package team039.building.recycler;

import team039.building.BuildingPlayer;
import team039.common.*;
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

        compHandler.updateAlliedRecyclerInformation();
        if(knowledge.lowestAlliedRecyclerID < knowledge.myRobotID) {
            myRC.setIndicatorString(2, "turning off");
            myRC.turnOff();
        }

        if( compHandler.canIBuild()) compHandler.build().startBuildingComponents(Prefab.commRecycler, myRC.getLocation(), RobotLevel.ON_GROUND);
    }
    
    @Override
    public void doSpecificActions() {
        super.doSpecificActions();

        MessageWrapper ping = new MessageWrapper();
        ping.genRecyclerPing(myRC);

        knowledge.msg().addToQueue(ping);
    }
    
    @Override
    public void doSpecificFirstRoundActions() {
        super.doSpecificFirstRoundActions();
        
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

        if (knowledge.myState == RobotState.IDLE && compHandler.canIBuild() && myRC.getTeamResources() > Prefab.lightSoldier.getTotalCost() + 300) {
            compHandler.build().autoBuildRobot(Prefab.lightSoldier);
        }
        if (knowledge.myState == RobotState.IDLE && compHandler.canIBuild() && myRC.getTeamResources() > Prefab.lightSoldier.getTotalCost() + 100 && Clock.getRoundNum() > 2000) {
            compHandler.build().autoBuildRobot(Prefab.lightSoldier);
        }
    }

}
