package newTeam.state;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.*;
import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.idle.Idling;

public class Wait extends BaseState {

    BaseState nextState;
    int turnsWaited;
    int turnsToWait;

    public Wait(int numTurns, BaseState oldState, BaseState newState) {
        super(oldState);

        turnsToWait = numTurns;
        nextState = newState;
        turnsWaited = 0;
    }

    @Override
    public void senseAndUpdateKnowledge() {
        turnsWaited++;
    }

    @Override
    public BaseState getNextState() {
        if(turnsWaited == turnsToWait)
            return nextState;
        return this;
    }

    @Override
    public BaseState execute() {
        return this;
    }

}
