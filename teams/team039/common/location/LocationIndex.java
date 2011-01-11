package team039.common.location;

import battlecode.common.*;
import team039.common.QuantumConstants;

/**
 * This class stores a positive integer for each map location, and uses a hash to keep track of
 * for which locations it is storing an integer.
 * @author Jason
 *
 */
public class LocationIndex {
    
    private static final int hashSize = QuantumConstants.LOCATION_HASH_SIZE;

    private final int[][] hash;
    private final boolean[][] memoryHash;
    
    public LocationIndex() {
        hash       = new int[hashSize][hashSize];
        memoryHash = new boolean[hashSize][hashSize];
    }
    
    
    /**
     * Stores an integer for a given location
     * @param location  location for which to store an integer
     * @param index     integer to be stored
     */
    public void setIndex(MapLocation location, int index) {
        int hashX = location.x % hashSize,
            hashY = location.y % hashSize;
        hash[hashX][hashY] = index;
        memoryHash[hashX][hashY] = true;
    }
    
    
    /**
     * Gets the stored integer for a given location, returning -1 if it isn't storing anything.
     * @param location  location for which to retrieve integer.
     * @return          the integer, or -1 if there isn't a stored integer
     */
    public int getIndex(MapLocation location) {
        int hashX = location.x % hashSize,
            hashY = location.y % hashSize;
        if(memoryHash[hashX][hashY]) {
            return hash[location.x % hashSize][location.y % hashSize];
        }
        return -1;
    }
    
    
    /**
     * Tells the index to stop storing an integer for a given location
     * @param location  the location to be removed.
     */
    public void remove(MapLocation location) {
        memoryHash[location.x % hashSize][location.y % hashSize] = false;
    }
    
    
    /**
     * Queries whether the index is storing an integer for a given location
     * @param location  the location to query
     * @return          true or false, accordingly.
     */
    public boolean contains(MapLocation location) {
        return memoryHash[location.x % hashSize][location.y % hashSize];
    }

}
