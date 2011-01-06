package team039;

import battlecode.common.*;

public class StartingLightPlayer extends LightPlayer {
	
	private final RobotController   myRC;
	private final Knowledge         knowledge;
	private final ComponentsHandler compHandler;
		
	public StartingLightPlayer(RobotController rc,
							   Knowledge know,
							   ComponentsHandler compHand) {
		
		super(rc, know, compHand);
		myRC = rc;
		knowledge = know;
		compHandler = compHand;
	}
	
	public void doSpecificActions() {
		super.doSpecificActions();
	}

}