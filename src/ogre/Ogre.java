/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

import java.util.LinkedList;
import java.util.Iterator;

/**
 *
 * @author Skot
 */
public class Ogre extends ogre.Unit
{
    //int treads, maxTreads;
    int treadID;
    int treadsPerRow;
    int maxMovement;
   
    LinkedList<Weapon> mainBattery = null;
    LinkedList<Weapon> secondaryBattery = null;
    LinkedList<Weapon> antiPersonnel = null;
    LinkedList<Weapon> missileBattery = null;
    Treads treads = null;
    
    
    
    
    public Ogre(int mark)
    {
        super(666);
        
        unitType = UnitType.Ogre;
        
        mainBattery = new LinkedList();
        mainBattery.clear();
        
        secondaryBattery = new LinkedList();
        secondaryBattery.clear();
        
        antiPersonnel = new LinkedList();
        antiPersonnel.clear();
        
        missileBattery = new LinkedList();
        missileBattery.clear();
       
                
        switch (mark)
        {
            //Mark III Ogre: 1 main, 4 secondary, 8 ap, 2 missiles, 45 treads, 15 per row
            case 3:
            default:
                unitName = "Ogre MK III";
                
                //treads = 45;
                //maxTreads = treads;
                //treadsPerRow = 15;
                maxMovement = 3;
                movement = maxMovement;
                
                //ONE main battery
                mainBattery.add(new Weapon(4,3,4,false, "Main Battery", 1));
                
                //FOUR secondary
                for (int i = 0; i < 4; i++)
                {
                    secondaryBattery.add(new Weapon(3,2,3,false,"Secondary Battery",i+2));
                }
            
                //EIGHT AP guns
                for (int i = 0; i < 8; i++)
                {
                    antiPersonnel.add(new Weapon(3,2,3,true,"Anti-personnel",i+6));
                }
            
                //TWO missiles (one use)
                missileBattery.add(new Weapon(6,5,3,false,"Missile",14,true));
                missileBattery.add(new Weapon(6,5,3,false,"Missile",15,true));
                
                //treadID = 16
                treads = new Treads(45,15,16);
                
                image = loadImage("ogre_mk3.png");
             
                break;                
        }    
    }
    
    //GET CURRENT MOVEMENT
    //Returns the Ogre's current movement allowance based on its remaining treads
    public int getCurrentMovement()
    {
        return (maxMovement - (treads.getLostOverRow()));
    }
    
    public int calculateReaminingTreadsInRow()
    {
        if ((treads.remainingTreads%treads.treadsPerRow) == 0)
            return (treads.treadsPerRow);
        else
            return (treads.remainingTreads%treads.treadsPerRow);
                
    }
    
    public LinkedList<Weapon> getWeapons()
    {
        LinkedList<Weapon> allWeapons = new LinkedList();
        allWeapons.clear();
        allWeapons.addAll(mainBattery);
        allWeapons.addAll(secondaryBattery);
        allWeapons.addAll(antiPersonnel);
        allWeapons.addAll(missileBattery);
        allWeapons.add(treads);
        
        return (allWeapons);
    }
    
