package team039.common;

import battlecode.common.*;

public interface SpecificPlayer {
        
    public void doSpecificActions();
    
    public void doSpecificFirstRoundActions();

    public void beginningStateSwitches();

    public void initialize();
    
    public SpecificPlayer determineSpecificPlayer(ComponentType compType);

}
