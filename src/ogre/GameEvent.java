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
public class GameEvent implements Serializable
{
    public String type;
    public Unit agent;
    public Hex source, destination;
    public int gamePhase;
    public String message;
    public boolean canUndoThis;
    
    GameEvent()
    {
        type = "none";
        agent = null;
        source = null;
        destination = null;
        gamePhase = 11;
        message = "";
    }
    
    GameEvent(String tp, Unit agt, Hex src, Hex dest, int phase, String msg, boolean undo)
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
