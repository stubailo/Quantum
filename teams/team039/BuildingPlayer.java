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
    
    public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
		SpecificPlayer result = this;
		
		switch(compType) {
		case RECYCLER:
			result = new RecyclerPlayer(myRC, knowledge, compHandler);
			break;
		}
		return result;
	}

}
