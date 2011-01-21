package newTeam.player.flying;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.exploring.FlyingSensorExploring;
import newTeam.player.BasePlayer;
import newTeam.common.*;

public class FlyingSensorPlayer extends BasePlayer {

    public FlyingSensorPlayer(BaseState state) {
        super(state);

        System.out.println( "I'm a flying sensor!" );
    }

    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return new FlyingSensorExploring( oldState );
    }

    @Override
    public void initialize() {

    }

    @Override
    public void doSpecificPlayerStatelessActions() {
        super.doSpecificPlayerStatelessActions();

    }

}
