package newTeam.handler.navigation;

import newTeam.common.QuantumConstants;
import newTeam.common.util.Logger;
import newTeam.common.Knowledge;
import newTeam.handler.SensorHandler;
import newTeam.handler.sensor.TerrainStatus;
import battlecode.common.Chassis;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.MovementController;
import battlecode.common.RobotController;

public class BugNavigator implements Navigator {
    
    private final RobotController myRC;
    private final Knowledge myK;
    private final MovementController myMC;
    private final TrackChecker myTrack;
    private final SensorHandler mySH;
    
    //TODO: Probably don't need to keep an array of memory...
    private final int MEMORY_LENGTH = QuantumConstants.BUG_MEMORY_LENGTH;
    private final int MAX_PENALTIES = 200;
    private final int ABORT = QuantumConstants.BUG_ABORT;
    private final int OFFSET = 2;
    private final MapLocation goal;
    
    private        boolean              navigating = false;
    private        boolean              goingToAdjacent = false;
    private        Direction            movementDirection = Direction.NONE;
    
    /*** Bug Navigation variables ***/
    private        boolean              tracking;
    private        boolean              trackingCW;
    private        boolean              startTracking;
    private        Direction            trackingDirection;
    private        Direction            prevTrackingDirection;
    private        Direction            prevDirectionToGoal;
    private        MapLocation          prevLocation;
    private        int                  turningNumber;
    private        MapLocation          bugStart;
    private        MapLocation []       bugPrevLocations;  
    private        Direction []         bugPrevDirections; 
    private        boolean []           bugPrevCW;
    private        int                  bugStep;
    private        MapLocation []       penaltyMarkers;
    private        int                  penalties;
    private        int                  cumulativePenalties;
    private        int                  prevNumberOfPenalties;
    private        int                  prevTurningNumber;
    
    private        MapLocation          marker;
    private        MapLocation          secondMarker;
    private        int                  markerTurningNumber;
    private        int                  secondMarkerTurningNumber;
    private        int                  turnsBackToMarker;
    private        int                  turnsMarkerToGoal;
    private        int                  turnsBugToGoal;
    private        boolean              backTracking;
    private        boolean              stopBackTracking;
    private        boolean              abortNext;
    
    private        int                  delayRect;
    private        int                  delayDiag;
    private        int                  delayRSq;
    private        int                  delayDSq;
    
    public BugNavigator(RobotController rc, SensorHandler sh, Knowledge know, MovementController mc,
                        MapLocation goalLocation, boolean navigatingToAdjacent) {
        myRC = rc;
        mySH = sh;
        myK = know;
        myMC = mc;
        myTrack = new TrackChecker();
        
        goal = goalLocation;
        goingToAdjacent = navigatingToAdjacent;
        reset();
        
        Chassis chassis = myRC.getChassis();
        delayRect = chassis.moveDelayOrthogonal;
        delayRSq = delayRect * delayRect;
        delayDiag = chassis.moveDelayDiagonal;
        delayDSq = delayDiag * delayDiag;
    }
    
    public BugNavigator(RobotController rc, SensorHandler sh, Knowledge know, MovementController mc,
                        MapLocation goalLocation) {
        this(rc, sh, know, mc, goalLocation, false);
    }
    
    private void reset() {
        navigating = true;
        tracking = false;
        startTracking = false;
        bugStart = myK.myLocation;
        prevLocation = myK.myLocation;
        prevDirectionToGoal = bugStart.directionTo(goal);
        bugPrevLocations = new MapLocation [MEMORY_LENGTH];
        bugPrevDirections = new Direction [MEMORY_LENGTH];
        bugPrevCW = new boolean [MEMORY_LENGTH];
        penaltyMarkers = new MapLocation [MAX_PENALTIES];
        penalties = 0;
        prevNumberOfPenalties = 0;
        cumulativePenalties = 0;
        bugStep = 0;
    }

