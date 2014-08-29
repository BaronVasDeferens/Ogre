/*
 *  OGRE GAME class
    
 */

package ogre;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.io.Serializable;
/**
 *
 * @author Skot
 */
public class OgreGame implements Serializable
{

    javax.swing.JFrame myFrame;
    java.awt.List weaponList;
    java.awt.Label unitNameLabel, unitStatsLabel, phaseLabel, upperCurrentTargetLabel, currentTargetLabel;
    JTextArea reportArea;
    JButton attackButton;
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
    Unit currentTarget;
    
    Ogre currentOgre = null;
    
    int gamePhase = 10;
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
        hexMap.setMaster(this);
        hexMap.setupMap();
        
        eventManager = new EventManager(this);
        
        //Assume scenarion ZERO (TEST) for now
        playerOne = new Player("Skot");
        playerTwo = new Player("Jordie");
        
        currentPlayer = playerOne;
        
        currentOgre = null;
        targettedOgreWeapon = null;
        
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
    public void attachComponents(javax.swing.JFrame myframe, java.awt.List list, Label label,
            Label statsLabel, Label phaselabel, Label upperTargetLbl, Label currTargetLbl, JButton atkButton, JTextArea repArea)
    {
        myFrame = myframe;
        weaponList = list;
        unitNameLabel = label;
        unitStatsLabel = statsLabel;
        phaseLabel = phaselabel;
        upperCurrentTargetLabel = upperTargetLbl;
        currentTargetLabel = currTargetLbl;
        attackButton = atkButton;
        reportArea = repArea;
        
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
            //reportArea.append("reason 1");
            return false;
        }
        
        //Validation
        //Is this actually a move? Are the source and destinations equal?
        else if ((!e.type.equals("MOVE")) || (e instanceof MoveEvent == false) || ((e.source.equals(e.destination))))
        {
            //reportArea.append("reason 2");
            return false;
        }
        
        //Check for craters
        else if (e.destination.isCrater)
            return false;
        
        //TODO: test for unit ownership        
        
