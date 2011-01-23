package newTeam.state.exploring;

import battlecode.common.*;

import newTeam.common.util.Logger;
import newTeam.state.BaseState;
import newTeam.state.attacking.Attacking;

public class SoldierExploring extends BaseState {
    
    private boolean enemiesNearby;
    private double  myHP;
    private double  myPrevHP;
    private boolean beingAttacked = false;
    private boolean turnedAround  = false;
    
    public SoldierExploring(BaseState oldState) {
        super(oldState);
        myMH.zigZag();
        myPrevHP = myRC.getHitpoints();
    }
    
    @Override
    public void senseAndUpdateKnowledge() {
        enemiesNearby = mySH.areEnemiesNearby();
        myHP = myRC.getHitpoints();
        if(myHP != myPrevHP) {
            beingAttacked = true;
            myPrevHP = myHP;
        }
    }
    
    @Override
    public BaseState getNextState() {
        
        if(enemiesNearby || turnedAround) {
            BaseState result = new Attacking(this);
            result.senseAndUpdateKnowledge();
            return result.getNextState();
        }
        
        return this;
    }
    
    @Override
    public BaseState execute() {
        if(beingAttacked) {
            if(myMH.setDirection(myK.myDirection.opposite())) {
                Logger.debug_printHocho("turned around");
                turnedAround = true;
            }
        }
        else {
            myMH.step();
        }
        return this;
    }

}
