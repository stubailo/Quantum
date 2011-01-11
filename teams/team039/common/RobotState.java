package team039.common;

public enum RobotState {
    // Just listed the first ones that came to mind.
    STARTING,

    /*
     * When a robot is just exploring the map looking for stimuli.  Primary example is a
     * constructor looking for mines to build on.
     */
    EXPLORING,

    /*
     * Used by robots with components capable of building while the robot is constructing
     * something which takes multiple turns (for example when a recycler is building
     * a soldier with multiple components).
     *
     * Entering and exiting this state is handled automatically by ComponentsHandler.BuildHandler.
     * After a robot exits this state, it goes back to idle.
     *
     * This state might be downgraded to a flag in BuildHandler if necessary, but this won't happen
     * until after the sprint round for sure.
     */
    BUILDING,
    FLEEING,
    ATTACKING,
    DEFENDING,

    /*
     * The JUST_BUILT state is given to robots when they are built.
     */
    JUST_BUILT,
    
    /*
     * StartingLightPlayer builds first two mines.
     */
    SCOUTING_STARTING_LOCATION,
    BUILDING_FIRST_MINE,
    BUILDING_SECOND_MINE,

    /*
     * When a robot is waiting to acquire a state, it is in IDLE.  When a robot stops
     * doing something (ex. building), it goes to IDLE and waits for a trigger.  Often,
     * a robot will have a switch to go straight from IDLE to, say, EXPLORING, but
     * it's good to have the intermediate state because it allows us to modify the
     * conditions later.
     *
     * IDLE could also be used to put the robot in a state where it will accept commands
     * from other units.
     *
     * If the robot has a sensor that only senses in one direction, it might make sense to
     * make it spin like a helicopter so that it can sense everything around itself to
     * look for stimuli.  Or maybe this could be a separate state, SENSE.
     */
    IDLE,

    /*
     * Used by a lightConstructor when it finds a mine.  When it is in this state,
     * it navigates to the mine location and begins building a mine there, switching
     * to the state BUILDING.
     */
    BUILDING_RECYCLER
}
