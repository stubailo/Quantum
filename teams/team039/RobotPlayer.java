package team039;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class RobotPlayer implements Runnable {

    private final RobotController myRC;

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    }

    public void run() {
	ComponentController [] components = myRC.newComponents(); 
	System.out.println(java.util.Arrays.toString(components));
	System.out.flush();
	if(myRC.getChassis()==Chassis.BUILDING)
	    runBuilder((MovementController)components[0],(SensorController)components[1],(BuilderController)components[2]);
	else if (myRC.getChassis()==Chassis.LIGHT) 
	    runConstructor((MovementController)components[0]);
	else
	    runMotor((MovementController)components[0]);
    }

    public void testit(MovementController m) {
	    m.withinRange(myRC.getLocation());
    }

    public void runBuilder(MovementController motor, SensorController sensor, BuilderController builder) {
    
	boolean equipBuilder = false;

	while (true) {
	    try {
		myRC.yield();

		if(equipBuilder){
//		    Class<? extends Robot> robotClass = myRC.getRobot().getClass();
//		    GameObject [] nearbots = sensor.senseNearbyGameObjects(robotClass);
//		    RobotInfo [] nearbotsInfo = new RobotInfo[nearbots.length];
//		    for (int i = 0; i<nearbots.length; i++) {
//			nearbotsInfo[i] = sensor.senseRobotInfo((Robot)nearbots[i]);
//		    }
		    RobotInfo [] nearbotsInfo = getNearbyRobotInfo(sensor);

		    System.out.println(java.util.Arrays.toString(nearbotsInfo));
		    for (RobotInfo r : nearbotsInfo) {
			if(r.robot.getTeam() == myRC.getTeam() && findComponentIndex(r.components, ComponentType.CONSTRUCTOR) == -1 ) {
			    builder.build(ComponentType.CONSTRUCTOR, r.location, r.robot.getRobotLevel());
			    equipBuilder = false;
			    break;
			}
		    }	
		}

		if(!motor.canMove(myRC.getDirection())) {
		    motor.setDirection(myRC.getDirection().rotateRight());
		} else if(myRC.getTeamResources()>=2*Chassis.LIGHT.cost + ComponentType.CONSTRUCTOR.cost) {
		    builder.build(Chassis.LIGHT,myRC.getLocation().add(myRC.getDirection()));
		    equipBuilder = true;
		}

	    } catch (Exception e) {
		System.out.println("caught exception:");
		e.printStackTrace();
	    }
	}
    }

    public void runMotor(MovementController motor) {
        
        while (true) {
            try {
                /*** beginning of main loop ***/
                while (motor.isActive()) {
                    myRC.yield();
                }

                if (motor.canMove(myRC.getDirection())) {
                    //System.out.println("about to move");
                    motor.moveForward();
                } else {
                    motor.setDirection(myRC.getDirection().rotateRight());
                }

                /*** end of main loop ***/
            } catch (Exception e) {
                System.out.println("caught exception:");
                e.printStackTrace();
            }
        }
    }

    public void runConstructor(MovementController motor) {
	
	while(true) {
	    try {
		/*** beginning of main loop ***/

	    } catch (Exception e) {
		System.out.println("caught exception:");
		e.printStackTrace();
	    }
	}
    }

    public RobotInfo [] getNearbyRobotInfo(SensorController sensor) {
	try {
	    Class<? extends Robot> robotClass = myRC.getRobot().getClass();
	    System.out.println(myRC.getRobot().getClass());
	    GameObject [] nearbots = sensor.senseNearbyGameObjects(Robot.class);
	    RobotInfo [] nearbotsInfo = new RobotInfo[nearbots.length];
	    for (int i = 0; i<nearbots.length; i++) {
		nearbotsInfo[i] = sensor.senseRobotInfo((Robot)nearbots[i]);
	    }
	    return nearbotsInfo;
	} catch (Exception e) {
	    System.out.println("caught exception:");
	    e.printStackTrace();
	    return null;
	}
    }

    public int findComponentIndex(ComponentType [] compArray, ComponentType t) {
	return java.util.Arrays.asList(compArray).indexOf(t);
    }
}
