package team039;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class BuildingPlayer extends RobotPlayer {
   
    private final RobotController myRC;

    public BuildingPlayer(RobotController rc) {
        super(rc);
        myRC = rc;
    }

    public void run() {
        while(true) {
            try {

                debug_printNewComponents();
                debug_setStrings();
                myRC.yield();
            }
            catch(Exception e) {
                System.out.println("Robot " + myRC.getRobot().getID() + " during round number " + Clock.getRoundNum() + " caught exception:");
                e.printStackTrace();
            }
        }
    }
}
