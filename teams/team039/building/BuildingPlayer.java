package team039.building;

import team039.building.recycler.RecyclerPlayer;
import team039.common.ComponentsHandler;
import team039.common.*;
import battlecode.common.*;

public class BuildingPlayer extends SpecificPlayerImpl {

    private final RobotController myRC;
    private final Knowledge knowledge;
    private final ComponentsHandler compHandler;

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
            System.out.println("I called JUST_BUILT at round " + knowledge.roundNum);
            knowledge.myState = RobotState.IDLE;
        }
    }

    public void doSpecificFirstRoundActions() {
        System.out.println( "testing for shutoff" );
    }

    public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
        SpecificPlayer result = this;

        switch (compType) {
            case RECYCLER:
                knowledge.debug_printCustomErrorMessage("I AM A JASON RECYCLER");
                result = new RecyclerPlayer(myRC, knowledge, compHandler);
                break;
        }
        return result;
    }
}
