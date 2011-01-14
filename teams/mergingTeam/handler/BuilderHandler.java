package newTeam.handler;

import battlecode.common.*;

public class BuilderHandler {

    private BuilderController myBC;

    private boolean currentlyBuilding;
    private boolean IAmABuilding;

    private Robot buildTarget = null;

    private MapLocation buildLocation = null;
    private RobotLevel buildHeight = null;
    private BuildInstructions buildInstructions = null;
    private int buildStep = 0;

    public BuilderHandler ()
    {
        currentlyBuilding = false;
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

    public void step() {

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

}
