package newTeam;

import battlecode.common.*;

import newTeam.player.BasePlayer;
import newTeam.handler.ComponentsHandler;
import newTeam.common.Knowledge;
import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.handler.BroadcastHandler;
import newTeam.handler.BuilderHandler;
import newTeam.handler.MovementHandler;
import newTeam.handler.SensorHandler;
import newTeam.handler.WeaponHandler;
import newTeam.common.util.Logger;

public class RobotPlayer implements Runnable {
   
    /*** RobotController ***/
    private final  RobotController      myRC;
    
    /*** ComponentsHandler ***/
    private final  ComponentsHandler    myCH;
    
    /*** Knowledge ***/
    private final  Knowledge            myK;
    
    /*** Specific Player ***/
    private        BasePlayer           mySP; 
    
    /*** State ***/
    private        BaseState            myRS;


//    private        MessageHandler      msgHandler;

    
    public RobotPlayer(RobotController rc) {
        myRC = rc;

        myK  = new Knowledge(rc);
        SensorHandler sh = new SensorHandler(myRC, myK);
        myCH = new ComponentsHandler(new BroadcastHandler(myK),
                                     new BuilderHandler(myK, sh),
                                     new MovementHandler(myRC, myK),
                                     sh,
                                     new WeaponHandler());
        myRS = new BaseState(myRC, myK, myCH);
        mySP = BasePlayer.determineInitialSpecificPlayer(rc, myRS);
    }
    
    

    public void run() {
        
        while(true) {
            try {
                
                myK.doStatelessUpdate();
                
                myRC.setIndicatorString(0, myRS.getClass().getName());
                
                ComponentType[] newCompTypes = myCH.updateComponents(myRC);
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
                
                myRC.yield();
                

            }
            catch(Exception e) {
                Logger.debug_printExceptionMessage(e);
            }
        }

    }

/*    public void doCommonEndTurnActions()
    {
        myCH.broadcast( myK.msg().composeMessage() );
        myK.msg().emptyQueue();
    }*/
    
    

}
