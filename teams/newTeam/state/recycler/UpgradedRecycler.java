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

        if( hasFactory && hasArmory && Clock.getRoundNum() == 600 && myRC.getTeamResources() > Prefab.flyingSoldier.getTotalCost() )
        {
            MapLocation location = myK.myLocation.add(myK.myLocation.directionTo( myK.myRecyclerNode.factoryLocation ).rotateRight());

            if( myMH.canMove(myK.myLocation.directionTo(location)) )
            myBH.networkBuildUnit(Prefab.flyingSoldier, location, myK.myRecyclerNode.factoryID, myK.myRecyclerNode.armoryID);
        }

        myBH.step();

        return this;
    }

}
