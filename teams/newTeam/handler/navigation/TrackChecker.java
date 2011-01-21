package newTeam.handler.navigation;

import newTeam.common.util.Logger;
import battlecode.common.Direction;
import battlecode.common.GameConstants;
import battlecode.common.MapLocation;

public class TrackChecker {
    private final int HASH_WIDTH = GameConstants.MAP_MAX_WIDTH;
    private final int HASH_HEIGHT = GameConstants.MAP_MAX_HEIGHT;
    
    private     boolean [][][]    directionsHash;
    
    public TrackChecker () {
        directionsHash = new boolean [HASH_WIDTH][HASH_HEIGHT][16];
    }
    /**
     * Records a tracking direction at the given location.  Returns true if the
     * direction had previously been recorded there.  
     * @param here
     * @param dir
     * @param CW
     * @return
     */
    public boolean addTrackingDirection(MapLocation here, Direction dir, boolean CW) {
        int directionNum = dir.ordinal();
        boolean prevEntry;
        
        if(directionNum >= 8) {
            return false;
        }       
        if(!CW) {
            directionNum += 8;
        }
        
        int xIndex = here.x % HASH_WIDTH;
        int yIndex = here.y % HASH_HEIGHT;
        prevEntry = directionsHash[xIndex][yIndex][directionNum];
        directionsHash[xIndex][yIndex][directionNum] = true;
        return prevEntry;
    }

    /**
     * Returns true if the recorded tracking direction indicates that bugging
     * from here to the goal is impossible.  Specifically, if you are bugging 
     * past the same location with the opposite direction and orientation, it
     * returns true.
     * @param here
     * @param dir
     * @param CW
     * @return
     */
    public boolean checkTrackingDirection(MapLocation here, Direction dir, boolean CW) {
        int directionNum = dir.opposite().ordinal();
        
        if(directionNum >= 8) {
            return false;
        }       
        if(CW) {
            directionNum += 8;
        } 
        if(directionsHash == null) {
            Logger.debug_printAntony("seriously what the fuck...");
        }
        return directionsHash[here.x % HASH_WIDTH][here.y % HASH_HEIGHT][directionNum];
    }

}
