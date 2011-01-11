/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sprint.common;

import battlecode.common.*;

/**
 * BuildOrder keeps track of the order in which a newly created Recycler can build things.
 * When the Recycler is initialized, it can select a build order, allowing you to have
 * different build orders at different stages of the game.
 *
 * @author sashko
 */
public class BuildOrder {

    private BuildInstructions[] units;

    public BuildOrder( BuildInstructions[] i_units ) {

        units = i_units;
    }

    /**
     * Gets the unit at that step in the build order
     *
     * @return        returns the component to be built at step stepNumber
     */
    public BuildInstructions getComponent(int stepNumber) {
        if (stepNumber >= 0 && stepNumber < units.length ) {
            return units[stepNumber];
        } else if ( stepNumber >= units.length ) {
            return units[units.length - 1];
        } else {
            return null;
        }
    }

}