    //Returns a list of the Ogres operational weapons
    //(number of operational wepaons) Name of weapon : strength : range: defense
    public LinkedList<String> getWeaponReadoutStrings()
    {
        LinkedList<String> returnList = new LinkedList();
        returnList.clear();
        
        Iterator iter;
        Weapon thisWeapon;
        int remaining = 0;
        
        String str;
        
        if (mainBattery.isEmpty() == false)
        {
            iter = mainBattery.iterator();
            while (iter.hasNext())
            {    
                thisWeapon = (Weapon)iter.next();
                if (thisWeapon.disabled == false)
                    remaining++;
            }
            
            str = "(" + remaining + ") Main Battery / Strength: " + mainBattery.peek().strength + " / Range: " + mainBattery.peek().range + " / Defense: " + mainBattery.peek().defense;
            returnList.add(str);
        }
        
        remaining = 0;
        
        if (secondaryBattery.isEmpty() == false)
        {
            iter = secondaryBattery.iterator();
            while (iter.hasNext())
            {    
                thisWeapon = (Weapon)iter.next();
                if (thisWeapon.disabled == false)
                    remaining++;
            }
            str = "(" + remaining + ") Seconadry Battery / Strength: " + secondaryBattery.peek().strength + " / Range: " + secondaryBattery.peek().range + " / Defense: " + secondaryBattery.peek().defense;
            returnList.add(str);
        }
        
        remaining = 0;
        
        if (antiPersonnel.isEmpty() == false)
        {
            iter = antiPersonnel.iterator();
            while (iter.hasNext())
            {    
                thisWeapon = (Weapon)iter.next();
                if (thisWeapon.disabled == false)
                    remaining++;
            }  
            
            str = "(" + remaining + ") Anti-personnel / Strength: " + antiPersonnel.peek().strength + " / Range: " + antiPersonnel.peek().range + " / Defense: " + antiPersonnel.peek().defense;
            returnList.add(str);
        }
        
        remaining = 0;    
            
        if (missileBattery.isEmpty() == false)
        {
            iter = missileBattery.iterator();
            while (iter.hasNext())
            {    
                thisWeapon = (Weapon)iter.next();
                if (thisWeapon.disabled == false)
                    remaining++;
            }
            
            str = "(" +remaining + ") Missile / Strength: " + missileBattery.peek().strength + " / Range: " + missileBattery.peek().range + " / Defense: " + missileBattery.peek().defense;
            returnList.add(str);
        }
        
        str = "(" + treads.remainingTreads + ")" + " Treads (" + calculateReaminingTreadsInRow() + " remain in row)";
        returnList.add(str);
        
        return (returnList);
    }
    
//GET ENUMERATED SYSTEMS STRING    
//the sequence shall forever be: MAIN-SECONDARY-AP-MISSILES-TREADS
    public LinkedList<String> getEnumeratedSystemsList()
    {
        LinkedList<String> returnList = new LinkedList();
        Iterator iter = mainBattery.iterator();
        Weapon thisWeapon;
        
        while (iter.hasNext())
        {    
            thisWeapon = (Weapon)iter.next();
            
            if (thisWeapon.disabled == false)
            {
               if (thisWeapon.dischargedThisRound == false)
                   returnList.add(thisWeapon.weaponName + "  Strength: " + thisWeapon.strength + "  Range: " + thisWeapon.range + " Defense: " + thisWeapon.defense);
                else
                   returnList.add(thisWeapon.weaponName + " DISCHARGED");
            }       
            else
               returnList.add("-- WEAPON DISABLED -- ");
        }
        
        iter = secondaryBattery.iterator();
        while (iter.hasNext())
        {    
            thisWeapon = (Weapon)iter.next();
            
            if (thisWeapon.disabled == false)
            {
               if (thisWeapon.dischargedThisRound == false)
                   returnList.add(thisWeapon.weaponName + "  Strength: " + thisWeapon.strength + "  Range: " + thisWeapon.range + " Defense: " + thisWeapon.defense);
                else
                   returnList.add(thisWeapon.weaponName + " DISCHARGED");
            }       
            else
               returnList.add("-- WEAPON DISABLED -- ");
        }
        
        iter = antiPersonnel.iterator();
        while (iter.hasNext())
        {    
            thisWeapon = (Weapon)iter.next();
            
            if (thisWeapon.disabled == false)
            {
               if (thisWeapon.dischargedThisRound == false)
                   returnList.add(thisWeapon.weaponName + "  Strength: " + thisWeapon.strength + "  Range: " + thisWeapon.range + " Defense: " + thisWeapon.defense);
                else
                   returnList.add(thisWeapon.weaponName + " DISCHARGED");
            }       
            else
               returnList.add("-- WEAPON DISABLED -- ");
        }
        
        iter = missileBattery.iterator();
        while (iter.hasNext())
        {    
            thisWeapon = (Weapon)iter.next();
            
            if (thisWeapon.disabled == false)
            {
               if (thisWeapon.dischargedThisRound == false)
                   returnList.add(thisWeapon.weaponName + "  Strength: " + thisWeapon.strength + "  Range: " + thisWeapon.range + " Defense: " + thisWeapon.defense);
                else
                   returnList.add(thisWeapon.weaponName + " DISCHARGED");
            }       
            else
               returnList.add("-- WEAPON DESTROYED -- ");
        }
        
        returnList.add(new String("Treads: " + treads.remainingTreads + "/" + treads.maxTreads));
        
        return returnList;
    }
    
    
    public Weapon getWeaponByID(int id)
    {
        LinkedList<Weapon> allWeapons = getWeapons();
        return (allWeapons.get(id));
    }
    
    
    public int dischargeWeapon(Weapon thisWeapon)
    {

        if (thisWeapon != null)
        {
            if ((thisWeapon.disabled == false) && (thisWeapon.dischargedThisRound == false))
            {
                return(thisWeapon.discharge());
            }
        }
        
        return (0);
    
    }
    
    
    //RESET WEAPONS
    //Removes discharged flag from all weapons
    public void resetWeapons()
    {
        Iterator allWeapons;
        Weapon thisWeapon;
       
        allWeapons = getWeapons().iterator();
         
        while (allWeapons.hasNext())
        {
            thisWeapon = (Weapon)allWeapons.next();
            
            if (thisWeapon.disabled == false)
            {
                thisWeapon.dischargedThisRound = false;
            }
        }
  
    }
    
    //IS NEUTRALIZED
    //Returns true if all weapons have been diabled and no further movement can be made
    public boolean isNeutralized()
    {
        Iterator iter;
        Weapon thisWeapon;
        boolean operational = false;
        
        iter = getWeapons().iterator();
        
        while (iter.hasNext())
        {
            thisWeapon = (Weapon)iter.next();
            
            if (thisWeapon.isDisabled() == false)
            {
                operational = true;
            }
        }
        
        return (operational);
    }
}
