package ogre;

/*
GAME STATE


 */
import java.io.Serializable;
import java.util.*;
import java.util.Date;
/**
 *
 * @author Skot
 */
public class GameState implements Serializable
{
    boolean isOpen;         //true when one player is using/writing to it
    
    Player playerOne, playerTwo, currentPlayer;
    Date dateCreated;
    Date lastModified;
    int turnNumber;
    Scenario scenario;
    
    LinkedList<GameEvent> gameEventList;
    
    
    
    
    GameState(Player p1, Player p2, Scenario scen)
    {
        playerOne = p1;
        playerTwo = p2;
        scenario = scen;
        turnNumber = 1;
        
        isOpen = false;
    }
}
