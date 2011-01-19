package newTeam.handler;

import battlecode.common.*;

import newTeam.common.Knowledge;
import newTeam.common.QuantumConstants;
import newTeam.common.util.Logger;

public class BroadcastHandler {

    private BroadcastController myBCC;
    private RobotController myRC;

    private Knowledge knowledge;

    private Message[] myMessageQueue;

    private int queueLocation;

    public BroadcastHandler ( Knowledge know, RobotController rc )
    {
        knowledge = know;
        myBCC = null;
        myRC = rc;

        queueLocation = 0;
        myMessageQueue = new Message[QuantumConstants.MESSAGE_QUEUE_LENGTH];
    }

    public void addBCC( BroadcastController newBCC )
    {
        

        if(myBCC != null) {
            int currentRange = myBCC.type().range;
            int newRange = newBCC.type().range;
    
            if( newRange > currentRange )
            {
                myBCC = newBCC;
            }
        }
        else {
            myBCC = newBCC;
        }

        Logger.debug_printSashko(myBCC.toString());
    }

    public boolean canBroadcast()
    {
        return myBCC != null;
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
            return myMessageQueue[ queueLocation ];
        } else {
            return null;
        }
    }

    public void broadcastFromQueue()
    {

        if( myBCC != null && !myBCC.isActive() )
        {
            Message nextMessage = getFromQueue();

            if( nextMessage != null )
            {

                try {
                    myBCC.broadcast(nextMessage);
                } catch ( Exception e )
                {
                    Logger.debug_printExceptionMessage(e);
                }
            }
        }
    }

    public Message[] receiveMessages()
    {
        return myRC.getAllMessages();
    }

}
