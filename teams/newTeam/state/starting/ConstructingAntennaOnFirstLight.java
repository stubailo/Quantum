package newTeam.state.starting;

import newTeam.state.recycler.ConstructingAntennaOnSelf;
import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;

public class ConstructingAntennaOnFirstLight extends BaseState {

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
            BaseState result = new Idling( this );
            return result;
        }

        return this;
    }

    @Override
    public BaseState execute() {

        //add a sensor method that checks if the square is occupied
        
        if( !myBH.getCurrentlyBuilding() && myK.totalFlux > Prefab.startingConstructor.getComponentCost() )
        {
            myBH.buildComponents( Prefab.startingConstructor , mySH.startingLightInfo.location);
        }

        myBH.step();

        if( myBH.finishedBuilding() )
        {
            BaseState result = new ConstructingAntennaOnSelf( this );
            return result;
        }

        return this;
    }

}
