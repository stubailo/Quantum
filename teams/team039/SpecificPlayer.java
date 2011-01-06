package team039;

import battlecode.common.*;

public interface SpecificPlayer {
		
	public void doSpecificActions();
	
	public void doSpecificFirstRoundActions();
	
	public SpecificPlayer determineSpecificPlayer(ComponentType compType);

}
