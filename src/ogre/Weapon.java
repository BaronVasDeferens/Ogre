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
    
    //TAKE DAMAGE
    public void takeDamage(String code, int str)
    {
        switch (code)
        {
            case "NE":
            case "D":
                break;
            case "X":
                disable();
                break;
            default:
                break;                
        }
    }
    
}

class Treads extends Weapon
{
    int remainingTreads, maxTreads, treadsPerRow, lostTreads, treadId;

    Treads(int max, int perRow, int trdId)
    {
        //(int atk, int rng, boolean infOnly, String name, int id)
        super(0,0,1,true,"Treads",trdId);
        remainingTreads = max;
        maxTreads = max;
        treadsPerRow = perRow;
        lostTreads = 0;
    }
    
    public int getLostOverRow()
    {
        return (lostTreads/treadsPerRow);
        
        //Here is the proof, if you don't believe it:
        /*
        int movement;
        int maxMove = 3;
        int maxTreads = 45;
        int treadsPerRow = 15;
        int remainingTreads = maxTreads;
        int lostTreads = maxTreads - remainingTreads;
        
        for (int i = maxTreads; i >= 0; i--)
        {
            lostTreads = maxTreads - remainingTreads;
            movement = maxMove - (lostTreads/treadsPerRow);
            System.out.println("(" + remainingTreads + ") " + "LOST: " + (lostTreads) + "  MOVE: " + movement);
            
            remainingTreads--;

        }
        
        */
        
    }
    
    //TAKE DAMAGE
    @Override
    public void takeDamage(String code, int str)
    {
        switch (code)
        {
            case "NE":
            case "D":
                break;
            case "X":
                remainingTreads -= str;
                lostTreads = maxTreads - remainingTreads;
                break;
            default:
                break;                
        }
    }
}
