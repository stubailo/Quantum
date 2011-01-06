package team039;

import battlecode.common.*;

public class LightSoldierPlayer extends LightPlayer {
	
	private final RobotController   myRC;
	private final Knowledge         knowledge;
	private final ComponentsHandler compHandler;
		
	public LightSoldierPlayer(RobotController rc,
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
	
    public void doSpecificFirstRoundActions() {
		super.doSpecificFirstRoundActions();
	}
	
	public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
		SpecificPlayer result = this;
		return result;
	}

}