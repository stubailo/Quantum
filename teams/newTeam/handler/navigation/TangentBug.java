package newTeam.handler.navigation;

import newTeam.common.util.Logger;
import newTeam.common.Knowledge;
import newTeam.common.QuantumConstants;
import newTeam.handler.SensorHandler;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.MovementController;
import battlecode.common.RobotController;

public class TangentBug implements Navigator {
    
//    private final int MAX_MOVES = QuantumConstants.TANGENT_BUG_PATH_LENGTH;
    private final int MAX_BUGS = QuantumConstants.NUMBER_OF_VIRTUAL_BUGS;
    private final int LOOP_BYTECODE_COST = 200;
    private final int BYTECODE_BUFFER = 250;
    private final int MIN_BYTECODES = LOOP_BYTECODE_COST + BYTECODE_BUFFER;
    private final int RECHECK_BYTECODES = 4;
    
    private final RobotController myRC;
    private final Knowledge myK;
    private final MovementController myMC;
    private final SensorHandler mySH;
    private final MapLocation goal;
    
    private         Direction           movementDirection;
    private         VirtualBug          movingBug;
    private         int                 movingBugIndex;
    private         int                 currBranchIndex;
    private         MapLocation         prevLocation;
    
    private         VirtualBug []       myVBs;
    private         VirtualBug          currentVB;
    private         int []              pathWeights;
    private         int                 numberOfBugs;
    private         int                 virtualBugIndex;
    private         int                 stepIndex;
    private         int                 currentPathWeight;
    private         int                 secondaryPathWeight;
    private         int                 secondaryBugIndex;
    private         MapLocation         secondaryGoal;
    private         boolean             goingToSecondaryGoal;
    private         boolean             bugging;
    
    /** variables relating to bug branching */
    private         int []              branchIndices;
    private         int []              parents;
    private         int []              turnsAlongPath;
    private         int []              turnsToNodes;
    
    
    public TangentBug(RobotController rc, Knowledge k, MovementController mc, 
                      SensorHandler sh, MapLocation newGoal) {
        myRC = rc;
        myK = k;
        myMC = mc;
        mySH = sh;
        
        goal = newGoal;
        currentVB = new VirtualBug(goal, myRC, myK.myLocation);
        currentVB.ID = 0;
        myVBs = new VirtualBug [MAX_BUGS];
        myVBs[0] = currentVB;
        currentPathWeight = 0;
        pathWeights = new int [MAX_BUGS];
        pathWeights[0] = currentPathWeight;
        secondaryPathWeight = QuantumConstants.BIG_INT;
        virtualBugIndex = 0;
        stepIndex = 0;
        goingToSecondaryGoal = false;
        numberOfBugs = 1;
        
        branchIndices = new int [MAX_BUGS];
        branchIndices[0] = 0;
        parents = new int[MAX_BUGS];
        parents[0] = 0;
        turnsAlongPath = new int[MAX_BUGS];
        turnsToNodes = new int[MAX_BUGS];
        turnsToNodes[0] = 0;
    }

    public MovementAction getNextAction() {
        
        //calculate
        calculateVirtualBugs();
        
        if(!myK.myLocation.equals(prevLocation) && !bugging) {
            stepIndex++;
        }
        
        MapLocation move = currentVB.getMove(stepIndex);
        if(move == null) {
            return MovementAction.NONE;
        }
        
        movementDirection = myK.myLocation.directionTo(move);
        MovementAction action;
        if(currentPathWeight >= QuantumConstants.BIG_INT) {
            action = MovementAction.GOAL_INACCESSIBLE;
        } else if(myMC.canMove(movementDirection)) {
            if(myK.myDirection == movementDirection) {
                action = MovementAction.MOVE_FORWARD;
//          } else if(knowledge.myDirection == dir.opposite()) {
//               action = MovementAction.MOVE_BACKWARD;
            } else {
                action = MovementAction.ROTATE;
            }
        } else {
            //TODO: probably will have to deal with this by bugging to the next move on the list.
            action = MovementAction.PATH_BLOCKED;
        }

        return action;
    }

    public Direction getMovementDirection() {
        return movementDirection;
    }

//    public void setGoal(MapLocation g) {
//        goal = g;
//    }

    public boolean reachedGoal() {
        return goal.equals(myK.myLocation);
    }

    public int distanceSquaredToGoal(MapLocation fromHere) {
        return fromHere.distanceSquaredTo(goal);
    }

