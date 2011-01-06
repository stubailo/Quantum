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

    private static final ComponentType[] lightSoldierComponents = {ComponentType.SIGHT, ComponentType.ANTENNA, ComponentType.BLASTER, ComponentType.BLASTER};
    private static final ComponentType[] lightContructorComponents = {ComponentType.SIGHT, ComponentType.ANTENNA, ComponentType.CONSTRUCTOR};
    public static BuildInstructions lightSoldier = new BuildInstructions(Chassis.LIGHT, lightSoldierComponents);
    public static BuildInstructions lightConstructor = new BuildInstructions(Chassis.LIGHT, lightContructorComponents);
}
