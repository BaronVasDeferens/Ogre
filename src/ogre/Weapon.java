/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

/**
 *
 * @author Skot
 */
public class Weapon 
{
    String weaponName;
    int weaponID;
    
    int strength;
    int range;
    int defense;
    
    boolean oneUseOnly;
    boolean softTargetsOnly;   //TRUE if only effective against infantry/ CP (eg antipersonell guns)
    
    boolean disabled;
    boolean dischargedThisRound = false;
    
    //Non-Ogre weapon constructor
    public Weapon(int atk, int rng, boolean infOnly, String name, int id)
    {
        strength = atk;
        range = rng;
        softTargetsOnly = infOnly;
        weaponName = name;
        weaponID = id;
        
        defense = 0;
        oneUseOnly = false;
        disabled = false;
    }
    
    //Ogre weapon constructor
    public Weapon(int atk, int rng, int def, boolean infOnly, String name, int id)
    {
        strength = atk;
        range = rng;
        defense = def;
        softTargetsOnly = infOnly;
        weaponName = name;
        weaponID = id;
        
        oneUseOnly = false;
        disabled = false;
    }
    
    //MISSILE constructor: not the extra oneUSeOnly arg at the end
    public Weapon(int atk, int rng, int def, boolean infOnly, String name, int id, boolean oneTime)
    {
        strength = atk;
        range = rng;
        defense = def;
        softTargetsOnly = infOnly;
        weaponName = name;
        weaponID = id;
        
        oneUseOnly = oneTime;
        disabled = false;
    }
    
    //ENABLE
    //Enables a unit
    public void enable()
    {
        disabled = false;
    }
    
    //DISABLE
    public void disable()
    {
        disabled = true;
    }
    
    //DISCHARGE
    public int discharge()
    {
        dischargedThisRound = true;
        
        if (oneUseOnly == true)
            disabled = true;
        
        return (strength);
    }
    
    //RESET
    public void resetForEndOfTurn()
    {
        dischargedThisRound = false;
    }        
    
}

class Treads extends Weapon
{
    int remainingTreads, maxTreads, treadsPerRow, treadId;

    Treads(int max, int perRow, int trdId)
    {
        //(int atk, int rng, boolean infOnly, String name, int id)
        super(0,0,1,true,"Treads",trdId);
        remainingTreads = max;
        maxTreads = max;
        treadsPerRow = perRow;
    }
    
    public int getCurrentMovement()
    {
        return (remainingTreads/treadsPerRow);
    }
}
