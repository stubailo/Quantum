package sprint.common;

import battlecode.common.*;

public class MapLocationSet {
    
    private final MapLocation   compareLocation;
    private final MapLocation[] array;
    private final boolean[][]   containsHash;
    private final int[][]       indexTable;
    private final int[]         removalShift;
    private       int           length;
    
    private static final int TABLE_SIZE = GameConstants.MAP_MAX_WIDTH;
    private static final int ARRAY_SIZE = 255;
    

    public MapLocationSet(MapLocation location) {
        compareLocation = location;
        array           = new MapLocation[ARRAY_SIZE];
        removalShift    = new int        [ARRAY_SIZE];
        containsHash    = new boolean    [TABLE_SIZE][TABLE_SIZE];
        indexTable      = new int        [TABLE_SIZE][TABLE_SIZE];
    }
    
    public void add (MapLocation location) {
        if(contains(location)) return;

        array[length] = location;
        
        int hashX = location.x % TABLE_SIZE;
        int hashY = location.y % TABLE_SIZE;
        containsHash[hashX][hashY] = true;
        indexTable  [hashX][hashY] = length;
        
        length   += 1;
    }
    
    public boolean contains (MapLocation location) {
        return containsHash[location.x % TABLE_SIZE][location.y % TABLE_SIZE];
    }
    
    //public MapLocation get (int index) {
    //}
    
    public int getLength() {
        return length;
    }
    
    public void remove (MapLocation location) {
        containsHash[location.x % TABLE_SIZE][location.y % TABLE_SIZE] = false;
        length -= 1;
    }
    
    public void remove (int index) {
        length -= 1;
        
    }
}
