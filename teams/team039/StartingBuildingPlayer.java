package team039;

import battlecode.common.*;

public class StartingBuildingPlayer extends BuildingPlayer {
	
	private final RobotController   myRC;
	private final Knowledge         knowledge;
	private final ComponentsHandler compHandler;
		
	public StartingBuildingPlayer(RobotController rc,
								  Knowledge know,
								  ComponentsHandler compHand) {
		
		super(rc, know, compHand);
		myRC = rc;
		knowledge = know;
		compHandler = compHand;
	}

}