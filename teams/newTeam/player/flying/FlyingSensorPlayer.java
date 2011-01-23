package newTeam.player.flying;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.*;
import newTeam.state.exploring.FlyingSensorExploring;
import newTeam.player.BasePlayer;
import newTeam.common.*;

public class FlyingSensorPlayer extends FlyingPlayer {

    public FlyingSensorPlayer(BaseState state) {
        super(state);

        System.out.println( "I'm a flying sensor!" );

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
