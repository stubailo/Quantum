package newTeam.state.recycler;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;
import newTeam.common.RecyclerNode;
import newTeam.common.QuantumConstants;

public class UpgradedRecycler extends BaseState {
    
    private static final double HIGHER_DELTA_FLUX_CUTOFF = QuantumConstants.HIGHER_DELTA_FLUX_CUTOFF;
    private static final double LOWER_DELTA_FLUX_CUTOFF  = QuantumConstants.LOWER_DELTA_FLUX_CUTOFF;
    private static final int    BUILD_BUFFER             = 150;

    private final MapLocation[]   buildLocations;
    private final int             numberOfLandBuildLocations;
    private final int             numberOfFlyingBuildLocations;
    
    private       RecyclerNode    myRecyclerNode;
    private       boolean         hasArmory;
    private       int             myArmoryID;
    private       boolean         hasFactory;
    private       int             myFactoryID;
    
    private boolean         needToBuildConstructor = false;
    private boolean         hasMadeFirstConstructor = false;
    private boolean         justBuiltConstructor = false;
    private int             lastRoundBuilt = 0;
    private boolean         mineBuilt = false;
    
    private double          ownIncome;
    private double          prevOwnIncome;

    public UpgradedRecycler(BaseState oldState) {
        super(oldState);
        
        RecyclerNode myRecyclerNode = myK.myRecyclerNode;
        buildLocations = myRecyclerNode.getBuildLocations();
        if(buildLocations == null) {
            Logger.debug_printCustomErrorMessage("UpgradedRecycler found no build locations", "Hocho");
            numberOfLandBuildLocations = 0;
            numberOfFlyingBuildLocations = 0;
        }
        else {
            numberOfLandBuildLocations = myRecyclerNode.getNumberOfLandBuildLocations();
            numberOfFlyingBuildLocations = myRecyclerNode.getNumberOfFlyingBuildLocations();
        }

    }

    @Override
    public void senseAndUpdateKnowledge() {
        myRecyclerNode = myK.myRecyclerNode;
        prevOwnIncome = ownIncome;
        ownIncome = mySH.getOwnIncome();
//        Logger.debug_printHocho("ownIncome: " + ownIncome);
        if(ownIncome - prevOwnIncome > 0) mineBuilt = true;
        
        hasFactory  = myRecyclerNode.factoryLocation != null;
        hasArmory   = myRecyclerNode.armoryLocation  != null;
        myFactoryID = myRecyclerNode.factoryID;
        myArmoryID  = myRecyclerNode.armoryID;
    }

    @Override
    public BaseState getNextState() {

        return this;
    }

