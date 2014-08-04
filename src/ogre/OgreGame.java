/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ogre;

import java.util.*;
import java.awt.*;
/**
 *
 * @author Skot
 */
public class OgreGame {

    javax.swing.JFrame myFrame;
    java.awt.List weaponList;
    java.awt.Label unitNameLabel, unitStatsLabel, phaseLabel;
    OgrePanel ogrePanel;
        
    HexMap hexMap;
    public int hexSide = 64;
    public final int HEX_ROWS = 15;
    public final int HEX_COLS = 21;
    public final int VIEW_WINDOW_WIDTH = 800;
    public final int VIEW_WINDOW_HEIGHT = 600;
    

    Player playerOne, playerTwo, currentPlayer;
    
    boolean gameOver = false;
    
    EventManager eventManager;
    Scenario scenario;
    LinkedList<Unit> allUnits;
    
    int gamePhase = -1;
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
        
        //Assume scenarion ZERO (TEST) for now
        playerOne = new Player("Skot");
        playerTwo = new Player("Kyle");
        
        currentPlayer = playerOne;
        
        scenario = new Scenario(0, playerOne, playerTwo);

        allUnits = new LinkedList();
        allUnits.clear();
        allUnits.addAll(playerOne.units);
        allUnits.addAll(playerTwo.units);
        
        Iterator iterator = allUnits.iterator();
        Unit currentUnit;
        int putX = 0;
        
        //DUMB PLACEMENT ROUTINE :: FOR TESTING ONLY
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
    public void attachComponents(javax.swing.JFrame myframe, java.awt.List list, java.awt.Label label,
            java.awt.Label statsLabel, java.awt.Label phaselabel)
    {
        myFrame = myframe;
        weaponList = list;
        unitNameLabel = label;
        unitStatsLabel = statsLabel;
        phaseLabel = phaselabel;
        
        myFrame.setTitle("OGRE");
        
        advanceGamePhase();
        
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
                    currentPlayer.units.add(newUnit);
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
    
    1       Player 1 SETUP
    2       Player 2 SETUP
    
    11      Player 1 MOVE
    12      Player 1 SHOOT
    13      Player 1 SECOND MOVE
    

    21      Player 2 MOVE
    22      Player 2 SHOOT
    23      Player 2 SECOND MOVE
    */
    public void advanceGamePhase()
    {
        gamePhase += 1;
        
        switch (gamePhase)
        {
            case 0:
                phaseLabel.setText("Phase: PREGAME SETUP");
                break;
            //Pre-game setup -> P1 Setup
            case 1:
                phaseLabel.setText("Phase: SETUP P1");
                break;
            case 2:
                phaseLabel.setText("Phase: SETUP P2");
                break;
            case 3:
                gamePhase = 11;
            //Player 1 MOVE
            case 11:
                phaseLabel.setText("Phase: MOVE P1");
                break;
            //Player 1 SHOOT
            case 12:
                phaseLabel.setText("Phase: SHOOT P1");
                break;
            //player 1 second move
            case 13:
                phaseLabel.setText("Phase: SECOND MOVE P1");
                break;
            case 14:
            case 20:
                gamePhase = 21;
            case 21:
                phaseLabel.setText("Phase: MOVE P2");
                break;
            case 22:
                phaseLabel.setText("Phase: SHOOT P2");
                break;
            case 23:
                phaseLabel.setText("Phase: SECOND MOVE P2");
                break;
            case 24:
                gamePhase = 11;
                phaseLabel.setText("Phase: MOVE P1");
                break;
            default:
                break;
        }
    }
    
    public void updateUnitReadouts(Unit thisUnit)
    {
        if (thisUnit != null)
        {
            String readout;

            //OGRE routine
            if (thisUnit.unitType.equals("OGRE"))
            {
                Ogre thisOgre = (Ogre)thisUnit;

                unitNameLabel.setText(thisOgre.unitName);
                unitStatsLabel.setText("Move: " + thisOgre.getCurrentMovement());
                weaponList.removeAll();

                java.util.Iterator iter = thisOgre.getWeaponReadoutStrings().iterator();
                String thisWeapon;

                int index = 0;

                while (iter.hasNext())
                {
                    thisWeapon = (String)iter.next();
                    weaponList.add(thisWeapon,index);
                    index++;
                }
            }
            
            else
            {
                readout = thisUnit.unitName;
                unitNameLabel.setText(readout);
                readout =  "Move: " + thisUnit.movement + "  Defense: " + thisUnit.defense;
                unitStatsLabel.setText(readout);
                
                readout = "Strength: " + thisUnit.unitWeapon.strength + "  Range: " + thisUnit.unitWeapon.range;
                weaponList.add(readout);
            }
        
        }
        
        else
        {
            weaponList.removeAll();
            unitNameLabel.setText("No Unit Selected");
            unitStatsLabel.setText("");
            
        }
    }
    
}
