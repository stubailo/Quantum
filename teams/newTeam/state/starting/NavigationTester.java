package newTeam.state.starting;

import battlecode.common.Direction;
import battlecode.common.MapLocation;
import newTeam.handler.navigation.NavigatorType;
import newTeam.state.BaseState;

public class NavigationTester extends BaseState {
    
    public NavigationTester(BaseState oldState, MapLocation goal) {
        super(oldState);
        
        myMH.initializeNavigationTo(goal, NavigatorType.TANGENT_BUG);
    }
    
    @Override
    public BaseState execute() {
        if(myMH.step()) {
            myMH.initializeNavigationTo(myK.myLocation.add(Direction.WEST, 16).add(Direction.NORTH, 3), 
                    NavigatorType.TANGENT_BUG);
        }
        return this;
    }

}
