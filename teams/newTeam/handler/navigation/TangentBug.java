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
    
    private final int MAX_MOVES = QuantumConstants.TANGENT_BUG_PATH_LENGTH;
    private final int MAX_BUGS = QuantumConstants.NUMBER_OF_VIRTUAL_BUGS;
    private final int LOOP_BYTECODE_COST = 600;
    private final int BYTECODE_BUFFER = 400;
    private final int MIN_BYTECODES = LOOP_BYTECODE_COST + BYTECODE_BUFFER;
    private final int RECHECK_BYTECODES = 4;
    
    private final RobotController myRC;
    private final Knowledge myK;
    private final MovementController myMC;
    private final SensorHandler mySH;
    private final MapLocation goal;
    
    private TrackChecker myTrack;
    
    private         Direction           movementDirection;
    private         VirtualBug          movingBug;
    private         int                 currNodeIndex;
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
    private         Direction           secondaryGoalDirection;
    private         boolean             goingToSecondaryGoal;
    private         boolean             resetNext;
    private         MapLocation         tangentBugStart;
//    private         boolean             bugging;
    
    /** variables relating to bug branching */
    private         int []              branchIndices;
    private         int []              parents;
    
    
    public TangentBug(RobotController rc, Knowledge k, MovementController mc, 
                      SensorHandler sh, MapLocation newGoal) {
        myRC = rc;
        myK = k;
        myMC = mc;
        mySH = sh;
        
        goal = newGoal;
        reset();
    }

    public MovementAction getNextAction() {
        //stop navigating if you are at goal or the motor is active
        if(reachedGoal()) {
            return MovementAction.AT_GOAL;
        }
        
        MapLocation location = myK.myLocation;
        
        if(resetNext) {
            reset();
        }
        
        //calculate
        calculateVirtualBugs();
               
        //don't move if motor is active.
        boolean motorActive = myMC.isActive();
        
        // check if we have reached the secondary goal.
        if(goingToSecondaryGoal && location.equals(secondaryGoal)) {
            movementDirection = secondaryGoalDirection;
            if(myK.myDirection != movementDirection && 
               secondaryGoal.equals(tangentBugStart) &&
               !motorActive) {
                
                resetNext = true;
                return MovementAction.ROTATE;
            } else {
                reset();
                calculateVirtualBugs();
            }
        }
        
        if(motorActive) {
            return MovementAction.NONE;
        }
        
        // check whether to move up or down the virtual bug tree.
        Logger.debug_printAntony("stepIndex: " + stepIndex + " nodeIndex: " + currNodeIndex + 
                " onCVB? " + (movingBug == currentVB));
        if(!location.equals(prevLocation)) {
            if(stepIndex < currNodeIndex) {
                stepIndex++;
            } else if(stepIndex > currNodeIndex && movingBug != currentVB) {
                stepIndex--;
            } else {
                stepIndex++;
                movingBug = currentVB;
            }
        } else if(stepIndex == 0 && movingBug != currentVB) {
            movingBug = currentVB;
        }
        
        Logger.debug_printAntony("stepIndex: " + stepIndex + " nodeIndex: " + currNodeIndex + 
                " onCVB? " + (movingBug == currentVB));

        // get the next move along the moving path.
        MapLocation move = movingBug.getMove(stepIndex);
        if(move == null) {
            return MovementAction.NONE;
        }
        Logger.debug_printAntony("trying to move to " + move + " from " + location);
        
        // get the movement action associated with the next move.  
        movementDirection = location.directionTo(move);
        MovementAction action;
        if(currentPathWeight >= QuantumConstants.BIG_INT) {
            action = MovementAction.GOAL_INACCESSIBLE;
        } else if(movementDirection == Direction.OMNI) {
//            Logger.debug_printCustomErrorMessage("movementDirection was set to OMNI, TB:146", "Antony");
            action = MovementAction.NONE;
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
            if(myK.myDirection == movementDirection) {
                action = MovementAction.PATH_BLOCKED;
                reset();
            } else {
                action = MovementAction.ROTATE;
            }
//            action = MovementAction.PATH_BLOCKED;
        }

        prevLocation = location;
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

    public void pauseNavigation () {
        //put stuff here... it won't compile without it
    }

    
    /****************** Private Methods ********************/
    
    private void calculateVirtualBugs() {
        
        if(goingToSecondaryGoal) {
            return;
        }
        
//        myRC.yield();
        int remainingBytecodes = Clock.getBytecodesLeft();
        int count = 0;
        boolean clone = false;
        
//        myRC.setIndicatorString(0, "bug: " + virtualBugIndex +
//                                   " currPath: " + currentVB.getPathWeight() +
//                                   " secIndex: " + secondaryBugIndex +
//                                   " secPath: " + secondaryPathWeight + currentVB.getMove(0));
        VirtualBug branchBug;
        while(remainingBytecodes > MIN_BYTECODES) {

            //check if we need to branch bugs
            clone = currentVB.shouldBranch();
            if(clone) {
                branchBug = currentVB.clone();
                branchBug.setOrientationClockwise(false);
                branchBug.index = numberOfBugs;
                myVBs[numberOfBugs] = branchBug;
                pathWeights[numberOfBugs] = branchBug.getPathWeight();
                branchIndices[numberOfBugs] = currentVB.getMoveIndex();
                parents[numberOfBugs] = virtualBugIndex;
                if(numberOfBugs == 1) {
                    secondaryPathWeight = myVBs[1].getPathWeight();
                    secondaryBugIndex = 1;
                }
                numberOfBugs++;
                remainingBytecodes = Clock.getBytecodesLeft();
            }
            
//            if(clone) {
//                myRC.setIndicatorString(0, String.valueOf(myVBs[0].getMoveIndex()) + 
//                                           myVBs[1].getMoveIndex());
//            } else
//                myRC.setIndicatorString(0, ":p");
            
            //calculate next step, and check if we need to explore a secondary goal.
            Direction newGoalDirection = currentVB.stepVirtualBug();
            if(newGoalDirection != null) {
                secondaryGoal = currentVB.getPathEndLocation();
                if(secondaryGoal == null) {
                    secondaryGoal = myK.myLocation;
                }
                secondaryGoalDirection = newGoalDirection;
                goingToSecondaryGoal = true;
                break;
            }

            currentPathWeight = currentVB.getPathWeight();
            pathWeights[virtualBugIndex] = currentPathWeight;
//            turnsAlongPath[virtualBugIndex] = currentVB.getTurnsAlongPath();
            
            //Switch paths if necessary
            if(currentPathWeight > secondaryPathWeight) {
                currentVB = myVBs[secondaryBugIndex];
                currentPathWeight = pathWeights[secondaryBugIndex];
                virtualBugIndex = secondaryBugIndex;
                
                if(!movingBug.equals(currentVB)) {
                    currNodeIndex = getBranchIndex(movingBug.index, currentVB.index);
                }
                
                secondaryPathWeight = QuantumConstants.BIG_INT;
                int weight;
                for(int i = 0; i < numberOfBugs; i++) {
                    weight = pathWeights[i];
                    if(weight < secondaryPathWeight && 
                            i != virtualBugIndex) {
                        
                        secondaryPathWeight = weight;
                        secondaryBugIndex = i;
                    }
                }  
            }
            
            //Go to the end of the current branch if we have too many bugs.
            if(numberOfBugs == MAX_BUGS) {
                secondaryGoal = currentVB.getPathEndLocation();
                if(secondaryGoal == null) {
                    secondaryGoal = myK.myLocation;
                }
                secondaryGoalDirection = currentVB.getPathEndDirection();
                goingToSecondaryGoal = true;
                break;
            }
            
            //Periodically recheck remaining bytecodes
            count++;
            if(count % RECHECK_BYTECODES == 0 && !clone) {
                remainingBytecodes = Clock.getBytecodesLeft();
            } else {
                remainingBytecodes -= LOOP_BYTECODE_COST;
            }
            myRC.setIndicatorString(1, "loop: " + count);
//            myRC.yield();
        }
        myRC.setIndicatorString(1, "loops: " + count + " stepIndex: " + stepIndex + 
                                   " bugIndex: " + virtualBugIndex);
        myRC.setIndicatorString(2, "direction: " + secondaryGoalDirection);
    }
    
    private void reset() {
        tangentBugStart = myK.myLocation;
        myTrack = new TrackChecker();
        currentVB = new VirtualBug(goal, myRC, tangentBugStart, myTrack, mySH);
        currentVB.index = 0;
        movingBug = currentVB;
        myVBs = new VirtualBug [MAX_BUGS];
        myVBs[0] = currentVB;
        currentPathWeight = 0;
        pathWeights = new int [MAX_BUGS];
        pathWeights[0] = currentPathWeight;
        secondaryPathWeight = QuantumConstants.BIG_INT;
        virtualBugIndex = 0;
        stepIndex = 0;
        goingToSecondaryGoal = false;
        resetNext = false;
        numberOfBugs = 1;
        currNodeIndex = MAX_MOVES;
        prevLocation = myK.myLocation;
        
        branchIndices = new int [MAX_BUGS];
        branchIndices[0] = 0;
        parents = new int [MAX_BUGS];
        parents[0] = 0;
    }
  
    private int getBranchIndex(int ID1, int ID2) {
        int parents1 [] = new int [MAX_BUGS];
        parents1[0] = ID1;
        int root1 = 0;
        while(parents1[root1] != 0) {
            root1++;
            parents1[root1] = parents[parents1[root1 - 1]];
        }
        
        int parents2 [] = new int [MAX_BUGS];
        parents2[0] = ID2;
        int root2 = 0;
        while(parents2[root2] != 0) {
            root2++;
            parents2[root2] = parents[parents2[root2 - 1]];
        }
        
        int branchNumber = 0;
        while(branchNumber <= root1 && branchNumber <= root2 && 
              parents1[root1 - branchNumber] == parents2[root2 - branchNumber]) {
            branchNumber++;
        }
        return branchIndices[parents1[root1 - branchNumber + 1]];
    }
}
