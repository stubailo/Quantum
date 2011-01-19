package newTeam.player.light;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.player.BasePlayer;
import newTeam.common.*;

public class LightPlayer extends BasePlayer {
    
    public LightPlayer(BaseState state) {
        
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
        
        case CONSTRUCTOR:
            return new LightConstructorPlayer(state);
        case BLASTER:
            return new LightSoldierPlayer(state);
        }
        
        return this;
    }

}
