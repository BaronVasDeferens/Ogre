/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

import java.io.Serializable;
/**
 *
 * @author Skot
 */
public class Weapon implements Serializable
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
    
    //IS DISABLED
    public boolean isDisabled()
    {
        return (disabled);
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
    public String takeDamage(String code, int str)
    {
        String report = weaponName;
        
        switch (code)
        {
            case "NE":
            case "D":
                report = report.concat(" is UNHARMED.");
                break;
            case "X":
                report = report.concat(" is INOPERATIVE!");
                disable();
                break;
            default:
                break;                
        }
        
        return (report);
    }
    
}

class Treads extends Weapon
{
    int remainingTreads, maxTreads, treadsPerRow, lostTreads, treadId;

    Treads(int max, int perRow, int trdId)
    {
        //(int atk, int rng, boolean infOnly, String name, int id)
        super(0,0,1,true,"TREADS",trdId);
        remainingTreads = max;
        maxTreads = max;
        treadsPerRow = perRow;
        lostTreads = 0;
    }
    
    public int getLostOverRow()
    {
        return (lostTreads/treadsPerRow);      
    }
    
    //TAKE DAMAGE
    @Override
    public String takeDamage(String code, int str)
    {
        String report = weaponName;
        
        switch (code)
        {
            case "NE":
            case "D":
                report = report.concat(" are UNDAMAGED.");
                break;
            case "X":
                remainingTreads -= str;
                lostTreads = maxTreads - remainingTreads;
                report = (str + " tread units are DESTROYED!");
                break;
            default:
                break;                
        }
        
        return report;
    }
    
    @Override
    //IS DISABLED
    public boolean isDisabled()
    {
        if (remainingTreads <= 0)
            return true;
        else
            return false;
    }
}
