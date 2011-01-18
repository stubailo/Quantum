/*package newTeam.state.recycler;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.*;
import newTeam.common.util.Logger;

public class RecyclerState extends BaseState {

    public RecyclerState(BaseState oldState) {
        super(oldState);

        if( myK.myRecyclerNode == null )
        {
            myK.myRecyclerNode = new RecyclerNode( myK.myRobotID, myK.myLocation );
        }
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

        

        if( myBCH.canBroadcast() && Clock.getRoundNum()%QuantumConstants.PING_CYCLE_LENGTH == 0 )
        {
            myBCH.addToQueue( myK.myRecyclerNode.generatePing() );
        }

        myBCH.broadcastFromQueue();

        return this;
    }

}*/
