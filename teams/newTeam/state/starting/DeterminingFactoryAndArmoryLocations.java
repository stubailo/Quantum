package newTeam.state.starting;

import battlecode.common.*;

import newTeam.common.util.Logger;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.BaseState;

public class DeterminingFactoryAndArmoryLocations extends BaseState {
    
    private       MapLocation goalLocation = null;
    protected     MapLocation factoryLocation = null;
    protected     MapLocation armoryLocation = null;
    private       MapLocation adjacentRecycler;
    private       MapLocation adjacentLocation;
    
    private       int           currentAdjacentIndex = 0;
    private       MapLocation[] adjacentLocations = new MapLocation[8];
    
    private       boolean     started = false;
    private       boolean     involved = false;
    private       boolean     tryingAdjacent = false;
    private       boolean     checkedAdjacent = false;
    private       boolean     checkedNonAdjacent = false;
    private       boolean     currentlyAdjacent = false;
    private       boolean     clockwise = false;
    private       boolean     aboutToTurn = false;
    private       boolean     movingOn = false;
    private       boolean     startingToMove = false;
    private       boolean     givingUp = false;
    private       Boolean     reachedGoal;
    
    
    
    public DeterminingFactoryAndArmoryLocations(BaseState oldState) {
        super(oldState);
    }
    
