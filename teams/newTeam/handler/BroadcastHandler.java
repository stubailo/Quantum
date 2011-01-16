package newTeam.handler;

import battlecode.common.*;

import newTeam.common.Knowledge;
import newTeam.common.QuantumConstants;
import newTeam.common.util.Logger;

public class BroadcastHandler {

    private BroadcastController myBCC;

    private Knowledge knowledge;

    private Message[] myMessageQueue;

    private int queueLocation;

    public BroadcastHandler ( Knowledge know )
    {
        knowledge = know;
        myBCC = null;

        queueLocation = 0;
        myMessageQueue = new Message[QuantumConstants.MESSAGE_QUEUE_LENGTH];
    }

    public void addBCC( BroadcastController newBCC )
    {
        int currentRange = myBCC.type().range;
        int newRange = newBCC.type().range;

        if( newRange > currentRange )
        {
            myBCC = newBCC;
        }
    }

    public void addToQueue(Message msgToAdd) {
        if( queueLocation < QuantumConstants.MESSAGE_QUEUE_LENGTH )
        myMessageQueue[ queueLocation ] = msgToAdd;
        queueLocation++;
    }

    public Message getFromQueue() {
        if( queueLocation > 0 )
        {
            queueLocation--;
            return myMessageQueue[ queueLocation + 1 ];
        } else {
            return null;
        }
    }

    public void broadcastFromQueue()
    {
        if( myBCC != null )
        {
            try {
                myBCC.broadcast(getFromQueue());
            } catch ( Exception e )
            {
                Logger.debug_printExceptionMessage(e);
            }
        }
    }

}