    public MovementAction getNextAction() {
        // do nothing if the motor is active or you are not navigating
        if(reachedGoal()) {
            return MovementAction.AT_GOAL;
        } else if(myMC == null || myMC.isActive() || !navigating) {
            return MovementAction.NONE;
        }

        MovementAction action;
        
        if(goingToAdjacent) {
            action = navigateToAdjacent();
        } else if(reachedGoal()) {
            navigating = false;
            action = MovementAction.AT_GOAL;
        } else {
            action = navigateBug();
        }
        
        prevLocation = myK.myLocation;
        return action;

    }

    public Direction getMovementDirection() {
        return movementDirection;
    }
    
    public boolean reachedGoal() {
//        Logger.debug_printHocho("checking if reachedGoal when at: " + myK.myLocation + " and facing: " + myK.myDirection);
        if(goingToAdjacent) {
//            Logger.debug_printHocho("trying to go adjacent to: " + goal + " and facing: " + myK.myLocation.directionTo(goal));
            MapLocation myLocation = myK.myLocation;
            return goal.isAdjacentTo(myLocation) && myK.myDirection == myLocation.directionTo(goal);
        }
        else {
            return goal.equals(myK.myLocation);
        }
    }
    
    public int distanceSquaredToGoal(MapLocation fromHere) {
        return fromHere.distanceSquaredTo(goal);
    }
    
    public void setGoingToAdjacent(boolean b) {
        goingToAdjacent = b;
    }
   
    public void pauseNavigation() {
        
    }
    
    /******************* Private Methods ********************/

