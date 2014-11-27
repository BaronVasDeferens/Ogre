/*
 *  OGRE GAME class
    
 */

package ogre;

import java.util.*;
import java.awt.*;
import javax.swing.*;
/**
 *
 * @author Skot
 */
public class OgreGame
{
    //Version info:
    String version = "1.0.0.3";

    /*
    VERSION:
    1.0.0.1:    (11-07-14) fixed bug in MyGamesPanel where games were mis-lableded
                upon Registration, sets username in gameFrame title
    1.0.0.2:    (11-11-14) actually fixed the MyGames bug
                fixed bug with Refresh games button, which resulted in mis-labelling
    1.0.0.3     (11-11-14) tweaked output to mail script in ServerThreadHandler
                Replaced a HeavyTank with a MissileTank
    */
    
    //Network resources
    String server = "167.114.68.235";
    //String server = "127.0.1.1";
    int port = 12321;
    
    
    javax.swing.JFrame myFrame;
    java.awt.List weaponList;
    java.awt.Label unitNameLabel, unitStatLabel, phaseLabel, upperCurrentTargetLabel, currentTargetLabel, ratioLabel;
    JTextArea reportArea;
    JButton attackButton, undoButton, advancePhaseButton;
    OgrePanel ogrePanel;
        
    GameState currentGameState = null;
    
    LoginObject activePlayerCredentials = null;
    
    GameStateUploadManager uploader;
    
    HexMap hexMap;
    public int hexSide = 64;
    public final int HEX_ROWS = 21;
    public final int HEX_COLS = 15;
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
    
