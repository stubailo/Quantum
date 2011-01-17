package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.util.Logger;

public class StartingLightConstructorScouting extends BaseState {
    
    private Direction resultOfScoutingDirection;
    
    public StartingLightConstructorScouting(BaseState oldState) {
        super(oldState);
        Logger.debug_printHocho("Reached SLCS state");
    }
    
    @Override
    public void senseAndUpdateKnowledge() {
        resultOfScoutingDirection = mySH.senseStartingLightConstructorSurroundings();
        
    }
    
    @Override
    public BaseState getNextState() {
        
        if(resultOfScoutingDirection == Direction.OMNI) {
            
            BaseState result;
            if(mySH.standardWayClear) {
                
            }
            else {
                result = new StartingMovingToBuild(this, mySH.startingFirstMineToBeBuiltLocation);
            }
            
            result.senseAndUpdateKnowledge();
            result = result.getNextState();
            return result;
        }
            
        return this;
    }
    
    @Override
    public StartingLightConstructorScouting execute() {
        myMH.setDirection(resultOfScoutingDirection);
        return this;
    }

}
