package newTeam.handler;

import battlecode.common.*;

public class WeaponHandler {
    
    private final WeaponController[] myWCs           = new WeaponController[4];
    private       int                numberOfWeapons = 0;
    
    public void addWC(WeaponController wc) {
        myWCs[numberOfWeapons] = wc;
        numberOfWeapons++;
    }
}
