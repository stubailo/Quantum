package team039.building;

import team039.building.recycler.RecyclerPlayer;
import team039.common.ComponentsHandler;
import team039.common.*;
import battlecode.common.*;

public class BuildingPlayer implements SpecificPlayer {

    private final RobotController myRC;
    private final Knowledge knowledge;
    private final ComponentsHandler compHandler;
    private Robot buildTarget = null;
    private MapLocation buildLocation = null;
    private RobotLevel buildHeight = null;
    private BuildInstructions buildInstructions = null;
    private int buildStep = 0;

    public BuildingPlayer(RobotController rc,
            Knowledge know,
            ComponentsHandler compHand) {

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
            System.out.println("I called JUST_BUILT at round " + knowledge.roundNum);
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
                break;
        }
        return result;
    }
}
