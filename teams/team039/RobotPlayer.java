package team039;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class RobotPlayer implements Runnable {
   
    private final RobotController myRC;

    public double previousFlux;

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
        System.out.println(myRC.getChassis());
        chassisTypePlayer.run();
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
