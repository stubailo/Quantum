package newTeam.handler.navigation;

import newTeam.common.QuantumConstants;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class VirtualBug {
    
    private final int MAX_MOVES = QuantumConstants.TANGENT_BUG_PATH_LENGTH;
//    private final int MAX_BUGS = QuantumConstants.NUMBER_OF_VIRTUAL_BUGS;
    private final MapLocation goal;
    private final RobotController myRC;
    
    private        boolean              tracking;
    private        boolean              trackingCW;
    private        boolean              startTracking;
    private        Direction            trackingDirection;
    private        Direction            prevTrackingDirection;
    private        Direction            prevDirectionToGoal;
    private        MapLocation          prevLocation;
    private        int                  turningNumber;
    private        Direction            movementDirection = Direction.NONE;
    /* moves lists the path to take in order, beginning with an index 0 corresponding to 
     * the first adjacent square.
     */
    private        MapLocation []       moves;
    private        int                  moveIndex;
    
    private        int                  turnsToGoal;
    private        int                  turnsAlongPath;
    private        int                  pathWeight;

    public VirtualBug(MapLocation g, RobotController rc) {
        goal = g;
        myRC = rc;
        moves = new MapLocation [MAX_MOVES];
        tracking = false;
        moveIndex = 0;
        
    }
    
    public VirtualBug(MapLocation g,
                      RobotController rc,
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
    }
    
    public VirtualBug clone() {
        return new VirtualBug(goal,
                              myRC,
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
    
    public void setOrientation(boolean clockwiseOrientation) {
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
    
    public boolean shouldBranch() {
        if(tracking) {
            return false;
        }
        
        Direction directionToGoal;
        if(moveIndex == 0) {
            directionToGoal = startLocation.directionTo(goal);
        }
        return false;
    }
    
    /** 
     * Calculates the next square in the virtual bug's path.  
     * @return Null if the step was successful, and a MapLocation if you need to begin
     *         exploring a secondary goal.
     */
    public MapLocation stepVirtualBug() {
        
        
        return null;
    }
    
    /******************* Private Methods ********************/

    private void updatePathWeight() {
        turnsToGoal = 
    }
}

