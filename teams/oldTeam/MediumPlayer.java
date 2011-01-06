package oldTeam;

import battlecode.common.*;

public class MediumPlayer extends RobotPlayer {
   
    private final RobotController myRC;

    public MediumPlayer(RobotController rc) {
        super(rc);
        myRC = rc;
    }

    @Override
    public void run() {
        while(true) {
            try {

            }
            catch(Exception e) {
                System.out.println("Robot " + myRC.getRobot().getID() + " during round number " + Clock.getRoundNum() + " caught exception:");
                e.printStackTrace();
            }
        }
    }
}
