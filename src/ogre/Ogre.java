/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

import java.util.LinkedList;

/**
 *
 * @author Skot
 */
public class Ogre extends ogre.Unit
{
    int treads;
    int treadsPerRow;
    int maxMovement;
    
    
    LinkedList<Weapon> mainBattery = null;
    LinkedList<Weapon> secondaryBattery = null;
    LinkedList<Weapon> antiPersonnel = null;
    LinkedList<Weapon> missileBattery = null;
    
    public Ogre(int mark)
    {
        super(666);
        
        switch (mark)
        {
            //Mark III
            case 3:
            default:
                treads = 45;
                treadsPerRow = 15;
                maxMovement = 3;
                
                //ONE main battery
                mainBattery.add(new Weapon(4,3,4,false, "OgreMainBattery", 0));
                
                //FOUR secondary
                for (int i = 0; i < 4; i++)
                {
                    secondaryBattery.add(new Weapon(3,2,3,false,"OgreSecondaryBattery",0));
                }
            
                //EIGHT AP guns
                for (int i = 0; i < 8; i++)
                {
                    secondaryBattery.add(new Weapon(3,2,3,true,"Anti-personnel",0));
                }
            
                //TWO missiles (one use)
                missileBattery.add(new Weapon(6,5,3,false,"missile",0));
                missileBattery.add(new Weapon(6,5,3,false,"missile",0));
             
                break;                
        }    
    }
    
    //GET CURRENT MOVEMENT
    //Returns the Ogre's current movement allowance based on its remaining treads
    public int getCurrentMovement()
    {
        return (treads/treadsPerRow);
    }
    
}
