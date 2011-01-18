package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;

public class BuildingFirstRecycler extends BaseState {
    
    private boolean switchBuildOrder;


    public BuildingFirstRecycler(BaseState oldState, boolean givenSwitchBuildOrder) {
        super(oldState);
        switchBuildOrder = givenSwitchBuildOrder;
    }

    @Override
    public void senseAndUpdateKnowledge() {

    }

    @Override
    public BaseState getNextState() {

        BaseState result = this;
        if( myBH.finishedBuilding() )
        {
            if(switchBuildOrder) {
                result = new BuildingSecondRecycler(this, mySH.startingFirstMineToBeBuiltLocation);
            }
            else {
                result = new BuildingSecondRecycler(this, mySH.startingSecondMineToBeBuiltLocation);
            }
            result.senseAndUpdateKnowledge();
            return result.getNextState();
        }

        return result;
    }

    @Override
    public BaseState execute() {

        //add a sensor method that checks if the square is occupied
        if( !myBH.getCurrentlyBuilding() && myRC.getTeamResources() > Prefab.commRecycler.getTotalCost() + 10 )
        {
            if(switchBuildOrder) {
                myBH.buildUnit( Prefab.commRecycler , mySH.startingSecondMineToBeBuiltLocation);
            }
            else {
                myBH.buildUnit( Prefab.commRecycler , mySH.startingFirstMineToBeBuiltLocation);
            }
        }

        myBH.step();

        return this;
    }

}
