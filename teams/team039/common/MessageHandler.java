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
    private Message[] messageQueue;

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
boolean haveGottenPinged = false;
        for (Message currentMessage : newMessages) {

            
            if( haveGottenPinged==false && MessageCoder.getMessageType(currentMessage).equals( MessageCoder.RECYCLER_PING ) && !knowledge.myRC.getChassis().equals(Chassis.BUILDING) )
            {
                haveGottenPinged = true;
                knowledge.recordRecyclerLocation( RecyclerNode.getFromPing(currentMessage) );
            } else if( MessageCoder.getMessageType(currentMessage).equals( MessageCoder.RECYCLER_DESIGNATION ) 
                    && knowledge.myRC.getChassis().equals(Chassis.BUILDING)
                    && (knowledge.myRecyclerNode==null || !knowledge.myRecyclerNode.hasParent() ) )
            {
                if( knowledge.myRobotID != MessageCoder.getBroadcasterID(currentMessage) )
                {
                knowledge.myRecyclerNode = new RecyclerNode();
                knowledge.myRecyclerNode.myRobotID = knowledge.myRobotID;
                knowledge.myRecyclerNode.myLocation = knowledge.myLocation;
                knowledge.myRecyclerNode.parentRobotID = MessageCoder.getBroadcasterID(currentMessage);
                knowledge.myRecyclerNode.parentLocation = MessageCoder.getBroadcasterLocation(currentMessage);

                System.out.println( "designated? " + knowledge.myRecyclerNode );
                }
            }
        }
    }

    /*
     * In the future there will be capability to send more than one message per turn.
     * right now, every message added to the queue replaces the previous one.
     */
    public void addToQueue(Message msgToAdd) {

        if (messageQueue == null) {
            messageQueue = new Message[10];
        }
        messageQueue[0] = msgToAdd;
        
    }

    public void emptyQueue() {
        messageQueue = null;
    }

    public Message composeMessage() {
        if (messageQueue != null) {

            
            return messageQueue[0];

        } else {
            return null;

        }
    }
}
