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

    public static final int mainHeaderLength = 3; //how many initial indices are used for the header


    /*
     * ints         ID of broadcaster, number of messages
     * locations    location of broadcaster, __________
     * strings      passcode, message type
     */

    public static final String GO_TO_FACTORY = "gtf";
    public static final String RECYCLER_PING = "rp";
    public static final String PARENT_DESIGNATION = "pd";

    public static final int subHeaderLength = 1; //how many indices are used for the header of each sub-message

    private int[] ints;
    private String[] strings;
    private MapLocation[] locations;

    private int broadcasterID;
    private int numberOfMessages;
    private int targetID;
    private MapLocation broadcasterLocation;
    private MapLocation targetLocation; //for messages that only have the header
    private String passcode;
    private String messageType;

    public MessageWrapper()
    {
        numberOfMessages = 1;
        broadcasterID = -1;
    }

    public int getIntData( int index )
    {
        return ints[index + mainHeaderLength];
    }
    
    public MapLocation getLocationData( int index )
    {
        return locations[index + mainHeaderLength];
    }

    public void genGoToFactoryMsg( RobotController myRC, int arg_targetID, MapLocation factoryLocation )
    {
        broadcasterID = myRC.getRobot().getID();
        targetID = arg_targetID;
        numberOfMessages = 0;

        broadcasterLocation = myRC.getLocation();
        targetLocation = factoryLocation;
        passcode = broadcasterLocation.toString();
        messageType = GO_TO_FACTORY;

        encodeHeader();
    }

    public void genDesignateMsg(  RobotController myRC, RecyclerNode myNode, int justBuiltID )
    {
        broadcasterID = myRC.getRobot().getID();
        targetID = justBuiltID;
        numberOfMessages = 0;

        broadcasterLocation = myRC.getLocation();
        targetLocation = null;
        passcode = broadcasterLocation.toString();
        messageType = PARENT_DESIGNATION;

        encodeHeader();

        if( myNode != null )
        {
        locations[ mainHeaderLength ] = myNode.myLocation;
        ints[ mainHeaderLength ] = myNode.myRobotID;
        }
    }

    public void genRecyclerPing( RecyclerNode myNode )
    {
        broadcasterID = myNode.myRobotID;
        targetID = myNode.parentRobotID;
        numberOfMessages = 0;

        broadcasterLocation = myNode.myLocation;
        targetLocation = myNode.parentLocation;
        passcode = broadcasterLocation.toString();
        messageType = RECYCLER_PING;

        encodeHeader();
    }

    public Message getMessage()
    {
        

        Message output = new Message();

        output.ints = ints;
        output.strings = strings;
        output.locations = locations;

        return output;
    }

    public int getBroadcasterID() {
        return broadcasterID;
    }

    public MapLocation getBroadcasterLocation() {
        return broadcasterLocation;
    }

    public String getMessageType() {
        return messageType;
    }

    public int getNumberOfMessages() {
        return numberOfMessages;
    }

    public int getTargetID() {
        return targetID;
    }

    public MapLocation getTargetLocation() {
        return targetLocation;
    }

    private void encodeHeader()
    {
        int[] t_ints = { broadcasterID, targetID, numberOfMessages, 0  };
        MapLocation[] t_mlocs = { broadcasterLocation, targetLocation, null,null };
        String[] t_strings = { passcode, messageType, null, null };

        strings = t_strings;
        ints = t_ints;
        locations = t_mlocs;
    }

    private void decodeHeader()
    {
        broadcasterID = ints[0];
        targetID = ints[1];
        numberOfMessages = ints[2];

        broadcasterLocation = locations[0];
        targetLocation = locations[1];

        passcode = strings[0];
        messageType = strings[1];
    }

    /*
     * Takes a message encoded with one of the MessageWrapper message generating functions
     * and extracts the information needed, based on the messageType.
     *
     * Returns a string that will match a messageType.
     */

    public String decode( Message messageToDecode )
    {
        ints = messageToDecode.ints;
        strings = messageToDecode.strings;
        locations = messageToDecode.locations;

        decodeHeader();

        //at this point, the header has been extracted.  The statements below will parse the rest of the message.
        if( messageType.equals(MessageWrapper.GO_TO_FACTORY))
        {
            ints = null;
            strings = null;
            locations = null;
        } else if( messageType.equals(MessageWrapper.RECYCLER_PING))
        {
            ints = null;
            strings = null;
            locations = null;
        } else if( messageType.equals(MessageWrapper.PARENT_DESIGNATION))
        {
            strings = null;
        }

        return messageType;
    }

    /*
     * Checks if a message has the correct passcode to see if it was created by our team
     *
     * Returns true or false
     */

    public static Boolean isValid ( Message receivedMessage )
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

    /* These are under construction... planned features
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
    } */

}
