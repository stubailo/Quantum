/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team039.common;

import battlecode.common.*;

/**
 * The name is short for Prefabricated. These are the robot builds we have hard-coded.
 *
 * @author sashko
 */
public abstract class Prefab {

    private static final ComponentType[] commRecyclerComponents = { ComponentType.RECYCLER, ComponentType.ANTENNA };
    public static BuildInstructions commRecycler = new BuildInstructions( "brc", Chassis.BUILDING, commRecyclerComponents);

    private static final ComponentType[] lightSoldierComponents = { ComponentType.SHIELD, ComponentType.SIGHT, ComponentType.BLASTER, ComponentType.BLASTER};
    private static final ComponentType[] lightContructorComponents = {ComponentType.SIGHT, ComponentType.ANTENNA, ComponentType.CONSTRUCTOR};
    private static final ComponentType[] startingConstructorComponents = {ComponentType.ANTENNA};

    public static BuildInstructions lightSoldier = new BuildInstructions( "ls", Chassis.LIGHT, lightSoldierComponents);
    public static BuildInstructions lightConstructor = new BuildInstructions( "lc", Chassis.LIGHT, lightContructorComponents);
    public static BuildInstructions startingConstructor = new BuildInstructions( "lcs", Chassis.LIGHT, startingConstructorComponents);
}
