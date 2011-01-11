package sprint.common;

import sprint.building.BuildingPlayer;
import sprint.building.recycler.StartingBuildingPlayer;
import sprint.common.ComponentsHandler;
import sprint.light.LightPlayer;
import sprint.light.StartingLightPlayer;
import battlecode.common.*;

public class SpecificPlayerImpl implements SpecificPlayer {

    private final RobotController   myRC;
    private final Knowledge         knowledge;
    private final ComponentsHandler compHandler;

    public SpecificPlayerImpl(RobotController rc,
                              Knowledge know,
                              ComponentsHandler compHand) {

        myRC        = rc;
        knowledge   = know;
        compHandler = compHand;
        // DONT DO ANYTHING HERE
        // If you're tempted to be putting code here you should probably put it in
        //   RobotPlayer
    }

    public void doSpecificActions() {
    }

    public void doSpecificFirstRoundActions() {
    }
    
    public void beginningStateSwitches() {}

    public void initialize() {}

    public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
        SpecificPlayer chassisTypePlayer = this;

        if(knowledge.roundNum == 0) {
            switch(myRC.getChassis()) {
            case BUILDING:
                chassisTypePlayer = new StartingBuildingPlayer(myRC, knowledge, compHandler);
                chassisTypePlayer.initialize();
                break;
                
            case LIGHT:
                chassisTypePlayer = new StartingLightPlayer(myRC, knowledge, compHandler);
                break;
            }
        }
        else {
            switch (myRC.getChassis()) {
            case BUILDING:
                chassisTypePlayer = new BuildingPlayer(myRC, knowledge, compHandler);
                break;
                
            case LIGHT:
                chassisTypePlayer = new LightPlayer(myRC, knowledge, compHandler);
                break;
            }
            // TODO: add other chassis types
        }
        return chassisTypePlayer.determineSpecificPlayer(compType);
    }
}
