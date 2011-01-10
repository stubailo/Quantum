package team039.common.location;

import battlecode.common.*;

public class LocationGrouping {
    
    private static final int MAX_DISTANCE = 8;
    private static final int DEFAULT_SIZE = 25;
    
    private final MapLocation[] locations;
    private final LocationHash  hash              = new LocationHash();
    private       int           numberOfLocations = 0;
    private final MapLocation   startingLocation;
    
    public LocationGrouping(MapLocation loc) {
        this(loc, DEFAULT_SIZE);
    }
    
    public LocationGrouping(MapLocation loc, int size) {
        locations = new MapLocation[size];
        locations[0] = loc;
        startingLocation = loc;
        numberOfLocations++;
    }
    
    public boolean add(MapLocation loc) {
        if(contains(loc)) return false;
        if(loc.distanceSquaredTo(startingLocation) <= MAX_DISTANCE) {
            locations[numberOfLocations] = loc;
            hash.add(loc);
            numberOfLocations++;
            return true;
        }
        return false;
    }
    
    public boolean contains(MapLocation loc) {
        return hash.contains(loc);
    }

}
