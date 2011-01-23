package newTeam.handler.navigation;

import newTeam.common.QuantumConstants;
import newTeam.common.util.Logger;
import newTeam.handler.SensorHandler;
import newTeam.handler.sensor.TerrainStatus;
import battlecode.common.Chassis;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile;

public class VirtualBug {
    
    public        int                  index;
    
    private final int MAX_MOVES = QuantumConstants.TANGENT_BUG_PATH_LENGTH;
//    private final int MAX_BUGS = QuantumConstants.NUMBER_OF_VIRTUAL_BUGS;
    private final RobotController myRC;
    private final SensorHandler mySH;
    private final MapLocation goal;
    private final MapLocation startLocation;
    private final TrackChecker myTrack;
    
    private        boolean              tracking;
    private        boolean              trackingCW;
    private        boolean              startTracking;
    private        Direction            trackingDirection;
    private        Direction            prevTrackingDirection; 
    private        Direction            prevDirectionToGoal;
    private        MapLocation          prevLocation;          
    private        int                  turningNumber;
    private        Direction            movementDirection = Direction.NONE; //never used...
    /* moves lists the path to take in order, beginning with an index 0 corresponding to 
     * the first adjacent square.
     */
    private        MapLocation []       moves;
    private        int                  moveIndex; 
    private        int                  delayRect;
    private        int                  delayDiag;
    private        int                  delayRSq;
    private        int                  delayDSq;
    private        int                  turnsToGoal;
    private        int                  turnsAlongPath;
    private        int                  pathWeight;
    
    private        MapLocation          virtualBugLocation;
    private        Direction            directionToGoal;
//    private        TerrainTile          testTile;               //never used...
    private        boolean              goingToSecondaryGoal;
//    private        MapLocation          counterClockwiseMove;
    
    public VirtualBug(MapLocation g, RobotController rc, MapLocation start,
                      TrackChecker track, SensorHandler sh) {
        goal = g;
        startLocation = start;
        virtualBugLocation = start;
        myRC = rc;
        mySH = sh;
        myTrack = track;
        moves = new MapLocation [MAX_MOVES];
        tracking = false;
        moveIndex = 0;
        turnsAlongPath = 0;
        turnsToGoal = goalWeight(startLocation, goal);
        
        DoCommonConstructorActions();
    }
    
    public VirtualBug(MapLocation g,
                      RobotController rc,
                      MapLocation start,
                      TrackChecker tc,
                      SensorHandler sh,
                      MapLocation bugLocation,
                      boolean track,
                      boolean trackCW,
                      boolean stTracking,
                      Direction trackingDir,
                      Direction prevTrackingDir,
                      Direction prevDirToGoal,
                      MapLocation prevLoc,
                      int turningNum,
                      MapLocation [] m,
                      int index,
                      int toGoal,
                      int alongPath,
                      int pw) {
        goal = g;
        myRC = rc;
        startLocation = start;
        myTrack = tc;
        mySH = sh;
        virtualBugLocation = bugLocation;
        tracking = track;
        startTracking = stTracking;
        trackingCW = trackCW;
        trackingDirection = trackingDir;
        prevTrackingDirection = prevTrackingDir;
        prevDirectionToGoal = prevDirToGoal;
        prevLocation = prevLoc;
        turningNumber = turningNum;
        moves = m;
        moveIndex = index;
        turnsToGoal = toGoal;
        turnsAlongPath = alongPath;
        pathWeight = pw;
        
        DoCommonConstructorActions();
    }
    
    private void DoCommonConstructorActions () {
        Chassis chassis = myRC.getChassis();
        delayRect = chassis.moveDelayOrthogonal;
        delayRSq = delayRect * delayRect;
        delayDiag = chassis.moveDelayDiagonal;
        delayDSq = delayDiag * delayDiag;
    }
    
    public VirtualBug clone() {
        return new VirtualBug(goal,
                              myRC,
                              startLocation,
                              myTrack,
                              mySH,
                              virtualBugLocation,
                              tracking,
                              trackingCW,
                              startTracking,
                              trackingDirection,
                              prevTrackingDirection,
                              prevDirectionToGoal,
                              prevLocation,
                              turningNumber,
                              moves.clone(),
                              moveIndex,
                              turnsToGoal,
                              turnsAlongPath,
                              pathWeight);
    }
    
    public void setOrientationClockwise(boolean clockwiseOrientation) {
        trackingCW = clockwiseOrientation;
    }
    
//    public MapLocation [] getMoves() {
//        return moves;
//    }
    
    public MapLocation getMove(int index) {
        return moves[index];
    }
    
