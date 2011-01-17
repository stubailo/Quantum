package newTeam.state.building;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;

public class ConstructingAntennaOnFirstLight extends BaseState {

    RobotInfo firstLightInfo;

    public ConstructingAntennaOnFirstLight(BaseState oldState) {
        super(oldState);

    }

    @Override
    public void senseAndUpdateKnowledge() {
        mySH.senseStartingLightPlayer();
    }

    @Override
    public BaseState getNextState() {

        if( myBH.finishedBuilding() )
        {
            return new ConstructingAntennaOnSelf( this );
        }

        return this;
    }

    @Override
    public BaseState execute() {

        //add a sensor method that checks if the square is occupied
        if( !myBH.getCurrentlyBuilding() && myRC.getTeamResources() > Prefab.startingConstructor.getComponentCost() + 10 )
        {
            myBH.buildComponents( Prefab.startingConstructor , mySH.startingLightInfo.location, RobotLevel.ON_GROUND);
        }

        myBH.step();

        return this;
    }

}
