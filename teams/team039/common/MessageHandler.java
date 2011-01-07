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

    RobotController myRC;
    Knowledge knowledge;

    public MessageHandler ( RobotController in_RC, Knowledge know  )
    {
        knowledge = know;
        myRC = in_RC;
    }

    /*
     * Should retrieve messages and then save new information to knowledge
     */
    public void receiveMessages()
    {
        Message[] newMessages;

        newMessages = myRC.getAllMessages();

        for ( Message currentMessage : newMessages )
        {

        }
    }
}
