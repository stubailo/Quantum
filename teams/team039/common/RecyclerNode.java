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
public class RecyclerNode {

    public int myRobotID;
    public int parentRobotID;

    public MapLocation myLocation;
    public MapLocation parentLocation;

    public MapLocation getVector()
    {
        return myLocation.add( -parentLocation.x, -parentLocation.y);
    }
}
