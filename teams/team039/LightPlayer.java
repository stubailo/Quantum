package team039;

import battlecode.common.*;

public class LightPlayer implements SpecificPlayer {
	
	private final RobotController   myRC;
	private final Knowledge         knowledge;
	private final ComponentsHandler compHandler;
		
	public LightPlayer(RobotController rc,
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