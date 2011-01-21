package newTeam.state.recycler;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;
import newTeam.state.idle.Idling;

public class UpgradedRecycler extends BaseState {

    private boolean hasFactory;
    private boolean hasArmory;
    private MapLocation[] buildLocations;

    public UpgradedRecycler(BaseState oldState) {
        super(oldState);

        buildLocations = myK.myRecyclerNode.genBuildLocations();
    }

    @Override
    public void senseAndUpdateKnowledge() {

        hasFactory = myK.myRecyclerNode.factoryLocation != null;
        hasArmory = myK.myRecyclerNode.armoryLocation != null;

    }

    @Override
    public BaseState getNextState() {

        return this;
    }

    @Override
    public BaseState execute() {

        if (hasFactory && hasArmory && Clock.getRoundNum() == 500 && myRC.getTeamResources() > Prefab.flyingSensor.getTotalCost()) {
            MapLocation location = getEmptyIntersectLocation();

            if (location != null) {
                myBH.networkBuildUnit(Prefab.flyingSensor, location, myK.myRecyclerNode.factoryID, myK.myRecyclerNode.armoryID);
            }
        } else if (hasFactory && hasArmory && Clock.getRoundNum() == 700 && myRC.getTeamResources() > Prefab.flyingConstructor.getTotalCost()) {
            MapLocation location = getEmptyFlyingLocation();

            if (location != null) {
                myBH.networkBuildUnit(Prefab.flyingConstructor, location, myK.myRecyclerNode.factoryID, myK.myRecyclerNode.armoryID);
            }
        }

        myBH.step();

        return this;
    }

    private MapLocation getEmptyIntersectLocation() {
        Direction testDirection = Direction.EAST;
        MapLocation testLocation = null;
        MapLocation location = null;
        for (int i = 0; i < 8; i++) {
            testLocation = myK.myLocation.add(testDirection);
            if (myMH.canMove(testDirection) && testLocation.isAdjacentTo(myK.myRecyclerNode.factoryLocation) && testLocation.isAdjacentTo(myK.myRecyclerNode.armoryLocation)) {
                location = testLocation;
                break;
            } else {
                testDirection = testDirection.rotateRight();
            }
        }

        return location;
    }

    private MapLocation getEmptyFlyingLocation() {
        Direction testDirection = Direction.EAST;
        MapLocation testLocation = null;
        MapLocation location = null;
        for (int i = 0; i < 8; i++) {

            

            testLocation = myK.myLocation.add(testDirection);
            if (mySH.senseAtLocation(testLocation, RobotLevel.IN_AIR) == null && adjOrOn(testLocation,myK.myRecyclerNode.factoryLocation) && adjOrOn(testLocation, myK.myRecyclerNode.armoryLocation)) {
                location = testLocation;
                System.out.println( testDirection );
                break;
            } else {
                testDirection = testDirection.rotateRight();
            }
        }

        return location;
    }

    private boolean adjOrOn( MapLocation loc1, MapLocation loc2 )
    {
        return loc1.isAdjacentTo(loc2) || loc1.equals(loc2);
    }
}
