/*
 * EVENT MAMANGER
 * Events represent actions within the game; changes of position, attacks, etc. The EventManager keeps
 * these events in a queue; UNDO is allows the user to "rewind" actions they've made. 
 * 
 * When the game advances to subsequent phases, the actions taken in the prior state are locked and cannot be undone.
 */
package ogre;

import java.util.LinkedList;

/**
 *
 * @author Skot
 */
public class EventManager 
{
    EventList<GameEvent> eventQueue = null;
    GameEvent currentEvent = null;
    OgreGame master = null;
    
    EventManager(OgreGame msr)
    {
        eventQueue = new EventList();
        eventQueue.clear();
        
        currentEvent = null;
        
        master = msr;
    }
    
    public void addEvent(GameEvent e)
    {
        //GameEvent(String tp, Unit agt, Hex src, Hex dest, int phase, String msg, int idee)
        eventQueue.add(e);
    }
    
    public void undo(int gamephase)
    {
        currentEvent = (GameEvent)eventQueue.peekLast();
        
       //Check for null/population
        if (currentEvent != null)
        {
            //Only MOVEs actions taken in the current turn can be rolled back
            if (currentEvent.gamePhase == gamephase)
            {
                //GameEvent(String tp, Unit agt, Hex src, Hex dest, int phase, String msg, int idee)
                GameEvent undoEvent = new GameEvent("MOVE",currentEvent.agent, currentEvent.destination, currentEvent.source, gamephase, currentEvent.message,false);
                master.move(undoEvent);

                eventQueue.pollLast();
                master.hexMap.updateMapImage();
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