    public MapLocation getPathEndLocation() {
        if(moveIndex == 0) {
            return startLocation;
        }
        return moves[moveIndex - 1];
    }
    
    public Direction getPathEndDirection() {
        if(moveIndex == 0){
            return directionToGoal;
        } else if(moveIndex == 1) {
            return startLocation.directionTo(moves[0]);
        } else {
            return moves[moveIndex - 2].directionTo(moves[moveIndex - 1]);
        }
    }
    
    public int getPathWeight() {
        return pathWeight;
    }
    
    public int getTurnsAlongPath() {
        return turnsAlongPath;
    }
    
    public int getMoveIndex() {
        return moveIndex - 1;
    }
    
//    public MapLocation getCounterClockwiseMove() {
//        return counterClockwiseMove;
//    }
    
    /** 
     * Determines whether you need to branch.  If you do, calculates both the clockwise
     * and counterclockwise move, turningNumber, and pathWeight.  Returns 
     * @return
     */
    public boolean shouldBranch() {
        //record the virtualBugLocation and directionToGoal
        directionToGoal = virtualBugLocation.directionTo(goal);
        
        // cannot branch while tracking
        if(tracking) {
            return false;
        }
        
        // check if you can move in the direction of goal.
        MapLocation nextLocation = virtualBugLocation.add(directionToGoal);
        TerrainStatus tile = mySH.getTerrainStatus(nextLocation);
        Logger.debug_printAntony("tile in direction of goal is " + nextLocation + " with status " + tile);
//        TerrainTile tile = myRC.senseTerrainTile(nextLocation);
//        if(tile != null){
//        myRC.setIndicatorString(0, nextLocation.toString() + tile);
//        }
        
        if(tile == null) {
            goingToSecondaryGoal = true;
            return false;
        } else if(tile != TerrainStatus.LAND) {
            tracking = true;
            trackingCW = true;
            startTracking = true;
            if(tile == TerrainStatus.OFF_MAP) {
                turnsAlongPath += QuantumConstants.BIG_INT;
            }
            return true;
        } else {
            addMove(nextLocation);
            return false;
        }        
    }
    
