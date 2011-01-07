/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team039.common;

import team039.building.BuildingPlayer;
import team039.building.recycler.StartingBuildingPlayer;
import team039.common.ComponentsHandler;
import team039.common.Knowledge;
import team039.common.SpecificPlayer;
import team039.light.LightPlayer;
import team039.light.StartingLightPlayer;
import battlecode.common.*;

/**
 *
 * @author sashko
 */
public class MessageHandler {

    private RobotController myRC;
    private MessageWrapper[] messageQueue;

    public MessageHandler(RobotController in_RC) {

        myRC = in_RC;

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
            System.out.println("Received: " + newMsgWrapper.decode(currentMessage));
        }
    }



    /*
     * In the future there will be capability to send more than one message per turn.
     * right now, every message added to the queue replaces the previous one.
     */
    public void addToQueue(MessageWrapper msgToAdd) {

        if( messageQueue == null) { messageQueue = new MessageWrapper[10]; }
        messageQueue[ 0] = msgToAdd;
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
