package newTeam.player.medium;

import battlecode.common.*;

import newTeam.player.BasePlayer;
import newTeam.state.BaseState;
import newTeam.state.idle.Idling;

public class MediumSoldierPlayer extends MediumPlayer {
    
    public MediumSoldierPlayer(BaseState state) {

        super(state);
    }

    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return new Idling( oldState );
    }

    @Override
    public void initialize() {

    }

    @Override
    public void doSpecificPlayerStatelessActions() {
        super.doSpecificPlayerStatelessActions();
    }

    @Override
    public BasePlayer determineSpecificPlayerGivenNewComponent(ComponentType compType,
                                                               BaseState state) {
        return this;
    }
}
