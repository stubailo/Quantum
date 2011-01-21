package newTeam.state.exploring;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;
import newTeam.state.idle.Idling;

public class FlyingSensorExploring extends BaseState {

    public FlyingSensorExploring(BaseState oldState) {
        super(oldState);

        myMH.zigZag();
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

        myMH.step();

        return this;
    }

}
