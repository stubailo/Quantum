package newTeam.state.building;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.state.idle.Idling;

public class MovingToBuildFactory extends BaseState {

    public MovingToBuildFactory(BaseState oldState) {
        super(oldState);

    }

    @Override
    public void senseAndUpdateKnowledge() {
    }

    @Override
    public BaseState getNextState() {

        if( inGoodBuildLocation() )
        {
            return new BuildingFactory( this, myRC.getLocation() );
        }

        return this;
    }

    @Override
    public BaseState execute() {



        return this;
    }

    public boolean inGoodBuildLocation()
    {
        Direction direction = Direction.NORTH;

        boolean foundVoid = false;
        boolean foundVoidAndEmpty = false;

        for ( int i=0; i<8; i++ )
        {
            if( !myMH.canMove(direction) )
            {
                foundVoid = true;
                break;
            }
            direction = direction.rotateRight();
        }

        if( !foundVoid )
        {
            return true;
        }

        for ( int i=0; i<8; i++ )
        {
            if( myMH.canMove(direction) )
            {
                foundVoidAndEmpty = true;
                break;
            }
            direction = direction.rotateRight();
        }

        if( !foundVoidAndEmpty )
        {
            return false;
        }

        int numberOfEmpties = 0;
        foundVoid = false;

        boolean discontinuous = false;

        for ( int i=0; i<8; i++ )
        {
            if( !foundVoid && myMH.canMove(direction) )
            {
                numberOfEmpties++;
            } else if (!foundVoid && !myMH.canMove(direction) ) {
                foundVoid = true;
            } else if (foundVoid && myMH.canMove(direction)  ) {
                discontinuous = true;
                break;
            }
            direction = direction.rotateRight();
        }

        Logger.debug_printSashko("empties: " + numberOfEmpties);

        if( discontinuous )
        {
            return false;
        }

        if( numberOfEmpties > 2 )
        {
            return true;
        } else {
            return false;
        }
    }

}
