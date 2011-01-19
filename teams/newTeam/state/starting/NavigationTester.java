package newTeam.state.starting;

import battlecode.common.MapLocation;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.BaseState;

public class NavigationTester extends BaseState {
    
    public NavigationTester(BaseState oldState, MapLocation goal) {
        super(oldState);
        
        myMH.initializeNavigationTo(goal, NavigatorType.BUG);
    }
    
    @Override
    public BaseState execute() {
        myMH.step();
        return this;
    }

}
