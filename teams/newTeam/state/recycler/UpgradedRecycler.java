package newTeam.state.recycler;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;
import newTeam.common.RecyclerNode;
import newTeam.common.QuantumConstants;

public class UpgradedRecycler extends BaseState {
    
    private static final int DELTA_FLUX_CUTOFF = QuantumConstants.DELTA_FLUX_CUTOFF;
    private static final int BUILD_BUFFER      = 150;

    private final MapLocation[]   buildLocations;
    
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

        buildLocations  = myK.myRecyclerNode.genBuildLocations();
        
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
                    MapLocation location = getEmptyFlyingLocation();
                    
                    if(location != null) {
                        Logger.debug_printHocho("trying to make flyingConstructor");
                        myBH.networkBuildUnit(Prefab.flyingConstructor, location, myFactoryID, myArmoryID);
                        lastRoundBuilt = Clock.getRoundNum();
                        mineBuilt = false;
                        needToBuildConstructor = false;
                        justBuiltConstructor = true;
                    }
                }
            }
            else if((mineBuilt || myK.deltaFlux > DELTA_FLUX_CUTOFF || justBuiltConstructor) && hasMadeFirstConstructor) {
                if(myK.totalFlux > Prefab.mediumSoldier.getTotalCost() + BUILD_BUFFER) {
                    MapLocation location = getEmptyIntersectLocation();
                    
                    if(location != null) {
                        Logger.debug_printHocho("trying to make mediumSoldier");
                        myBH.networkBuildUnit(Prefab.mediumSoldier, location, myFactoryID, myArmoryID);
                        lastRoundBuilt = Clock.getRoundNum();
                        justBuiltConstructor = false;
                    }
                }
            }
            else {
                if(myK.totalFlux > Prefab.flyingSensor.getTotalCost() + BUILD_BUFFER) {
                    MapLocation location = getEmptyFlyingLocation();
                    
                    if(location != null) {
                        Logger.debug_printHocho("trying to make flyingSensor");
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

    private MapLocation getEmptyIntersectLocation() {
        Direction testDirection = Direction.EAST;
        MapLocation location = null;
        for (int i = 0; i < 8; i++) {
            MapLocation testLocation = myK.myLocation.add(testDirection);
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
        MapLocation location = null;
        for (int i = 0; i < 8; i++) {

            MapLocation testLocation = myK.myLocation.add(testDirection);
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
