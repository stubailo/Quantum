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
        attack(mapLocations, levels, hps, new int[mapLocations.length]);
    }
    
    public void attack(MapLocation[] mapLocations, RobotLevel[] levels, double[] hps, int[] priorities) {
        recursiveAttackAlgorithm(mapLocations, levels, hps, priorities, 0);
    }
    
    private void recursiveAttackAlgorithm(MapLocation[] mapLocations, RobotLevel[] levels, double[] hps, int[] priorities, int index) {
        
        // End of recursion
        if(index == numberOfWeapons) return;
        
        WeaponController myWC = myWCs[index];
        
        // Nothing to do if active :/
        if(myWC.isActive()) {
            recursiveAttackAlgorithm(mapLocations, levels, hps, priorities, index + 1);
            return;
        }
        
        ComponentType    myWeaponType    = myWC.type();
        boolean          canAttackAir    = myWeaponType != ComponentType.HAMMER;
        double           damage          = myWeaponType.attackPower;
        
        double           highestKillHp   = 0;
        int              killPriority    = 0;
        int              killIndex       = -1;
        double           lowestNotKillHp = Chassis.HEAVY.maxHp + 1;
        int              notKillPriority = 0;
        int              notKillIndex    = -1;
        
        for(int i = 0; i < mapLocations.length; i++) {
            MapLocation mapLocation = mapLocations[i];
            if(myWC.withinRange(mapLocation)) {
                if(canAttackAir || levels[i] == RobotLevel.ON_GROUND) {
                    
                    // We can attack
                    double hp       = hps[i];
                    int    priority = priorities[i];
                    
                    // hp can be less than or equal to zero if we've already shot that robot
                    if(hp <= 0) continue;
                    
                    if(hp <= damage) {
                        
                        // Kill possible
                        if(hp > highestKillHp && priority >= killPriority) {
                            highestKillHp = hp;
                            killPriority  = priority;
                            killIndex     = i;
                        }
                    }
                }
            }
        }
        
        if(killIndex != -1) {
            try {
                
                // Kill it!
                myWC.attackSquare(mapLocations[killIndex], levels[killIndex]);
                hps[killIndex] -= damage;
                
                // Now we recurse
                recursiveAttackAlgorithm(mapLocations, levels, hps, priorities, index + 1);
                
                // We've attacked, so we're done
                return;
            }
            catch(Exception e) {
                Logger.debug_printExceptionMessage(e);
                return;
            }
        }
        
        // We can't kill anything, so we recurse first
        recursiveAttackAlgorithm(mapLocations, levels, hps, priorities, index +1);
        
        for(int i = 0; i < mapLocations.length; i++) {
            MapLocation mapLocation = mapLocations[i];
            if(myWC.withinRange(mapLocation)) {
                if(canAttackAir || levels[i] == RobotLevel.ON_GROUND) {
                    
                    // We can attack
                    double hp       = hps[i];
                    int    priority = priorities[i];
                    
                    // hp can be less than or equal to zero if we've already shot that robot
                    if(hp <= 0) continue;
                    
                    if(hp > damage) {
                        
                        // Kill not possible
                        if(hp < lowestNotKillHp && priority >= notKillPriority) {
                            lowestNotKillHp = hp;
                            notKillPriority = priority;
                            notKillIndex    = i;
                        }
                    }
                    else {
                        
                        // Kill possible
                        if(hp > highestKillHp && priority >= killPriority) {
                            highestKillHp = hp;
                            killPriority  = priority;
                            killIndex     = i;
                        }
                    }
                }
            }
        }
        
        if(killIndex != -1) {
            try {
                
                // Kill it!
                myWC.attackSquare(mapLocations[killIndex], levels[killIndex]);
                hps[killIndex] -= damage;
                
                // Now we recurse
                recursiveAttackAlgorithm(mapLocations, levels, hps, priorities, index + 1);
                
                // We've attacked, so we're done
                return;
            }
            catch(Exception e) {
                Logger.debug_printExceptionMessage(e);
                return;
            }
        } else if(notKillIndex != -1) {
            try {
                
                // Damage it!
                myWC.attackSquare(mapLocations[notKillIndex], levels[notKillIndex]);
                hps[notKillIndex] -= damage;
                
                return;
            }
            catch(Exception e) {
                Logger.debug_printExceptionMessage(e);
                return;
            }
        }
    }
    
    private void debug_printWeaponTypes() {
        String testString = "";
        for(int index = 0; index < numberOfWeapons; index++) {
            WeaponController myWC = myWCs[index];
            testString += myWC.type().toString() + " ";
        }
        Logger.debug_printHocho(testString);
    }
}
