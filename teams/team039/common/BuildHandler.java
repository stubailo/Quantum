/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package team039.common;

import team039.handler.ComponentsHandler;
import battlecode.common.*;

/**
 *
 * @author sashko
 */
public class BuildHandler {

    private boolean haveBuildController = false;
    private Robot buildTarget = null;
    private MapLocation buildLocation = null;
    private RobotLevel buildHeight = null;
    private BuildInstructions buildInstructions = null;
    private int buildStep = 0;
    private ComponentsHandler compHandler;
    private RobotController myRC;
    private Knowledge knowledge;

    public BuildHandler(RobotController rc, ComponentsHandler ch, Knowledge know) {
        myRC = rc;
        compHandler = ch;
        knowledge = know;
    }

    public void step() {
        //build actions
        if (!compHandler.builderActive() && buildTarget != null) {
            //skip components that this building can't build
            while (buildStep != buildInstructions.getNumSteps() && !compHandler.canIBuildThis(buildInstructions.getComponent(buildStep))) {
                buildStep++;
            }

            if (buildStep < buildInstructions.getNumSteps()) {
                if (!compHandler.buildComponent(buildInstructions.getComponent(buildStep), buildLocation, buildHeight)) {
                } else {
                    buildStep++;
                }
            } else {
                finishBuilding();
            }
        }
    }

    public Boolean autoBuildRobot(BuildInstructions instructions) {
        if (myRC.getTeamResources() < instructions.getTotalCost()) {
            return false;
        }
        if (buildTarget == null) {
            Chassis chassis = instructions.getBaseChassis();
            MapLocation location = compHandler.getAdjacentEmptySpot(chassis.level);
            if (location == null) {
                return false;
            } else {
                buildChassisAndThenComponents(instructions, location);
                return true;
            }
        } else {
            return false;
        }
    }

    /**
     * Builds a chassis in the designated location, then begins building components
     * on it with startBuildingComponents
     */
    public Boolean buildChassisAndThenComponents(BuildInstructions instructions, MapLocation location) {
        Chassis chassis = instructions.getBaseChassis();
        if (compHandler.buildChassis(chassis, location)) {
            startBuildingComponents(instructions, location, chassis.level);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Initiates component build process by setting all of the required variables
     */
    public void startBuildingComponents(BuildInstructions instructions, MapLocation location, RobotLevel height) {
        //first, find the robot
        buildTarget = compHandler.senseARobot(location, height);
        buildStep = 0;
        buildInstructions = instructions;
        buildLocation = location;
        buildHeight = height;

        knowledge.myState = RobotState.BUILDING;
    }

    /**
     * Called after a successful build process... anything to activate the just-built robot should go here
     */
    private void finishBuilding() {
        buildTarget = null;
        buildStep = 0;
        buildInstructions = null;
        buildLocation = null;
        buildHeight = null;

        knowledge.myState = RobotState.IDLE;
    }

    /**
     * Only called if something went wrong - erase all evidence of target
     */
    public void abortBuilding() {
        buildTarget = null;
        buildStep = 0;
        buildInstructions = null;
        buildLocation = null;
        buildHeight = null;

        knowledge.myState = RobotState.IDLE;
    }
}
