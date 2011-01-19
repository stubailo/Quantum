package newTeam.state.building;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.idle.Idling;
import newTeam.state.building.WaitUntilDone;

public class GoToNextBuilding extends BaseState {

    MapLocation targetLocation;

    public GoToNextBuilding( BaseState oldState, MapLocation goTo ) {
        super(oldState);

        targetLocation = goTo;

        myMH.initializeNavigationToAdjacent(targetLocation, NavigatorType.BUG);
    }

    @Override
    public void senseAndUpdateKnowledge() {
    }

    @Override
    public BaseState getNextState() {

        if(myMH.reachedGoal())
        {

            return new WaitUntilDone( this );
        }

        return this;
    }

    @Override
    public BaseState execute() {

        
        myMH.step();

        return this;
    }

}
