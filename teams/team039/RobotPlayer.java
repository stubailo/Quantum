package team039;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class RobotPlayer implements Runnable {
   
    private final RobotController myRC;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    }

    public void run() {
        RobotPlayer chassisTypePlayer = new RobotPlayer(myRC);

        switch(myRC.getChassis()) {

            case LIGHT:
                chassisTypePlayer = new LightPlayer(myRC);
                break;

            case MEDIUM:
                chassisTypePlayer = new MediumPlayer(myRC);
                break;

            case HEAVY:
                chassisTypePlayer = new HeavyPlayer(myRC);
                break;

            case FLYING:
                chassisTypePlayer = new FlyingPlayer(myRC);
                break;

            case BUILDING:
                chassisTypePlayer = new BuildingPlayer(myRC);
                break;
        }
    }
}
