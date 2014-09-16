/*
PLAYER
Represents a player, thier name, associated units, victory conditions, etc.
 */

package ogre;

import java.util.LinkedList;
import java.io.Serializable;
import java.util.Iterator;

/**
 *
 * @author Skot
 */
public class Player implements Serializable
{
    String name;
    String password;        //TODO: fix this glaering security hole
    String emailAddress;
    LinkedList<Unit> units;
    
    Player(String myName)
    {
        name = myName;
        password = null;
        emailAddress = null;
        units = new LinkedList();
        units.clear();
    }
    
    Player (String myName, String myPasswd)
    {
        this(myName);
        password = myPasswd;
        emailAddress = null;
    }
    
    Player (String myName, String passwd, String emailAddy)
    {
        this(myName, passwd);
        emailAddress = emailAddy;
    }
    
    Player(String myName, LinkedList myUnits)
    {
        this(myName);
        setUnits(myUnits);
        
    }
    
    public void setUnits(LinkedList unitList)
    {
        units = unitList;
    }
    
    //REDAY FOR SECOND MOVE
    //Prepares the appropriate units for their second move
    public void readyForSecondMove()
    {
        Iterator iter = units.iterator();
        Unit thisUnit;
        
        while (iter.hasNext())
        {
            thisUnit = (Unit)iter.next();
            
            if ((thisUnit.movementPostShooting > 0) && (thisUnit.disabled == false))
            {
                thisUnit.hasMoved = false;
            }
        }
    }
    
    
    //READY FOR NEXT TURN
    //Inbetween house-keeping; returns values to "ready states." Returns a linked list of units
    //ready for the next turn.
    public LinkedList<Unit> readyForNextTurn()
    {
        Iterator iter = units.iterator();
        Unit thisUnit;
        
        LinkedList<Unit> deadUnits = new LinkedList();
        deadUnits.clear();
        
        while (iter.hasNext())
        {
            thisUnit = (Unit)iter.next();
            
            //NON-OGRE units
            if (thisUnit.unitType != UnitType.Ogre)
            {
                if (thisUnit.isAlive)
                {
                    thisUnit.processEndOfTurn();
                    
                    if (thisUnit.unitWeapon != null)
                        thisUnit.unitWeapon.resetForEndOfTurn();
                }
                
                else
                {
                    deadUnits.add(thisUnit);
                }
            }
            
            //reset OGRE all weapons
            else
            {
                Ogre thisOgre = (Ogre)thisUnit;
                thisOgre.hasMoved = false;
                thisOgre.resetWeapons();
            }
        }
        
        units.removeAll(deadUnits);
        return (units);
    }
}
