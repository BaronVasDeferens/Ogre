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
        
        disabled = false;
    }
    
}

class Treads extends Weapon
{
    int remainingTreads, maxTreads, treadsPerRow, treadId;

    Treads(int max, int perRow, int trdId)
    {
        //(int atk, int rng, boolean infOnly, String name, int id)
        super(0,0,true,"Treads",trdId);
        remainingTreads = max;
        maxTreads = max;
        treadsPerRow = perRow;
    }
    
    public int getCurrentMovement()
    {
        return (remainingTreads/treadsPerRow);
    }
}