    public void setGoingToAdjacent(boolean b) {
        //TODO: decide if I want this to do anything
    }

//    public void initiateNavigation(MapLocation newGoal) {
//        goal = newGoal;
//        currentVB = new VirtualBug(goal, myRC);
//        myVBs = new VirtualBug [MAX_BUGS];
//        myVBs[0] = currentVB;
//        currentPathWeight = 0;
//        pathWeights = new int [MAX_BUGS];
//        pathWeights[0] = currentPathWeight;
//        secondaryPathWeight = QuantumConstants.BIG_INT;
//        virtualBugIndex = 0;
//        stepIndex = 0;
//        goingToSecondaryGoal = false;
//    }
//
//    public void initiateNavigation() {
//        initiateNavigation(goal);
//    }
    
    public void pauseNavigation() {
        
    }
    
    /****************** Private Methods ********************/
    
    private void calculateVirtualBugs() {
        
        if(goingToSecondaryGoal) {
            return;
        }
        
        int remainingBytecodes = Clock.getBytecodesLeft();
        int count = 0;

        VirtualBug branchBug;
        while(remainingBytecodes > MIN_BYTECODES) {

            //check if we need to branch bugs
            if(currentVB.shouldBranch()) {
                branchBug = currentVB.clone();
                branchBug.setOrientationClockwise(false);
//                branchBug.ID = numberOfBugs;
                myVBs[numberOfBugs] = branchBug;
                pathWeights[numberOfBugs] = branchBug.getPathWeight();
//                turnsAlongPath[numberOfBugs] = branchBug.getTurnsAlongPath();
//                turnsToNodes[numberOfBugs] = turnsAlongPath[numberOfBugs];
//                branchIndices[numberOfBugs] = currentVB.getMoveIndex();
//                parents[numberOfBugs] = virtualBugIndex;
                if(numberOfBugs == 1) {
                    secondaryPathWeight = myVBs[2].getPathWeight();
                    secondaryBugIndex = 2;
                }
                numberOfBugs++;
                //TODO: deal with having too many virtual bugs
            }
            
            //calculate next step, and check if we need to explore a secondary goal.
            MapLocation newGoal = currentVB.stepVirtualBug();
            if(newGoal != null) {
                secondaryGoal = newGoal;
                goingToSecondaryGoal = true;
                //TODO: update the other virtual bugs starting from the secondary goal.
                break;
            }
            
            currentPathWeight = currentVB.getPathWeight();
            pathWeights[virtualBugIndex] = currentPathWeight;
            turnsAlongPath[virtualBugIndex] = currentVB.getTurnsAlongPath();
            
            if(currentPathWeight > secondaryPathWeight) {
                currentVB = myVBs[secondaryBugIndex];
                currentPathWeight = pathWeights[secondaryBugIndex];
                virtualBugIndex = secondaryBugIndex;
                
                secondaryPathWeight = QuantumConstants.BIG_INT;
                int weight;
                for(int i = 0; i < numberOfBugs; i++) {
                    weight = pathWeights[i];
                    if(weight < secondaryPathWeight && 
                            weight >= currentPathWeight && 
                            i != virtualBugIndex) {
                        
                        secondaryPathWeight = weight;
                        secondaryBugIndex = i;
                    }
                }               
            }
            
            count++;
            if(count % RECHECK_BYTECODES == 0) {
                remainingBytecodes = Clock.getBytecodesLeft();
            } else {
                remainingBytecodes -= LOOP_BYTECODE_COST;
            }
        }
    }
    
    private void reset() {
        //TODO: write a reset function
    }
    
//    private void recalculateBugs() {
//        int remainingBytecodes = Clock.getBytecodesLeft();
//        int count = 0;
//        
//        while(remainingBytecodes > MIN_BYTECODES) {
//            
//            count++;
//            if(count % RECHECK_BYTECODES == 0) {
//                remainingBytecodes = Clock.getBytecodesLeft();
//            } else {
//                remainingBytecodes -= LOOP_BYTECODE_COST;
//            }
//        }
//    }
//    
//    private int getBranchIndex(int ID1, int ID2) {
//        int parents1 [] = new int [MAX_BUGS];
//        parents1[0] = ID1;
//        int root1 = 0;
//        while(parents1[root1] != 0) {
//            root1++;
//            parents1[root1] = parents[parents1[root1 - 1]];
//        }
//        
//        int parents2 [] = new int [MAX_BUGS];
//        parents2[0] = ID2;
//        int root2 = 0;
//        while(parents2[root2] != 0) {
//            root2++;
//            parents2[root2] = parents[parents2[root2 - 1]];
//        }
//        
//        int branchNumber = 0;
//        while(branchNumber <= root1 && branchNumber <= root2 && 
//              parents1[root1 - branchNumber] == parents2[root2 - branchNumber]) {
//            branchNumber++;
//        }
//        return branchIndices[parents1[root1 - branchNumber + 1]];
//    }
}
