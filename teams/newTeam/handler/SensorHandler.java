package newTeam.handler;

import battlecode.common.*;
import newTeam.common.Knowledge;
import newTeam.common.QuantumConstants;
import newTeam.common.util.Logger;

public class SensorHandler {
    
    
    private final RobotController myRC;
    private final Knowledge       knowledge;
    
    
    /*** Controller ***/
    private final SensorController[] mySCs           = new SensorController[4];
    private       int                numberOfSensors = 0;
    
    /*** Constants ***/
    private static final int MAX_TOTAL_NUMBER_OF_ROBOTS  = QuantumConstants.MAX_TOTAL_NUMBER_OF_ROBOTS;
    private static final int MAX_NUMBER_OF_SENSED_THINGS = QuantumConstants.MAX_NUMBER_OF_SENSED_THINGS;
    
    /*** 
     * REDUNDANCY PREVENTERS
     * 
     * These booleans prevent unnecessary redundant computation and are reset by the "refresh" method.
     */
    private int             lastRoundRefreshed      = -1;
    private boolean         robotsSensed            = false;
    private boolean         minesSensed             = false;
    private boolean         buildingsSensed         = false;
    //private final boolean[] sensableRobotsIdHash    = new boolean[MAX_TOTAL_NUMBER_OF_ROBOTS];
    
    /***
     * INFO
     * 
     * 
     */
    private final   Robot[] sensableRobots          = new Robot[MAX_NUMBER_OF_SENSED_THINGS];
    private         int     numberOfSensableRobots  = 0;
    private final   Mine[]  sensableMines           = new Mine[MAX_NUMBER_OF_SENSED_THINGS];
    private         int     numberOfSensableMines   = 0;

    
    
    public SensorHandler(RobotController rc, Knowledge know) {
        
        myRC      = rc;
        knowledge = know;
    }
    
    
    
    /**
     * Adds a SensorController to the Handler
     * @param sc    SensorController to be added
     */
    public void addSC(SensorController sc) {
        mySCs[numberOfSensors] = sc;
        numberOfSensors++;
    }
    
    
    
    /**
     * Uses some basic knowledge to reset appropriate redundancy preventers
     */
    public void refresh() {
        if(lastRoundRefreshed == Clock.getRoundNum()) return;
        lastRoundRefreshed = Clock.getRoundNum();
        
        robotsSensed = false;
        if(knowledge.justMoved || knowledge.justTurned) {
            minesSensed = false;
            buildingsSensed = false;
        }
    }



    private void senseRobots() {
        if(robotsSensed) return;
        robotsSensed = true;
        numberOfSensableRobots = 0;
        boolean[] sensableRobotsIdHash = new boolean[MAX_TOTAL_NUMBER_OF_ROBOTS];
        
        for(int index = 0; index < numberOfSensors; index ++) {
            for(Robot sensableRobot : mySCs[index].senseNearbyGameObjects(Robot.class)) {
                int id = sensableRobot.getID();
                if(!sensableRobotsIdHash[id]) {
                    sensableRobots[numberOfSensableRobots++] = sensableRobot;
                    sensableRobotsIdHash[id] = true;
                }
            }
        }
    }
    
    public Robot[] getSensableRobots() {
        if(!robotsSensed) {
            senseRobots();
        }
        return sensableRobots;
    }
    
    public int getNumberOfSensableRobots() {
        if(!robotsSensed) {
            senseRobots();
        }
        return numberOfSensableRobots;
    }
    
    
    
    private void senseMines() {
        if(minesSensed) return;
        minesSensed = true;
        boolean[] sensableMinesIdHash = new boolean[MAX_TOTAL_NUMBER_OF_ROBOTS];
        
        for(int index = 0; index < numberOfSensors; index ++) {
            for(Mine sensableMine : mySCs[index].senseNearbyGameObjects(Mine.class)) {
                int id = sensableMine.getID();
                if(!sensableMinesIdHash[id]) {
                    sensableMines[numberOfSensableMines++] = sensableMine;
                    sensableMinesIdHash[id] = true;
                }
            }
        }
    }
    
    public Mine[] getSensableMines() {
        if(!minesSensed) {
            senseMines();
        }
        return sensableMines;
    }
    
    public int getNumberOfSensableMines() {
        if(!minesSensed) {
            senseMines();
        }
        return numberOfSensableMines;
    }
}
