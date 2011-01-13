package team039.player.building;

import team039.player.SpecificPlayer;
import team039.player.SpecificPlayerImpl;
import team039.player.building.recycler.RecyclerPlayer;
import team039.common.*;
import team039.common.util.Logger;
import team039.handler.ComponentsHandler;
import battlecode.common.*;

public class BuildingPlayer extends SpecificPlayerImpl {

    private final RobotController myRC;
    private final Knowledge knowledge;
    private final ComponentsHandler compHandler;

    public BuildOrder buildOrder;

    public BuildingPlayer(RobotController rc,
            Knowledge know,
            ComponentsHandler compHand) {

        super(rc, know, compHand);
        myRC = rc;
        knowledge = know;
        compHandler = compHand;
    }

    public void doSpecificActions() {
        super.doSpecificActions();

        if (knowledge.myState == RobotState.BUILDING) {
            compHandler.build().step();
        }
    }

    public void beginningStateSwitches() {
        
    }

    public void doSpecificFirstRoundActions() {
        super.doSpecificFirstRoundActions();
    }

    public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
        SpecificPlayer result = this;

        switch (compType) {
            case RECYCLER:
                result = new RecyclerPlayer(myRC, knowledge, compHandler);
                result.initialize();
                break;
        }
        return result;
    }
}
