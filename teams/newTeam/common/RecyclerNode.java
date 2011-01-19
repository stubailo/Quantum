/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package newTeam.common;

import battlecode.common.*;

/**
 *
 * @author sashko
 */
public class RecyclerNode {

    public int myRobotID;
    public int parentRobotID;

    public MapLocation myLocation;
    public MapLocation parentLocation;

    public boolean hasFactory;
    public MapLocation factoryLocation;

    public RecyclerNode()
    {

    }

    public RecyclerNode ( int i_myID, MapLocation i_myLocation )
    {
        myRobotID = i_myID;
        myLocation = i_myLocation;
    }

    @Override
    public String toString()
    {
        return "RecyclerNode [id: " + myRobotID + " parent id: " + parentRobotID + (parentLocation!=null? " vector: (" +
                getVector().x + ", " + getVector().y + ") ]":"");
    }

    public boolean hasParent()
    {
        return parentLocation!=null;
    }

    public MapLocation getVector()
    {
        if( myLocation!=null && parentLocation!=null )
        {
            return myLocation.add( -parentLocation.x, -parentLocation.y);
        } else {
            return null;
        }
    }

    public Message generatePing()
    {
        int bodyLength = 2;

        int[] ints = new int[bodyLength];
        MapLocation[] locations = new MapLocation[bodyLength];
        String[] strings = new String[bodyLength];

        ints[0] = parentRobotID;
        ints[1] = hasFactory?1:0;

        locations[0] = parentLocation;
        locations[1] = factoryLocation;

        return MessageCoder.encodeMessage(MessageCoder.RECYCLER_PING, myRobotID, myLocation, Clock.getRoundNum(), false, strings, ints, locations);
    }

    public Message generateDesignation()
    {
        int[] ints = new int[1];
        MapLocation[] locations = new MapLocation[1];
        String strings[] = new String[1];

        ints[0] = myRobotID;

        locations[0] = myLocation;


        return MessageCoder.encodeMessage(MessageCoder.RECYCLER_DESIGNATION, myRobotID, myLocation, Clock.getRoundNum(), false, strings, ints, locations);
    }

    public static RecyclerNode getFromPing( Message ping )
    {
        RecyclerNode output = new RecyclerNode();

        output.myRobotID = MessageCoder.getBroadcasterID(ping);
        output.myLocation = MessageCoder.getBroadcasterLocation(ping);

        output.parentRobotID = MessageCoder.getIntFromBody(ping, 0);
        output.parentLocation = MessageCoder.getLocationFromBody(ping, 0);

        output.hasFactory = MessageCoder.getIntFromBody(ping, 1) == 1;
        output.factoryLocation = MessageCoder.getLocationFromBody(ping, 1);

        return output;
    }
}
