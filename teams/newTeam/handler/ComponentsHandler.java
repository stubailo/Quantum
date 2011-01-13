package newTeam.handler;

import battlecode.common.*;

import battlecode.common.BroadcastController;
import battlecode.common.BuilderController;
import battlecode.common.ComponentController;
import battlecode.common.ComponentType;
import battlecode.common.MovementController;
import battlecode.common.SensorController;
import battlecode.common.WeaponController;

public class ComponentsHandler {
    
    public  final BroadcastHandler  myBCH;
    public  final BuildHandler      myBH;
    public  final MovementHandler   myMH;
    public  final SensorHandler     mySH;
    public  final WeaponHandler     myWH;
    
    public boolean hasWeapons = false;
    
    
    public ComponentsHandler(BroadcastHandler bch,
                             BuildHandler     bh,
                             MovementHandler  mh,
                             SensorHandler    sh,
                             WeaponHandler    wh) {
        
        myBCH = bch;
        myBH  = bh;
        myMH  = mh;
        mySH  = sh;
        myWH  = wh;
        
    }
    
    
    
    public ComponentType[] updateComponents(RobotController rc) {
        ComponentController[] newComps = rc.newComponents();

        int length = newComps.length;

        if (length == 0) return null;
        
        ComponentType[] newCompTypes = new ComponentType[length];

        int index = 0;

        for ( ComponentController newComp : newComps ) {
            ComponentType newCompType = newComp.type();
            newCompTypes[index] = newCompType;
            index++;

            

            switch (newCompType) {
                // TODO: handle IRON, JUMP, DROPSHIP, BUG, DUMMY, MEDIC
                // TODO: handle passives

                case BUILDING_MOTOR:
                case SMALL_MOTOR:
                    myMH.addMC((MovementController) newComp);
                    break;

                case ANTENNA:
                case DISH:
                case NETWORK:
                    myBCH.addBCC((BroadcastController) newComp);
                    break;

                case SATELLITE:
                case TELESCOPE:
                case SIGHT:
                case RADAR:
                case BUILDING_SENSOR:
                    mySH.addSC((SensorController) newComp);
                    break;

                case BLASTER:
                case SMG:
                case RAILGUN:
                case HAMMER:
                case BEAM:
                    myWH.addWC((WeaponController) newComp);
                    hasWeapons = true;
                    break;

                case CONSTRUCTOR:
                case RECYCLER:
                case FACTORY:
                case ARMORY:
                    myBH.addBC((BuilderController) newComp);
                    break;
            }
        }

        
        return newCompTypes;
    }

}
