package newTeam.handler;

import battlecode.common.*;

import newTeam.handler.building.BuildInstructions;
import newTeam.common.util.Logger;
import newTeam.common.*;

public class BuilderHandler {

    private BuilderController myBC;
    private final Knowledge knowledge;
    private final SensorHandler mySH;
    private final BroadcastHandler myBCH;
    private boolean IAmABuilding;
    private boolean currentlyBuilding;
    private Robot buildTarget = null;
    private MapLocation buildLocation = null;
    private RobotLevel buildHeight = null;
    private BuildInstructions buildInstructions = null;
    private int buildStep = 0;
    boolean needsFactory = false;
    boolean needsArmory = false;
    boolean needsRecycler = false;
    Message lastMessage = null;
    private boolean builtSuccessfully = false;
    private int roundsWaited = 0;
    private boolean waitingToBuild = false;

    public BuilderHandler(Knowledge know, SensorHandler sh, BroadcastHandler bch) {
        currentlyBuilding = false;
        knowledge = know;
        mySH = sh;
        myBCH = bch;
    }

    public boolean getCurrentlyBuilding() {
        return currentlyBuilding;
    }

    public boolean finishedBuilding() {
        if (builtSuccessfully) {
            builtSuccessfully = false;
            return true;
        } else {
            return false;
        }
    }

    public void addBC(BuilderController newBC) {
        //assumes that you can only have one component capable of building at a time
        myBC = newBC;

        //am I a building? this is important because if I am a building I can
        //always sense what I'm building
        if (newBC.type() == ComponentType.CONSTRUCTOR) {
            IAmABuilding = false;
        } else {
            IAmABuilding = true;
        }

    }

    public void buildUnit(BuildInstructions instructions, MapLocation location) {
        try {
            myBC.build(instructions.getBaseChassis(), location);

            buildComponents( instructions, location );
        } catch (Exception e) {
            Logger.debug_printExceptionMessage(e);
            abortBuilding();
        }
    }

    public void buildComponents(BuildInstructions instructions, MapLocation location) {
        try {
            currentlyBuilding = true;

            if (IAmABuilding) {
                buildTarget = mySH.senseAtLocation(location, instructions.getBaseChassis().level);
            }

            buildLocation = location;
            buildHeight = instructions.getBaseChassis().level;
            buildInstructions = instructions;
            buildStep = 0;
        } catch (Exception e) {
            Logger.debug_printExceptionMessage(e);
            abortBuilding();
        }
    }

    public void step() {
        if (!currentlyBuilding) {
            return;
        }
        //don't do anything if it's currently waiting for the builder to recharge
        if (myBC.isActive()) {
            return;
        }

        if ( waitingToBuild )
        {
            buildTarget = mySH.senseAtLocation(buildLocation, buildHeight);
            if( buildTarget != null )
            {
                Logger.debug_printSashko( "sensed something.." );
                waitingToBuild = false;
            } else if( roundsWaited < QuantumConstants.BUILD_TIMEOUT ) {
                roundsWaited++;
                return;
            } else {
                abortBuilding();
                return;
            }
        }

        //skip things I can't build
        while (buildStep != buildInstructions.getNumSteps()
                && !BuildMappings.canBuild(myBC.type(), buildInstructions.getComponent(buildStep))) {


            buildStep++;
        }
        try {

            if (buildStep < buildInstructions.getNumSteps()) {


                myBC.build(buildInstructions.getComponent(buildStep), buildLocation, buildHeight);
                buildStep++;


            }

            if (buildStep == buildInstructions.getNumSteps()) {
                finishBuilding();
            }
        } catch (Exception e) {
            abortBuilding();
            Logger.debug_printExceptionMessage(e);
        }
    }

    public Message getDesignationMessage() {
        return lastMessage;
    }

    public Robot getBuildTarget() {
        return buildTarget;
    }

