package newTeam.state.starting;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.handler.navigation.NavigatorType;

public class DesperatelyDeterminingFactoryAndArmoryLocations extends DeterminingFactoryAndArmoryLocations {
    
    private final MapLocation[] adjacentLocations  = new MapLocation[12];
    private       int           current1Index      = 0;
    private       int           current2Index      = 0;
    private       boolean       new2Array          = true;
    private final MapLocation[] potentialLocations = new MapLocation[11];
    private final boolean       clockwise;
    private       Direction     addDirection1;
    private       Direction     addDirection2;
    private       Direction     addDirection3;
    private       Direction     addDirection4;
    
    public DesperatelyDeterminingFactoryAndArmoryLocations(BaseState oldState, Direction dir1, boolean rotateRight) {
        super(oldState);
        adjacentLocations[0] = myK.myLocation;
        Direction dir2       = rotateRight ? dir1.rotateRight() : dir1.rotateLeft();
        adjacentLocations[1] = adjacentLocations[0].add(dir1);
        adjacentLocations[2] = adjacentLocations[1].add(dir1);
        adjacentLocations[3] = adjacentLocations[1].add(dir2);
        dir1 = rotateRight ? dir1.rotateRight().rotateRight() : dir1.rotateLeft().rotateLeft();
        dir2 = rotateRight ? dir1.rotateRight() : dir1.rotateLeft();
        adjacentLocations[4] = adjacentLocations[3].add(dir1);
        adjacentLocations[5] = adjacentLocations[4].add(dir1);
        adjacentLocations[6] = adjacentLocations[4].add(dir2);
        dir1 = rotateRight ? dir1.rotateRight().rotateRight() : dir1.rotateLeft().rotateLeft();
        dir2 = rotateRight ? dir1.rotateRight() : dir1.rotateLeft();
        adjacentLocations[7] = adjacentLocations[6].add(dir1);
        adjacentLocations[8] = adjacentLocations[7].add(dir1);
        adjacentLocations[9] = adjacentLocations[7].add(dir2);
        dir1 = rotateRight ? dir1.rotateRight().rotateRight() : dir1.rotateLeft().rotateLeft();
        dir2 = rotateRight ? dir1.rotateRight() : dir1.rotateLeft();
        adjacentLocations[10] = adjacentLocations[9].add(dir1);
        adjacentLocations[11] = adjacentLocations[10].add(dir1);
        
        clockwise = rotateRight;
        addDirection1 = dir1;
        addDirection2 = dir2;
        if(clockwise) {
            addDirection3 = dir2.rotateRight();
            addDirection4 = addDirection3.rotateRight();
        }
        else {
            addDirection3 = dir2.rotateLeft();
            addDirection4 = addDirection3.rotateLeft();
        }
    }
    
