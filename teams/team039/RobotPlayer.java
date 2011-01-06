package team039;

import battlecode.common.*;

public class RobotPlayer implements Runnable {
   
    private final  RobotController     myRC;
    
    // ComponentsHandler
    private final  ComponentsHandler   compHandler;
    
    // Knowledge
    private final  Knowledge           knowledge;
    

    public RobotPlayer(RobotController rc) {
        myRC = rc;
    	knowledge = new Knowledge(myRC);
    	compHandler = new ComponentsHandler(myRC, knowledge);
    }

    public void run() {
    	doCommonActions();    	
    	doCommonFirstRoundActions();
    	SpecificPlayer specificPlayer = determineSpecificPlayer();
    	specificPlayer.doSpecificFirstRoundActions();
    	specificPlayer.doSpecificActions();
        while(true) {
            try {

                debug_setStrings();
                debug_printComponents();
                myRC.yield();
                
                
                doCommonActions();
                specificPlayer.doSpecificActions();
            }
            catch(Exception e) {
                System.out.println("Robot " + myRC.getRobot().getID() + 
                                   " during round " + Clock.getRoundNum() + 
                                   " caught exception:");
                e.printStackTrace();
            }
        }

    }

    public void doCommonFirstRoundActions() {
    	
    }
    
    public ComponentType doCommonActions() {
    	knowledge.update();
        return compHandler.updateComponents();
    }

    
    
    public SpecificPlayer determineSpecificPlayer() {
    	
    	// Use of BuildingPlayer below is arbitrary - it's a place holder
    	SpecificPlayer result = new BuildingPlayer(myRC, knowledge, compHandler);
    	
    	// First buildings are special
    	if(knowledge.roundNum == 0) {
    		
    		switch(myRC.getChassis()) {
    		
    		case BUILDING:
    			result = new StartingBuildingPlayer(myRC, knowledge, compHandler);
    			break;
    			
    		case LIGHT:
    			result = new StartingLightPlayer(myRC, knowledge, compHandler);
    		}
    	}
    	
    	/*else {
	    	switch(myRC.getChassis()) {
	    	case BUILDING:
	    	    result = new BuildingPlayer(myRC);
	    	    break;
	
	    	case LIGHT:
	    	    result = new LightPlayer(myRC);
	    	    break;
	
	
	    	case MEDIUM:
	    	    result = new MediumPlayer(myRC);
	    	    break;
	
	
	    	case HEAVY:
	    	    result = new HeavyPlayer(myRC);
	    	    break;
	
	
	    	case FLYING:
	    	    result = new FlyingPlayer(myRC);
	    	    break;
	
	
	    	case DUMMY:
	    	    result = new DummyPlayer(myRC);
	    	    break;
	    	}
    	}*/
    	return result;
    }

    public void debug_printComponents() {
        ComponentController[] newComps = myRC.newComponents();
        

        if(newComps.length > 0) {
            String displayString = "New components:";
            for(ComponentController newComp : newComps) {
                displayString += " " + newComp.type();
            }
            System.out.println(displayString);
        }
    }

    public void debug_setStrings() { 
        myRC.setIndicatorString(0, String.valueOf(myRC.getTeamResources()));
    }   
}
