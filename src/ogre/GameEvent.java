/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

import java.io.Serializable;
import java.util.LinkedList;

/**
 *
 * @author Skot
 * 
 */
public class GameEvent implements Serializable
{
    public String type;
    public int gamePhase;
    public String message;
    public boolean canUndoThis;
    
    GameEvent()
    {
        type = "none";
        gamePhase = 0;
        message = "";
    }
    
    GameEvent(String tp, int phase, String msg, boolean undo)
    {
        type = tp;
        gamePhase = phase;
        message = msg;
        canUndoThis = undo;
    }
}

class MoveEvent extends GameEvent
{
    public Unit agent;
    public Hex source, destination;
    
    MoveEvent()
    {
        super();
        agent = null;
        source = null;
        destination = null; 
    }
    
    public MoveEvent(String tp, Unit agt, Hex src, Hex dest, int phase, String msg, boolean undo)
    {
        type = tp;
        agent = agt;
        source = src;
        destination = dest;
        gamePhase = phase;
        message = msg;
        canUndoThis = undo;
        
        // Create a journal of the action (eg "Missile tanks moves from X to Y") 
        String eventDescription = agt.unitName;
        eventDescription += " " + tp + "S  ";
        eventDescription += "[" + src.getCol() + "," + src.getRow() +"] to [" + dest.getCol() + "," + dest.getRow() + "]";
        message = eventDescription;       
    }
}

class AttackEvent extends GameEvent
{
    Player attacker;
    Unit defendingUnit;
    Weapon defendingWeapon;
    LinkedList<Unit> attackingUnits;
    LinkedList<Weapon> attackingWeapons;
    String result;
    
    //GameEvent(String tp, int phase, String msg, boolean undo)   
    AttackEvent(Player atkr, Unit dfndr, Weapon dfndWeap, LinkedList<Unit> atckUnits, LinkedList<Weapon> slctdWeapons, int phase, String msg, String rslt)
    {
        super("ATTACK",phase,msg,false);
        attacker = atkr;
        attackingUnits = atckUnits;
        attackingWeapons = slctdWeapons;        
        
        defendingUnit = dfndr;
        defendingWeapon = dfndWeap;
        
        result = rslt;
        
        //Format message as follows:
        // If only one attacker, format: OWNER'S UNIT attacks DFENDING UNIT: RESULT!
        Unit aUnit;
        java.util.Iterator iter = attackingUnits.iterator();
        String unitNameList = "";
        
        while (iter.hasNext())
        {
            aUnit = (Unit)iter.next();
            unitNameList += aUnit.unitName;
            unitNameList += " ";  
        }
        
        String eventDescription = unitNameList + "ATTACKS ";
        
        //LIST ATTACKING OGRE'S WEAPONS
        if (attackingWeapons != null)
        {
            eventDescription += " USING:\n";
            
            iter = attackingWeapons.iterator();
            Weapon aWeapon;
            
            while (iter.hasNext())
            {
                aWeapon = (Weapon)iter.next();
                eventDescription += "\t" + aWeapon.weaponName + "\n";
            }
            
        }

       //LIST TARGETTED WEAPON/UNIT 
        if (defendingWeapon != null)
            eventDescription += defendingWeapon.weaponName + ": ";
        else
            eventDescription += dfndr.unitName + ": ";
            
        
        switch (result)
        {
            case "X":
                eventDescription += " *** DESTROYED ***";
                break;
            case "NE":
                eventDescription += " MISSED";
                break;
            case "D":
                if (dfndr.unitType == UnitType.Infantry)
                    eventDescription += " ** MAN DOWN **";
                else if (dfndr.unitType == UnitType.Ogre)
                    eventDescription += " NO EFFECT";
                else
                    eventDescription += " ** DISABLED **";
                break;
            default:
                break;
        }
        //eventDescription += "\n";
        message = eventDescription;
    }
}

