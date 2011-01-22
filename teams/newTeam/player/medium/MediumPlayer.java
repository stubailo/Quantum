package newTeam.player.medium;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.exploring.SoldierExploring;
import newTeam.player.BasePlayer;

public class MediumPlayer extends BasePlayer {

    public MediumPlayer(BaseState state) {

        super(state);
    }

    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return new SoldierExploring( oldState );
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

        switch(compType) {
        
        case BLASTER:
        case RAILGUN:
            return new MediumSoldierPlayer(state);
        }
        
        return this;
    }

}
