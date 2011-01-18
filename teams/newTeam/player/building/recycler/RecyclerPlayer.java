package newTeam.player.building.recycler;

import battlecode.common.ComponentType;
import newTeam.common.RecyclerNode;
import newTeam.common.util.Logger;
import newTeam.player.BasePlayer;
import newTeam.player.building.recycler.RecyclerCommPlayer;
import newTeam.player.building.BuildingPlayer;
import newTeam.state.BaseState;
import newTeam.state.idle.Idling;

public class RecyclerPlayer extends BuildingPlayer {
    
    protected final RecyclerNode myRN;
    
    public RecyclerPlayer(BaseState state) {
        super(state);
        if(myK.myRecyclerNode == null) {
            myK.initializeRecyclerNode();
        }
        myRN = myK.myRecyclerNode;
    }
    
    @Override
    public BaseState determineNewStateBasedOnNewSpecificPlayer(BaseState oldState) {
        return new Idling( oldState );
    }
    
    @Override
    public void initialize() {
        if(!myCH.mySH.amLowestIDRecycler()) {
            myRC.setIndicatorString(0, "turning off...");
            Logger.debug_printHocho("turning off...");
            myRC.turnOff();
        }
    }
    
    @Override
    public void doSpecificPlayerStatelessActions() {
        super.doSpecificPlayerStatelessActions();
    }
    
    @Override
    public BasePlayer determineSpecificPlayerGivenNewComponent(ComponentType compType,
                                                               BaseState state) {

        switch(compType) {
        case ANTENNA:
        case DISH:
        case NETWORK:
            return new RecyclerCommPlayer(state);
        }
        return this;
    
    }

}