    private MovementAction navigateBug() {
        MapLocation location = myK.myLocation;
        Direction directionToGoal = location.directionTo(goal);
        
//        Logger.debug_printHocho("bugging from " + location + " to " + goal);
        
        if(location.isAdjacentTo(goal)) {
//            Logger.debug_printHocho("adjacent!");
            if(myK.myDirection == directionToGoal) {
//                Logger.debug_printHocho("facing the right way");
                TerrainStatus goalStatus = mySH.getTerrainStatus(goal);
                if(goalStatus != null) {
                    if(goalStatus == TerrainStatus.LAND) {
                        return MovementAction.MOVE_FORWARD;
                    }
                    else {
                        return MovementAction.GOAL_INACCESSIBLE;
                    }
                }
            }
            else {
//                Logger.debug_printHocho("need to rotate to " + directionToGoal + " from " + myK.myDirection);
                movementDirection = directionToGoal;
                return MovementAction.ROTATE;
            }
        }
        
        int bugPos = bugStep % MEMORY_LENGTH;
        MovementAction action;  //this variable is returned at the end.
        
        // if you have moved between calls to navigateBug, set the previous tracking direction and 
        // the previous direction to goal
        if(!prevLocation.equals(location)) {
            prevTrackingDirection = prevLocation.directionTo(location);
            prevDirectionToGoal = prevLocation.directionTo(goal);
            startTracking = false;
        }
        
//        myRC.setIndicatorString(1, String.valueOf(startTracking)+" "+ String.valueOf(tracking) + " " + String.valueOf(!prevLocation.equals(location)));
//        myRC.setIndicatorString(2, prevDirectionToGoal.toString() + " " + directionToGoal.toString());
        if(tracking) {

            if(directionToGoal != prevDirectionToGoal && !startTracking) {
                // changing goal contributes negatively to the turning number.
                turningNumber -= calculateTurningChange(prevDirectionToGoal, 
                                                        directionToGoal, trackingCW);
                prevDirectionToGoal = directionToGoal;
            }
            
            //set the initial direction to begin testing from.
            Direction startDirection = prevTrackingDirection.opposite();

            int turn;
            if(startTracking) {
                startTracking = false;
                turn = 0;
            } else if(prevTrackingDirection.ordinal() % 2 == 1){
                startDirection = (trackingCW) ? 
                        startDirection.rotateLeft() : 
                        startDirection.rotateRight();
                turn = -3;
            } else {
                startDirection = (trackingCW) ?
                        startDirection.rotateLeft().rotateLeft() :
                        startDirection.rotateRight().rotateRight();
                        
                turn = -2;
            }            
            Direction testDirection = startDirection;
            
            
            // check directions beginning with the reference direction, in an order depending on 
            // if you are tracking clockwise or counterclockwise
            boolean pathBlocked = true;
            while(pathBlocked) {
                //increment direction
                if(trackingCW) {
                    testDirection = testDirection.rotateLeft();
                } else {
                    testDirection = testDirection.rotateRight();
                }
                
                //update turning number
                turn++;
                
                if(myMC.canMove(testDirection)){
                    trackingDirection = testDirection;
                    pathBlocked = false;
                } else if(testDirection == startDirection) {
                    turn = 0;
                    break;
                }
            }

            //TODO: adjust the turningNumber when you calculate different rotation directions without moving.
            if(!prevLocation.equals(location)) {
               
                prevTurningNumber = turningNumber;
                if(!stopBackTracking) {
                    turningNumber += turn;
                    turnsBackToMarker += (trackingDirection.ordinal() % 2 == 1) ? 
                                          delayDiag - OFFSET : delayRect - OFFSET;
                    turnsBugToGoal = manhattanWeight(location, goal, delayRect);

                }
                if(!backTracking) {
                    
                    //record any penalty locations
                    if(checkForPenalty(prevTurningNumber, turningNumber) && penalties < MAX_PENALTIES) {
                        penaltyMarkers[penalties] = location;
                        penalties++;                    
                    }
                }
            }
            
            myRC.setIndicatorString(1, "backTracking: " + backTracking + 
                    " stopBackTracking: " + stopBackTracking +
                    " moved? " + (prevLocation.equals(location)));

            //stop backtracking if you have passed the backtracking marker.
            if(stopBackTracking) {
                stopBackTracking = false;
                backTracking = false;
//                turningNumber = markerTurningNumber;
                markerTurningNumber = secondMarkerTurningNumber;
                marker = secondMarker;               
            }
            
            //stop backtracking if you have reached your marker
            if(backTracking && location.equals(marker)) {
                stopBackTracking = true;
                turningNumber = markerTurningNumber;
            }
            
            int bugWeight = turnsBugToGoal + calculateTurningAdjustment();
            int pathWeight = turnsBackToMarker + turnsMarkerToGoal;
            //check if you are finished tracking or should begin backtracking
            if(turningNumber <= 0 && 
               location.distanceSquaredTo(goal) < bugPrevLocations[bugPos].distanceSquaredTo(goal)) {
                
                tracking = false;
                action = getBugAction(directionToGoal);
                movementDirection = directionToGoal;
            } else if((pathWeight < bugWeight || bugWeight > ABORT) &&
                      !prevLocation.equals(location) &&
                      !stopBackTracking && !backTracking) {
                
                backTracking = true;
                trackingCW = !trackingCW;
                secondMarker = location;
                secondMarkerTurningNumber = turningNumber;
                turnsBackToMarker = 0;
                turnsMarkerToGoal = bugWeight;
                penalties = 0;
                prevNumberOfPenalties = 0;
                cumulativePenalties = 0;
                penaltyMarkers = new MapLocation[MAX_PENALTIES];
                
                prevTrackingDirection = prevTrackingDirection.opposite();
                movementDirection = prevTrackingDirection;
                action = getBugAction(movementDirection);
                if(bugWeight > ABORT) {
                    if(abortNext) {
                        action = MovementAction.GOAL_INACCESSIBLE;
                        reset();
                    } else {
                        abortNext = true;
                    }
                }
            } else {
                movementDirection = trackingDirection;
                action = getBugAction(trackingDirection);
            }
            
//            myRC.setIndicatorString(0, trackingDirection.toString() + " " + movementDirection.toString()
//                    + " " + directionToGoal.toString());
//            if(marker!=null && secondMarker != null) {
//            myRC.setIndicatorString(0,  "(" +location.x +"," +location.y +
//                                        "); (" + marker.x + "," + marker.y +
//                                        "); (" + secondMarker.x +","+secondMarker.y +")");
//            }
//            myRC.setIndicatorString(2, "Marker: " + String.valueOf(turnsBackToMarker + turnsMarkerToGoal) +
//                    " Bug: " + String.valueOf(turnsBugToGoal + calculateTurningAdjustment()) +
//                    " Penalties: " + penalties + " Turns: " + turningNumber
//                    + " Marker TN: " + markerTurningNumber);

        } else {
            //not tracking, so check if you can move towards the goal
            if(myMC.canMove(directionToGoal)) {
                movementDirection = directionToGoal;
                action = getBugAction(directionToGoal);
            } else {               
                //the path is blocked, so begin tracking.
                tracking = true;
                startTracking = true;
                bugStep++;
                bugPos = (bugPos + 1) % QuantumConstants.BUG_MEMORY_LENGTH;
                bugPrevLocations[bugPos] = location;
            
                //determine if you should track around the obstacle clockwise or counterclockwise
                Direction ccwDir = directionToGoal;
                Direction cwDir = directionToGoal;
                boolean searching = true;
                
                //find counter clockwise turning number
                int ccwTurn = 0;
                while(searching) {
                    ccwTurn++;
                    ccwDir = ccwDir.rotateRight();
                    if(myMC.canMove(ccwDir)) {
                        searching = false;
                    }
                    
                    if(ccwDir == directionToGoal) {
                        ccwTurn = 0;
                        searching = false;
                    }
                }
                
                //find clockwise turning number
                int cwTurn = 0;
                searching = true;
                while(searching) {
                    cwTurn++;
                    cwDir = cwDir.rotateLeft();
                    if(myMC.canMove(cwDir)) {
                        searching = false;
                    }
                    
                    if(cwDir == directionToGoal) {
                        cwTurn = 0;
                        searching = false;
                    }
                }
                
                // choose a tracking direction, and set the marker turning number
                if(location.add(cwDir).distanceSquaredTo(goal) < 
                   location.add(ccwDir).distanceSquaredTo(goal)) {
                    
                    trackingCW = true;
                    bugPrevDirections[bugPos] = cwDir;
                    markerTurningNumber = ccwTurn;
                } else {
                    trackingCW = false;
                    bugPrevDirections[bugPos] = ccwDir;
                    markerTurningNumber = cwTurn;
                }
                
                //set a tracking marker
                marker = location;
                turnsBackToMarker = 0;
                turnsMarkerToGoal = manhattanWeight(marker, goal);
                turnsBugToGoal = turnsMarkerToGoal;
                backTracking = false;
                stopBackTracking = false;
                abortNext = false;
                penaltyMarkers = new MapLocation [MAX_PENALTIES];
                penalties = 0;
                prevNumberOfPenalties = 0;
                cumulativePenalties = 0;
                prevTurningNumber = 0;
                
                //other tracking information
                bugPrevCW[bugPos] = trackingCW;
                trackingDirection = bugPrevDirections[bugPos];
                prevTrackingDirection = directionToGoal.opposite();
//                turningNumber = turn;
                turningNumber = calculateTurningChange(directionToGoal, trackingDirection, trackingCW);
                if(turningNumber == -4) {
                    turningNumber = 4;
                }
                movementDirection = trackingDirection;
                action = getBugAction(trackingDirection);
            }
        }
//        myRC.setIndicatorString(0, "turningNumber: " + String.valueOf(turningNumber));
        return action;
    }
    
