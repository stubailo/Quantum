package sprint;

import sprint.building.BuildingPlayer;
import sprint.building.recycler.StartingBuildingPlayer;
import sprint.common.*;
import sprint.common.util.Logger;
import sprint.light.LightPlayer;
import sprint.light.StartingLightPlayer;
import battlecode.common.*;

public class RobotPlayer implements Runnable {
   
    private final  RobotController     myRC;
    
    /*** ComponentsHandler ***/
    private final  ComponentsHandler   compHandler;
    
    /*** Knowledge ***/
    private final  Knowledge           knowledge;
    
    /*** Specific Player ***/
    private        SpecificPlayer      specificPlayer;  


    private        MessageHandler      msgHandler;

    
    public RobotPlayer(RobotController rc) {
        myRC = rc;

        knowledge = new Knowledge(myRC);
        compHandler = new ComponentsHandler(myRC, knowledge);
    }
    
    

    public void run() {
        
        // Order of these two should be determined by dependency.
        doCommonFirstRoundActions();

        doCommonActions();
        specificPlayer.doSpecificFirstRoundActions();
        specificPlayer.doSpecificActions();
        while(true) {
            try {
                debug_testSomething();
                debug_setStrings();
                myRC.yield();
                
                // Depending on new components, SpecificPlayer type might change!
                doCommonActions();

                specificPlayer.doSpecificActions();

                doCommonEndTurnActions();
            }
            catch(Exception e) {
                Logger.debug_printExceptionMessage(e);
            }
        }

    }
    
    

    public void doCommonFirstRoundActions() {
        debug_printGameConstants();
        knowledge.update();
        specificPlayer = new SpecificPlayerImpl(myRC, knowledge, compHandler);

        knowledge.myState = RobotState.JUST_BUILT;
    }
    
    
    
    public void doCommonActions() {
        knowledge.update();
        
        ComponentType[] newCompTypes = compHandler.updateComponents();
        if(newCompTypes != null) {
            
            for(ComponentType newCompType : newCompTypes) {
                specificPlayer = specificPlayer.determineSpecificPlayer(newCompType);
                
            }
            
        }
    }

    public void doCommonEndTurnActions()
    {
        compHandler.broadcast( knowledge.msg().composeMessage() );
        knowledge.msg().emptyQueue();
    }
    
    

    public void debug_testSomething() {
    }
    
    
    /**
     * Indicator string use should be contained within this method.
     */
    public void debug_setStrings() { 
        myRC.setIndicatorString(0, knowledge.myLocation.toString());
        myRC.setIndicatorString(1, String.valueOf(knowledge.deltaFlux));
        myRC.setIndicatorString(2, String.valueOf(knowledge.totalFlux));
    }
    
    public static void debug_printGameConstants() {
        Logger.debug_print("MAP_MAX_WIDTH: " + String.valueOf(GameConstants.MAP_MAX_WIDTH));
    }
}
