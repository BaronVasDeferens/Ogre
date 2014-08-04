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