    /** 
     * Calculates the next square in the virtual bug's path.  
     * @return null if the step was successful, or MapLocation if you need to begin
     *         exploring a secondary goal.
     */
    public Direction stepVirtualBug() {
        // return the secondary goal if there is unexplored area.
        if(goingToSecondaryGoal) {
            return directionToGoal;
        }
        
        // already found move in shouldBranch() if you are not tracking.
        if(!tracking) {
            return null;
        }
        
        Direction testDir;
        TerrainStatus tile;
        MapLocation nextLocation = null;
        int turn;
        if(startTracking) {
            startTracking = false;
            testDir = directionToGoal;
            turn = 0;
            
            //find the next tile to move toward
            boolean searching = true;
            while(searching) {
                turn++;
                if(trackingCW) {
                    testDir = testDir.rotateLeft();
                } else {
                    testDir = testDir.rotateRight();
                }
                nextLocation = virtualBugLocation.add(testDir);
                tile = mySH.getTerrainStatus(nextLocation);
                Logger.debug_printAntony("VBL: " + virtualBugLocation + " going to: " +
                        nextLocation + " tile: " + tile );
//                tile = myRC.senseTerrainTile(nextLocation);
                
                //set a secondary goal if you need to move toward an unexplored tile
                if(tile == null) {
                    goingToSecondaryGoal = true;
                    return testDir;
                } else if(tile == TerrainStatus.LAND) {
                    //add the next move 
                    searching = false;
                    trackingDirection = testDir;
                    prevDirectionToGoal = directionToGoal;
                    addMove(nextLocation);
                    turningNumber = turn;
                    
//                    //check if tracking direction indicates that these bugs can't reach the goal.
//                    if(myTrack.checkTrackingDirection(nextLocation, testDir, trackingCW)) {
//                        turnsAlongPath += QuantumConstants.BIG_INT;
//                    }
//                    
//                    // record tracking direction
//                    if(myTrack.addTrackingDirection(virtualBugLocation, testDir, trackingCW)) {
//                        turnsAlongPath += QuantumConstants.BIG_INT;
//                    }
                } else if(tile == TerrainStatus.OFF_MAP) {
                    searching = false;
                    turnsAlongPath += QuantumConstants.BIG_INT;
                    pathWeight = turnsAlongPath + turnsToGoal + calculateTurningAdjustment();
                }
            }
            
            if(turningNumber <= 0) {
                tracking = false;
            }
            
            if(nextLocation.equals(goal)) {
                return testDir;
            }
            
            return null;
            
        } else {
            // update turning number if the direction to goal has changed.
            if(directionToGoal != prevDirectionToGoal) {
                turningNumber -= calculateTurningChange(prevDirectionToGoal, 
                                                        directionToGoal, trackingCW);
            }
            
            //set the direction to start testing from depending on if the last move was diagonal
            turn = -1;
            testDir = trackingCW ? trackingDirection.rotateRight() : trackingDirection.rotateLeft();
            if(trackingDirection.ordinal() % 2 == 1) {
                turn -= 1;
                testDir = trackingCW ? testDir.rotateRight() : testDir.rotateLeft();
            }
            
            //find the next tile to move toward.
            boolean searching = true;
            while(searching) {
                nextLocation = virtualBugLocation.add(testDir);
                tile = mySH.getTerrainStatus(nextLocation);
//                tile = myRC.senseTerrainTile(nextLocation);
                Logger.debug_printAntony("bug #" + index + ": VBL: " + 
                        virtualBugLocation + " testDir: " + testDir + "tile: " + tile + 
                        " TN: " + turningNumber);
                
                //set a secondary goal if your move is to an unexplored tile.
                if(tile == null) {
                    goingToSecondaryGoal = true;
                    return testDir;
                } else if(tile == TerrainStatus.LAND) {
                    //add the next move.
                    searching = false;
                    trackingDirection = testDir;
                    prevDirectionToGoal = directionToGoal;
                    turningNumber += turn;
                    
                    //check if tracking direction indicates that these bugs can't reach the goal.
                    Logger.debug_printAntony("are any of these null? "+ nextLocation + testDir 
                            + trackingCW + myTrack);
                    if(myTrack.checkTrackingDirection(nextLocation, testDir, trackingCW)) {
                        turnsAlongPath += QuantumConstants.BIG_INT;
                    }
                    
                    //record tracking direction.
                    if(myTrack.addTrackingDirection(virtualBugLocation, testDir, trackingCW)) {
                        turnsAlongPath += QuantumConstants.BIG_INT;
                    }
                } else if(tile == TerrainStatus.OFF_MAP) {
                    searching = false;
                    turnsAlongPath += QuantumConstants.BIG_INT;
                    pathWeight = turnsAlongPath + turnsToGoal + calculateTurningAdjustment();
                }
                                
                turn++;
                testDir = trackingCW ? testDir.rotateLeft() : testDir.rotateRight();
            }
            
            if(turningNumber <= 0 ) {
                tracking = false;
            } else {
                addMove(nextLocation);
                if(nextLocation.equals(goal)) {
                    return testDir;
                }
            }
            
            return null;
        }
    }
    
    /******************* Private Methods ********************/

//    private void updatePathWeight() {
//        turnsToGoal = 
//    }
    
    private void addMove(MapLocation move) {
        //TODO: deal with index going out of bounds.  
        if(moveIndex >= MAX_MOVES) {
            return;
        }        
        moves[moveIndex] = move;
        Logger.debug_printAntony("adding " + move + " to bug #" + index + " at index " + moveIndex);
        moveIndex++;

        prevLocation = virtualBugLocation;
        directionToGoal = move.directionTo(goal);
        if(virtualBugLocation.directionTo(move).ordinal() % 2 == 1) {
            turnsAlongPath += delayDiag;
        } else {
            turnsAlongPath += delayRect;
        }
       
        turnsToGoal = goalWeight(move, goal);
        pathWeight = turnsAlongPath + turnsToGoal + calculateTurningAdjustment();
        
        virtualBugLocation = move;
    }
    
    private int calculateTurningAdjustment() {
        int i1 = (turningNumber/2);
        int i2;
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
                i2 = turningNumber - 1;
        }                
        
        return delayDiag * i1 + delayRect * i2;
    }
    
    private int goalWeight(MapLocation start, MapLocation end) {
        //May be more efficient way to do this besides Math.abs.
        return goalWeight(start, end, delayRect);
//        return delayRSq * start.distanceSquaredTo(end);
    }
    
    private int goalWeight(MapLocation start, MapLocation end, int delay) {
        return delay*(Math.abs(start.x - end.x) + Math.abs(start.y - end.y));
    }
    
    private int calculateTurningChange(Direction oldDir, Direction newDir, boolean CW) {
        int turn = -4;
        Direction testDir = oldDir.opposite();
        while(testDir != newDir) {
            turn++;
            testDir = CW ? testDir.rotateLeft() : testDir.rotateRight();
        }
        return turn;
    }
}

