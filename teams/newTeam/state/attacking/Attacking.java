package newTeam.state.attacking;

import battlecode.common.*;

import newTeam.common.Knowledge;
import newTeam.handler.ComponentsHandler;
import newTeam.state.BaseState;
import newTeam.state.exploring.SoldierExploring;

public class Attacking extends BaseState {
    
    private RobotInfo[] enemyRobotInfos;
    private int         numberOfEnemies;
    
    public Attacking(BaseState state) {
        super(state);
    }
    
    @Override
    public void senseAndUpdateKnowledge() {
        enemyRobotInfos = mySH.getEnemyRobotInfos();
        numberOfEnemies = mySH.getNumberOfSensableEnemyRobots();
    }
    
    @Override
    public BaseState getNextState() {
        
        if(enemyRobotInfos != null) return this;
        
        return new SoldierExploring(this);
    }
    
    public BaseState execute() {
        
        myWH.attack(enemyRobotInfos, numberOfEnemies);
        
        return this;
    }
    
}
