package team039;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class BuildingPlayer extends RobotPlayer {
   
    /*** FIELDS **/
    private final RobotController    myRC;
    private       MovementController myMC;
    private       SensorController   mySC;
    private       WeaponController   myWC;
    private       BuilderController  myBC;

    private       boolean            hasSensor;
    private       boolean            hasWeapon;
    private       boolean            hasBuilder;


    /*** CONSTRUCTOR **/
    public BuildingPlayer(RobotController rc) {
        super(rc);
        myRC = rc;
    }

    /*** FIRST ROUND ACTIONS **/
    public void doFirstRoundActions() {
        ComponentController[] newComponents = myRC.newComponents();
        myMC = (MovementController) newComponents[0];
        if(newComponents.length > 1) {
            mySC = (SensorController)  newComponents[1];
            myBC = (BuilderController) newComponents[2];
            hasSensor  = true;
            hasBuilder = true;
        }
    }

    public void run() {

        doFirstRoundActions();

    /*** MAIN LOOP **/
        while(true) {
            try {

                debug_printNewComponents();
                debug_setStrings();
                myRC.yield();

                MapLocation lightLocation = myRC.getLocation();
                RobotLevel lightLevel = myRC.getRobot().getRobotLevel();
                Robot[] nearbyRobots = mySC.senseNearbyGameObjects(Robot.class);
                for(Robot nearbyRobot: nearbyRobots) {
                    RobotInfo nearbyRobotInfo = mySC.senseRobotInfo(nearbyRobot);
                    //System.out.println("Found robot " + nearbyRobotInfo.robot.getID() + " at location " + nearbyRobotInfo.location + " at level " + nearbyRobotInfo.robot.getRobotLevel());
                    if(nearbyRobotInfo.chassis == Chassis.LIGHT && nearbyRobotInfo.robot.getTeam() == myRC.getRobot().getTeam()) {
                        int totalWeight = 0;
                        for(ComponentType compType : nearbyRobotInfo.components) {
                            totalWeight += compType.weight;
                        }
                        if(Chassis.LIGHT.weight - totalWeight > 0) {

                            lightLocation = nearbyRobotInfo.location;
                            lightLevel = nearbyRobotInfo.robot.getRobotLevel();
                        }
                        else {
                            myRC.setIndicatorString(2, "LIGHT is full!");
                        }
                    }
                    myRC.setIndicatorString(1, lightLocation.toString());
                }

                /*if(myRC.getDirection() != myRC.getLocation().directionTo(lightLocation)) {
                    if(!myMC.isActive()) {
                        myRC.setIndicatorString(2, "Setting direction.");
                        myMC.setDirection(myRC.getLocation().directionTo(lightLocation));
                    }
                }*/
                if(myRC.getTeamResources() >= ComponentType.ANTENNA.cost + .85 &&
                   myRC.getLocation() != lightLocation) {
                    myRC.setIndicatorString(2, "Facing light.");
                    System.out.println("Attempting to add Antenna");
                    myBC.build(ComponentType.ANTENNA, lightLocation, lightLevel);
                }
            }
            catch(Exception e) {
                System.out.println("Robot " + myRC.getRobot().getID() + " during round number " + Clock.getRoundNum() + " caught exception:");
                e.printStackTrace();
            }
        }
    }
}
