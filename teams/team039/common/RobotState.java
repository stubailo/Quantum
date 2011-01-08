package team039.common;

public enum RobotState {
    // Just listed the first ones that came to mind.
    STARTING,
    EXPLORING,
    BUILDING,
    FLEEING,
    ATTACKING,
    DEFENDING,

    /*
     * The JUST_BUILT state is, quite obviously, given to robots when they are built.
     */

    JUST_BUILT,

    /*
     * When a robot is waiting to acquire a state, it is in IDLE.  So if a robot is doing
     * something then it stops, it goes to IDLE and waits for a trigger.  Often,
     * a robot will have a switch to go straight from IDLE to, say, EXPLORING, but
     * it's good to have the intermediate state because it allows us to modify the
     * conditions later.
     *
     * IDLE could also be used to put the robot in a state where it will accept commands
     * from other units.
     *
     * If the robot has a sensor that only senses in one direction, it might make sense to
     * make it spin like a helicopter so that it can sense everything around itself to
     * look for stimuli.
     */
    IDLE
}
