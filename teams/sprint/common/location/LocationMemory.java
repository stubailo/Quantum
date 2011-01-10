package sprint.common.location;

import battlecode.common.*;
import sprint.common.QuantumConstants;

public class LocationMemory {
    
    private final LocationType[][] locationTypes;
    private final int[][]          roundLastUpdated;
    private static final int hashSize = QuantumConstants.LOCATION_HASH_SIZE;
    
    public LocationMemory() {
        locationTypes    = new LocationType[hashSize][hashSize];
        roundLastUpdated = new int[hashSize][hashSize];
    }
    
    public LocationType getType(MapLocation location) {
        return locationTypes[location.x % hashSize][location.y % hashSize];
    }
    
    public int getRoundLastUpdated(MapLocation location) {
        return roundLastUpdated[location.x % hashSize][location.y % hashSize];
    }
    
    public void setType(MapLocation location, LocationType locationType, int roundNum) {
        int hashX = location.x % hashSize,
            hashY = location.y % hashSize;
        locationTypes[hashX][hashY]    = locationType;
        roundLastUpdated[hashX][hashY] = roundNum;
    }

}
