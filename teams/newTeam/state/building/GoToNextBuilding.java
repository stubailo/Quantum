package newTeam.state.building;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.idle.Idling;

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
        return this;
    }

    @Override
    public BaseState execute() {

        if(!myMH.reachedGoal())
        myMH.step();

        return this;
    }

}
