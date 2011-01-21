package newTeam.player.flying;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.player.BasePlayer;
import newTeam.common.*;

public class FlyingConstructorPlayer extends BasePlayer {

    public FlyingConstructorPlayer(BaseState state) {

        super(state);
    }

    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return oldState;
    }

    @Override
    public void initialize() {

    }

    @Override
    public void doSpecificPlayerStatelessActions() {
        super.doSpecificPlayerStatelessActions();

    }

}