    @Override
    public BaseState execute() {
        
        if(hasFactory && hasArmory && Clock.getRoundNum() >= lastRoundBuilt + GameConstants.POWER_WAKE_DELAY) {
            
            if(needToBuildConstructor) {
                if(myK.totalFlux > Prefab.flyingConstructor.getTotalCost() + BUILD_BUFFER) {
                    MapLocation location = getBuildLocation(false);
                    
                    if(location != null) {
//                        Logger.debug_printHocho("trying to make flyingConstructor");
                        myBH.networkBuildUnit(Prefab.flyingConstructor, location, myFactoryID, myArmoryID);
                        lastRoundBuilt = Clock.getRoundNum();
                        mineBuilt = false;
                        needToBuildConstructor = false;
                        justBuiltConstructor = true;
                    }
                }
            }
            else if((mineBuilt || myK.averageDeltaFlux > HIGHER_DELTA_FLUX_CUTOFF || justBuiltConstructor) && hasMadeFirstConstructor && myK.averageDeltaFlux > LOWER_DELTA_FLUX_CUTOFF) {
                if(myK.totalFlux > Prefab.mediumSoldier.getTotalCost() + BUILD_BUFFER) {
                    MapLocation location = getBuildLocation(true);
                    
                    if(location != null) {
//                        Logger.debug_printHocho("trying to make mediumSoldier");
                        myBH.networkBuildUnit(Prefab.mediumSoldier, location, myFactoryID, myArmoryID);
                        lastRoundBuilt = Clock.getRoundNum();
                        justBuiltConstructor = false;
                    }
                }
            }
            else {
                if(myK.totalFlux > Prefab.flyingSensor.getTotalCost() + BUILD_BUFFER) {
                    MapLocation location = getBuildLocation(false);
                    
                    if(location != null) {
//                        Logger.debug_printHocho("trying to make flyingSensor");
                        myBH.networkBuildUnit(Prefab.flyingSensor, location, myFactoryID, myArmoryID);
                        lastRoundBuilt = Clock.getRoundNum();
                        needToBuildConstructor = true;
                        hasMadeFirstConstructor= true;
                    }
                }
            }
        }

//        if (hasFactory && hasArmory && Clock.getRoundNum() == 500 && myRC.getTeamResources() > Prefab.flyingSensor.getTotalCost()) {
//            MapLocation location = getEmptyIntersectLocation();
//
//            if (location != null) {
//                myBH.networkBuildUnit(Prefab.flyingSensor, location, myFactoryID, myArmoryID);
//            }
//        } else if (hasFactory && hasArmory && Clock.getRoundNum() == 700 && myRC.getTeamResources() > Prefab.flyingConstructor.getTotalCost()) {
//            MapLocation location = getEmptyFlyingLocation();
//
//            if (location != null) {
//                myBH.networkBuildUnit(Prefab.flyingConstructor, location, myFactoryID, myArmoryID);
//            }
//        }

        myBH.step();

        return this;
    }

    private MapLocation getBuildLocation(boolean onLand) {
        for(int index = 0; index < (onLand ? numberOfLandBuildLocations : numberOfFlyingBuildLocations); index++) {
            MapLocation potentialLocation = buildLocations[index];
            if(mySH.senseAtLocation(potentialLocation, (onLand ? RobotLevel.ON_GROUND : RobotLevel.IN_AIR)) == null) {
                return potentialLocation;
            }
        }
        return null;
    }
        
//    private MapLocation getEmptyIntersectionLocation() {
//        Direction testDirection = Direction.EAST;
//        MapLocation location = null;
//        for (int i = 0; i < 8; i++) {
//            MapLocation testLocation = myK.myLocation.add(testDirection);
//            if (myMH.canMove(testDirection) && testLocation.isAdjacentTo(myK.myRecyclerNode.factoryLocation) && testLocation.isAdjacentTo(myK.myRecyclerNode.armoryLocation)) {
//                location = testLocation;
//                break;
//            } else {
//                testDirection = testDirection.rotateRight();
//            }
//        }
//
//        return location;
//    }
//
//    private MapLocation getEmptyFlyingLocation() {
//        Direction testDirection = Direction.EAST;
//        MapLocation location = null;
//        MapLocation myLocation = myK.myLocation;
//        MapLocation factoryLocation = myK.myRecyclerNode.factoryLocation;
//        MapLocation armoryLocation  = myK.myRecyclerNode.armoryLocation;
//        if(mySH.senseAtLocation(myLocation, RobotLevel.IN_AIR) == null && adjOrOn(myLocation, )))
//        for (int i = 0; i < 8; i++) {
//
//            MapLocation testLocation = myLocation.add(testDirection);
//            if (mySH.senseAtLocation(testLocation, RobotLevel.IN_AIR) == null && adjOrOn(testLocation,factoryLocation) && adjOrOn(testLocation, armoryLocation)) {
//                location = testLocation;
//                Logger.debug_printSashko( testDirection.toString() );
//                break;
//            } else {
//                testDirection = testDirection.rotateRight();
//            }
//        }
//
//        return location;
//    }
//
//    private boolean adjOrOn( MapLocation loc1, MapLocation loc2 )
//    {
//        return loc1.isAdjacentTo(loc2) || loc1.equals(loc2);
//    }
}
