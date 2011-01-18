package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;

public class ConstructingAntennaOnFirstLight extends BaseState {

    RobotInfo firstLightInfo;

    public ConstructingAntennaOnFirstLight(BaseState oldState) {
        super(oldState);

    }

    @Override
    public void senseAndUpdateKnowledge() {
        if(!mySH.amLowestIDRecycler()) {
            myRC.turnOff();
        }
        mySH.senseStartingLightPlayer();
    }

    @Override
    public BaseState getNextState() {

        if( myBH.finishedBuilding() )
        {
            BaseState result = new ConstructingAntennaOnSelf( this );
            result.senseAndUpdateKnowledge();
            result = result.getNextState();
            return result;
        }

        return this;
    }

    @Override
    public BaseState execute() {

        //add a sensor method that checks if the square is occupied
        if( !myBH.getCurrentlyBuilding() && myRC.getTeamResources() > ComponentType.ANTENNA.cost + 10 )
        {
            myBH.buildComponents( Prefab.startingConstructor , mySH.startingLightInfo.location, RobotLevel.ON_GROUND);
        }

        myBH.step();

        if( myBH.finishedBuilding() )
        {
            BaseState result = new ConstructingAntennaOnSelf( this );
            result.senseAndUpdateKnowledge();
            result = result.getNextState();
            result.execute();
            return result;
        }

        return this;
    }

}
