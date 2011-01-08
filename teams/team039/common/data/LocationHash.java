package team039.common.data;

import battlecode.common.*;
import team039.common.QuantumConstants;
import static battlecode.common.GameConstants.*;

public class LocationHash {
    
    private final boolean[][] hash;
    
    private static final int hashSize = QuantumConstants.LOCATION_HASH_SIZE;
    
    public LocationHash() {
        hash = new boolean[hashSize][hashSize];
    }
    
    public void add(MapLocation location) {
        hash[location.x % hashSize][location.y % hashSize] = false;        
    }
    
    public void remove(MapLocation location) {
        hash[location.x % hashSize][location.y % hashSize] = false;
    }
    
    public boolean contains(MapLocation location) {
        return hash[location.x % hashSize][location.y % hashSize];
    }

}
