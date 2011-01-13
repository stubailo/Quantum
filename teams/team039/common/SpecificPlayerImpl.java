package team039.common;

import team039.building.*;
import team039.building.recycler.*;
import team039.handler.ComponentsHandler;
import team039.light.*;
import battlecode.common.*;
import team039.common.util.*;

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
        beginningStateSwitches();
    }

    public void doSpecificFirstRoundActions() {
    }
    
    public void beginningStateSwitches() {}

    public void initialize() {}

    public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
        SpecificPlayer chassisTypePlayer = this;

        if(Clock.getRoundNum() == 0) {
            switch(myRC.getChassis()) {
            case BUILDING:
                chassisTypePlayer = new RecyclerPlayer(myRC, knowledge, compHandler);
                chassisTypePlayer.initialize();
                
                chassisTypePlayer = new StartingBuildingPlayer(myRC, knowledge, compHandler);
                chassisTypePlayer.initialize();
                break;
                
            case LIGHT:
                chassisTypePlayer = new LightPlayer(myRC, knowledge, compHandler);
                chassisTypePlayer.initialize();

                chassisTypePlayer = new LightConstructorPlayer(myRC, knowledge, compHandler);
                chassisTypePlayer.initialize();

                chassisTypePlayer = new StartingLightPlayer(myRC, knowledge, compHandler);
                chassisTypePlayer.initialize();
                break;
            }
        }
        else {
            switch (myRC.getChassis()) {
            case BUILDING:
                Logger.debug_printSashko("initializing initial constructor");
                chassisTypePlayer = new BuildingPlayer(myRC, knowledge, compHandler);
                break;
                
            case LIGHT:
                chassisTypePlayer = new LightPlayer(myRC, knowledge, compHandler);
                chassisTypePlayer.initialize();
                break;
            }
            // TODO: add other chassis types
        }
        return chassisTypePlayer.determineSpecificPlayer(compType);
    }
}