    private void finishBuilding() {
        // lastMessage = genDesignationMessage();

        currentlyBuilding = false;
        buildTarget = null;

        buildLocation = null;
        buildHeight = null;
        buildInstructions = null;
        buildStep = 0;

        builtSuccessfully = true;
    }

    public void abortBuilding() {
        currentlyBuilding = false;
        buildTarget = null;

        buildLocation = null;
        buildHeight = null;
        buildInstructions = null;
        buildStep = 0;
    }

    //networked building

    /*
     * Called from an upgraded Recycler base, it sends commands to the armory and factory to build the unit.
     *
     * Need to add abort/failsafe stuff
     */
    public void networkBuildUnit( BuildInstructions instructions, MapLocation location, int factoryID, int armoryID )
    {
        switch ( whoBuildsChassis( instructions.getBaseChassis() ) )
        {
            case RECYCLER:
                buildUnit( instructions, location );
                myBCH.addToQueue( genBuildCommandMessage( instructions, location, factoryID ) );
                myBCH.addToQueue( genBuildCommandMessage( instructions, location, armoryID ) );
                break;
            case FACTORY:
                myBCH.addToQueue( genBuildCommandMessage( instructions, location, factoryID ) );
                myBCH.addToQueue( genBuildCommandMessage( instructions, location, armoryID ) );
                waitToBuild( instructions, location );
                break;
            case ARMORY:
                myBCH.addToQueue( genBuildCommandMessage( instructions, location, armoryID ) );
                myBCH.addToQueue( genBuildCommandMessage( instructions, location, factoryID ) );
                waitToBuild( instructions, location );
                break;
        }
    }

    /*
     * If this Robot isn't building the chassis, wait until a chassis appears in the
     * designated location, then build on it.
     */
    public void waitToBuild( BuildInstructions instructions, MapLocation location )
    {
        currentlyBuilding = true;
        buildLocation = location;
        buildHeight = instructions.getBaseChassis().level;
        buildInstructions = instructions;
        buildStep = 0;
        waitingToBuild = true;
        roundsWaited = 0;
    }

    /*
     * Am I the one who should build this chassis? (one-to-one correspondence)
     */
    public boolean chassisBuiltByMe( Chassis chassis )
    {
        switch (myBC.type()) {

            case RECYCLER:
                return chassis == Chassis.LIGHT;
            case FACTORY:
                return chassis == Chassis.MEDIUM || chassis == Chassis.HEAVY;
            case ARMORY:
                return chassis == Chassis.FLYING;
            default:
                return false;
        }
    }

    /*
     * Who should build this chassis? (one-to-one correspondence)
     */
    public ComponentType whoBuildsChassis( Chassis chassis )
    {
        switch ( chassis ) {
            case LIGHT:
                return ComponentType.RECYCLER;
            case MEDIUM:
            case HEAVY:
                return ComponentType.FACTORY;
            case FLYING:
                return ComponentType.ARMORY;
            case BUILDING:
                return ComponentType.CONSTRUCTOR;
            default:
                return null;
        }
    }

    /*
     * Generates a message that commands the building with builderID to build the unit
     * specified by instructions.
     */
    private Message genBuildCommandMessage( BuildInstructions instructions, MapLocation location, int builderID )
    {
        int[] ints = { builderID };
        String[] strings = { instructions.instructionsID };
        MapLocation[] locations = { location };

        return MessageCoder.encodeMessage( MessageCoder.BUILD_UNIT_COMMAND, knowledge.myRobotID, knowledge.myLocation, Clock.getRoundNum(), false, strings, ints, locations);
    }

    /*
     * In case you need to build one component. Left over from a previous iteration
     * of networked building.
     */
    private boolean buildComponent( ComponentType component, MapLocation location, RobotLevel height )
    {
        try {
            myBC.build(component, location, height);
            return true;
        } catch (Exception e) {
            Logger.debug_printExceptionMessage(e);
            return false;
        }
    }

}
