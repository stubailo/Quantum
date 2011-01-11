package team039.handler;

import battlecode.common.*;
import team039.common.Knowledge;

public class SensorHandler {
    
    private final RobotController myRC;
    private final Knowledge       knowledge;
    
    
    /*** Controller ***/
    //private final SensorController[] mySensorSlots
    //private final SensorController[] mySCs
    //private final int                numberOfSensors;

    public SensorHandler(RobotController rc, Knowledge know) {
        
        myRC      = rc;
        knowledge = know;
    }
    
}
