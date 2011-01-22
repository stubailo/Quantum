package newTeam.player.flying;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.player.BasePlayer;
import newTeam.common.*;
import newTeam.state.exploring.LookForSensorToFollow;

public class FlyingConstructorPlayer extends BasePlayer {

    public FlyingConstructorPlayer(BaseState state) {

        super(state);
    }

    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return new LookForSensorToFollow( oldState );
    }

    @Override
    public void initialize() {

    }

    @Override
    public void doSpecificPlayerStatelessActions() {
        super.doSpecificPlayerStatelessActions();

    }

}
