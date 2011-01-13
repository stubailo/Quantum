package team039.common.location;

import battlecode.common.*;
import team039.common.QuantumConstants;
import team039.common.Knowledge;

public class LocationMemory {
    
    private static final int hashSize = QuantumConstants.LOCATION_HASH_SIZE;

    private final Knowledge        knowledge;
    private final LocationType[][] locationTypes;
    private final int[][]          roundLastUpdated;
    
    public LocationMemory(Knowledge know) {
        knowledge = know;
        locationTypes    = new LocationType[hashSize][hashSize];
        roundLastUpdated = new int[hashSize][hashSize];
    }
    
    public LocationType getType(MapLocation location) {
        return locationTypes[location.x % hashSize][location.y % hashSize];
    }
    
    public int getRoundLastUpdated(MapLocation location) {
        return roundLastUpdated[location.x % hashSize][location.y % hashSize];
    }
    
    public void setType(MapLocation location, LocationType locationType) {
        setType(location, locationType, knowledge.roundNum);
    }
    
    public void setType(MapLocation location, LocationType locationType, int roundNum) {
        int hashX = location.x % hashSize,
            hashY = location.y % hashSize;
        
        roundLastUpdated[hashX][hashY] = roundNum;
        
        switch(locationTypes[hashX][hashY]) {
        
        case DEBRIS:
            switch(locationType) {
            case LAND:           locationTypes[hashX][hashY] = LocationType.DESTROYED_DEBRIS; return;
            }
            break;
            
        case UNMINED_MINE:
        case OUR_MINE:
        case ENEMY_MINE:
            switch(locationType) {
            case LAND:           locationTypes[hashX][hashY] = LocationType.UNMINED_MINE;     return;
            case ENEMY_BUILDING: locationTypes[hashX][hashY] = LocationType.ENEMY_MINE;       return;
            case OUR_RECYCLER:   locationTypes[hashX][hashY] = LocationType.OUR_MINE;         return;
            }
            break;
            
        }
        locationTypes[hashX][hashY] = locationType;
    }

}
