/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

import java.util.LinkedList;
import java.awt.Polygon;
import java.util.Iterator;
/**
 *
 * @author Skot
 */
public class OgreGame {

    javax.swing.JFrame myFrame;
    javax.swing.JList weaponList;
    
    HexMap hexMap;
    public int hexSide = 64;
    public final int HEX_ROWS = 15;
    public final int HEX_COLS = 21;
    public final int VIEW_WINDOW_WIDTH = 800;
    public final int VIEW_WINDOW_HEIGHT = 600;
    
    OgrePanel ogrePanel;
    javax.swing.JList WeaponSystemsList;
    
    Player playerOne, playerTwo, currentPlayer;
    
    boolean gameOver = false;
    
    EventManager eventManager;
    Scenario scenario;
    LinkedList<Unit> allUnits;
    
    int gamePhase = 11;
    /*
    STATE   PHASE
    00      Pre-game setup
    
    10      Player 1 SETUP
    11      Player 1 MOVE
    12      Player 1 SHOOT
    13      Player 1 SECOND MOVE
    
    20      Player 2 SETUP
    21      Player 2 MOVE
    22      Player 2 SHOOT
    23      Player 2 SECOND MOVE
    */     
    
    
    //Default constructor
    OgreGame(OgrePanel ogrPnl)
    {
        hexMap = new HexMap(HEX_ROWS,HEX_COLS, hexSide);
        hexMap.setMinimumMapSize(VIEW_WINDOW_WIDTH,VIEW_WINDOW_HEIGHT);
        hexMap.setupMap();
        
        eventManager = new EventManager(this);
        
        //Assume scenarion ZERO for now
        scenario = new Scenario(0);
        allUnits = scenario.getAllUnits();
        
        Iterator iterator = allUnits.iterator();
        Unit currentUnit;
        int putX = 0;
        
        while (iterator.hasNext())
        {
            currentUnit = (Unit)iterator.next();
            hexMap.addUnit(hexMap.getHexFromCoords(putX,putX), currentUnit);
            putX++;
        }
        
        ogrePanel = ogrPnl;
        ogrePanel.setHexMap(hexMap);
        ogrePanel.setMaster(this);
    }
    
    
    //Give Ogre game awarness of the frame in which it lives
    public void attachComponents(javax.swing.JFrame myframe, javax.swing.JList jlist)
    {
        myFrame = myframe;
        weaponList = jlist;
    }
    
    
    
    //MOVE
    //Accepts a GameEvent ("MOVE") object, determines it validity 
    //and applies the appropriate changes to lists, maps.
    public boolean move(GameEvent e)
    {
        //Basic validations
        if ((e.agent == null) || (e.destination == null) || (e.source == null))
            return false;
        
        //Validation
        //Is this actually a move? Are the source and destinations equal?
        else if ((!e.type.equals("MOVE")) || ((e.source.equals(e.destination))))
            return false;
        
        //TODO: test for unit ownership        
        
        if((e.agent.unitType.equals("INFANTRY")) && (e.destination.isOccupied()))
        {
            //COLLAPSE INFANTRY
            //Consolidate two units into one, provided they are: 1) both infantry; 
            //2) combined defense values equal 3 or less
            if (e.destination.getUnit().unitType.equals("INFANTRY"))
            {
                if ((e.agent.defense + e.destination.getUnit().defense) <= 3)
                {
                    //HOW DO WE "UNDO" THIS?
                    //Create a subclass of the GameEvent?
                    
                    Unit newUnit = new Infantry(e.agent.unitID, (e.agent.defense + e.destination.getUnit().defense));
                    
                    allUnits.remove(e.agent);
                    allUnits.remove(e.destination.getUnit()); 
                    allUnits.add(newUnit);
                    e.source.setOccupingUnit(null);
                    e.destination.setOccupingUnit(newUnit);
                    
                    e.source.deselect();
                    hexMap.selectedHexes.clear();
                    hexMap.adjacentHexes.clear();
                    
                    //hexMap.updateMapImage();
                    
                    return(true);
                    
                }
            }
            
            else
            {
                hexMap.deselectAllSelectedHexes();
                hexMap.adjacentHexes.clear();
                hexMap.updateMapImage();
                return false;
            }    
        }
        
        //both hexes are occupied by non-interactive units. 
        else if (e.destination.isOccupied())
        {
            hexMap.deselectAllSelectedHexes();
            hexMap.adjacentHexes.clear();
            hexMap.updateMapImage();
            return (false);
        }
        
        //OGRE OVERRUN
        //goes here

        //BASIC MOVE
        //From one occupied hex to an unoccupied one. Clean up.
        else if (e.destination.isOccupied() == false)
        {
            hexMap.addUnit(e.destination, e.agent);
            e.source.setOccupingUnit(null);
            e.source.deselect();
            
            Iterator iter = hexMap.selectedHexes.iterator();
            Hex cur;
            while (iter.hasNext())
            {
                cur = (Hex)iter.next();
                cur.deselect();
            }
            hexMap.selectedHexes.clear();
            hexMap.adjacentHexes.clear();
            
            if (e.canUndoThis)
                eventManager.addEvent(e);
            
            return (true);
            
        }
        
        
        
        return (false);
    }
    
    
    //UNDO
    //Passes the a request to "undo" prior moves.
    //As long as the move requested to undo is a "MOVE", then it should be granted
    public void undo()
    {
        eventManager.undo(gamePhase);
    }
    
    
    public int getGamePhase()
    {
        return (gamePhase);
    }
    
    
    //ADVANCE GAME PHASE
    //Allows the UI to advance the game phase
     /*
    STATE   PHASE
    00      Pre-game setup
    
    10      Player 1 SETUP
    11      Player 1 MOVE
    12      Player 1 SHOOT
    13      Player 1 SECOND MOVE
    
    20      Player 2 SETUP
    21      Player 2 MOVE
    22      Player 2 SHOOT
    23      Player 2 SECOND MOVE
    */
    public void advanceGamePhase()
    {
        gamePhase += 1;
        
        switch (gamePhase)
        {
            //Pre-game setup -> P1 Setup
            case 1:
                gamePhase = 10;
                break;
            //P1 stup -> P1 move
            case 11:
                break;
            case 12:
                
            default:
                break;
        }
    }
    
}
