package team039;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class RobotPlayer implements Runnable {
   
    private final  RobotController     myRC;
    private        MovementController  myMC;
    private        SensorController    mySC;
    private        BuilderController   myBC;
    private        WeaponController    myWC;
    private        BroadcastController myCC;

    private        boolean             hasSensor  = false;
    private        boolean             hasBuilder = false;
    private        boolean             hasWeapon  = false;
    private        boolean             hasComm    = false;

    private        MapLocation         startLocation;

    private double previousFlux;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    }

    public void run() {
        while(true) {
            try {

                int roundNum = Clock.getRoundNum();
                if(roundNum == 1) {
                    doFirstRoundActions();
                }

            }
            catch(Exception e) {
                System.out.println("Robot " + myRC.getRobot().getID() + \
                                   " during round " + Clock.getRoundNum() + \
                                   " caught exception:");
                e.printStackTrace();
            }
        }

    }

    public void doFirstRoundActions() {
        
    }

    public double determineDeltaFlux() {
        double deltaFlux = myRC.getTeamResources() - previousFlux;
        previousFlux = deltaFlux + previousFlux;
        return deltaFlux;
    }

    public void debug_printNewComponents() {
        ComponentController[] newComps = myRC.newComponents();

        if(newComps.length > 0) {
            for(ComponentController newComp : newComps) {
                System.out.println(newComp.type());
            }
        }
    }

    public void debug_setStrings() { 
        myRC.setIndicatorString(0, String.valueOf(myRC.getTeamResources()));
    }   
}
