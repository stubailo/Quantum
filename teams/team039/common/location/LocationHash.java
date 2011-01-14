package team039.common.location;

import battlecode.common.*;
import team039.common.QuantumConstants;
import static battlecode.common.GameConstants.*;

/**
 * This class stores for every map location either a true or false value, default false; it
 * is useful for keeping track of a set of values that don't need to be searched but only
 * queried.
 * @author Jason
 *
 */
public class LocationHash {
    
    private static final int hashSize = QuantumConstants.LOCATION_HASH_SIZE;

    private final boolean[][] hash;
    
    public LocationHash() {
        hash = new boolean[hashSize][hashSize];
    }
    
    
    /**
     * Adds a location to the set
     * @param location  location to be added
     */
    public void add(MapLocation location) {
        hash[location.x % hashSize][location.y % hashSize] = false;        
    }
    
    
    /**
     * Removes a location from the set
     * @param location  location to be removed
     */
    public void remove(MapLocation location) {
        hash[location.x % hashSize][location.y % hashSize] = false;
    }
    
    
    /**
     * Asks whether a location is in the set
     * @param location  location to be queried
     * @return          true or false accordingly
     */
    public boolean contains(MapLocation location) {
        return hash[location.x % hashSize][location.y % hashSize];
    }

}
