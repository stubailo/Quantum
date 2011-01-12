package team039.building.recycler;

import team039.common.*;
import team039.common.util.*;
import team039.handler.ComponentsHandler;
import battlecode.common.*;

public class StartingBuildingPlayer extends RecyclerPlayer {

    private final RobotController myRC;
    private final Knowledge knowledge;
    private final ComponentsHandler compHandler;

    public StartingBuildingPlayer(RobotController rc,
            Knowledge know,
            ComponentsHandler compHand) {

        super(rc, know, compHand);
        myRC = rc;
        knowledge = know;
        compHandler = compHand;
    }

    @Override
    public void doSpecificActions() {
        super.doSpecificActions();

    }

    @Override
    public void doSpecificFirstRoundActions() {
        super.doSpecificFirstRoundActions();

    }

    @Override
    public void initialize()
    {
        
        knowledge.myRecyclerNode = new RecyclerNode();
        knowledge.myRecyclerNode.myLocation = myRC.getLocation();
        knowledge.myRecyclerNode.myRobotID = myRC.getRobot().getID();
        knowledge.myRecyclerNode.parentLocation = null;
        knowledge.myRecyclerNode.parentRobotID = 0;

        Message message = MessageCoder.encodeMessage("msg1", knowledge.myRobotID, knowledge.myLocation, knowledge.roundNum, false, null, null, null);
        Message message2 = MessageCoder.encodeMessage("msg2", knowledge.myRobotID, knowledge.myLocation, knowledge.roundNum, false, null, null, null);
        Message [] messages = {message, message2};

        Message message_merge = MessageCoder.mergeMessages( messages );
        Message[] messages_split = MessageCoder.splitMessages(message_merge);

        System.out.println("lolol: " + MessageCoder.getMessageType(messages_split[0]));
    }

    @Override
    public SpecificPlayer determineSpecificPlayer(ComponentType compType) {
        SpecificPlayer result = this;
        return result;
    }

    @Override
    public void beginningStateSwitches() {
        super.beginningStateSwitches();


    }
}
