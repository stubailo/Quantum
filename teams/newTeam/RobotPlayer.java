package newTeam;

import battlecode.common.*;

import newTeam.player.BasePlayer;
import newTeam.handler.ComponentsHandler;
import newTeam.common.Knowledge;
import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.handler.*;
import newTeam.common.util.Logger;

public class RobotPlayer implements Runnable {
   
    /*** RobotController ***/
    private final  RobotController      myRC;

    /*** Knowledge ***/
    private final  Knowledge            myK;
    
    /*** ComponentsHandler ***/
    private final  ComponentsHandler    myCH;
    
    /*** Other handlers ***/
    public  final  BroadcastHandler     myBCH;
    public  final  BuilderHandler       myBH;
    public  final  MovementHandler      myMH;
    public  final  SensorHandler        mySH;
    public  final  WeaponHandler        myWH;
    public  final  MessageHandler       myMSH;
    
    /*** Specific Player ***/
    private        BasePlayer           mySP; 
    
    /*** State ***/
    private        BaseState            myRS;


//    private        MessageHandler      msgHandler;

    
    public RobotPlayer(RobotController rc) {
        myRC  = rc;

        myK   = new Knowledge(rc);
        mySH  = new SensorHandler(myRC, myK);
        myBCH = new BroadcastHandler(myK, myRC);
        myBH  = new BuilderHandler(myK, mySH);
        myMH  = new MovementHandler(myRC, myK);
        myWH  = new WeaponHandler();
        myMSH = new MessageHandler( myRC );
        myCH  = new ComponentsHandler(myBCH,
                                      myBH,
                                      myMH,
                                      mySH,
                                      myWH,
                                      myMSH);
    }
    
    

    public void run() {
        
        
        // Round 0 actions, slightly different
        myK.doStatelessUpdate();
        myMSH.receiveMessages();
        
        ComponentType[] newCompTypes = myCH.updateComponents(myRC);
        
        // These two lines are different than the main loop
        myRS = new BaseState(myRC, myK, myCH);
        mySP = BasePlayer.determineInitialSpecificPlayer(myRC, myRS);
        
        myRC.setIndicatorString(0, myRS.getClass().getName());
        
        if(newCompTypes != null) {
            
            for(ComponentType newCompType : newCompTypes) {
                mySP = mySP.determineSpecificPlayerGivenNewComponent(newCompType, myRS);
                myRS = mySP.getNewStateBasedOnNewSpecificPlayer(myRS);
            }
        }

        
        myRS.senseAndUpdateKnowledge();
        
        
        myRS = myRS.getNextState();
        
        // initialize and clean up as necessary?
        
        myRC.setIndicatorString(1, myRS.getClass().getName());

        myRS = myRS.execute();
        
        myRC.setIndicatorString(2, myRS.getClass().getName());
        
        mySP.doSpecificPlayerStatelessActions();
        
        myRC.yield();
        
        while(true) {
            try {
                
                // Rounds 1 and later actions
                
                // Stateless actions
                myK.doStatelessUpdate();
                mySH.refresh();
                myMSH.receiveMessages();
                
                myRC.setIndicatorString(0, myRS.getClass().getName());
                
                newCompTypes = myCH.updateComponents(myRC);
                
                if(newCompTypes != null) {
                    
                    for(ComponentType newCompType : newCompTypes) {
                        mySP = mySP.determineSpecificPlayerGivenNewComponent(newCompType, myRS);
                        myRS = mySP.getNewStateBasedOnNewSpecificPlayer(myRS);
                    }
                }
                // State actions
                myRS.senseAndUpdateKnowledge();
                
                myRS = myRS.getNextState();
                
                // initialize and clean up as necessary?
                
                myRC.setIndicatorString(1, myRS.getClass().getName());
                
                myRS = myRS.execute();
                
                myRC.setIndicatorString(2, myRS.getClass().getName());
                
                mySP.doSpecificPlayerStatelessActions();

                myBCH.broadcastFromQueue();
                
                myRC.yield();
                

            }
            catch(Exception e) {
                Logger.debug_printExceptionMessage(e);
            }
        }

    }
    
    

}
