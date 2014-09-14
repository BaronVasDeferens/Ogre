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
    Player playerOne, playerTwo, currentPlayer;
    Date lastModified;
    int turnNumber;
    LinkedList<GameEvent> gameEventList;
    
}
