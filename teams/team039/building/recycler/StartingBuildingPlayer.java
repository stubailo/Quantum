package team039.building.recycler;

import team039.common.*;
import battlecode.common.*;

public class StartingBuildingPlayer extends RecyclerPlayer {

    private final RobotController myRC;
    private final Knowledge knowledge;
    private final ComponentsHandler compHandler;

    public StartingBuildingPlayer(RobotController rc,
            Knowledge know,
            ComponentsHandler compHand) {

        super(rc, know, compHand);
        myRC = rc;
        knowledge = know;
        compHandler = compHand;
    }

    @Override
    public void doSpecificActions() {
        super.doSpecificActions();

        MessageWrapper sampleMessage = new MessageWrapper();
        sampleMessage.genGoToFactoryMsg(myRC, 0, null);

        System.out.println(myRC.getTeamResources());

        knowledge.msg().addToQueue(sampleMessage);
    }

    @Override
    public void doSpecificFirstRoundActions() {
        super.doSpecificFirstRoundActions();

        if( compHandler.canIBuild()) compHandler.build().startBuildingComponents(Prefab.commRecycler, myRC.getLocation(), RobotLevel.ON_GROUND);
    }

    @Override
    public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
        SpecificPlayer result = this;
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
