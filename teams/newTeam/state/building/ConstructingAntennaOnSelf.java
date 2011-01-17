package newTeam.state.building;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;
import newTeam.state.idle.Idling;

public class ConstructingAntennaOnSelf extends BaseState {

    RobotInfo firstLightInfo;

    public ConstructingAntennaOnSelf(BaseState oldState) {
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
        if( !myBH.getCurrentlyBuilding() && myRC.getTeamResources() > Prefab.commRecycler.getComponentCost() + 10 )
        {
            myBH.buildComponents( Prefab.startingConstructor , myRC.getLocation(), RobotLevel.ON_GROUND);
        }

        myBH.step();

        return this;
    }

}
