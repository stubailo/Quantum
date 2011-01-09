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

    @Override
    public void beginningStateSwitches() {
        super.beginningStateSwitches();


    }
}
