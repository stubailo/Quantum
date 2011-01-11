package team039.building.recycler;

import team039.building.BuildingPlayer;
import team039.common.*;
import team039.common.util.*;
import team039.handler.ComponentsHandler;
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
    public void initialize()
    {
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

        if( knowledge.myRecyclerNode != null )
        {
            
            MessageWrapper ping = new MessageWrapper();
            ping.genRecyclerPing( knowledge.myRecyclerNode );

            Logger.debug_printSashko("pinging: " + knowledge.myRecyclerNode.myRobotID + " " + knowledge.myRecyclerNode.parentRobotID);

            knowledge.msg().addToQueue(ping);
        }
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


    private boolean haveBuiltConstructor = false;

    int numberOfSoldiersBuilt = 0;
    @Override
    public void beginningStateSwitches() {
        super.beginningStateSwitches();

        if( haveBuiltConstructor == false && knowledge.myState == RobotState.IDLE && compHandler.canIBuild() && myRC.getTeamResources() > Prefab.lightConstructor.getTotalCost() + 100 )
        {
            compHandler.build().autoBuildRobot(Prefab.lightConstructor);
            haveBuiltConstructor = true;
        } else if (numberOfSoldiersBuilt <= QuantumConstants.SOLDIERS_PER_CONSTRUCTOR && knowledge.myState == RobotState.IDLE && compHandler.canIBuild() && myRC.getTeamResources() > Prefab.lightSoldier.getTotalCost() + 300-(myRC.getRobot().getID()/5)) {
            compHandler.build().autoBuildRobot(Prefab.lightSoldier);
            numberOfSoldiersBuilt++;
        }
        else if (numberOfSoldiersBuilt > QuantumConstants.SOLDIERS_PER_CONSTRUCTOR && knowledge.myState == RobotState.IDLE && compHandler.canIBuild() && myRC.getTeamResources() > Prefab.lightSoldier.getTotalCost() + 300-(myRC.getRobot().getID()/5)) {
            compHandler.build().autoBuildRobot(Prefab.lightConstructor);
            Logger.debug_printSashko("building another constructor");
            numberOfSoldiersBuilt = 0;
        }
    }

}
