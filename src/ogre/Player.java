/*
PLAYER
Represents a player, thier name, associated units, victory conditions, etc.
 */

package ogre;

import java.util.LinkedList;

/**
 *
 * @author Skot
 */
public class Player 
{
    String name;
    LinkedList<Unit> units;
    
    Player(String myName)
    {
        name = myName;
        units = new LinkedList();
        units.clear();
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
}
