package newTeam.state.attacking;

import battlecode.common.*;

import newTeam.common.Knowledge;
import newTeam.handler.ComponentsHandler;
import newTeam.state.BaseState;

public class Attacking extends BaseState {
    
    private RobotInfo[] enemyRobotInfos;
    
    public Attacking(BaseState state) {
        super(state);
    }
    
    @Override
    public void senseAndUpdateKnowledge() {
        enemyRobotInfos = mySH.getEnemyRobotInfos();
    }
    
    @Override
    public BaseState getNextState() {
        
        if(enemyRobotInfos != null) return this;
        
        
        
        return this;
    }
    
    public BaseState execute() {
        
        myWH.attack(enemyRobotInfos);
        
        return this;
    }
    
}
