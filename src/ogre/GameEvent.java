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

    }
}

