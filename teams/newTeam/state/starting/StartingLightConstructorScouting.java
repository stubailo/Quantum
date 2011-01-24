package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.util.Logger;

public class StartingLightConstructorScouting extends BaseState {
    
    private Direction resultOfScoutingDirection;
    
    public StartingLightConstructorScouting(BaseState oldState) {
        super(oldState);
//        Logger.debug_printHocho("Reached SLCS state");
    }
    
    @Override
    public void senseAndUpdateKnowledge() {
        resultOfScoutingDirection = mySH.senseStartingLightConstructorSurroundings();
        mySH.senseEdges();
    }
    
    @Override
    public BaseState getNextState() {
        
        if(resultOfScoutingDirection == Direction.OMNI) {
            
            BaseState result = null;
            if(mySH.standardWayClear) {
                result = new StartingMovingToBuildRecycler(this, false);
            }
            else {
                result = new StartingMovingToBuildRecycler(this, true);
            }
            
            result.senseAndUpdateKnowledge();
            return result.getNextState();
        }
            
        return this;
    }
    
    @Override
    public StartingLightConstructorScouting execute() {
        
        myMH.setDirection(resultOfScoutingDirection);
        return this;
    }

}
