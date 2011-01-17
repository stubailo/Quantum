package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;
import newTeam.state.idle.Idling;

public class BuildingSecondRecycler extends BaseState {


    public BuildingSecondRecycler(BaseState oldState) {
        super(oldState);

    }

    @Override
    public void senseAndUpdateKnowledge() {

    }

    @Override
    public BaseState getNextState() {

        if( myBH.finishedBuilding() )
        {
            return new Idling( this );
        }

        return this;
    }

    @Override
    public BaseState execute() {

        //add a sensor method that checks if the square is occupied
        if( !myBH.getCurrentlyBuilding() && myRC.getTeamResources() > Prefab.commRecycler.getTotalCost() + 10 )
        {
            myBH.buildUnit( Prefab.commRecycler , mySH.startingSecondMineToBeBuiltLocation);
        }

        myBH.step();

        return this;
    }

}
