package newTeam.state.attacking;

import battlecode.common.*;

import newTeam.common.Knowledge;
import newTeam.common.util.Logger;
import newTeam.handler.ComponentsHandler;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.BaseState;
import newTeam.state.exploring.SoldierExploring;

public class Attacking extends BaseState {
    
    private static final int MOVE_AWAY_DISTANCE  = 1;
    
    private RobotInfo[] enemyRobotInfos;
    private int         numberOfEnemies;
    private MapLocation nearestEnemyLocation = myK.myLocation;
    private boolean     nearestEnemyMoved;
    private boolean     moving = false;
    
    public Attacking(BaseState state) {
        super(state);
    }
    
    @Override
    public void senseAndUpdateKnowledge() {
        mySH.thoroughRefresh();
        enemyRobotInfos = mySH.getEnemyRobotInfos();
        numberOfEnemies = mySH.getNumberOfSensableEnemyRobots();
        
        MapLocation newNearestEnemyLocation = mySH.getNearestEnemyLocation();
        if(newNearestEnemyLocation != null && !newNearestEnemyLocation.equals(nearestEnemyLocation)) {
            nearestEnemyLocation = newNearestEnemyLocation;
            nearestEnemyMoved = true;
//            Logger.debug_printHocho("I am at: " + myK.myLocation);
//            Logger.debug_printHocho("nearest enemy moved to: " + nearestEnemyLocation);
        }
        else {
            nearestEnemyMoved = false;
        }
    }
    
    @Override
    public BaseState getNextState() {
        
        if(enemyRobotInfos == null) return new SoldierExploring(this);
        
        
        return this;
    }
    
    public BaseState execute() {
        
        moving = false;
        
        int distanceToNearestEnemy = myK.myLocation.distanceSquaredTo(nearestEnemyLocation);
        Direction directionToNearestEnemy = myK.myLocation.directionTo(nearestEnemyLocation);
        
        if(distanceToNearestEnemy > myWH.range) {
            myMH.initializeNavigationTo(nearestEnemyLocation, NavigatorType.BUG);
            moving = true;
        }
        else if(distanceToNearestEnemy < myWH.moveAwayDistance) {
            myMH.initializeNavigationTo(myK.myLocation.add(directionToNearestEnemy.opposite(), MOVE_AWAY_DISTANCE), NavigatorType.BUG_BACKWARD);
            moving = true;
        }
        else if(directionToNearestEnemy != myK.myDirection) {
            myMH.setDirection(directionToNearestEnemy);
        }
        
        myWH.attack(enemyRobotInfos, numberOfEnemies);
        if(moving) myMH.step();
        
        return this;
    }
    
}
