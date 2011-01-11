/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team039.common;
import battlecode.common.*;
import team039.common.util.*;

/**
 *
 * @author sashko
 */
public class MessageHandler {

    private RobotController myRC;
    private MessageWrapper[] messageQueue;

    private Knowledge knowledge;

    public MessageHandler(RobotController in_RC, Knowledge know) {

        myRC = in_RC;

        knowledge = know;

        messageQueue = null;
    }

    /*
     * Should retrieve messages and then save new information to knowledge
     */
    public void receiveMessages() {
        Message[] newMessages;

        newMessages = myRC.getAllMessages();

        for (Message currentMessage : newMessages) {
            MessageWrapper newMsgWrapper = new MessageWrapper();
            String messageType = newMsgWrapper.decode(currentMessage);

            if( messageType.equals( MessageWrapper.RECYCLER_PING ) )
            {
                knowledge.recordRecyclerLocation(newMsgWrapper.getBroadcasterID(), newMsgWrapper.getTargetID(), newMsgWrapper.getBroadcasterLocation(), newMsgWrapper.getTargetLocation());
            } else if ( messageType.equals( MessageWrapper.PARENT_DESIGNATION ) )
            {
                if( newMsgWrapper.getTargetID() == myRC.getRobot().getID() )
                knowledge.receiveDesignation( newMsgWrapper.getIntData(0), newMsgWrapper.getLocationData(0) );

            }
            

        }
    }

    /*
     * In the future there will be capability to send more than one message per turn.
     * right now, every message added to the queue replaces the previous one.
     */
    public void addToQueue(MessageWrapper msgToAdd) {

        if (messageQueue == null) {
            messageQueue = new MessageWrapper[10];
        }
        messageQueue[0] = msgToAdd;
    }

    public void emptyQueue() {
        messageQueue = null;
    }

    public Message composeMessage() {
        if (messageQueue != null) {
            Message output = new Message();
            output = messageQueue[0].getMessage();
            return output;
        } else {
            return null;

        }
    }
}
