/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ogre;


import java.util.LinkedList;
import java.util.Iterator;
import java.util.Random;
/**
 *
 * @author skot
 */
abstract class AttackStrategy {
 
    Unit thisUnit;
    Unit primaryTarget;
    
    AttackStrategy(Unit me, Unit trgt) { 
        thisUnit = me;
        primaryTarget = trgt; 
    }
    
    public abstract void makeAttack(OgreGame gm, GameState gameState);
    
    protected void delay(int millis) {
        try { Thread.sleep(millis); }
        catch (Exception e) { }
    }
}

// ATTACK: EVERYTHING IN SIGHT
// Brings to bear as much lethal force as it can on what it can. No target priority.
class EverythingInSight extends AttackStrategy {
    
    EverythingInSight(Unit me) { super(me, null); }
    // 
    @Override
    public void makeAttack(OgreGame gm, GameState gameState) {
        
       
        // First consideration: is thisUnit an Ogre? If so, we have many weapons to bring to bear.
        // Populate the weapons list. If non-Ogre, populate with what we have
        LinkedList<Weapon> allWeapons = new <Weapon>LinkedList();
        allWeapons.clear();

        if (thisUnit instanceof OgreUnit) {
            OgreUnit ogre = (OgreUnit)thisUnit;
            allWeapons = ogre.getWeapons();
        }
        
        else
            allWeapons.add(thisUnit.unitWeapon);
        
        Hex thisHex = gm.ogrePanel.hexMapRenderer.getHexFromCoords(thisUnit.yLocation, thisUnit.xLocation);
        
        // Select a weapon and obtain a list of targets within range
        Iterator iter = allWeapons.iterator();
        Weapon currentWeapon;
        LinkedList<Hex> hexesWithinRange = new <Hex>LinkedList();
        LinkedList<Hex> enemyLocations = new <Hex>LinkedList();
        LinkedList<Unit> enemiesInRange = new <Unit>LinkedList();
        Random rando = new Random();
        boolean OKtoFire = true;
        
        while (iter.hasNext()) {
            
            OKtoFire = true;
            gm.currentTarget = null;
            gm.hexMap.adjacentHexes.clear();
            gm.hexMap.deselectAllSelectedHexes();
            gm.ogrePanel.hexMapRenderer.updateMapImage();
            
            currentWeapon = (Weapon)iter.next();
            
            if (thisUnit instanceof OgreUnit) {
                gm.selectedOgreWeapons.clear();
                gm.selectedOgreWeapons.add(currentWeapon);
            }
            // Sanity checks:
            // Current weapon isn't treads, anti-infantry vs non-infantry, etc
            if ((currentWeapon.strength > 0) && (currentWeapon.disabled == false) && (currentWeapon.isDisabled() == false)) {
            
                hexesWithinRange.clear();
                enemyLocations.clear();
                enemiesInRange.clear();

                hexesWithinRange = gm.hexMap.getHexesWithinRange(
                        thisHex,
                        currentWeapon.range,
                        true,
                        true,
                        null);
                
                gm.hexMap.select(thisHex);
                gm.hexMap.adjacentHexes = hexesWithinRange;
                gm.ogrePanel.hexMapRenderer.updateMapImage();

                enemyLocations = gm.hexMap.getOccupiedHexes(gm.passivePlayer);

                // Find the intersection of the two lists: hexes in range and enemy-occupied hexes
                // and select a target from these
                enemyLocations.retainAll(hexesWithinRange);

                if (enemyLocations.size() > 0) {

                    gm.updateUnitReadouts(thisHex.occupyingUnit);

                    // Select a random target from within the range of this weapon
                    gm.currentTarget = enemyLocations.get(rando.nextInt(enemyLocations.size())).occupyingUnit;
                    
                    if (gm.currentTarget != null) {
                        
                        gm.hexMap.select(gm.ogrePanel.hexMapRenderer.getHexFromCoords(gm.currentTarget.yLocation, gm.currentTarget.xLocation));

                        if (thisUnit instanceof OgreUnit) {
                            gm.currentOgre = (OgreUnit)thisUnit;
                            //gm.updateOgreWeaponSelectionList(gm.currentOgre);
                        }
                        
                        if ((currentWeapon.softTargetsOnly == true) &&
                                ((gm.currentTarget.unitType != UnitType.Infantry) || 
                                (gm.currentTarget.unitType != UnitType.CommandPost) || 
                                (gm.currentTarget.unitType != UnitType.MobileCommandPost))) {
                            OKtoFire = false;
                        }

                        // If the selected target happens to be an Ogre, choose the first non-INOP weapon (for now)
                        if (gm.currentTarget instanceof OgreUnit) {
                            // TO DO
                        }
                        
                        if (OKtoFire) {
                            delay(500);
                            // Announce it:
                            gm.reportArea.append(thisUnit.unitName + " attacks " + gm.currentTarget.unitName + "!\n");
                            gm.reportArea.append("Firing " + currentWeapon.weaponName + "!\n");

                            gm.ogrePanel.hexMapRenderer.updateMapImage();
                            delay(500);
                            gm.attack();
                            gm.currentTarget = null;
                            gm.hexMap.deselectAllSelectedHexes();
                            gm.ogrePanel.hexMapRenderer.updateMapImage();
                            delay(500);
                        }
                    }
                    
                    else {
                        gm.currentTarget = null;
                        gm.hexMap.adjacentHexes.clear();
                        gm.hexMap.deselectAllSelectedHexes();
                        gm.ogrePanel.hexMapRenderer.updateMapImage();
                    }
                }        
            }
        }
        
    }
}