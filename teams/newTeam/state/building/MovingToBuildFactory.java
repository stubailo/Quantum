package newTeam.state.building;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.idle.Idling;

public class MovingToBuildFactory extends BaseState {

    public MovingToBuildFactory(BaseState oldState) {
        super(oldState);

        myMH.circle(myK.myRecyclerNode.myLocation, true);
    }

    @Override
    public void senseAndUpdateKnowledge() {
    }

    @Override
    public BaseState getNextState() {

        if( inGoodBuildLocation() )
        {
            return new BuildingFactory( this ); //BuildingFactory( this, myRC.getLocation() );
        }

        return this;
    }

    @Override
    public BaseState execute() {

        myMH.step();

        return this;
    }

    public boolean inGoodBuildLocation()
    {
        Direction testDirection = Direction.EAST;
        boolean foundVoid = false;

        for( int i=0; i<8; i++ )
        {
            if(!myMH.canMove( testDirection ) )
            {
                if( !testDirection.isDiagonal() )
                {
                    foundVoid = true;
                    break;
                } else {
                    if( !myMH.canMove(testDirection.rotateRight()) )
                    {
                        foundVoid = true;
                        break;
                    }
                }
            }
            testDirection = testDirection.rotateRight();
        }

        if( !foundVoid )
        {
            return true;
        }
        
        boolean foundEmpty = false;

        for( int i=0; i<8; i++ )
        {
            if(myMH.canMove( testDirection ))
            {
                foundEmpty = true;
                break;
            }

            testDirection = testDirection.rotateRight();
        }

        if( !foundEmpty )
        {
            return false;
        }

        //now you should be on the first empty clockwise direction

        foundVoid = false;

        Direction startDirection = testDirection;

        Direction nextDirection;

        nextDirection = testDirection.rotateRight();

        if( foundVoid == false && myMH.canMove(nextDirection) )
            {
                testDirection = nextDirection;
            } else if( foundVoid == false && !testDirection.isDiagonal() && myMH.canMove(nextDirection.rotateRight()) ) {
                testDirection = nextDirection.rotateRight();
            } else if( foundVoid == false ) {
                foundVoid = true;
                testDirection = nextDirection;
            } else if( foundVoid == true && !myMH.canMove(testDirection) ){
                testDirection = nextDirection;
            } else {
                return false;
            }

        while( testDirection != startDirection )
        {
            nextDirection = testDirection.rotateRight();

            if( foundVoid == false && myMH.canMove(nextDirection) )
            {
                testDirection = nextDirection;
            } else if( foundVoid == false && !testDirection.isDiagonal() && myMH.canMove(nextDirection.rotateRight()) ) {
                testDirection = nextDirection.rotateRight();
            } else if( foundVoid == false ) {
                foundVoid = true;
                testDirection = nextDirection;
            } else if( foundVoid == true && !myMH.canMove(testDirection) ){
                testDirection = nextDirection;
            } else {
                return false;
            }
        }

        return true;

    }

}
