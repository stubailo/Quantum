package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.exploring.Exploring;
import newTeam.state.starting.StartingMovingToBuild;
import newTeam.common.MessageCoder;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;

public class BuildingFirstArmory extends BaseState {
    
    private final MapLocation armoryLocation;


    public BuildingFirstArmory(BaseState oldState, MapLocation givenArmoryLocation) {
        super(oldState);
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
            MapLocation[] locations = { armoryLocation };

            myBCH.addToQueue( MessageCoder.encodeMessage( MessageCoder.ARMORY_BUILT, myK.myRobotID, myK.myLocation, Clock.getRoundNum(), false, strings, ints, locations) );

            BaseState result = new Exploring( this);
            result.senseAndUpdateKnowledge();
            return result.getNextState();
        }

        return this;
    }

    @Override
    public BaseState execute() {

        //add a sensor method that checks if the square is occupied
        if( !myBH.getCurrentlyBuilding() && myRC.getTeamResources() > Prefab.armory.getTotalCost() )
        {
            myBH.buildUnit( Prefab.armory , armoryLocation);
        }

        myBH.step();

        return this;
    }

}
