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
    
    private MapLocation[]   buildLocations                  = new MapLocation[9];
    private boolean         buildLocationsDetermined        = false;
    private int             numberOfLandBuildLocations      = 0;
    private int             numberOfFlyingBuildLocations    = 0;

    public RecyclerNode()
    {

    }

    public RecyclerNode ( int i_myID, MapLocation i_myLocation )
    {
        myRobotID = i_myID;
        myLocation = i_myLocation;
    }

    private boolean genBuildLocations ()
    {
        if(factoryLocation == null || armoryLocation == null) return false;
        if(buildLocationsDetermined) return true;
        buildLocationsDetermined = true;

        MapLocation[] adjacentLocations = new MapLocation[6];

        MapLocation[] outputLocations = new MapLocation[4];

        Direction primaryDir = factoryLocation.directionTo(myLocation);
        
        int numberOfPotentialLandLocations = 0;

        if( primaryDir.isDiagonal() )
        {
            adjacentLocations[0] = factoryLocation.add(primaryDir.rotateLeft());
            adjacentLocations[1] = factoryLocation.add(primaryDir.rotateRight());
            numberOfPotentialLandLocations = 2;
        } else {
            adjacentLocations[0] = factoryLocation.add(primaryDir.rotateLeft());
            adjacentLocations[1] = factoryLocation.add(primaryDir.rotateRight());
            adjacentLocations[2] = factoryLocation.add(primaryDir.rotateLeft().rotateLeft());
            adjacentLocations[3] = factoryLocation.add(primaryDir.rotateRight().rotateRight());
            numberOfPotentialLandLocations = 4;
        }

        for(int index = 0; index < numberOfPotentialLandLocations; index++)
        {
            MapLocation potentialBuildLocation = adjacentLocations[index];
            if(potentialBuildLocation.isAdjacentTo(armoryLocation)) {
                buildLocations[numberOfLandBuildLocations++] = potentialBuildLocation;
            }
        }
        
        numberOfFlyingBuildLocations = numberOfLandBuildLocations;
        
        if(myLocation.isAdjacentTo(factoryLocation) && myLocation.isAdjacentTo(armoryLocation)) {
            buildLocations[numberOfFlyingBuildLocations++] = myLocation;
        }
        
        if(factoryLocation.isAdjacentTo(myLocation) && factoryLocation.isAdjacentTo(armoryLocation)) {
            buildLocations[numberOfFlyingBuildLocations++] = factoryLocation;
        }
        
        if(armoryLocation.isAdjacentTo(factoryLocation) && armoryLocation.isAdjacentTo(myLocation)) {
            buildLocations[numberOfFlyingBuildLocations++] = armoryLocation;
        }
        return true;
    }
    
    public MapLocation[] getBuildLocations() {
        if(!genBuildLocations()) return null;
        return buildLocations;
    }
    
    public int getNumberOfLandBuildLocations() {
        if(!genBuildLocations()) return -1;
        return numberOfLandBuildLocations;
    }
    
    public int getNumberOfFlyingBuildLocations() {
        if(!genBuildLocations()) return -1;
        return numberOfFlyingBuildLocations;
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
