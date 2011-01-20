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

    public int factoryID;
    public MapLocation factoryLocation;

    public int armoryID;
    public MapLocation armoryLocation;

    public RecyclerNode()
    {

    }

    public RecyclerNode ( int i_myID, MapLocation i_myLocation )
    {
        myRobotID = i_myID;
        myLocation = i_myLocation;
    }

    public MapLocation[] genBuildLocations ()
    {
        if( factoryLocation==null )
        {
            MapLocation[] output = { myLocation };
            return output;
        }

        MapLocation[] adjacentLocations = new MapLocation[4];

        MapLocation[] outputLocations = new MapLocation[4];

        Direction primaryDir = factoryLocation.directionTo(myLocation);

        if( primaryDir.isDiagonal() )
        {
            adjacentLocations[0] = factoryLocation.add(primaryDir.rotateLeft());
            adjacentLocations[1] = factoryLocation.add(primaryDir.rotateRight());
        } else {
            adjacentLocations[0] = factoryLocation.add(primaryDir.rotateLeft());
            adjacentLocations[1] = factoryLocation.add(primaryDir.rotateRight());
            adjacentLocations[2] = factoryLocation.add(primaryDir.rotateLeft().rotateLeft());
            adjacentLocations[3] = factoryLocation.add(primaryDir.rotateRight().rotateRight());
        }

        if( armoryLocation == null )
        {
            return adjacentLocations;
        }

        int outputi = 0;

        for( MapLocation location : adjacentLocations )
        {
            if( location == null )
            {
                break;
            } else if ( location.isAdjacentTo( armoryLocation ) ) {
                outputLocations [outputi] = location;
                outputi++;
            }
        }

        return outputLocations;
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
        int bodyLength = 4;

        int[] ints = new int[bodyLength];
        MapLocation[] locations = new MapLocation[bodyLength];
        String[] strings = new String[bodyLength];

        ints[0] = myRobotID;
        ints[1] = parentRobotID;
        ints[2] = factoryID;
        ints[3] = armoryID;

        locations[0] = myLocation;
        locations[1] = parentLocation;
        locations[2] = factoryLocation;
        locations[3] = armoryLocation;

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

        output.myRobotID = MessageCoder.getIntFromBody(ping, 0);
        output.myLocation = MessageCoder.getLocationFromBody(ping, 0);

        output.parentRobotID = MessageCoder.getIntFromBody(ping, 1);
        output.parentLocation = MessageCoder.getLocationFromBody(ping, 1);

        output.factoryID = MessageCoder.getIntFromBody(ping, 2);
        output.factoryLocation = MessageCoder.getLocationFromBody(ping, 2);

        output.armoryID = MessageCoder.getIntFromBody(ping, 3);
        output.armoryLocation = MessageCoder.getLocationFromBody(ping, 3);

        return output;
    }
}
