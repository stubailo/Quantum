/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package newTeam.handler;

import battlecode.common.*;

/**
 *
 * @author sashko
 */
public class MessageHandler {

    private Message[] messages;

    private RobotController myRC;

    public MessageHandler( RobotController rc )
    {
        myRC = rc;
    }

    public void receiveMessages()
    {
        messages = myRC.getAllMessages();
    }

    public Message[] getMessages()
    {
        return messages;
    }

}
