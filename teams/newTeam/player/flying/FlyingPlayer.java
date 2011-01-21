package newTeam.player.flying;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.player.BasePlayer;
import newTeam.common.*;

public class FlyingPlayer extends BasePlayer {

    public FlyingPlayer(BaseState state) {

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

        boolean hasBeenPinged = false;

        Message[] messages = myCH.myMSH.getMessages();

        for( Message message : messages )
        {
            if( !hasBeenPinged && MessageCoder.getMessageType(message).equals(MessageCoder.RECYCLER_PING) && MessageCoder.isValid(message) )
            {
                myK.myRecyclerNode = RecyclerNode.getFromPing(message);
            }
        }
    }

    @Override
    public BasePlayer determineSpecificPlayerGivenNewComponent(ComponentType compType,
                                                               BaseState state) {

        switch(compType) {

        case RADAR:
            return new FlyingSensorPlayer(state);
        case CONSTRUCTOR:
            return new FlyingConstructorPlayer(state);
        }

        return this;
    }

}
