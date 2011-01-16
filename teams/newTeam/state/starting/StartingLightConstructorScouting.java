package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.building.MovingToBuild;
import newTeam.common.util.Logger;

public class StartingLightConstructorScouting extends BaseState {
    
    private Direction resultOfScoutingDirection;
    
    public StartingLightConstructorScouting(BaseState oldState) {
        super(oldState);
        Logger.debug_printHocho("Reached SLCS state");
    }
    
    @Override
    public BaseState getNextState() {
        
        resultOfScoutingDirection = mySH.senseStartingLightConstructorSurroundings();
            
        if(resultOfScoutingDirection == Direction.OMNI) {
            BaseState result = new StartingMovingToBuild(this, mySH.startingIdealBuildingLocation);
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