    @Override
    public void senseAndUpdateKnowledge() {
        MapLocation myLocation = myK.myLocation;
        
        Direction directionToAdjacent = null,
                  testDirection1 = null,
                  testDirection2 = null,
                  testDirection3 = null,
                  testDirection4 = null;
        
        if(!started) {
            adjacentRecycler = mySH.getOrthogonallyAdjacentStartingRecycler(myLocation);
            if(adjacentRecycler != null) {
                started = true;
                currentlyAdjacent = true;
            }
            else {
                Direction directionToJustBuilt = myLocation.directionTo(mySH.startingFirstMineToBeBuiltLocation);
                goalLocation = myLocation.add(directionToJustBuilt.rotateRight());
                if(mySH.getOrthogonallyAdjacentStartingRecycler(goalLocation) == null) {
                    goalLocation = myLocation.add(directionToJustBuilt.rotateLeft());
                    if(mySH.getOrthogonallyAdjacentStartingRecycler(goalLocation) == null) {
                        goalLocation = myLocation.add(directionToJustBuilt);
                        if(mySH.getOrthogonallyAdjacentStartingRecycler(goalLocation) == null) {
                            Logger.debug_printCustomErrorMessage("uhoh UHOH", "Hocho");
                        }
                    }
                }
                myMH.initializeNavigationTo(goalLocation, NavigatorType.BUG);
            }
        }
        if(involved) {
            if(reachedGoal) {
                
                if(currentlyAdjacent) {
                    adjacentRecycler = mySH.getOrthogonallyAdjacentStartingRecycler(myLocation);
                    directionToAdjacent = myLocation.directionTo(adjacentRecycler);
                    if(clockwise ^ aboutToTurn) {
                        testDirection2 = directionToAdjacent.rotateLeft();
                        testDirection1 = testDirection2.rotateLeft();
                        testDirection3 = directionToAdjacent.rotateRight();
                        testDirection4 = testDirection3.rotateRight();
                    }
                    else {
                        testDirection3 = directionToAdjacent.rotateLeft();
                        testDirection4 = testDirection3.rotateLeft();
                        testDirection2 = directionToAdjacent.rotateRight();
                        testDirection1 = testDirection2.rotateRight();
                    }
                    
                    movingOn = true;
                    if(inGoodBuildLocation() && !checkedNonAdjacent) {
                        if(myMH.canMove(testDirection4)) {
                            if(myMH.canMove(testDirection3) && !checkedAdjacent) {
                                tryingAdjacent = true;
                                checkedAdjacent = true;
                                movingOn = false;
                            }
                            else if(myMH.canMove(directionToAdjacent.opposite())) {
                                tryingAdjacent = false;
                                checkedNonAdjacent = true;
                                movingOn = false;
                            }
                        }
                        else {
                            if(myMH.canMove(directionToAdjacent.opposite()) && myMH.canMove(testDirection1)) {
                                tryingAdjacent = false;
                                checkedNonAdjacent = true;
                                movingOn = false;
                            }
                        }
                    }
                    
                    if(movingOn) {
                        if(currentAdjacentIndex == 7) {
                            givingUp = true;
                            return;
                        }
                        else {
                            goalLocation = adjacentLocations[++currentAdjacentIndex];
                            aboutToTurn  = !aboutToTurn;
                        }
                    }
                    else {
                        if(tryingAdjacent) {
                            goalLocation = myLocation.add(testDirection4);
                        }
                        else {
                            goalLocation = myLocation.add(directionToAdjacent.opposite());
                        }
                    }
                }
                else {
                    if(inGoodBuildLocation(myLocation.directionTo(adjacentLocation))) {
                        
                        factoryLocation = adjacentLocation;
                        armoryLocation  = myLocation;
                        goalLocation    = myLocation;
                    }
                    else {
                        goalLocation = adjacentLocations[currentAdjacentIndex];
//                        Logger.debug_printHocho("traveling back to " + goalLocation + ", checkedAdjcent? " + checkedAdjacent + ", checkedNonAdjacent? " + checkedNonAdjacent);
                    }
                }
                
                startingToMove = true;
            }
        }
        else if(started) {
            
            adjacentRecycler = mySH.getOrthogonallyAdjacentStartingRecycler(myLocation);
            directionToAdjacent = myLocation.directionTo(adjacentRecycler);
            testDirection2 = directionToAdjacent.rotateLeft();
            testDirection1 = testDirection2.rotateLeft();
            testDirection3 = directionToAdjacent.rotateRight();
            testDirection4 = testDirection3.rotateRight();
            MapLocation testLocation1 = myLocation.add(testDirection1);
//            MapLocation testLocation4 = myLocation.add(testDirection4);
            
            if(mySH.getOrthogonallyAdjacentStartingRecycler(testLocation1) == null) {
                
                clockwise   = false;
                aboutToTurn = false;
                
                Direction directionMemory;
                
                directionMemory = testDirection1;
                testDirection1  = testDirection4;
                testDirection4  = directionMemory;
                directionMemory = testDirection2;
                testDirection2  = testDirection3;
                testDirection3  = directionMemory;
            }
            else {
                clockwise = true;
                aboutToTurn = false;
            }
            
            adjacentLocations[0] = myLocation;
            adjacentLocations[1] = myLocation.add(testDirection1);
            adjacentLocations[2] = adjacentLocations[1].add(testDirection2);
            adjacentLocations[3] = adjacentLocations[2].add(directionToAdjacent);
            adjacentLocations[4] = adjacentLocations[3].add(testDirection3);
            adjacentLocations[5] = adjacentLocations[4].add(testDirection4);
            adjacentLocations[7] = myLocation.add(testDirection3);
            adjacentLocations[6] = adjacentLocations[7].add(directionToAdjacent);
            
//            Logger.debug_printHocho("myLocation: " + myLocation);
            
//            Logger.debug_printHocho("directionToAdjacent: " + directionToAdjacent);
//            Logger.debug_printHocho("testDirection1" + testDirection1);
//            for(MapLocation location: adjacentLocations) {
//                Logger.debug_printHocho("adjacentLocation: " + location);
//            }
            
            movingOn = true;
            if(inGoodBuildLocation()) {
//                Logger.debug_printHocho("in good build location");
                if(myMH.canMove(testDirection4)) {
                    if(myMH.canMove(testDirection3) && !checkedAdjacent) {
                        tryingAdjacent = true;
                        movingOn = false;
                        checkedAdjacent = true;
                    }
                    else if(myMH.canMove(directionToAdjacent.opposite())) {
                        tryingAdjacent = false;
                        movingOn = false;
                    }
                }
                else {
                    if(myMH.canMove(directionToAdjacent.opposite()) && myMH.canMove(testDirection1)) {
                        tryingAdjacent = false;
                        movingOn = false;
                    }
                }
            }
            
            if(movingOn) {
                goalLocation = adjacentLocations[++currentAdjacentIndex];
                aboutToTurn  = !aboutToTurn;
            }
            else {
                if(tryingAdjacent) {
                    goalLocation = myLocation.add(testDirection4);
                }
                else {
                    goalLocation = myLocation.add(directionToAdjacent.opposite());
                }
            }
            startingToMove = true;
            involved = true;
        }
        
        if(startingToMove) {
            if(currentlyAdjacent) {
                if(movingOn) {
                    checkedAdjacent = false;
                    checkedNonAdjacent = false;
                    currentlyAdjacent = true;
                }
                else {
                    currentlyAdjacent = false;
                    adjacentLocation = myLocation;
                }
            }
            else {
                currentlyAdjacent = true;
            }
            myMH.initializeNavigationTo(goalLocation, NavigatorType.BUG);
        }
        
        startingToMove = false;
    }
    
    @Override
    public BaseState getNextState() {
        
        if(factoryLocation != null && armoryLocation != null) {
            // NEXT STATE!
            Logger.debug_printHocho("factoryLocation: " + factoryLocation + ", armoryLocation: " + armoryLocation);
            try {
                MapLocation turnOnLocation = mySH.turnOnRecyclerLocation(factoryLocation);
                if(turnOnLocation != null) {
                    Logger.debug_printHocho("myLocation: " + myK.myLocation + ", turnOnLocation " + turnOnLocation);
                    BaseState result = new TurningOnRecycler(this, turnOnLocation, factoryLocation, armoryLocation);
                    result.senseAndUpdateKnowledge();
                    return result.getNextState();
                }
                else {
                    Direction dir1 = adjacentLocations[7].directionTo(adjacentLocations[6]);
                    BaseState result = new DesperatelyDeterminingFactoryAndArmoryLocations(this, dir1, dir1.rotateRight() == adjacentLocations[6].directionTo(adjacentLocations[5]));
                    result.senseAndUpdateKnowledge();
                    return result.getNextState();
                }
            }
            catch(Exception e) {
                Logger.debug_printExceptionMessage(e);
            }
            myRC.breakpoint();
        }
        
        return this;
    }
    
