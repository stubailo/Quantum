package newTeam.handler;

import newTeam.common.util.Logger;
import newTeam.common.Knowledge;
import battlecode.common.*;

public class WeaponHandler {
    
    private final Knowledge          myK;
    private final WeaponController[] myWCs           = new WeaponController[20];
    private       int                numberOfWeapons = 0;
    
    public WeaponHandler(Knowledge know) {
        myK = know;
    }
    
    public void addWC(WeaponController wc) {
        boolean replacing = false;
        WeaponController myWC1 = null, myWC2;
        for(int index = 0; index < numberOfWeapons; index++) {
            if(replacing) {
                myWC2 = myWCs[index];
                myWCs[index] = myWC1;
                myWC1 = myWC2;
            }
            else {
                myWC1 = myWCs[index];
                if(wc.type().delay > myWC1.type().delay) {
                    myWCs[index] = wc;
                    replacing = true;
                }
            }
        }
        if(replacing) {
            myWCs[numberOfWeapons++] = myWC1;
        }
        else {
            myWCs[numberOfWeapons++] = wc;
        }
        
        debug_printWeaponTypes();
        
    }
    
    public void attack(MapLocation[] mapLocations, RobotLevel[] levels, double[] hps) {
        for(WeaponController myWC : myWCs) {
            
        }
    }
    
    private void recursiveAttackAlgorithm(MapLocation[] mapLocations, RobotLevel[] levels, double[] hps, int[] priorities, int index) {
        WeaponController myWC = myWCs[index];
        
        // Nothing to do if active :/
        if(myWC.isActive()) {
            recursiveAttackAlgorithm(mapLocations, levels, hps, priorities, index + 1);
            return;
        }
        
        ComponentType    myWeaponType    = myWC.type();
        boolean          canAttackAir    = myWeaponType != ComponentType.HAMMER;
        double           damage          = myWeaponType.attackPower;
        double           highestKillHp   = -1;
        int              killPriority;
        int              killIndex;
        double           lowestNotKillHp = Chassis.HEAVY.maxHp + 1;
        int              notKillPriority;
        int              notKillIndex;
        
        for(int i = 0; i < mapLocations.length; i++) {
            MapLocation mapLocation = mapLocations[i];
            if(myWC.withinRange(mapLocation)) {
                if(canAttackAir || levels[i] == RobotLevel.ON_GROUND) {
                    
                    // We can attack
                    double hp = hps[i];
                    
                    if(hp > damage) {
                        
                        // Kill not possible
                        if(hp > highestKillHp) {
                            
                        }
                        
                    }
                    else {
                        
                        // Kill possible
                        
                    }
                }
            }
        }
    }
    
    private void debug_printWeaponTypes() {
        String testString = "";
        for(WeaponController myWC : myWCs) {
            testString += myWC.type().toString() + " ";
        }
        Logger.debug_printHocho(testString);
    }
}
