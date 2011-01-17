package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;

public class BuildingFirstRecycler extends BaseState {


    public BuildingFirstRecycler(BaseState oldState) {
        super(oldState);
        
    }

    @Override
    public void senseAndUpdateKnowledge() {

    }

    @Override
    public BaseState getNextState() {

        if( myBH.finishedBuilding() )
        {
            return new BuildingSecondRecycler( this );
        }

        return this;
    }

    @Override
    public BaseState execute() {

        //add a sensor method that checks if the square is occupied
        if( !myBH.getCurrentlyBuilding() && myRC.getTeamResources() > Prefab.commRecycler.getTotalCost() + 10 )
        {
            myBH.buildUnit( Prefab.commRecycler , mySH.startingFirstMineToBeBuiltLocation);
        }

        myBH.step();

        return this;
    }

}
