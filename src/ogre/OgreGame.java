/*
 *  OGRE GAME class
    
 */

package ogre;

import java.util.*;
import java.awt.*;
import java.io.Serializable;
/**
 *
 * @author Skot
 */
public class OgreGame implements Serializable
{

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
    LinkedList<Weapon> selectedOgreWeapons;     //tracks which Ogre weapons are selected for FIRING
    Weapon targettedOgreWeapon = null;          //which Ogre weapon has been selected for destruction
    
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
    //TODO: put the ogrePanel in attachComponents function below instead 
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

        selectedOgreWeapons = new LinkedList();
        selectedOgreWeapons.clear();
        
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
            hexMap.addUnit(hexMap.getHexFromCoords(putX+3,putX+3), currentUnit);
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
    public boolean move(MoveEvent e)
    {
        //Basic validations
        if (e == null)
            return false;
        
        else if ((e.agent == null) || (e.destination == null) || (e.source == null))
        {
            System.out.println("reason 1");
            return false;
        }
        //Validation
        //Is this actually a move? Are the source and destinations equal?
        else if ((!e.type.equals("MOVE")) || (e instanceof MoveEvent == false) || ((e.source.equals(e.destination))))
        {
            System.out.println("reason 2");
            return false;
        }
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
    
    //SHOOT
    public void attack(Player attacker, Player defender, LinkedList<Unit> selectedUnits, LinkedList<Weapon> selectedWeapons)
    {
        
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
        
        hexMap.deselectAllSelectedHexes();
        hexMap.adjacentHexes.clear();
        hexMap.updateMapImage();
        
        switch (gamePhase)
        {
            case 0:
                phaseLabel.setText("Phase: PREGAME SETUP");
                break;
            //Pre-game setup -> P1 Setup
            case 1:
                phaseLabel.setText("Phase: SETUP (" + playerOne.name + ")");
                break;
            case 2:
                phaseLabel.setText("Phase: SETUP (" + playerTwo.name + ")");
                break;
            case 3:
                gamePhase = 11;
            //Player 1 MOVE
            case 11:
                phaseLabel.setText("Phase: MOVE (" + playerOne.name + ")");
                break;
            //Player 1 SHOOT
            case 12:
                phaseLabel.setText("Phase: SHOOT (" + playerOne.name + ")");
                break;
            //player 1 second move
            case 13:
                phaseLabel.setText("Phase: SECOND MOVE (" + playerOne.name + ")");
                
                //Commit the game state to the server here
                switchCurrentPlayer();
                break;
            case 14:
            case 20:
                gamePhase = 21;
            case 21:
                phaseLabel.setText("Phase: MOVE (" + playerTwo.name + ")");
                break;
            case 22:
                phaseLabel.setText("Phase: SHOOT (" + playerTwo.name + ")");
                break;
            case 23:
                phaseLabel.setText("Phase: SECOND MOVE (" + playerTwo.name + ")");
                
                //Commit the game state to the server here
                switchCurrentPlayer();
                break;
            case 24:
                gamePhase = 11;
                phaseLabel.setText("Phase: MOVE (" + playerOne.name + ")");
                break;
            default:
                break;
        }
    }
    
    //SWITCH CURRENT PLAYER
    //Derp. Only supports two players. Sad.
    public void switchCurrentPlayer()
    {
        if (currentPlayer == playerOne)
            currentPlayer = playerTwo;
        else
            currentPlayer = playerOne;
    }
    
    public void updateUnitReadouts(Unit thisUnit)
    {
        if (thisUnit != null)
        {
            String readout = "";

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
            //All other non-Ogre units
            else
            {
                readout = thisUnit.unitName;
                unitNameLabel.setText(readout);
                readout =  "Move: " + thisUnit.movement + "  Defense: " + thisUnit.defense;
                unitStatsLabel.setText(readout);
                
                if (thisUnit.unitWeapon != null)
                {
                    readout = "Strength: " + thisUnit.unitWeapon.strength + "  Range: " + thisUnit.unitWeapon.range;
                    unitStatsLabel.setText(unitStatsLabel.getText() + " " + readout);
                }
            }
        
        }
        
        else
        {
            weaponList.removeAll();
            unitNameLabel.setText("No Unit Selected");
            unitStatsLabel.setText("");
            
        }
    }
    
    //UPDATE OGRE WEAPON SELECTION LIST
    //Displays an Ogre's arsenal. 
    //If the Ogre is friendly, display its arsenal and allow multiple selections to be made.
    //If the ogre is an enemy, only allow a single system to be selected.
    public void updateOgreWeaponSelectionList(Ogre thisOgre)
    {
        Iterator weaps;
        Weapon thisWeapon;
        //int pos = 0;
        
        weaponList.removeAll();
        unitStatsLabel.setText("");
        
        //FRIENDLY OGRE: allow multiple selection
        if (currentPlayer.units.contains(thisOgre))
        {
            unitNameLabel.setText(currentPlayer.name + "'s OGRE");
            unitStatsLabel.setText("Select a weapon(s) to USE");
            weaponList.setMultipleMode(true);
            
            weaps = thisOgre.mainBattery.iterator();
            while (weaps.hasNext())
            {
                thisWeapon = (Weapon)weaps.next();
                weaponList.add(thisWeapon.weaponName + "  Strength: " + thisWeapon.strength + "  Range: " + thisWeapon.range + " Defense: " + thisWeapon.defense);
            }
            
            weaps = thisOgre.secondaryBattery.iterator();
            while (weaps.hasNext())
            {
                thisWeapon = (Weapon)weaps.next();
                weaponList.add(thisWeapon.weaponName + "  Strength: " + thisWeapon.strength + "  Range: " + thisWeapon.range + " Defense: " + thisWeapon.defense);
            }
            
            weaps = thisOgre.missileBattery.iterator();
            while (weaps.hasNext())
            {
                thisWeapon = (Weapon)weaps.next();
                weaponList.add(thisWeapon.weaponName + "  Strength: " + thisWeapon.strength + "  Range: " + thisWeapon.range + " Defense: " + thisWeapon.defense);
            }
   
        }
        
        //ENEMY OGRE: disallow multi-selection
        else
        {
            unitNameLabel.setText("Enemy OGRE");
            unitStatsLabel.setText("Select one weapon to TARGET");
            weaponList.setMultipleMode(false);
            
            weaps = thisOgre.mainBattery.iterator();
            while (weaps.hasNext())
            {
                thisWeapon = (Weapon)weaps.next();
                weaponList.add(thisWeapon.weaponName + "  Strength: " + thisWeapon.strength + "  Range: " + thisWeapon.range + " Defense: " + thisWeapon.defense);
            }
            
            weaps = thisOgre.secondaryBattery.iterator();
            while (weaps.hasNext())
            {
                thisWeapon = (Weapon)weaps.next();
                weaponList.add(thisWeapon.weaponName + " Strength: " + thisWeapon.strength + "  Range: " + thisWeapon.range + " Defense: " + thisWeapon.defense);
            }
            
            weaps = thisOgre.missileBattery.iterator();
            while (weaps.hasNext())
            {
                thisWeapon = (Weapon)weaps.next();
                weaponList.add(thisWeapon.weaponName + " Strength: " + thisWeapon.strength + "  Range: " + thisWeapon.range + " Defense: " + thisWeapon.defense);
            }
            
            weaponList.add("Treads: " + thisOgre.treads + "/" + thisOgre.maxTreads);
            
        }
        
    }
    
}
