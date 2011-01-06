package team039.common;

import battlecode.common.*;

public interface SpecificPlayer {
        
    public void doSpecificActions();
    
    public void doSpecificFirstRoundActions();
    
    public SpecificPlayer determineSpecificPlayer(ComponentType compType);

}
