package ogre;

/*
GAME STATE

Tracks the overall state of a game; the players, units, map, terrain, victory conditions, etc.

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
    boolean isOpen;         //true when one player is "checked out" this game
    Date dateCreated;
    Date lastModified;
    int idNumber;
    
    
    Player playerOne, playerTwo, currentPlayer;

    int turnNumber;
    
    Scenario scenario;
    HexMap hexMap;
    int hexRows, hexCols;

    EventManager eventManager;
    
    
    
    
    GameState(Player p1, Player p2, ScenarioType scenType, int hexMapRows, int hexMapCols, int hexsize)      //TODO; accept victory condition args here
    {
        Random rando = new Random();
        idNumber = rando.nextInt(1000000) + 1;
        
        //TODO: timestamp
        
        playerOne = p1;
        playerTwo = p2;
        
        currentPlayer = playerOne;
        
        eventManager = null;
        
        scenario = new Scenario(p1,p2, scenType);
        
        hexMap = new HexMap(hexMapRows, hexMapCols, hexsize);
        
        UnitPlacementManager.placeUnits(hexMap, scenario.allUnits);
        
        turnNumber = 1;
        
        isOpen = false;
    }
}
