package team039;

import battlecode.common.*;

public class BuildingPlayer implements SpecificPlayer {
	
	private final RobotController   myRC;
	private final Knowledge         knowledge;
	private final ComponentsHandler compHandler;
		
	public BuildingPlayer(RobotController rc,
						  Knowledge know,
						  ComponentsHandler compHand) {
		
		myRC = rc;
		knowledge = know;
		compHandler = compHand;
	}
	
	public void doSpecificActions() {
		
	}
	
    public void doSpecificFirstRoundActions() {
		
	}

}
