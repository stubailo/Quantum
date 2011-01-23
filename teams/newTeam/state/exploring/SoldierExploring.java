package newTeam.state.exploring;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.attacking.Attacking;

public class SoldierExploring extends BaseState {
    
    private boolean enemiesNearby;
    
    public SoldierExploring(BaseState oldState) {
        super(oldState);
        myMH.zigZag();
    }
    
    @Override
    public void senseAndUpdateKnowledge() {
        enemiesNearby = mySH.areEnemiesNearby();
    }
    
    @Override
    public BaseState getNextState() {
        
        if(enemiesNearby) {
            BaseState result = new Attacking(this);
            result.senseAndUpdateKnowledge();
            return result.getNextState();
        }
        
        return this;
    }
    
    @Override
    public BaseState execute() {
        
        myMH.step();
        return this;
    }

}