    private int calculateTurningChange(Direction oldDir, Direction newDir, boolean CW) {
        int turn = -4;
        Direction testDir = oldDir.opposite();
        while(testDir != newDir) {
            turn++;
            if(CW) {
                testDir = testDir.rotateLeft();
            } else {
                testDir = testDir.rotateRight();
            }
        }
//        Logger.debug_printHocho(String.valueOf(turn));
        return turn;
    }
    
    private MovementAction getBugAction(Direction dir) {
        MovementAction action;
        if(myK.myDirection == dir)
            action = MovementAction.MOVE_FORWARD;
//        else if(knowledge.myDirection == dir.opposite())
//            action = MovementAction.MOVE_BACKWARD;
        else
            action = MovementAction.ROTATE;
        
        return action;
    }
    
    private MovementAction navigateToAdjacent() {
      
        MapLocation location = myK.myLocation;
        int distanceSquaredToGoal = location.distanceSquaredTo(goal);
        
        if(distanceSquaredToGoal == 0) {
            Direction moveDir = myK.myDirection;
            boolean searching = true;
            while(searching){
                if(myMC.canMove(moveDir)) {
                    searching = false;
                } else {
                    moveDir = moveDir.rotateRight();
                }
                
                if(searching && moveDir == myK.myDirection) {
                    moveDir = Direction.NONE;
                    searching = false;
                }
            }
            
            if(moveDir == myK.myDirection) {
                movementDirection = myK.myDirection;
                return MovementAction.MOVE_FORWARD;
            } else if(moveDir == myK.myDirection.opposite()) {
                movementDirection = myK.myDirection.opposite();
                return MovementAction.MOVE_BACKWARD;
            } else if(moveDir != Direction.NONE) {
                movementDirection = moveDir;
                return MovementAction.ROTATE;
            } else {
                movementDirection = Direction.NONE;
                return MovementAction.NONE;
            }
            
        } else if(distanceSquaredToGoal <= 2) {
            Direction directionToGoal = location.directionTo(goal);
            movementDirection = directionToGoal;
            if(directionToGoal == myK.myDirection || directionToGoal == Direction.OMNI) {
                navigating = false;
                return MovementAction.AT_GOAL;
            } else {
                return MovementAction.ROTATE;
            }
        }    
            
        return navigateBug();

    }
    
