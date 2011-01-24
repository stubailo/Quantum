package newTeam.state.recycler;

import battlecode.common.*;

import newTeam.state.BaseState;
import newTeam.state.idle.Idling;
import newTeam.common.Prefab;
import newTeam.common.util.Logger;
import newTeam.state.idle.Idling;

public class ConstructingAntennaOnSelf extends BaseState {
    
    private boolean turnedOn = false;
    private int     turnedOnRound;

    public ConstructingAntennaOnSelf(BaseState oldState) {
        super(oldState);
        myK.turnOff = true;
    }

    @Override
    public void senseAndUpdateKnowledge() {
        if(myK.turnedOn) {
            turnedOnRound = Clock.getRoundNum();
            turnedOn = true;
        }
        
    }

    @Override
    public BaseState getNextState() {
        
        if(turnedOn) {
            if( myBH.finishedBuilding() )
            {
                return new WaitingForBaseUpgrade(this);
            }
        }

        return this;
    }

    @Override
    public BaseState execute() {

        if(turnedOn && Clock.getRoundNum() - turnedOnRound >= GameConstants.POWER_WAKE_DELAY) {
            //add a sensor method that checks if the square is occupied
            Logger.debug_printHocho("currentlyBuilding? " + myBH.getCurrentlyBuilding() + " flux " + myK.totalFlux + " threshold " + (Prefab.commRecycler.getComponentCost() - ComponentType.RECYCLER.cost + 10) );
            if( !myBH.getCurrentlyBuilding() && myK.totalFlux > Prefab.commRecycler.getComponentCost() - ComponentType.RECYCLER.cost + 10 )
            {
                Logger.debug_printSashko("building antenna");
                myBH.buildComponents( Prefab.commRecycler , myRC.getLocation());
            }
    
            myBH.step();
        }
            
        return this;
    }

}
