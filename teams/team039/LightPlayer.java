package team039;

import battlecode.common.*;
import static battlecode.common.GameConstants.*;

public class LightPlayer extends RobotPlayer {
   
    /*** FIELDS **/
    private final RobotController    myRC;
    private       MovementController myMC;
    private       SensorController   mySC;
    private       WeaponController   myWC;
    private       BuilderController  myBC;

    private       boolean            hasSensor;
    private       boolean            hasWeapon;
    private       boolean            hasBuilder;


    /*** CONSTRUCTOR **/
    public LightPlayer(RobotController rc) {
        super(rc);
        myRC = rc;
    }

    /*** FIRST ROUND ACTIONS **/
    public void doFirstRoundActions() {
        ComponentController[] newComponents = myRC.newComponents();
        myMC = (MovementController) newComponents[0];
        if(newComponents.length > 1) {
            myBC = (BuilderController) newComponents[1];
            mySC = (SensorController)  newComponents[2];
            hasBuilder = true;
            hasSensor  = true;
        }
    }

    public void run() {

        doFirstRoundActions();

    /*** MAIN LOOP **/
        while(true) {
            try {

                debug_printNewComponents();
                debug_setStrings();
                myRC.yield();

                /*if(!canSenseMine()) {
                    // turn
                }
                else {
                    // 1. find open spot near mine
                    // 2. go to that spot
                }*/

            }
            catch(Exception e) {
                System.out.println("Robot " + myRC.getRobot().getID() + " during round number " + Clock.getRoundNum() + " caught exception:");
                e.printStackTrace();
            }
        }
    }
}
