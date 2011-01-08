package team039.light;

import team039.common.*;
import battlecode.common.*;

public class LightPlayer implements SpecificPlayer {
    
    private final RobotController   myRC;
    private final Knowledge         knowledge;
    private final ComponentsHandler compHandler;
        
    public LightPlayer(RobotController rc,
                       Knowledge know,
                       ComponentsHandler compHand) {
        
        myRC        = rc;
        knowledge   = know;
        compHandler = compHand;
    }
    
    public void doSpecificActions() {
        beginningStateSwitches();
    }
    
    public void doSpecificFirstRoundActions() {
        
    }

    public void beginningStateSwitches()
    {
        if( knowledge.myState == RobotState.JUST_BUILT )
        {
            System.out.println( "I called JUST_BUILT at round " + knowledge.roundNum  );
            knowledge.myState = RobotState.IDLE;
        }
    }
    
    public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
        SpecificPlayer result = this;
        
        switch(compType) {
        case CONSTRUCTOR:
            result = new LightConstructorPlayer(myRC, knowledge, compHandler);
            break;
        case BLASTER:
            result = new LightSoldierPlayer(myRC, knowledge, compHandler);
            break;
        }
        
        return result;
    }

}
