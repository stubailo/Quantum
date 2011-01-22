package newTeam.state.exploring;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.exploring.FlyingSensorExploring;

public class FlyingSensorFleeing extends BaseState {


    public FlyingSensorFleeing(BaseState oldState) {

        super(oldState);

        myMH.initializeNavigationTo( myK.myRecyclerNode.myLocation, NavigatorType.BUG);

        
    }

    @Override
    public void senseAndUpdateKnowledge() {

    }

    @Override
    public BaseState getNextState() {

        return this;
    }

    @Override
    public BaseState execute() {

        if( myK.myLocation.distanceSquaredTo( myK.myRecyclerNode.myLocation ) < 9 )
        {
            return new FlyingSensorExploring( this );
        }

        myMH.step();

        return this;
    }

}
