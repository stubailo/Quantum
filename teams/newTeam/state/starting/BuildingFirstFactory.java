package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.starting.StartingMovingToBuild;
import newTeam.common.MessageCoder;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;

public class BuildingFirstFactory extends BaseState {
    
    private final MapLocation factoryLocation;
    private final MapLocation armoryLocation;


    public BuildingFirstFactory(BaseState oldState, MapLocation givenFactoryLocation, MapLocation givenArmoryLocation) {
        super(oldState);
        factoryLocation = givenFactoryLocation;
        armoryLocation = givenArmoryLocation;
    }

    @Override
    public void senseAndUpdateKnowledge() {
        mySH.senseEdges();

    }

    @Override
    public BaseState getNextState() {

        if( myBH.finishedBuilding() )
        {
            int[] ints = { 0 };
            String[] strings = { null };
            MapLocation[] locations = { factoryLocation };

            myBCH.addToQueue( MessageCoder.encodeMessage( MessageCoder.FACTORY_BUILT, myK.myRobotID, myK.myLocation, Clock.getRoundNum(), false, strings, ints, locations) );

            BaseState result = new StartingMovingToBuild( this, armoryLocation);
            result.senseAndUpdateKnowledge();
            return result.getNextState();
        }

        return this;
    }

    @Override
    public BaseState execute() {

        //add a sensor method that checks if the square is occupied
        if( !myBH.getCurrentlyBuilding() && myRC.getTeamResources() > Prefab.factory.getTotalCost() )
        {
            myBH.buildUnit( Prefab.factory , factoryLocation);
        }

        myBH.step();

        return this;
    }

}