    @Override
    public void senseAndUpdateKnowledge() {
        MapLocation myLocation = myK.myLocation;

        if(new2Array) {
            if(myLocation.equals(adjacentLocations[current1Index])) {
                if(inGoodBuildLocation()) {
                    
                    switch(current1Index % 3) {
                    
                    case 0:
                        if(clockwise) {
                            addDirection1 = addDirection1.rotateRight().rotateRight();
                            addDirection2 = addDirection1.rotateRight();
                            addDirection3 = addDirection2.rotateRight();
                            addDirection4 = addDirection3.rotateRight();
                        }
                        else {
                            addDirection1 = addDirection1.rotateLeft().rotateLeft();
                            addDirection2 = addDirection1.rotateLeft();
                            addDirection3 = addDirection2.rotateLeft();
                            addDirection4 = addDirection3.rotateLeft();
                        }
                        
                        potentialLocations[6] = myLocation.add(addDirection1.opposite()).add(addDirection2.opposite());
                        potentialLocations[5] = potentialLocations[6].add(addDirection1);
                        potentialLocations[4] = potentialLocations[5].add(addDirection1);
                        potentialLocations[3] = potentialLocations[4].add(addDirection1);
                        potentialLocations[2] = potentialLocations[3].add(addDirection1);
                        potentialLocations[1] = potentialLocations[2].add(addDirection3);
                        potentialLocations[0] = potentialLocations[1].add(addDirection3);
                        potentialLocations[7] = potentialLocations[6].add(addDirection3);
                        potentialLocations[8] = potentialLocations[7].add(addDirection3);
                        potentialLocations[9] = potentialLocations[8].add(addDirection3);
                        potentialLocations[10] = potentialLocations[9].add(addDirection1);
                        break;
                        
                    case 1:
                        potentialLocations[8] = myLocation.add(addDirection1.opposite()).add(addDirection2.opposite());
                        potentialLocations[7] = potentialLocations[8].add(addDirection1);
                        potentialLocations[6] = potentialLocations[7].add(addDirection1);
                        potentialLocations[5] = potentialLocations[6].add(addDirection1);
                        potentialLocations[4] = potentialLocations[5].add(addDirection1);
                        potentialLocations[3] = potentialLocations[4].add(addDirection3);
                        potentialLocations[2] = potentialLocations[3].add(addDirection3);
                        potentialLocations[1] = potentialLocations[2].add(addDirection3);
                        potentialLocations[0] = potentialLocations[1].add(addDirection1.opposite());
                        potentialLocations[9] = potentialLocations[8].add(addDirection3);
                        potentialLocations[10] = potentialLocations[9].add(addDirection3);
                        break;
                        
                        
                    case 2:
                        potentialLocations[6] = myLocation.add(addDirection1.opposite()).add(addDirection2.opposite());
                        potentialLocations[5] = potentialLocations[6].add(addDirection1);
                        potentialLocations[4] = potentialLocations[5].add(addDirection1);
                        potentialLocations[3] = potentialLocations[4].add(addDirection2);
                        potentialLocations[2] = potentialLocations[3].add(addDirection3);
                        potentialLocations[1] = potentialLocations[2].add(addDirection3);
                        potentialLocations[0] = potentialLocations[1].add(addDirection1.opposite());
                        potentialLocations[7] = potentialLocations[6].add(addDirection3);
                        break;
                    }
                    
                    new2Array = false;
                    current2Index = 0;
                    myMH.initializeNavigationTo(potentialLocations[0], NavigatorType.BUG);
                }
                else {
                    if(current1Index == 11) {
                        // COMPLETELY GIVE UP?!
                    }
                    else {
                        myMH.initializeNavigationTo(adjacentLocations[++current1Index], NavigatorType.BUG);
                    }
                }
            }
        }
        if(!new2Array) {
            if(myLocation.equals(potentialLocations[current2Index])) {
                if(myLocation.isAdjacentTo(adjacentLocations[current1Index])) {
                    if(inGoodBuildLocation(myLocation.directionTo(adjacentLocations[current1Index]))) {
                        factoryLocation = adjacentLocations[current1Index];
                        armoryLocation  = potentialLocations[current2Index];
                    }
                    else {
                        if(current2Index == ((current1Index % 3 == 2) ? 7 : 10)) {
                            if(current1Index == 11) {
                                // COMPLETELY GIVE UP?!
                            }
                            else {
                                current2Index = 0;
                                new2Array = true;
                                myMH.initializeNavigationTo(adjacentLocations[++current1Index], NavigatorType.BUG);
                            }
                        }
                        else {
                            myMH.initializeNavigationTo(potentialLocations[++current2Index], NavigatorType.BUG);
                        }
                    }
                }
                else {
                    if(inGoodBuildLocation()) {
                        factoryLocation = adjacentLocations[current1Index];
                        armoryLocation  = potentialLocations[current2Index];
                    }
                    else {
                        if(current2Index == ((current1Index % 3 == 2) ? 7 : 10)) {
                            if(current1Index == 11) {
                                // COMPLETELY GIVE UP?!
                            }
                            else {
                                current2Index = 0;
                                new2Array = true;
                                myMH.initializeNavigationTo(adjacentLocations[++current1Index], NavigatorType.BUG);
                            }
                        }
                        else {
                            myMH.initializeNavigationTo(potentialLocations[++current2Index], NavigatorType.BUG);
                        }
                    }
                }
            }
        }
    }
    
    
    @Override
    public BaseState execute() {
        Boolean reachedGoal = myMH.step();
        if(reachedGoal == null) {
            if(new2Array) {
                if(current1Index == 11) {
                    // COMPLETELY GIVE UP?!
                }
                else {
                    myMH.initializeNavigationTo(adjacentLocations[++current1Index], NavigatorType.BUG);
                    senseAndUpdateKnowledge();
                    return execute();
                }
            }
            else {
                if(current2Index == ((current1Index % 3 == 2) ? 7 : 10)) {
                    if(current1Index == 11) {
                        // COMPLETELY GIVE UP?!
                    }
                    else {
                        current2Index = 0;
                        new2Array = true;
                        myMH.initializeNavigationTo(adjacentLocations[++current1Index], NavigatorType.BUG);
                        senseAndUpdateKnowledge();
                        return execute();
                    }
                }
                else {
                    myMH.initializeNavigationTo(potentialLocations[++current2Index], NavigatorType.BUG);
                    senseAndUpdateKnowledge();
                    return execute();
                }
            }
        }
        return this;
    }

}
