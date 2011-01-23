package newTeam.player.flying;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.util.Logger;
import newTeam.state.idle.WaitingForConstructor;
import newTeam.common.QuantumConstants;
import newTeam.common.MessageCoder;

public class FlyingSensorPlayer extends FlyingPlayer {

    public FlyingSensorPlayer(BaseState state) {
        super(state);

        Logger.debug_printSashko( "I'm a flying sensor!" );

        myK.squadLeaderID = 0; //here used to refer to number of squad members
    }

    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return new WaitingForConstructor( oldState );
    }

    @Override
    public void initialize() {

    }

    @Override
    public void doSpecificPlayerStatelessActions() {
        super.doSpecificPlayerStatelessActions();

        if( Clock.getRoundNum() % QuantumConstants.PING_CYCLE_LENGTH == 0 )
        {
            int[] ints = { myK.squadLeaderID };
            String[] strings = {null};
            MapLocation[] locations = { myK.lastKnownEnemyLocation };

            myCH.myBCH.addToQueue( MessageCoder.encodeMessage( MessageCoder.FLYING_SENSOR_PING , myRC.getRobot().getID(), myRC.getLocation(), Clock.getRoundNum(), false, strings, ints, locations) );
        }

    }

}
