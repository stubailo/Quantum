/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package team039.common;

import battlecode.common.*;

/**
 *
 * @author sashko
 */
public class MessageWrapper {

    /*
     * THIS CLASS IS NOT DONE YET
     *
     *  MESSAGE STRUCTURE
     *  MapLocations[0] is the location of broadcaster
     *  Int[0] is the robot ID of the broadcaster
     *  String[0] is the passcode of the team (some sort of hash of the round number?
     * this is important to check if the message is a. from our team and b. not fake)
     *
     *  Each message type should strive to use an equal number of ints, strings, and MapLocations.
     *  That way they are in coherent blocks. Also, it is optimal if the data points
     * with the same index are related (example: int is timestamp of most recent sighting,
     * MapLocation is a location, and String is the type of incident found; 347, loc, "our recycler")
     *
     *  This structure is capable of putting together and separating messages for rebroadcast.
     *
     */

    public static final int mainHeaderLength = 2; //how many initial indices are used for the header


    /*
     * ints         ID of broadcaster, number of messages
     * locations    location of broadcaster, __________
     * strings      passcode, _________
     */

    public static final int subHeaderLength = 1; //how many indices are used for the header of each sub-message

    private static Message[] messagesArray;


    private int broadcasterID;
    private int numberOfMessages;
    private MapLocation broadcasterLocation;
    private String passcode;

    public MessageWrapper( int broadcastID )
    {
        numberOfMessages = 0;
        broadcasterID = broadcastID;

        messagesArray = new Message[16]; //maximum number of messages that can be combined in one.  Reducing this number doesn't decrease the size of each message broadcast.
    }

    public void addMessage ( Message toBeAdded )
    {
        messagesArray[numberOfMessages] = toBeAdded;
        numberOfMessages++;
    }

    public void addMessages ( Message[] messages )
    {
        for( Message currMessage : messages )
        {
            addMessage( currMessage );
        }
    }

    public void addMessageWrapper ( MessageWrapper toMerge )
    {
        addMessages( toMerge.getMessagesArray() );
    }

    public Message[] getMessagesArray() {
        return messagesArray;
    }

    public static Boolean validate ( Message receivedMessage )
    {
        //we need a more secure code than locations[0].toString()

        String code = receivedMessage.strings[0];

        if(code.equals( receivedMessage.locations[0].toString() ) )
        {
            return true;
        } else {
            return false;
        }
    }

    public Message encode()
    {
        Message output = new Message();

        //generate main heading


        return output;
    }

    public void decode( Message messageToDecode )
    {

    }


}
