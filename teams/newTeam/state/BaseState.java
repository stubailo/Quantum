package newTeam.state;

import battlecode.common.*;

import newTeam.handler.BroadcastHandler;
import newTeam.handler.BuilderHandler;
import newTeam.handler.ComponentsHandler;
import newTeam.handler.MovementHandler;
import newTeam.handler.SensorHandler;
import newTeam.handler.WeaponHandler;
import newTeam.common.Knowledge;

public class BaseState {
    
    protected final RobotController     myRC;
    protected final Knowledge           myK;
    protected final ComponentsHandler   myCH;
    
    protected final BroadcastHandler    myBCH;
    protected final BuilderHandler      myBH;
    protected final MovementHandler     myMH;
    protected final SensorHandler       mySH;
    protected final WeaponHandler       myWH;
    
    public BaseState(RobotController rc, Knowledge know, ComponentsHandler ch) {
        myRC  = rc;
        myK   = know;
        myCH  = ch;
        
        myBCH = ch.myBCH;
        myBH  = ch.myBH;
        myMH  = ch.myMH;
        mySH  = ch.mySH;
        myWH  = ch.myWH;
    }
    
    public BaseState(BaseState oldState) {
        myRC  = oldState.myRC;
        myK   = oldState.myK;
        myCH  = oldState.myCH;
        
        myBCH = oldState.myBCH;
        myBH  = oldState.myBH;
        myMH  = oldState.myMH;
        mySH  = oldState.mySH;
        myWH  = oldState.myWH;
    }
    
    public void senseAndUpdateKnowledge() {
        
    }
    
    public BaseState getNextState() {
        return this;
    }
    
    public void initialize() {
        
    }
    
    public void step() {
        
    }
    
    public void cleanup() {
        
    }

}
