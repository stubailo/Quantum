package team039.common.data;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class MapLocationHash {
    
    private final boolean[][] hash;
    
    private static final int hashSize = GameConstants.MAP_MAX_WIDTH;
    
    public MapLocationHash() {
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
