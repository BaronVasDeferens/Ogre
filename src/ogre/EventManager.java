/*
 * EVENT MAMANGER
 * Events represent actions within the game; changes of position, attacks, etc. The EventManager keeps
 * these events in a queue; UNDO is allows the user to "rewind" actions they've made. 
 * 
 * When the game advances to subsequent phases, the actions taken in the prior state are locked and cannot be undone.
 */
package ogre;

import java.util.LinkedList;
import java.io.Serializable;
/**
 *
 * @author Skot
 */
public class EventManager implements Serializable
{
    EventList<GameEvent> eventQueue = null;
    GameEvent currentEvent = null;
    OgreGame master = null;
    
    EventManager()
    {
        eventQueue = new EventList();
        eventQueue.clear();
        currentEvent = null;
    }
    
    
    EventManager(OgreGame msr)
    {
        eventQueue = new EventList();
        eventQueue.clear();
        currentEvent = null;
        
        setMaster(msr);
    }
    
    public void setMaster(OgreGame msr)
    {
        master = msr;
    }
    
    public void addEvent(GameEvent e)
    {
        //GameEvent(String tp, Unit agt, Hex src, Hex dest, int phase, String msg, int idee)
        eventQueue.addLast(e);
    }
    
    public void undo(int gamephase)
    {
        currentEvent = (GameEvent)eventQueue.peekLast();
                
       //Check for null/population
        if (currentEvent != null)
        {
            //Only MOVEs actions taken in the current turn can be rolled back
            if ((currentEvent.gamePhase == gamephase) && (currentEvent.type.equals("MOVE")))
            {
                MoveEvent temp = (MoveEvent)currentEvent;

                //MoveEvent (String tp, Unit agt, Hex src, Hex dest, int phase, String msg, boolean undo)
                MoveEvent undoEvent = new MoveEvent("MOVE", temp.agent, temp.destination, temp.source, 
                        gamephase, currentEvent.message, false);
                
                //Pass the "reverse event" to the move function:
                if (master.move(undoEvent))
                {
                    //If successful, allow this unit to move again
                    temp.agent.hasMoved = false;
                    //...and remove the prior move from the history
                    eventQueue.pollLast();
                    master.ogrePanel.hexMapRenderer.updateMapImage();
                }
            }
            
            else
                System.out.println("CANNOT UNDO PRIOR MOVE");
        }
        
        else
            System.out.println("NOTHING TO UNDO");
    }
}

class EventList<Object> extends java.util.LinkedList implements java.io.Serializable
{
    
}
