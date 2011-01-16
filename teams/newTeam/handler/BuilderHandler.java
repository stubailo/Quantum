package newTeam.handler;

import battlecode.common.*;

import newTeam.common.*;
import newTeam.common.util.*;

public class BuilderHandler {

    private BuilderController myBC;
    Knowledge knowledge;

    
    private boolean IAmABuilding;

    private boolean currentlyBuilding;
    private Robot buildTarget = null;

    private MapLocation buildLocation = null;
    private RobotLevel buildHeight = null;
    private BuildInstructions buildInstructions = null;
    private int buildStep = 0;

    Message lastMessage = null;

    public BuilderHandler ( Knowledge know )
    {
        currentlyBuilding = false;
        knowledge = know;
    }

    public void addBC( BuilderController newBC )
    {
        //assumes that you can only have one component capable of building at a time
        myBC = newBC;

        //am I a building? this is important because if I am a building I can
        //always sense what I'm building
        if( newBC.type()==ComponentType.CONSTRUCTOR )
        {
            IAmABuilding = false;
        } else {
            IAmABuilding = true;
        }

    }

    public void buildUnit( BuildInstructions instructions, MapLocation location )
    {
        try {
            myBC.build( instructions.getBaseChassis(), location);

            currentlyBuilding = true;

            if( IAmABuilding )
            {
                //use the sensor handler to set the buildTarget
                //buildTarget = null;
            }

            buildLocation = location;
            buildHeight = instructions.getBaseChassis().level;
            buildInstructions = instructions;
            buildStep = 0;
        } catch (Exception e)
        {
            abortBuilding();
        }
    }

    public void step() {

        if( !currentlyBuilding )
        {
            return;
        }
        //don't do anything if it's currently waiting for the builder to recharge
        if ( myBC.isActive() ) {
            return;
        }

        //skip things I can't build
        while (buildStep != buildInstructions.getNumSteps()
                && !BuildMappings.canBuild(myBC.type(), buildInstructions.getComponent(buildStep))) {
            buildStep++;
        }


        if (buildStep < buildInstructions.getNumSteps()) {

            try {
                myBC.build(buildInstructions.getComponent(buildStep), buildLocation, buildHeight);
                buildStep++;
            } catch ( Exception e )
            {
                Logger.debug_printExceptionMessage(e);
            }

        } else {
            finishBuilding();
        }
    }

    /*
     * Has to be called before finishBuilding to be effective
     */

    public Message genDesignationMessage()
    {
        if( IAmABuilding && currentlyBuilding )
        {
            String[] bodyStrings = { buildInstructions.instructionsID  };
            int [] bodyInts = { buildTarget.getID() };
            MapLocation [] bodyLocations = { null };

            Message output = MessageCoder.encodeMessage(MessageCoder.JUST_BUILT_UNIT_DESIGNATION, knowledge.myRobotID, knowledge.myLocation, Clock.getRoundNum(), false, bodyStrings, bodyInts, bodyLocations);

            return output;
        } else {
            return null;
        }
    }

    public Message getDesignationMessage()
    {
        return lastMessage;
    }

    private void finishBuilding()
    {
        lastMessage = genDesignationMessage();
        currentlyBuilding = false;
        buildTarget = null;

        buildLocation = null;
        buildHeight = null;
        buildInstructions = null;
        buildStep = 0;
    }

    private void abortBuilding()
    {
        currentlyBuilding = false;
        buildTarget = null;

        buildLocation = null;
        buildHeight = null;
        buildInstructions = null;
        buildStep = 0;
    }

}
