package newTeam.state.building;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.idle.Idling;

public class MovingToBuildArmory extends BaseState {

    MapLocation[] adjacentLocations;
    MapLocation facLoc;

    int strategyNum = 0; //which strategy are we using to try to find a spot?

    public MovingToBuildArmory(BaseState oldState, MapLocation factoryLocation) {
        super(oldState);

        facLoc = factoryLocation;
        adjacentLocations = new MapLocation[4];

        Direction primaryDir = factoryLocation.directionTo(myK.myRecyclerNode.myLocation);

        if( primaryDir.isDiagonal() )
        {
            adjacentLocations[0] = factoryLocation.add(primaryDir.rotateLeft());
            adjacentLocations[1] = factoryLocation.add(primaryDir.rotateRight());
        } else {
            adjacentLocations[0] = factoryLocation.add(primaryDir.rotateLeft());
            adjacentLocations[1] = factoryLocation.add(primaryDir.rotateRight());
            adjacentLocations[2] = factoryLocation.add(primaryDir.rotateLeft().rotateLeft());
            adjacentLocations[3] = factoryLocation.add(primaryDir.rotateRight().rotateRight());
        }

        myMH.circle(factoryLocation, true);
    }

    @Override
    public void senseAndUpdateKnowledge() {
    }

    @Override
    public BaseState getNextState() {

        if( inGoodBuildLocation() )
        {
            return new BuildingArmory( this ); //BuildingFactory( this, myRC.getLocation() );
        }

        if( strategyNum == 0 && myMH.getPathBlocked() )
        {
            Logger.debug_printSashko( "path blocked yo");
            Direction tempDir = myK.myRecyclerNode.myLocation.directionTo(facLoc).rotateRight();

            strategyNum = 1;
            myMH.initializeNavigationTo(myK.myRecyclerNode.myLocation.add(tempDir), NavigatorType.BUG);
        }

        if( strategyNum == 1 && myMH.reachedGoal() )
        {
            strategyNum = 2;
            myMH.circle(myK.myRecyclerNode.myLocation, true);
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
        boolean adj = false;

        for( MapLocation location : adjacentLocations )
         {
                if( location != null && myRC.getLocation().isAdjacentTo( location ) && myMH.canMove( myRC.getLocation().directionTo(location) ) )
                {
                    
                    adj=true;
                    break;
                }
        }

        if( adj == false )
        {
            return false;
        }

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