    int gameRound = 1;
    
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
        ogrePanel = ogrPnl;
        loadGameState(null); 
    }
    
    
    //Give Ogre game awarness of the frame in which it lives
    public void attachComponents(javax.swing.JFrame myframe, java.awt.List list, Label label,
            Label statsLabel, Label phaselabel, Label upperTargetLbl, Label currTargetLbl,
            JButton atkButton, JTextArea repArea, Label ratLabel, JButton undoBtn, JButton advPhase)
    {
        myFrame = myframe;
        weaponList = list;
        unitNameLabel = label;
        unitStatLabel = statsLabel;
        phaseLabel = phaselabel;
        upperCurrentTargetLabel = upperTargetLbl;
        currentTargetLabel = currTargetLbl;
        attackButton = atkButton;
        reportArea = repArea;
        ratioLabel = ratLabel;
        undoButton = undoBtn;
        advancePhaseButton = advPhase;
        
        myFrame.setTitle("OGRE" + version);
        ratioLabel.setText("");
        
        //Disable certain functions until a gameState has been loaded
        attackButton.setEnabled(false);
        advancePhaseButton.setEnabled(false);
        undoButton.setEnabled(false);
        
        
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
        if((e.agent.unitType == UnitType.Infantry) && (e.destination.isOccupied()))
        {
            //Consolidate two units into one, provided they are: 1) both infantry; 
            //2) combined defense values equal 3 or less
            //3) both belong to the operating player
            if ((e.destination.getUnit().unitType == UnitType.Infantry) && (currentPlayer.units.contains(e.destination.occupyingUnit)) && (currentPlayer.units.contains(e.source.occupyingUnit)))
            {
                if ((e.agent.defense + e.destination.getUnit().defense) <= 3)
                {
                    //TODO: HOW DO WE "UNDO" THIS?
                    //Create a subclass of the GameEvent?
                    
                    Unit newUnit = new Infantry(e.agent.defense + e.destination.getUnit().defense);
                    
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
                ogrePanel.hexMapRenderer.updateMapImage();
                
                return false;
            }    
        }
        
        //both hexes are occupied by non-interactive units. 
        else if (e.destination.isOccupied())
        {
            hexMap.deselectAllSelectedHexes();
            hexMap.adjacentHexes.clear();
            ogrePanel.hexMapRenderer.updateMapImage();        
            
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
            ratioLabel.setText("");
        }
        
        //An OGRE has been targetted.
        else if (thisUnit.unitType == UnitType.Ogre)
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
                currentTargetLabel.setText("Enemy " + thisOgre.unitName + ": " + targettedOgreWeapon.weaponName);
            }
            
            else
            {
                currentTargetLabel.setText("Enemy " + thisOgre.unitName + ": " + targettedOgreWeapon.weaponName);
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
        
        updateRatioLabel();
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
        int tempStrength = 0;              //This is an ugly work-around for attacks on treads. See below
        
        //attackingUnits and attackingWeapons are required for eventManager logging
        LinkedList<Unit> attackingUnits = new LinkedList();
        LinkedList<Weapon> attackingWeapons = new LinkedList();
        attackingUnits.clear();
        attackingWeapons.clear();
        
        String resultText = "";
        
        Iterator iter;
        
        //Lets look for some GOTCHAs:
        //NOt enough combatants (shouldn't happen)
        if ((currentTarget == null) || (hexMap.selectedHexes.size() < 2))
        {
            reportArea.append("ERROR (Attack): too few combatants: " + hexMap.selectedHexes.size());
            AOK = false;
        }
        
        //Target is an Ogre, but no specific system is targetted: shouldn't happen
        else if ((currentTarget.unitType == UnitType.Ogre) && (targettedOgreWeapon == null))
        {
            reportArea.append("ERROR: (Attack): no Ogre sub-system is targetted\n");
            AOK = false;
        }
        
        //Check to see if AP weapons are being used against hard targets
        //Infantry and CommandPosts are considered "soft targets" and are vulnerale to AP attack
        if (((currentTarget.unitType != UnitType.Infantry) && (currentTarget.unitType != UnitType.CommandPost)) && (!selectedOgreWeapons.isEmpty()))
        {
            iter = selectedOgreWeapons.iterator();
            Weapon thisWp;
            
            while (iter.hasNext())
            {
                thisWp = (Weapon)iter.next();
                
                if (thisWp.softTargetsOnly == true)
                {
                    reportArea.append("AP guns are only effective against Infantry and Command Post units.\n");
                    //reportArea.append(thisWp.weaponName + ":" + thisWp.softTargetsOnly + "\n");
                    AOK = false;
                }
            }
        }
        
        if (AOK)
        {
            //Everythings cool. Let's do some organizing:
            
            //Remove the currentTarget from the selected hexes
            hexMap.selectedHexes.remove(ogrePanel.hexMapRenderer.getHexFromCoords(currentTarget.yLocation, currentTarget.xLocation));
            
            //Combat tallies
            int defense = 0;
            int strength = 0;
            
            if (currentTarget.unitType == UnitType.Ogre)
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
                
                else if (targettedOgreWeapon.isDisabled())
                {
                    AOK = false;
                    reportArea.append("ERROR: Targetted weapon is already INOP.\n");
                }
                
                else
                    defense = targettedOgreWeapon.defense;
            }
            
            else //non-Ogre target
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

                    if (currentUnit.unitType != UnitType.Ogre)
                    {
                        strength += currentUnit.dischargeWeapon();
                        attackingUnits.add(currentUnit);
                    }
                    else
                        attackingUnits.add(currentUnit);
                        
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
                        {
                            strength += currentWeapon.discharge();
                            attackingWeapons.add(currentWeapon);
                        }
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
            if ((defense <= 0) && (currentTarget.unitType != UnitType.CommandPost))
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
                
                if (currentTarget.unitType == UnitType.CommandPost)
                {
                    ratio = strength;
                }
                
                //Attacks against treads resolve at 1:1
                else if (currentTarget.unitType == UnitType.Ogre)
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
                

                    ratio = (float)strength/defense;
                
                    reportArea.append((strength + ":" + defense + "\n"));
                    
                    //Less than 1:2, NO EFFECT
                    if (ratio < .5f)
                    {
                        reportArea.append("Attack FAILS (too weak)!\n");
                        ratio = 0;
                    }
                    
                    //(1:2) Greater than .5 but less than 1 
                    else if ((ratio >= .5f) && (ratio < 1))
                    {
                        ratio = (float).5;
                    }
                    
                    else
                    {
                        ratio = (int)ratio;
                    }
                    
                    //reportArea.append("RATIO: " + ratio + "\n");
                
                
                //Obtain a result based on ratio
                String result = combatResult(ratio);
                
                reportArea.append("RESULT: " + result + "\n");
                
                if (!result.equals("ERR"))
                {
                    //Non-Ogre Unit: 
                    if (currentTarget.unitType != UnitType.Ogre)
                    {
                        resultText = currentTarget.takeDamage(result);
                        reportArea.append(resultText + "\n");
                        
                        //check for unit death
                        if (currentTarget.isAlive == false)
                        {
                            hexMap.deselect(ogrePanel.hexMapRenderer.getHexFromCoords(currentTarget.yLocation, currentTarget.xLocation));
                            ogrePanel.hexMapRenderer.getHexFromCoords(currentTarget.yLocation, currentTarget.xLocation).setOccupyingUnit(null);
                        }
                        
                        //Reload the damaged unit's image
                        else if (currentTarget.unitType.equals(UnitType.Infantry))
                        {
                            ogrePanel.hexMapRenderer.updateMapImage();
                        }
                        
                    }
                    
                    //Ogre weapon
                    else
                    {
                       resultText = targettedOgreWeapon.takeDamage(result, tempStrength);
                       reportArea.append(resultText + "\n");
                    }
                    
                    //Log the event into the EventQueue
                    ///(Player atkr, Unit dfndr, Weapon dfndWeap, LinkedList<Unit> atckUnits, LinkedList<Weapon> slctdWeapons, int phase, String msg, String rslt)
                    ///(Player atkr, Unit dfndr, Weapon dfndWeap, LinkedList<Unit> atckUnits, LinkedList<Weapon> slctdWeapons, int phase, String msg, String rslt)
                    eventManager.addEvent(new AttackEvent(currentPlayer, currentTarget, targettedOgreWeapon, attackingUnits, attackingWeapons, gamePhase, resultText, result));
 
                }
                
                else
                    reportArea.append("ERROR: bad combat result. Bad! \n");
            }
             
        }
        
        //End-of-battle cleanup, or AOK == false

        hexMap.deselect(ogrePanel.hexMapRenderer.getHexFromCoords(currentTarget.yLocation, currentTarget.xLocation));
        hexMap.deselectAllSelectedHexes();
        hexMap.adjacentHexes.clear();
        currentTarget = null;
        selectedOgreWeapons.clear();
        targettedOgreWeapon = null;
        updateCurrentTarget(null);

        attackButton.setEnabled(false);
        updateUnitReadouts(null);

        ogrePanel.hexMapRenderer.updateMapImage();
        
    }
    
    
    public String combatResult(float ratio)
    {
        Random d6 = new Random();
        int dieRoll = d6.nextInt(6) + 1;
        String result = "ERR";

        //less than 
        if (ratio < .5f)
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
        else if (ratio >= 5)
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
        if (currentGameState != null)
        {
            gamePhase += 1;

            updateUnitReadouts(null);

            hexMap.deselectAllSelectedHexes();
            hexMap.adjacentHexes.clear();
            ogrePanel.hexMapRenderer.updateMapImage();

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
                case 10:
                    gamePhase = 11;
                case 11:
                    reportArea.append("Round " + gameRound);
                    reportArea.append(": " + currentPlayer.name + "'s turn\n");

                    phaseLabel.setText("Phase: MOVE (" + currentPlayer.name + ")");

                    undoButton.setEnabled(true);

                    //Disable the attack readouts
                    attackButton.setEnabled(false);
                    weaponList.setEnabled(false);
                    upperCurrentTargetLabel.setText("");
                    currentTargetLabel.setText("");
                    ratioLabel.setText("");
                    break;

                //Player 1 SHOOT
                case 12:
                    phaseLabel.setText("Phase: SHOOT (" + currentPlayer.name + ")");

                    undoButton.setEnabled(false);

                    //enable attack readouts
                    attackButton.setEnabled(false);
                    weaponList.setEnabled(true);
                    upperCurrentTargetLabel.setText("Current Target:");
                    currentTargetLabel.setText("NONE");
                    break;

                //player 1 second move
                case 13:
                    phaseLabel.setText("Phase: 2nd MOVE (" + currentPlayer.name + ")");

                    playerOne.readyForSecondMove();

                    undoButton.setEnabled(true);

                    //Disable the attack readouts
                    attackButton.setEnabled(false);
                    weaponList.setEnabled(false);
                    upperCurrentTargetLabel.setText("");
                    currentTargetLabel.setText("");
                    ratioLabel.setText("");
                    break;

                //End of turn
                //Commit the game state to the server here
                case 14:
                    //Append a Turn End GameState....
                    //GameEvent(String tp, int phase, String msg, boolean undo)
                    String msg = ">> " + currentGameState.currentPlayer.name + " ENDS TURN <<<";
                    GameEvent marker = new GameEvent("ENDTURN",currentGameState.turnNumber, msg, false);
                    currentGameState.eventQueue.add(marker);
                    
                    switchCurrentPlayer();
                    gameRound++;
                    msg = ">>> " + currentPlayer.name + " BEGIN TURN " + gameRound + " <<<";
                    marker = new GameEvent("BEGINTURN",currentGameState.turnNumber, msg, false);
                    currentGameState.eventQueue.add(marker);
                    
                    //Upload changes to gameState
                    //TOD: find a way to guarantee that moves made will be committed automatically
                    uploader = new GameStateUploadManager(server, port, activePlayerCredentials);
                    currentGameState.gamePhase = 10;
                    currentGameState.turnNumber = gameRound;
                    currentGameState.isOpen = false;
                    
                    uploader.uploadGameState(currentGameState);

                    
                    phaseLabel.setText("No game loaded");
                    undoButton.setEnabled(false);
                    advancePhaseButton.setEnabled(false);
                    
                    //Display end of turn message
                    JOptionPane.showMessageDialog(myFrame, "Your turn is over.",
            "END OF TURN", JOptionPane.WARNING_MESSAGE);
                    
                    //Clear the board
                    loadGameState(null);
                    ogrePanel.gameOver = true;
                    
                    break;
                    /*
                    gamePhase = 21;
                case 20:
                case 21:
                    phaseLabel.setText("Phase: MOVE (" + playerTwo.name + ")");

                    //Disable the attack readouts
                    attackButton.setEnabled(false);
                    weaponList.setEnabled(false);
                    upperCurrentTargetLabel.setText("");
                    currentTargetLabel.setText("");
                    ratioLabel.setText("");
                    break;

                case 22:
                    phaseLabel.setText("Phase: SHOOT (" + playerTwo.name + ")");

                    undoButton.setEnabled(false);

                    //enable attack readouts
                    attackButton.setEnabled(false);
                    weaponList.setEnabled(true);
                    upperCurrentTargetLabel.setText("Current Target:");
                    currentTargetLabel.setText("NONE");
                    break;

                case 23:
                    phaseLabel.setText("Phase: 2nd MOVE (" + playerTwo.name + ")");

                    playerTwo.readyForSecondMove();

                    undoButton.setEnabled(true);

                    //Disable the attack readouts
                    attackButton.setEnabled(false);
                    weaponList.setEnabled(false);
                    upperCurrentTargetLabel.setText("");
                    currentTargetLabel.setText(""); 
                    ratioLabel.setText("");
                    break;

                case 24:
                    gameRound++;               

                    //Commit the game state to the server here

                    switchCurrentPlayer();

                    gamePhase = 11;

                    uploader = new GameStateUploadManager(server, port, activePlayerCredentials);
                    uploader.uploadGameState(currentGameState);

                    phaseLabel.setText("Phase: MOVE (" + playerOne.name + ")");

                    undoButton.setEnabled(true);

                    //Disable the attack readouts
                    attackButton.setEnabled(false);
                    weaponList.setEnabled(false);
                    upperCurrentTargetLabel.setText("");
                    currentTargetLabel.setText("");
                    ratioLabel.setText("");
                    break;
                */            
                default:
                    break;
            }
        }    
    }
    
    //SWITCH CURRENT PLAYER
    //Currently nly supports two players. Sad.
    public void switchCurrentPlayer()
    {
        allUnits.removeAll(playerOne.units);
        allUnits.addAll(playerOne.readyForNextTurn());
        
        allUnits.removeAll(playerTwo.units);
        allUnits.addAll(playerTwo.readyForNextTurn());
        
        if (currentPlayer == playerOne)
        {
            currentPlayer = playerTwo;
            currentGameState.currentPlayer = playerTwo;
        }    
        else
        {
            currentPlayer = playerOne;
            currentGameState.currentPlayer = playerOne;
        }    
        
        //reportArea.append("Round " + gameRound);
        //reportArea.append(": " + currentPlayer.name + "'s turn\n");
        
        ogrePanel.hexMapRenderer.updateMapImage();
    }
    
    //UPDATE UNIT READOUTS
    //Called (mostly) during NON-COMBAT
    //Non-Ogre units: change the labels to reflect unit stats and ownership
    //Ogre units have their weapons populate the weaponList
    public void updateUnitReadouts(Unit thisUnit)
    {
        if (thisUnit != null)
        {
            String readout = "";

            //OGRE routine
            if (thisUnit.unitType == UnitType.Ogre)
            {
                Ogre thisOgre = (Ogre)thisUnit;
                currentOgre = thisOgre;
                
                if (currentPlayer.units.contains(thisOgre))
                    readout = currentPlayer.name + "'s ";
                else
                    readout = "Enemy ";
                
                unitNameLabel.setText(readout + thisOgre.unitName);
                unitStatLabel.setText("Move: " + thisOgre.getCurrentMovement());
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
            unitStatLabel.setText("");
            
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
            unitStatLabel.setText("");
        }
        
        else
        {
            currentOgre = thisOgre;

            Iterator weaps;
            String thisWeapon;

            weaponList.removeAll();
            unitStatLabel.setText("");

            //FRIENDLY OGRE: allow multiple selection
            if (currentPlayer.units.contains(thisOgre))
            {
                unitNameLabel.setText(currentPlayer.name + "'s " + thisOgre.unitName);
                unitStatLabel.setText("Select weapon(s) to FIRE");
                weaponList.setMultipleMode(true);
            }

            //ENEMY OGRE: disallow multi-selection
            else
            {
                unitNameLabel.setText("Enemy OGRE");
                unitStatLabel.setText("Select one weapon to TARGET");
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
    
    
    //UPDATE RATIO LABEL
    //Updates the ratio label (strength:defense) when the game situation changes (attackers/defender changed)
    public void updateRatioLabel()
    {
        int str = 0, def = 0;
        float ratio;
        String output = "";
        
        str = getCurrentStrength();
        def = getCurrentDefense();
        
        if ((currentTarget != null) && (targettedOgreWeapon != null))
        {
            if (targettedOgreWeapon.weaponName.equals("TREADS"))
            {
                str = 1;
                def = 1;
            }  
        }

        output = str + ":" + def;

        if (def >= 1)
        {
            ratio = (float)str/def;
            
            //Less than 1:2, NO EFFECT
            if (ratio < .5f)
            {
                output = output.concat(" (NOGO)");
            }

            //(1:2) Greater than .5 but less than 1 
            else if ((ratio >= .5f ) && (ratio < 1))
            {
                output = output.concat(" (1:2)");
            }

            else
            {
                output = output.concat(" (" + (int)ratio + ":1)");
            }
        }    

        ratioLabel.setText(output);
    }
    
    
    //Returns the total strength of all currently selected weapons
    public int getCurrentStrength()
    {
        int totalStrength = 0;

        Iterator iter = hexMap.selectedHexes.iterator();
        Hex hex;
        Unit thisUnit;
        
        while (iter.hasNext())
        {
            hex = (Hex)iter.next();
            thisUnit = hex.occupyingUnit;
            
            //If the unit belongs to the current (attacking) player, add it's strength to the total
            if (currentPlayer.units.contains(thisUnit))
            {
                //handle friendly ogre weapons
                if ((thisUnit.unitType == UnitType.Ogre) && (selectedOgreWeapons.isEmpty() == false))
                {
                     Iterator iter2 = selectedOgreWeapons.iterator();
                     Weapon thisWeapon;
                     
                     while (iter2.hasNext())
                     {
                         thisWeapon = (Weapon)iter2.next();
                         
                         if ((thisWeapon.dischargedThisRound == false) && (thisWeapon.disabled == false))
                         {
                             totalStrength += thisWeapon.strength;
                         }
                     }
                }
                
                //handle friendly basic units
                else if (thisUnit.unitWeapon != null)
                {
                    totalStrength += thisUnit.unitWeapon.strength;
                }
            }
        }
            
        return totalStrength;
    }
    
    //GET CURRENT DEFENSE
    //Returns the defense value of the current target
    public int getCurrentDefense()
    {
        if (currentTarget == null)
        {
            return 0;
        }    
        else if (targettedOgreWeapon != null)
        {
            return (targettedOgreWeapon.defense);
        }
        else
        {
            return (currentTarget.defense);
        } 
    }
    
    
    //LOGIN
    public void login()
    {
        LoginManager loginManager = new LoginManager(server, port, this, activePlayerCredentials);  
        loginManager.displayUI();
    }
    
    //REGISTER
    public void register()
    {
        RegistrationManager registerManager = new RegistrationManager(server, port, this, activePlayerCredentials);  
    }
    
    //DISPLAY MY GAMES
    //Allows the logged-in user to view and manage her in-progress games
    public void displayMyGames()
    {
        //check for user login creds
        if (activePlayerCredentials != null)
        {
            MyGamesFrame myGames = new MyGamesFrame(this,activePlayerCredentials);
            myGames.setVisible(true);
        }
        
//        else
//            System.out.println("creds are null");
    }
    
    
    //CREATE NEW GAME
    public void createNewGame()
    {
        //Check: if the current game state isn't null, there is a game in progress
        if (currentGameState != null)
        {
            JOptionPane.showMessageDialog(myFrame, "There is a game in progress.",
            "GAME IN PROGRESS", JOptionPane.WARNING_MESSAGE);
        }
        
        //Check to see if the user is logged in
        else if (activePlayerCredentials != null)
        {
            NewGameManager newGameManager = new NewGameManager(activePlayerCredentials, this);
        }
        
        else
        {
            JOptionPane.showMessageDialog(myFrame, "You must log in first!",
            "ERROR", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    
    public boolean setCurrentGameState(GameState setState)
    {
        boolean success = false;
        
        if (setState == null)
            return false;
        
        //Prior gamestate in progress
        else if (currentGameState != null)
        {
            JOptionPane.showMessageDialog(myFrame, "GAME IN PROGRESS",
            "There is already a game in progress!", JOptionPane.WARNING_MESSAGE);
        }
        
        //Reset everything to reelct the new gameState
        else
        {
            success = (loadGameState(setState));
        }
        
        return (success);
    }
    
    //LOAD GAME STATE
    //
    private boolean loadGameState(GameState loadState)
    {
        boolean success = false;
        
        // Generate a journal of events
        String gameEventJournal;
        
        
        //The initial load state is null. Fresh start.
        if (loadState == null)
        {
            hexMap = null;
            eventManager = null;
            playerOne = null;
            playerTwo = null;
            currentPlayer = null;
            selectedOgreWeapons = null;
            currentOgre = null;
            allUnits = null;
            
            currentGameState = null;
            
            if (ogrePanel != null)
            {
                ogrePanel.setEnabled(false);
            }
        }
        
        else
        {
            //Begin loading prior gamestate info
            
            currentGameState = loadState;          
            
            eventManager = new EventManager(this);
            if (loadState.eventQueue == null)
                loadState.eventQueue = new EventList();
            
            eventManager.eventQueue = loadState.eventQueue;
            
            //Scan across the Event List and create a list of all moves made

            gameEventJournal = "*** " + loadState.playerOne.name + " vs " + loadState.playerTwo.name + " ***\n";
            gameEventJournal += "#" + loadState.idNumber + "\n";
  
            Iterator iter = eventManager.eventQueue.iterator();
            GameEvent aGameEvent;
            
            while (iter.hasNext())
            {
                aGameEvent = (GameEvent)iter.next();
                gameEventJournal += aGameEvent.message + "\n";
            }
            
            hexMap = null;
            hexMap = loadState.hexMap;

            playerOne = loadState.playerOne;
            playerTwo = loadState.playerTwo;
            
            currentPlayer = loadState.currentPlayer;

            currentOgre = null;
            targettedOgreWeapon = null;

            scenario = loadState.scenario;

            selectedOgreWeapons = new LinkedList();
            selectedOgreWeapons.clear();

            allUnits = loadState.scenario.allUnits;

            ogrePanel.setHexMap(hexMap);
            ogrePanel.setMaster(this);
            
            gameRound = loadState.turnNumber;
            gamePhase = loadState.gamePhase;
            
            loadState.isOpen = true;
                       
            advanceGamePhase();
            
            advancePhaseButton.setEnabled(true);
            undoButton.setEnabled(true);
            
            ogrePanel.setEnabled(true);
            ogrePanel.gameOver = false;
            
            success = true;
            
            // Display the game log
            EventJournalFrame journal = new EventJournalFrame();
            journal.textArea.setText(gameEventJournal);
            journal.setTitle(loadState.playerOne.name + " vs " + loadState.playerTwo.name + " (#" + loadState.idNumber + ")\n");
            journal.setVisible(true);

        }
        
        return (success);
    }
}
