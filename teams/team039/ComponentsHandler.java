package team039;

import battlecode.common.*;

public class ComponentsHandler {

	private final RobotController myRC;
	private final Knowledge       knowledge;
	
	// Controllers
    public         MovementController   myMC;
    public         SensorController[]   mySCs = new SensorController[4];
    public         BuilderController    myBC;
    public         WeaponController[]   myWCs = new WeaponController[18];
    public         BroadcastController  myCC;

    // Controller info
    public         int                 numberOfSensors = 0;
    public         boolean             hasBuilder      = false;
    public         int                 numberOfWeapons = 0;
    public         boolean             hasComm         = false;
	
	public ComponentsHandler(RobotController rc, Knowledge know) {
		myRC = rc;
		knowledge = know;
	}
	
	public void sense() {
		try {
			
			if(numberOfSensors == 0) return;
			if(numberOfSensors == 1) {
				SensorController sensor = mySCs[0];
				
				Mine[] sensedMines = sensor.senseNearbyGameObjects(Mine.class);
				
				if(sensedMines.length > 0) {
					for(Mine sensedMine : sensedMines) {
						MineInfo sensedMineInfo = sensor.senseMineInfo(sensedMine);
						
					}
				}
			}
			else {
				
			}
			
		}
		catch(Exception e) {
            System.out.println("Robot " + myRC.getRobot().getID() + 
                               " during round " + Clock.getRoundNum() + 
                               " caught exception:");
            e.printStackTrace();
        }
	}
	
	public ComponentType updateComponents() {
        ComponentController[] newComps = myRC.newComponents();
        
        if(newComps.length == 0) return null;
        
        ComponentType result = null;
        
        for(ComponentController newComp: newComps) {
        	switch(newComp.type()) { // TODO: handle IRON, JUMP, DROPSHIP, BUG, DUMMY
        							 // TODO: handle passives
        	
        	case BUILDING_MOTOR:
        	case SMALL_MOTOR:
        	case MEDIUM_MOTOR:
        	case LARGE_MOTOR:
        	case FLYING_MOTOR:
        	    myMC = (MovementController) newComp;
        	    break;
        	    
        	case ANTENNA:
        	case DISH:
        	case NETWORK:
        		myCC = (BroadcastController) newComp;
        		hasComm = true;
        		break;
        		
        	case SATELLITE:
        	case TELESCOPE:
        	case SIGHT:
        	case RADAR:
        	case BUILDING_SENSOR:
        		mySCs[numberOfSensors] = (SensorController) newComp;
        		numberOfSensors += 1;
        		break;
        		
        	case SMG:
        	case BLASTER:
        	case RAILGUN:
        	case HAMMER:
        	case BEAM:
        	case MEDIC: // Should MEDIC be under weapons?
        		myWCs[numberOfWeapons] = (WeaponController) newComp;
        		numberOfWeapons += 1;
        		break;
        		
        	case CONSTRUCTOR:
        		result = ComponentType.CONSTRUCTOR;
        		myBC = (BuilderController) newComp;
        		break;
        		
        	case RECYCLER:
        		result = ComponentType.RECYCLER;
        		myBC = (BuilderController) newComp;
        		break;
        		
        	case FACTORY:
        		result = ComponentType.FACTORY;
        		myBC = (BuilderController) newComp;
        		break;
        		
        	case ARMORY:
        		result = ComponentType.ARMORY;
        		myBC = (BuilderController) newComp;
        		break;
        	}
        }
        return result;
    }
}