    private int calculateTurningAdjustment() {
        int i1 = (turningNumber/2);
        int i2 = 0;
        int p = 0;
        switch(turningNumber) {
            case 0:
            case 1:
            case 2:
                i2 = 0;
                break;
                
            case 3:
                i2 = 2;
                break;
                
            default:
                if(turningNumber > 3) {
                    i2 = turningNumber - 1;
                }
        }
        
//        myRC.setIndicatorString(2, "penalties: " + penalties + 
//                " prevPenalties: " + prevNumberOfPenalties +
//                " cumPenalties: " + cumulativePenalties+ " p: "+p);
        
        if(penalties > prevNumberOfPenalties && penalties > 1) {
            for(int j = prevNumberOfPenalties; j < penalties - 1; j++) {
                cumulativePenalties += manhattanWeight(penaltyMarkers[j], penaltyMarkers[j+1]);
            }
            prevNumberOfPenalties = penalties - 1;
        }
        
        if(penalties > 0) {
            p = cumulativePenalties + 
                manhattanWeight(myK.myLocation, penaltyMarkers[penalties - 1], delayDiag);
        }
        
        return delayDiag * i1 + delayRect * i2 + p;
    }
    
    private int manhattanWeight(MapLocation start, MapLocation end) {
        //May be more efficient way to do this besides Math.abs.
        return manhattanWeight(start, end, delayRect);
//        return delayRSq * start.distanceSquaredTo(end);
    }
    
    private int manhattanWeight(MapLocation start, MapLocation end, int delay) {
        //May be more efficient way to do this besides Math.abs.
        return delay*(Math.abs(start.x - end.x) + Math.abs(start.y - end.y));
//        return delayRSq * start.distanceSquaredTo(end);
    }
    
    private int euclideanWeight(MapLocation start, MapLocation end, int delay) {
        return (int) (delay*Math.sqrt(start.distanceSquaredTo(end)));
    }
    
    private boolean checkForPenalty(int turn1, int turn2) {
        turn1 = (turn1 > 2) ? turn1 : 4;
        turn2 = (turn2 > 2) ? turn2 : 4;
        return Math.abs((turn1 + 1)/2 - (turn2 + 1)/2) >= 1;
    }
}
