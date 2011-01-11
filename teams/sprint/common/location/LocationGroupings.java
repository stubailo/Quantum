package sprint.common.location;

import battlecode.common.*;

public class LocationGroupings {
    
    private static final int DEFAULT_SIZE = 100;
    
    private final LocationGrouping[] locationGroupings = new LocationGrouping[DEFAULT_SIZE];
    private final int[]              lastRoundVisited  = new int[DEFAULT_SIZE];
    private       int                numberOfGroupings = 0;
    private final LocationHash       totalHash         = new LocationHash();
    private final LocationIndex      locationIndex     = new LocationIndex();
    
    
    public LocationGroupings() {
        
    }
    
    public int add(MapLocation loc, int roundNum) {
        
        if(totalHash.contains(loc)) {
            // Already contains
            int index = locationIndex.getIndex(loc);
            lastRoundVisited[index] = roundNum;
            return index;
        }
        totalHash.add(loc);
        for(int i = 0; i < numberOfGroupings; i++) {
            LocationGrouping locationGrouping = locationGroupings[i];
            if(locationGrouping.add(loc)) {
                locationIndex.setIndex(loc, i);
                lastRoundVisited[i] = roundNum;
                return i;
            }
        }
        locationGroupings[numberOfGroupings] = new LocationGrouping(loc);
        lastRoundVisited[numberOfGroupings] = roundNum;
        locationIndex.setIndex(loc, numberOfGroupings);
        return numberOfGroupings++;
    }
    
    public boolean add(MapLocation loc, int groupNumber, int roundNum) {
        if(totalHash.contains(loc)) return false;
        return true;
    }
    
    public void visit(int groupNumber, int roundNum) {
        lastRoundVisited[groupNumber] = roundNum;
    }
    
    public void visit(MapLocation loc, int roundNum) {
        lastRoundVisited[locationIndex.getIndex(loc)] = roundNum;
    }
    
    public boolean contains(MapLocation loc) {
        return totalHash.contains(loc);
    }
    

}