        //COLLAPSE INFANTRY
        if((e.agent.unitType.equals("INFANTRY")) && (e.destination.isOccupied()))
        {
            //Consolidate two units into one, provided they are: 1) both infantry; 
            //2) combined defense values equal 3 or less
            //3) both belong to the operating player
            if ((e.destination.getUnit().unitType.equals("INFANTRY") && (currentPlayer.units.contains(e.destination.occupyingUnit)) && currentPlayer.units.contains(e.source.occupyingUnit)))
            {
                if ((e.agent.defense + e.destination.getUnit().defense) <= 3)
                {
                    //TODO: HOW DO WE "UNDO" THIS?
                    //Create a subclass of the GameEvent?
                    
                    Unit newUnit = new Infantry(e.agent.unitID, (e.agent.defense + e.destination.getUnit().defense));
                    
                    //set newUnit to have "moved"
                    newUnit.hasMoved = true;
                    
                    //remove both "old" units from units lists
                    currentPlayer.units.remove(e.agent);
                    allUnits.remove(e.agent);
                    e.agent = null;
                    e.source.setOccupyingUnit(null);
                    
                    currentPlayer.units.remove(e.destination.occupyingUnit);
                    allUnits.remove(e.destination.occupyingUnit);                   
                    e.destination.setOccupyingUnit(null);
                    
                    
                    allUnits.add(newUnit);
                    currentPlayer.units.add(newUnit);
                    
                    hexMap.addUnit(e.destination, newUnit);
                    //e.destination.setOccupyingUnit(newUnit);

                    //reportArea.append("I think the destination is: " + e.destination.getCol() + "," + e.destination.getRow());
                    //reportArea.append("new unit @ " + hexMap.getHexFromCoords(newUnit.yLocation, newUnit.xLocation).getCol() + "," + hexMap.getHexFromCoords(newUnit.yLocation, newUnit.yLocation).getRow());
                    
                    e.source.deselect();
                    hexMap.deselectAllSelectedHexes();
                    hexMap.adjacentHexes.clear();
                    
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
            e.agent.hasMoved = true;
            
            hexMap.addUnit(e.destination, e.agent);
            e.source.setOccupyingUnit(null);
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
    
    
    //UPDATE CURRENT TARGET
    //Handles the ins and outs of updating the combat readouts
    public void updateCurrentTarget(Unit thisUnit)
    {
        if (thisUnit == null)
        {
            currentTarget = null;
            targettedOgreWeapon = null;
            
            updateOgreWeaponSelectionList(null);
            
            currentTargetLabel.setText("NONE");
            
        }
        
        //An OGRE has been targetted.
        else if (thisUnit.unitType.equals("OGRE"))
        {
            currentTarget = thisUnit;
            Ogre thisOgre = (Ogre)thisUnit;
//            updateOgreWeaponSelectionList(thisOgre);
            
            //Set the default target weapon to the first entry on the list
            if (targettedOgreWeapon == null)
            {
                updateOgreWeaponSelectionList(thisOgre);
                targettedOgreWeapon = thisOgre.getWeaponByID(0);
                weaponList.select(0);
                currentTargetLabel.setText("Enemy Ogre: " + targettedOgreWeapon.weaponName);
            }
            
            else
            {
                currentTargetLabel.setText("Enemy Ogre: " + targettedOgreWeapon.weaponName);
            }
        }
        
        else
        {
            currentTarget = thisUnit;
            targettedOgreWeapon = null;
            currentTargetLabel.setText(thisUnit.unitName);
        }
        
        //ARM THE ATTACK BUTTON?
        //If there is at least two units in the selected hexes list...
        
    }
    
    
    //ATTACK
    //Recall that all targets must be selected from within (revised) weapon radii, so shooters and target should be legal
    //The attack button is lit when a few base criteria are met:
    //currentTarget != null
    //2 or more units in selectedHexes
    //if an ogre is in play, either targettedOgreWeapons or selectedOgreWeapons are non-null values
    public void attack()
    {
        boolean AOK = true;
        int tempStrength = 0;       
        //This is an ugly work-around for attacks on treads. See below
        
        Iterator iter;
        
        //Lets look for some GOTCHAs:
        //NOt enough combatants (shouldn't happen)
        if ((currentTarget == null) || (hexMap.selectedHexes.size() < 2))
        {
            reportArea.append("ERROR (Attack): too few combatants: " + hexMap.selectedHexes.size());
            AOK = false;
        }
        
        //Target is an Ogre, but no specific system is targetted: shouldn't happen
        else if ((currentTarget.unitType.equals("OGRE")) && (targettedOgreWeapon == null))
        {
            reportArea.append("ERROR: (Attack): target is ogre but no weapon selected\n");
            AOK = false;
        }
        
        //Check to see if AP weapons are being used against hard targets
        //if NOT(infantry OR CP) AND Ogre Weapons in play...
        if ((!(currentTarget.unitType.equals("INFANTRY")) || (currentTarget.unitType.equals("CP"))) && (!selectedOgreWeapons.isEmpty()))
        {
            iter = selectedOgreWeapons.iterator();
            Weapon thisWp;
            
            while (iter.hasNext())
            {
                thisWp = (Weapon)iter.next();
                
                if (thisWp.softTargetsOnly == true)
                {
                    reportArea.append("ERROR (Attack): cannot use AP guns against hard targets.\n");
                    reportArea.append(thisWp.weaponName + ":" + thisWp.softTargetsOnly + "\n");
                    AOK = false;
                }
            }
        }
        
        if (AOK)
        {
            //Everythings cool. Let's do some organizing:
            
            //Remove the currentTarget from the selected hexes
            hexMap.selectedHexes.remove(hexMap.getHexFromCoords(currentTarget.yLocation, currentTarget.xLocation));
            
            //Combat tallies
            int defense = 0;
            int strength = 0;
            
            if (currentTarget.unitType.equals("OGRE"))
            {

                //Treads may be attacked by single units only
                //After removing the target unit from selectedHexes, only the attackers should remain...
                if ((targettedOgreWeapon.weaponName.equals("TREADS")))
                {
                    defense = 1;
                    
                    if ((hexMap.selectedHexes.size() > 1))
                    {
                        reportArea.append("ERROR (Attack): multiple units may not attack TREADS.\n");
                        AOK = false;
                    }
                }
                
                else
                    defense = targettedOgreWeapon.defense;
            }
            else
                defense = currentTarget.defense;
            
            
            if (AOK)
            {
                iter = hexMap.selectedHexes.iterator();
                Hex currentHex;
                Unit currentUnit;

                //Total up the total attack strength for the basic units
                while (iter.hasNext())
                {
                    currentHex = (Hex)iter.next();
                    currentUnit = currentHex.occupyingUnit;

                    if (currentUnit.unitType.equals("OGRE") == false)
                        strength += currentUnit.dischargeWeapon();
                }

                //Next, total up any Ogre weapons 
                if (selectedOgreWeapons.isEmpty() == false)
                {
                    Weapon currentWeapon;
                    iter = selectedOgreWeapons.iterator();

                    while(iter.hasNext())
                    {
                        currentWeapon = (Weapon)iter.next();
                        if ((currentWeapon.disabled == false) && (currentWeapon.dischargedThisRound == false))
                            strength += currentWeapon.discharge();
                        else
                        {
                            reportArea.append("ERROR (Attack): previously discharged/disabled weapon detected.\n");
                            AOK = false;
                        }
                    }
                }
            }    

            //Analysis of the values...
            
            //Disallow zero defense (if NOT command post)
            if ((defense <= 0) && (currentTarget.unitType.equals("CP") == false))
            {
                reportArea.append("ERROR (Attack): defense values less than or equal to zero.\n");
                AOK = false;
            }
            
            if (strength <= 0)
            {
                reportArea.append("ERROR (Attack): strength values less than or equal to zero.\n");
                AOK = false;
            }
            
            //Analysis of the ratio...
            
            if (AOK)
            {          
                float ratio = 0;
                
                if (currentTarget.equals("CP"))
                {
                    ratio = strength;
                }
                
                //Attacks against treads resolve at 1:1
                else if (currentTarget.unitType.equals("OGRE"))
                {
                    if (targettedOgreWeapon != null)
                    {
                        if (targettedOgreWeapon.weaponName.equals("TREADS"))
                        {
                            tempStrength = strength;
                            strength = 1;
                            defense = 1;
                        }
                        
                        else
                        {
                            defense = targettedOgreWeapon.defense;
                        }
                    }
                }
                

                    ratio = (float)(strength/defense);
                
                    reportArea.append((strength + ":" + defense + "\n"));
                    
                    //Less than 1:2, NO EFFECT
                    if (ratio < .5)
                    {
                        reportArea.append("Attack FAILS (too weak)!\n");
                        ratio = 0;
                    }
                    
                    //(1:2) Greater than .5 but less than 1 
                    else if ((ratio >= .5) && (ratio < 1))
                    {
                        ratio = (float).5;
                    }
                    
                    else
                    {
                        ratio = (int)ratio;
                    }
                    
                    //reportArea.append("RATIO: " + ratio + "\n");
                
                
                //Obtain a result based on ratio...
                String result = combatResult(ratio);
                
                reportArea.append("RESULT: " + result + "\n");
                
                if (!result.equals("ERR"))
                {
                    //Non-Ogre
                    if (currentTarget.unitType.equals("OGRE") == false)
                    {
                        currentTarget.takeDamage(result);
                        
                        //check for unit death
                        if (currentTarget.isAlive == false)
                        {
                            reportArea.append("killed unit @ " + hexMap.getHexFromCoords(currentTarget.yLocation, currentTarget.xLocation).getCol() + "," + hexMap.getHexFromCoords(currentTarget.yLocation, currentTarget.xLocation).getRow() +"\n");
                            hexMap.deselect(hexMap.getHexFromCoords(currentTarget.yLocation, currentTarget.xLocation));
                            hexMap.getHexFromCoords(currentTarget.yLocation, currentTarget.xLocation).setOccupyingUnit(null);
                            reportArea.append(currentTarget.unitName + " DESTROYED\n");
                        }
                    }
                    
                    //Ogre weapon
                    else
                    {
                       targettedOgreWeapon.takeDamage(result, tempStrength);
                       
                       if (result.equals("X"))
                       {
                           if (targettedOgreWeapon.weaponName.equals("TREADS"))
                           {
                               reportArea.append("Ogre loses " + tempStrength + " treads\n");
                           }
                           
                           else
                               reportArea.append(targettedOgreWeapon.weaponName + " DESTROYED\n");
                       }
                    }
                    
                    hexMap.deselect(hexMap.getHexFromCoords(currentTarget.yLocation, currentTarget.xLocation));
                    hexMap.deselectAllSelectedHexes();
                    hexMap.adjacentHexes.clear();
                    currentTarget = null;
                    selectedOgreWeapons.clear();
                    targettedOgreWeapon = null;
                    updateCurrentTarget(null);
                    
                    attackButton.setEnabled(false);
                    updateUnitReadouts(null);
                    
                    hexMap.updateMapImage();
                    
                }
                
                else
                    reportArea.append("ERROR: bad combat result.\n");
            }
            
            //AOK == false
            else
            {
                hexMap.deselect(hexMap.getHexFromCoords(currentTarget.yLocation, currentTarget.xLocation));
                hexMap.deselectAllSelectedHexes();
                hexMap.adjacentHexes.clear();
                currentTarget = null;
                selectedOgreWeapons.clear();
                targettedOgreWeapon = null;
                updateCurrentTarget(null);

                attackButton.setEnabled(false);
                updateUnitReadouts(null);

                hexMap.updateMapImage();
            }
            
        }
    }
    
    
    public String combatResult(float ratio)
    {
        Random d6 = new Random();
        int dieRoll = d6.nextInt(6) + 1;
        String result = "ERR";

        //less than 
        if (ratio < .5)
            result = "NE";

        //1:2
        else if (ratio < 1)
        {
            switch (dieRoll)
            {
                case 1:
                case 2:
                case 3:
                case 4:
                    result = "NE";
                    break;
                case 5:
                    result = "D";
                    break;
                case 6:
                    result = "X";
                    break;
                default:
                    break;
            }  
        }

        //1:1
        else if (ratio == 1)
        {
            switch (dieRoll)
            {
                case 1:
                case 2:
                    result = "NE";
                    break;
                case 3:
                case 4:
                    result = "D";
                    break;
                case 5:
                case 6:
                    result = "X";
                    break;
                default:
                    break;
            } 
        }

        //2:1
        else if (ratio == 2)
        {
            switch (dieRoll)
            {
                case 1:
                    result = "NE";
                    break;
                case 2:
                case 3:
                    result = "D";
                    break;
                case 4:
                case 5:
                case 6:
                    result = "X";
                    break;
                default:
                    break;
            }
        }

        //3:1
        else if (ratio == 3)
        {
            switch (dieRoll)
            {
                case 1:
                case 2:
                    result = "D";
                    break;
                case 3:
                case 4:
                case 5:
                case 6:
                    result = "X";
                    break;
                default:
                    break;
            }
        }

        //4:1
        else if (ratio == 4)
        {
            switch (dieRoll)
            {
                case 1:
                    result = "D";
                    break;
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    result = "X";
                    break;
                default:
                    break;
            }
        }

        //5:1 or better
        else
        {
            result = "X";
        }

        return (result);
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
                
        updateUnitReadouts(null);
        
        hexMap.deselectAllSelectedHexes();
        hexMap.adjacentHexes.clear();
        hexMap.updateMapImage();
        
        currentTarget = null;
        selectedOgreWeapons.clear();
        targettedOgreWeapon = null;
        currentOgre = null;
        
        switch (gamePhase)
        {
            case 0:
                phaseLabel.setText("Phase: PRE-GAME SETUP");
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
                //Disable the attack readouts
                attackButton.setEnabled(false);
                weaponList.setEnabled(false);
                upperCurrentTargetLabel.setText("");
                currentTargetLabel.setText("");
                break;
                
            //Player 1 SHOOT
            case 12:
                phaseLabel.setText("Phase: SHOOT (" + playerOne.name + ")");
                
                //enable attack readouts
                attackButton.setEnabled(false);
                weaponList.setEnabled(true);
                upperCurrentTargetLabel.setText("Current Target:");
                currentTargetLabel.setText("NONE");
                break;
                
            //player 1 second move
            case 13:
                phaseLabel.setText("Phase: SECOND MOVE (" + playerOne.name + ")");
                
                playerOne.readyForSecondMove();
                
                //Disable the attack readouts
                attackButton.setEnabled(false);
                weaponList.setEnabled(false);
                upperCurrentTargetLabel.setText("");
                currentTargetLabel.setText("");
                break;
            
            //End of playerOne's turn
            //Commit the game state to the server here
            case 14:
                switchCurrentPlayer();
                gamePhase = 21;
            case 21:
                phaseLabel.setText("Phase: MOVE (" + playerTwo.name + ")");
                
                //Disable the attack readouts
                attackButton.setEnabled(false);
                weaponList.setEnabled(false);
                upperCurrentTargetLabel.setText("");
                currentTargetLabel.setText("");
                break;
                
            case 22:
                phaseLabel.setText("Phase: SHOOT (" + playerTwo.name + ")");
                
                //enable attack readouts
                attackButton.setEnabled(false);
                weaponList.setEnabled(true);
                upperCurrentTargetLabel.setText("Current Target:");
                currentTargetLabel.setText("NONE");
                break;
                
            case 23:
                phaseLabel.setText("Phase: SECOND MOVE (" + playerTwo.name + ")");
                
                playerTwo.readyForSecondMove();
                
                //Disable the attack readouts
                attackButton.setEnabled(false);
                weaponList.setEnabled(false);
                upperCurrentTargetLabel.setText("");
                currentTargetLabel.setText("");                
                break;
            
            case 24:
                               
                //Commit the game state to the server here
                switchCurrentPlayer();
                gamePhase = 11;
                phaseLabel.setText("Phase: MOVE (" + playerOne.name + ")");
                
                //Disable the attack readouts
                attackButton.setEnabled(false);
                weaponList.setEnabled(false);
                upperCurrentTargetLabel.setText("");
                currentTargetLabel.setText("");
                break;
                
            default:
                break;
        }
    }
    
    //SWITCH CURRENT PLAYER
    //Derp. Only supports two players. Sad.
    public void switchCurrentPlayer()
    {
        allUnits.removeAll(playerOne.units);
        allUnits.addAll(playerOne.readyForNextTurn());
        
        allUnits.removeAll(playerTwo.units);
        allUnits.addAll(playerTwo.readyForNextTurn());
        
        if (currentPlayer == playerOne)
            currentPlayer = playerTwo;
        else
            currentPlayer = playerOne;
        
        reportArea.append(currentPlayer.name + "'s turn!\n");
        
        hexMap.updateMapImage();
    }
    
    //UPDATE UNIT READOUTS
    //Non-Ogre units: change the labels to reflect unit stats and ownership
    //Ogre units have their weapons populate the weaponList
    public void updateUnitReadouts(Unit thisUnit)
    {
        if (thisUnit != null)
        {
            String readout = "";

            //OGRE routine
            if (thisUnit.unitType.equals("OGRE"))
            {
                Ogre thisOgre = (Ogre)thisUnit;
                currentOgre = thisOgre;
                
                if (currentPlayer.units.contains(thisOgre))
                    readout = currentPlayer.name + "'s ";
                else
                    readout = "Enemy ";
                
                unitNameLabel.setText(readout + thisOgre.unitName);
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
                weaponList.removeAll();
                
                if (currentPlayer.units.contains(thisUnit))
                    readout = currentPlayer.name + "'s " + thisUnit.unitName;
                else
                    readout = "Enemy "+ thisUnit.unitName;
 
                unitNameLabel.setText(readout);
                weaponList.add("Move: " + thisUnit.movement + "/" + thisUnit.movementPostShooting);
                weaponList.add("Defense: " + thisUnit.defense);
                
                if (thisUnit.unitWeapon != null)
                {
                    weaponList.add("Strength: " + thisUnit.unitWeapon.strength);
                    weaponList.add("Range: " + thisUnit.unitWeapon.range);   
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
    //Displays an Ogre's arsenal for COMBAT
    //If the Ogre is friendly, display its arsenal and allow multiple selections to be made.
    //If the ogre is an enemy, only allow a single system to be selected.
    public void updateOgreWeaponSelectionList(Ogre thisOgre)
    {
        if (thisOgre == null)
        {
            currentOgre = null;
            weaponList.removeAll();
            unitStatsLabel.setText("");
        }
        
        else
        {
            currentOgre = thisOgre;

            Iterator weaps;
            String thisWeapon;

            weaponList.removeAll();
            unitStatsLabel.setText("");

            //FRIENDLY OGRE: allow multiple selection
            if (currentPlayer.units.contains(thisOgre))
            {
                unitNameLabel.setText(currentPlayer.name + "'s " + thisOgre.unitName);
                unitStatsLabel.setText("Select weapon(s) to FIRE");
                weaponList.setMultipleMode(true);
            }

            //ENEMY OGRE: disallow multi-selection
            else
            {
                unitNameLabel.setText("Enemy OGRE");
                unitStatsLabel.setText("Select one weapon to TARGET");
                weaponList.setMultipleMode(false);

                if (targettedOgreWeapon != null)
                {
                    weaponList.select(targettedOgreWeapon.weaponID - 1);
                }
            }

            LinkedList<String> weapons = thisOgre.getEnumeratedSystemsList();
            weaps = weapons.iterator();
            while (weaps.hasNext())
            {
                thisWeapon = (String)weaps.next();
                weaponList.add(thisWeapon);
            }

            if (targettedOgreWeapon != null)
                weaponList.select(targettedOgreWeapon.weaponID - 1);
        }
    }
}
