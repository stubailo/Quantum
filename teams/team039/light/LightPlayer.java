package team039.light;

import team039.common.*;
import team039.common.util.Logger;
import team039.handler.ComponentsHandler;
import battlecode.common.*;

public class LightPlayer extends SpecificPlayerImpl {
    
    private final RobotController   myRC;
    private final Knowledge         knowledge;
    private final ComponentsHandler compHandler;
        
    public LightPlayer(RobotController rc,
                       Knowledge know,
                       ComponentsHandler compHand) {
        
        super(rc, know, compHand);
        myRC        = rc;
        knowledge   = know;
        compHandler = compHand;
    }
    
    public void doSpecificActions() {
        super.doSpecificActions();
    }
    
    public void doSpecificFirstRoundActions() {
        super.doSpecificActions();
    }

    public void beginningStateSwitches()
    {
        if( knowledge.myState == RobotState.JUST_BUILT )
        {
            Logger.debug_print( "I called JUST_BUILT at round " + knowledge.roundNum  );
            knowledge.myState = RobotState.IDLE;
        }
    }

    public Direction deflect() {

        if( !knowledge.myRecyclerNode.hasParent() )
        {
            return Direction.NONE;
        }

        
            Direction prefDirection;

            MapLocation prefVector = knowledge.myRecyclerNode.getVector();
            /*if (knowledge.oldRecyclerNode != null) {
                MapLocation myMovementVector = knowledge.myLocation.add(-knowledge.oldRecyclerNode.myLocation.x, -knowledge.oldRecyclerNode.myLocation.y);
                prefVector = prefVector.add(myMovementVector.x, myMovementVector.y);
            }*/

            MapLocation origin = new MapLocation(0, 0);

            prefDirection = origin.directionTo(prefVector);

            int random = Clock.getRoundNum() + myRC.getRobot().getID();

            Direction newDirection;

            switch (random % 14) {
                case 6:
                case 7:
                case 8:
                    newDirection = prefDirection.rotateLeft();
                    break;
                case 9:
                case 10:
                case 11:
                    newDirection = prefDirection.rotateRight();
                    break;
                case 12:
                    newDirection = prefDirection.rotateLeft().rotateLeft();
                    break;
                case 13:
                    newDirection = prefDirection.rotateRight().rotateRight();
                    break;
                default:
                    newDirection = prefDirection;
            }

            return( newDirection );

    }
    
    public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
        SpecificPlayer result = this;

        

        switch(compType) {
        case CONSTRUCTOR:
            result = new LightConstructorPlayer(myRC, knowledge, compHandler);
            result.initialize();
            break;
        case BLASTER:
            result = new LightSoldierPlayer(myRC, knowledge, compHandler);
            result.initialize();
            break;
        }
        
        return result;
    }

}
