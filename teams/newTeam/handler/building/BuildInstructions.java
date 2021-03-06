/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package newTeam.handler.building;

import battlecode.common.*;

/**
 * BuildInstructions keeps track of all of the different unit types that can be built.
 * The goal of this class is to completely encapsulate the process of building components,
 * so that buildings always call methods from this class.
 *
 * @author sashko
 */
public class BuildInstructions {

    private int totalCost = 0;
    private int numSteps = 0;
    private int totalWeight = 0;
    private int componentCost = 0;
    private Chassis baseChassis;
    private ComponentType[] instructions;

    public String instructionsID; // used in messages to refer to this unit type

    /**
     * Takes an array of components and calculates the cost and number of steps
     */
    public BuildInstructions( String id, Chassis chassis, ComponentType[] components) {

        instructionsID = id;

        instructions = new ComponentType[20];
        totalCost = chassis==null?0:chassis.cost;
        baseChassis = chassis;

        for (ComponentType currComp : components) {
            componentCost += currComp.cost;
            totalCost += currComp.cost;
            totalWeight += currComp.weight;
            instructions[numSteps] = currComp;
            numSteps++;
        }
    }

    /**
     * Gets the component at that step in the build process
     *
     * @return        returns the component to be built at step stepNumber
     */
    public ComponentType getComponent(int stepNumber) {
        if (stepNumber >= 0 && stepNumber < numSteps) {
            return instructions[stepNumber];
        } else {
            return null;
        }
    }

    //the get methods are obvious
    public int getNumSteps() {
        return numSteps;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public Chassis getBaseChassis() {
        return baseChassis;
    }

    public int getComponentCost() {
        return componentCost;
    }


}
