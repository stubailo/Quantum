package sprint.building;

import sprint.building.recycler.RecyclerPlayer;
import sprint.common.*;
import sprint.common.util.Logger;
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

        beginningStateSwitches();

        if (knowledge.myState == RobotState.BUILDING) {
            compHandler.build().step();
        }
    }

    public void beginningStateSwitches() {
        if (knowledge.myState == RobotState.JUST_BUILT) {
            Logger.debug_printSashko("I called JUST_BUILT at round " + knowledge.roundNum);
            knowledge.myState = RobotState.IDLE;
        }
    }

    public void doSpecificFirstRoundActions() {
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
