package newTeam.state.factory;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;
import newTeam.state.idle.Idling;

public class BuildingMediums extends BaseState {

    public BuildingMediums(BaseState oldState) {
        super(oldState);

    }

    @Override
    public void senseAndUpdateKnowledge() {
    }

    @Override
    public BaseState getNextState() {

        if( myBH.finishedBuilding() )
        {            
            return new Idling(this);
        }

        return this;
    }

    @Override
    public BaseState execute() {

        //add a sensor method that checks if the square is occupied
        if( !myBH.getCurrentlyBuilding() && myRC.getTeamResources() > Prefab.mediumSoldier.getTotalCost() + 200 )
        {
            MapLocation buildLocation = mySH.findEmptyLocationToBuild();
            if(buildLocation!=null)
            myBH.buildUnit( Prefab.mediumSoldier , buildLocation);
        }

        myBH.step();

        return this;
    }

}
