package team039;

import battlecode.common.*;

public class RecyclerCommPlayer extends RecyclerPlayer {
	
	private final RobotController   myRC;
	private final Knowledge         knowledge;
	private final ComponentsHandler compHandler;
		
	public RecyclerCommPlayer(RobotController rc,
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