    @Override
    public BaseState execute() {
        
        if(givingUp) {
            Direction dir1 = adjacentLocations[7].directionTo(adjacentLocations[6]);
            return new DesperatelyDeterminingFactoryAndArmoryLocations(this, dir1, dir1.rotateRight() == adjacentLocations[6].directionTo(adjacentLocations[5]));
        }
        
        if(started) {
            reachedGoal = myMH.step();
            if(reachedGoal == null) {
                if(currentAdjacentIndex == 7) {
                    Direction dir1 = adjacentLocations[7].directionTo(adjacentLocations[6]);
                    BaseState result = new DesperatelyDeterminingFactoryAndArmoryLocations(this, dir1, dir1.rotateRight() == adjacentLocations[6].directionTo(adjacentLocations[5]));
                    result.senseAndUpdateKnowledge();
                    return result.getNextState();
                } else {
                    myMH.initializeNavigationTo(adjacentLocations[++currentAdjacentIndex], NavigatorType.BUG);
                    return execute();
                }
            }
        }
        return this;
    }
    
    public boolean inGoodBuildLocation()
    {
        Direction testDirection = Direction.EAST;
        boolean foundVoid = false;
        
        int i = 0;
        int voidIndex = 0;

        for( ; i < 8; i++ )
        {
            if(!myMH.canMove( testDirection ) )
            {
                foundVoid = true;
                voidIndex = i;
                break;
            }
            i++;
            testDirection = testDirection.rotateRight().rotateRight();
        }

        if( !foundVoid )
        {
            return true;
        }
        
//        Logger.debug_printHocho("void found: " + i);
        i++;
        testDirection = testDirection.rotateRight();

        for( ; i < 8; i++ )
        {
            if(myMH.canMove( testDirection ))
            {
                break;
            }

            testDirection = testDirection.rotateRight();
        }
        
//        Logger.debug_printHocho("empty found: " + i);
        i = (testDirection.ordinal() % 2 == 1) ? i + 1 : i + 2;
        testDirection = (testDirection.ordinal() % 2 == 1) ? testDirection.rotateRight() : testDirection.rotateRight().rotateRight();
        
        foundVoid = false;
        
        for( ; i < 8; i++ )
        {
            if(!myMH.canMove( testDirection ) )
            {
                foundVoid = true;
                break;
            }
            i++;
            testDirection = testDirection.rotateRight().rotateRight();
        }
        
        if( !foundVoid )
        {
            return true;
        }
        
//        Logger.debug_printHocho("second void found: " + i);
        i++;
        testDirection = testDirection.rotateRight();
        
        for( ; i < 8 + voidIndex; i++ )
        {
            if(myMH.canMove( testDirection ))
            {
                return false;
            }

            testDirection = testDirection.rotateRight();
        }

        return true;

    }
    
    public boolean inGoodBuildLocation(Direction cantMoveDirection)
    {
        Direction testDirection = Direction.EAST;
        boolean foundVoid = false;
        
        int i = 0;
        int voidIndex = 0;

        for( ; i < 8; i++ )
        {
            if(!myMH.canMove( testDirection ) || testDirection == cantMoveDirection )
            {
                foundVoid = true;
                voidIndex = i;
                break;
            }
            i++;
            testDirection = testDirection.rotateRight().rotateRight();
        }

        if( !foundVoid )
        {
            return true;
        }
        
//        Logger.debug_printHocho("void found: " + i + ", voidIndex: " + voidIndex);
        i++;
        testDirection = testDirection.rotateRight();

        for( ; i < 8; i++ )
        {
            if(myMH.canMove( testDirection ) && testDirection != cantMoveDirection)
            {
                break;
            }

            testDirection = testDirection.rotateRight();
        }
        
//        Logger.debug_printHocho("empty found: " + i);
        i = (testDirection.ordinal() % 2 == 1) ? i + 1 : i + 2;
        testDirection = (testDirection.ordinal() % 2 == 1) ? testDirection.rotateRight() : testDirection.rotateRight().rotateRight();
        
        foundVoid = false;
        
        for( ; i < 8; i++ )
        {
            if(!myMH.canMove( testDirection ) || testDirection == cantMoveDirection)
            {
                foundVoid = true;
                break;
            }
            i++;
            testDirection = testDirection.rotateRight().rotateRight();
        }
        
        if( !foundVoid )
        {
            return true;
        }
        
//        Logger.debug_printHocho("second void found: " + i);
        i++;
        testDirection = testDirection.rotateRight();
        
        for( ; i < 8 + voidIndex; i++ )
        {
//            Logger.debug_printHocho("testDirection: " + testDirection);
            if(myMH.canMove( testDirection ) && testDirection != cantMoveDirection)
            {
                return false;
            }

            testDirection = testDirection.rotateRight();
        }

        return true;

    }

}
