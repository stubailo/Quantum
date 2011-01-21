/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newTeam.common;

import newTeam.handler.building.BuildInstructions;
import battlecode.common.*;
import newTeam.common.*;

/**
 * The name is short for Prefabricated. These are the robot builds we have hard-coded.
 *
 * @author sashko
 */
public abstract class Prefab {

    private static final ComponentType[] commRecyclerComponents = { ComponentType.RECYCLER, ComponentType.ANTENNA, ComponentType.PROCESSOR };
    private static final ComponentType[] factoryComponents = { ComponentType.FACTORY, ComponentType.DISH };
    private static final ComponentType[] armoryComponents = { ComponentType.ARMORY };
    public static BuildInstructions factory = new BuildInstructions( "bf", Chassis.BUILDING, factoryComponents );
    public static BuildInstructions armory = new BuildInstructions( "ba", Chassis.BUILDING, armoryComponents );
    public static BuildInstructions commRecycler = new BuildInstructions( "brc", Chassis.BUILDING, commRecyclerComponents);

    private static final ComponentType[] lightSoldierComponents = { ComponentType.SHIELD, ComponentType.SIGHT, ComponentType.BLASTER, ComponentType.BLASTER};
    private static final ComponentType[] lightContructorComponents = {ComponentType.SIGHT, ComponentType.ANTENNA, ComponentType.CONSTRUCTOR};
    private static final ComponentType[] startingConstructorComponents = {ComponentType.ANTENNA};

    public static BuildInstructions lightSoldier = new BuildInstructions( "ls", Chassis.LIGHT, lightSoldierComponents);
    public static BuildInstructions lightConstructor = new BuildInstructions( "lc", Chassis.LIGHT, lightContructorComponents);
    public static BuildInstructions startingConstructor = new BuildInstructions( "lcs", Chassis.LIGHT, startingConstructorComponents);

    private static final ComponentType[] flyingSensorComponents = { ComponentType.ANTENNA, ComponentType.RADAR, ComponentType.SHIELD };
    private static final ComponentType[] flyingConstructorComponents = { ComponentType.CONSTRUCTOR, ComponentType.ANTENNA };

    public static BuildInstructions flyingSensor = new BuildInstructions( "fs", Chassis.FLYING, flyingSensorComponents);
    public static BuildInstructions flyingConstructor = new BuildInstructions( "fc", Chassis.FLYING, flyingConstructorComponents);

    private static final ComponentType[] mediumSoldierComponents = { ComponentType.DISH, ComponentType.RADAR, ComponentType.BLASTER };

    public static BuildInstructions mediumSoldier = new BuildInstructions( "ms", Chassis.MEDIUM, mediumSoldierComponents);

    /*
     * IMPORTANT ADD NETWORKED BUILD UNITS TO THIS ARRAY
     */
    private static BuildInstructions[] networkBuildList = { flyingSensor, flyingConstructor };

    public static BuildInstructions getInstructionsFromID ( String id )
    {
        for ( BuildInstructions current : networkBuildList )
        {
            if( current.instructionsID.equals(id) )
            {
                return current;
            }
        }

        return null;
    }
}
