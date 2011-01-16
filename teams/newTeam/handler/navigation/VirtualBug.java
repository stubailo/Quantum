package newTeam.handler.navigation;

import newTeam.common.QuantumConstants;
import newTeam.common.util.Logger;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.TerrainTile;

public class VirtualBug {
    
    private final int MAX_MOVES = QuantumConstants.TANGENT_BUG_PATH_LENGTH;
//    private final int MAX_BUGS = QuantumConstants.NUMBER_OF_VIRTUAL_BUGS;
    private final RobotController myRC;
    private final MapLocation goal;
    private final MapLocation startLocation;
    
    private        boolean              tracking;
    private        boolean              trackingCW;
    private        boolean              startTracking;
    private        Direction            trackingDirection;
    private        Direction            prevTrackingDirection; //never used...
    private        Direction            prevDirectionToGoal;
    private        MapLocation          prevLocation;          //never used...
    private        int                  turningNumber;
    private        Direction            movementDirection = Direction.NONE; //never used...
    /* moves lists the path to take in order, beginning with an index 0 corresponding to 
     * the first adjacent square.
     */
    private        MapLocation []       moves;
    private        int                  moveIndex; 
    private        int                  delayRect;
    private        int                  delayDiag;
    private        int                  turnsToGoal;
    private        int                  turnsAlongPath;
    private        int                  pathWeight;
    
    private        MapLocation          virtualBugLocation;
    private        Direction            directionToGoal;
//    private        TerrainTile          testTile;               //never used...
    private        boolean              goingToSecondaryGoal;
//    private        MapLocation          counterClockwiseMove;

    public VirtualBug(MapLocation g, RobotController rc, MapLocation start) {
        goal = g;
        startLocation = start;
        virtualBugLocation = start;
        myRC = rc;
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
        virtualBugLocation = bugLocation;
        tracking = track;
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
        delayRect = myRC.getChassis().moveDelayOrthogonal;
        delayDiag = myRC.getChassis().moveDelayDiagonal;
    }
    
    public VirtualBug clone() {
        return new VirtualBug(goal,
                              myRC,
                              startLocation,
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
        return moves[moveIndex];
    }
    
    public int getPathWeight() {
        return pathWeight;
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
        TerrainTile tile = myRC.senseTerrainTile(nextLocation);
        
        if(tile == null) {
            goingToSecondaryGoal = true;
            return false;
        } else if(tile != TerrainTile.LAND) {
            tracking = true;
            trackingCW = true;
            startTracking = true;
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
    public MapLocation stepVirtualBug() {
        // return the secondary goal if there is unexplored area.
        if(goingToSecondaryGoal) {
            return moves[moveIndex];
        }
        
        // already found move in shouldBranch() if you are not tracking.
        if(!tracking) {
            return null;
        }
        
        Direction testDir;
        TerrainTile tile;
        MapLocation nextLocation;
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
                tile = myRC.senseTerrainTile(nextLocation);
                
                //set a secondary goal if you need to move toward an unexplored tile
                if(tile == null) {
                    goingToSecondaryGoal = true;
                    return moves[moveIndex];
                } else if(tile == TerrainTile.LAND) {
                    //add the next move 
                    searching = false;
                    trackingDirection = testDir;
                    prevDirectionToGoal = directionToGoal;
                    addMove(nextLocation);
                    turningNumber = turn;                 
                }
            }
            
            return null;
            
        } else {
            // update turning number if the direction to goal has changed.
            if(directionToGoal != prevDirectionToGoal) {
                turningNumber -= calculateTurningChange(prevDirectionToGoal, directionToGoal, trackingCW);
            }
            
            //set the direction to start testing from depending on if the last move was diagonal
            turn = -1;
            testDir = trackingCW ? trackingDirection.rotateRight() : trackingDirection.rotateLeft();
            if(trackingDirection.ordinal() % 2 == 1) {
                turn -= 1;
                testDir = trackingCW ? trackingDirection.rotateRight() : trackingDirection.rotateLeft();
            }
            
            //find the next tile to move toward.
            boolean searching = true;
            while(searching) {
                nextLocation = virtualBugLocation.add(testDir);
                tile = myRC.senseTerrainTile(nextLocation);
                
                //set a secondary goal if your move is to an unexplored tile.
                if(tile == null) {
                    goingToSecondaryGoal = true;
                    return moves[moveIndex];
                } else if(tile == TerrainTile.LAND) {
                    //add the next move.
                    searching = false;
                    trackingDirection = testDir;
                    prevDirectionToGoal = directionToGoal;
                    addMove(nextLocation);
                    turningNumber += turn;
                }
                                
                turn++;
                testDir = trackingCW ? trackingDirection.rotateLeft() : trackingDirection.rotateRight();
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
        moveIndex++;
        moves[moveIndex] = move;
        prevLocation = virtualBugLocation;
        directionToGoal = move.directionTo(goal);
        if(virtualBugLocation.directionTo(move).ordinal() % 2 == 1) {
            turnsAlongPath += delayDiag;
        } else {
            turnsAlongPath += delayRect;
        }
       
        turnsToGoal = goalWeight(move, goal);
        pathWeight = turnsAlongPath + turnsToGoal;
        
        virtualBugLocation = move;
    }
    
    private int goalWeight(MapLocation start, MapLocation end) {
        //May be more efficient way to do this besides Math.abs.
        return delayRect*(Math.abs(start.x - end.x) + Math.abs(start.y - end.y));
    }
    
    private int calculateTurningChange(Direction oldDir, Direction newDir, boolean CW) {
        int turn = -4;
        Direction testDir = oldDir.opposite();
        while(testDir != newDir) {
            turn++;
            testDir = CW ? testDir.rotateLeft() : testDir.rotateRight();
        }
        Logger.debug_printHocho(String.valueOf(turn));
        return turn;
    }
}

