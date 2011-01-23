package newTeam.state.attacking;

import battlecode.common.*;

import newTeam.common.Knowledge;
import newTeam.handler.ComponentsHandler;
import newTeam.state.BaseState;
import newTeam.state.exploring.SoldierExploring;

public class Attacking extends BaseState {
    
    private static final int NEAR_ENEMY_DISTANCE = 8;
    
    private RobotInfo[] enemyRobotInfos;
    private int         numberOfEnemies;
    private MapLocation nearestEnemyLocation;
    
    public Attacking(BaseState state) {
        super(state);
        
    }
    
    @Override
    public void senseAndUpdateKnowledge() {
        enemyRobotInfos      = mySH.getEnemyRobotInfos();
        numberOfEnemies      = mySH.getNumberOfSensableEnemyRobots();
        nearestEnemyLocation = mySH.getNearestEnemyLocation();
    }
    
    @Override
    public BaseState getNextState() {
        
        if(enemyRobotInfos == null) return new SoldierExploring(this);
        
        
        return null;
    }
    
    public BaseState execute() {
        
        int distanceToNearestEnemy = myK.myLocation.distanceSquaredTo(nearestEnemyLocation);
        
        if(distanceToNearestEnemy > myWH.range) {
            
        }
        else if(distanceToNearestEnemy < NEAR_ENEMY_DISTANCE) {
            
        }
        
        myWH.attack(enemyRobotInfos, numberOfEnemies);
        
        return this;
    }
    
}
