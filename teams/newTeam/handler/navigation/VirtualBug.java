package newTeam.handler.navigation;

import newTeam.common.QuantumConstants;
import battlecode.common.Direction;
import battlecode.common.MapLocation;

public class VirtualBug {
    
    private final int MAX_MOVES = QuantumConstants.TANGENT_BUG_PATH_LENGTH;
    private final int MAX_BUGS = QuantumConstants.NUMBER_OF_VIRTUAL_BUGS;
    
    private        MapLocation          goal;
    private        boolean              tracking;
    private        boolean              trackingCW;
    private        boolean              startTracking;
    private        Direction            trackingDirection;
    private        Direction            prevTrackingDirection;
    private        Direction            prevDirectionToGoal;
    private        MapLocation          prevLocation;
    private        int                  turningNumber;
    private        Direction            movementDirection = Direction.NONE;
    private        MapLocation []       moves;
    private        int                  moveIndex;
    
    private        int                  turnsToGoal;
    private        int                  turnsAlongPath;
    private        int                  pathWeight;

    public VirtualBug(MapLocation g) {
        goal = g;
        moves = new MapLocation [MAX_MOVES];
    }
    
    public VirtualBug(MapLocation g,
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
        return false;
    }
    
    public boolean shouldCheckSecondaryGoal() {
        return false;
    }
}

