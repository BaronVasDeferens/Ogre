/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ogre;


import java.util.LinkedList;
import java.util.Iterator;
import java.util.Random;
/**
 *
 * @author skot
 */
public abstract class MovementStrategy {
    
    Unit thisUnit;
    Unit primaryTarget = null;
    Hex destination = null;
    
    MovementStrategy(Unit actor, Unit primary, Hex dest) {
        thisUnit = actor;
        primaryTarget = primary;
        destination = dest;
    }
    
    public abstract MoveEvent getMoveEvent(OgreGame gameMAster, GameState gameState);
}    
    

// DIRECT ROUTE
// An AI unit with this movement strategy will tend to plot a direct course (ish) toward its 
// specified target, preferring to always keep moving over remaining still

class DirectRoute extends MovementStrategy {

    DirectRoute(Unit actor, Unit primary, Hex dest) {  super(actor,primary, dest);  } 
    
    public MoveEvent getMoveEvent(OgreGame gameMaster, GameState gameState) {
    
        MoveEvent move = null;
        
        // There are some things we're going to NEED. Fail if they aren't provided.
        if ((gameMaster == null) || (gameState == null) || (thisUnit.disabled == true))
            return null;
        
        if (primaryTarget != null) {
        
            HexMap hexMap = gameMaster.hexMap;
            Hex from = gameMaster.ogrePanel.hexMapRenderer.getHexFromCoords(thisUnit.yLocation, thisUnit.xLocation);
            destination = gameMaster.ogrePanel.hexMapRenderer.getHexFromCoords(primaryTarget.yLocation, primaryTarget.xLocation);
            
            LinkedList<Hex> possibleMoves = new <Hex>LinkedList();
            possibleMoves.clear();
            possibleMoves = gameMaster.hexMap.getHexesWithinRange(
                            from,
                            thisUnit.movement, 
                            false, 
                            (thisUnit.unitType == UnitType.Ogre),
                            gameMaster.hexMap.getOccupiedHexes(gameMaster.passivePlayer));
            
            // We now have a list of all the possible hexes to move to...done't we?
            if (possibleMoves != null) {
                Iterator possibleHexes = possibleMoves.iterator();
                Hex current = null;
                Hex bestHex = possibleMoves.peekFirst();
                Hex targetHex = gameMaster.ogrePanel.hexMapRenderer.getHexFromCoords(primaryTarget.yLocation, primaryTarget.xLocation);
                
                while (possibleHexes.hasNext()) {
                    
                    current = (Hex)possibleHexes.next();
                    
                    if ( (Math.abs(targetHex.getCol() - current.getCol()) + Math.abs(targetHex.getRow() - current.getRow())) < 
                            (Math.abs(targetHex.getCol() - bestHex.getCol()) + Math.abs(targetHex.getRow() - bestHex.getRow()))) {
                        bestHex = current;
                    }
                }
                
                 hexMap.highlightHex(bestHex);
                 move = new MoveEvent("MOVE", thisUnit, from, bestHex, gameMaster.gamePhase, "", false);
                 
            }
            
        }
        
        return move;
        
    }
    
    /*
    public MoveEvent getMoveEvent(OgreGame gameMaster, GameState gameState) {
        

        MoveEvent move = null;
        
        // There are some things we're going to NEED. Fail if they aren't provided.
        if ((gameMaster == null) || (gameState == null) || (thisUnit.disabled == true))
            return null;
        
        if (primaryTarget != null) {
            
            HexMap hexMap = gameMaster.hexMap;
            Hex from = gameMaster.ogrePanel.hexMapRenderer.getHexFromCoords(thisUnit.yLocation, thisUnit.xLocation);
            
            // Determine the distance to target and compute a path
            // Generate a target hex for each of the units movement points; each iteration considers
            // the distance from the prior iterations position; the final version is used as the destination hex
            
            Hex candidateHex = from;
            destination = gameMaster.ogrePanel.hexMapRenderer.getHexFromCoords(primaryTarget.yLocation, primaryTarget.xLocation);
            LinkedList<Hex> adjHexes = new <Hex>LinkedList();
            adjHexes.clear();
            LinkedList<Hex> shortList = new <Hex>LinkedList();
            shortList.clear();
            LinkedList<Hex> beenThere = new <Hex>LinkedList();
            beenThere.clear();
            Random rando = new Random();
            
            for (int i = 0; i < thisUnit.movement; i++) {
                
                // From each candidate hex, obtain the immdeidate adjacent (1) hexes which also:
                // are not craters (false),
                // and if the unit is Ogre or Infantry, ignore ridges,
                // and are not occupied by any enemy units
                adjHexes = 
                        hexMap.getHexesWithinRange(candidateHex,
                        1, 
                        false, 
                        ((thisUnit.unitType == UnitType.Ogre) || (thisUnit.unitType == UnitType.Infantry)),
                        gameMaster.hexMap.getOccupiedHexes(gameMaster.passivePlayer));
               
                
                // Can we reach our destination? If we can move into it, do so
                if ((adjHexes.contains(destination)) && (destination.isOccupied() == false)) {
                    candidateHex = destination;
                    break;
                }
                
                // Are we a ramming Ogre? 
                else if ((adjHexes.contains(destination)) && (destination.isOccupied() == true) && (thisUnit.unitType == UnitType.Ogre)) {
                    gameMaster.reportArea.append("AI Ogre considers ramming...\n");
                    return null;
                }
                
                
                else {
                    // Perform a rough estimate of which way to travel
                    shortList.clear();
                    beenThere.clear();
                    Iterator adjIter = adjHexes.iterator();
                    Hex tmpHex;
                    int adjCol, adjRow;
                    
                    System.out.println("***************************");
                    
                    // Iterate across all the adjacent hexes...
                    while (adjIter.hasNext()) {
                        
                        tmpHex = (Hex)adjIter.next();
                        
                        adjRow = candidateHex.getRow() - destination.getRow();
                        adjCol = candidateHex.getCol() - destination.getCol();
                        
                        //System.out.println("adjCol: " + adjCol + "  adjRow: " + adjRow);
                                                
                        // if the candidateCol - destinationCol is positive, then the unit should consider
                        // moving to an adjacent hex if the adjacent hex's column is less than the candidates
                        if ((adjCol > 0) && (tmpHex.getCol() < candidateHex.getCol())) {
                            if ((tmpHex.isOccupied() == false) && (beenThere.contains(tmpHex) == false)) {
                                shortList.addLast(tmpHex);
                                System.out.println("1 Considering " + tmpHex.getCol() + "," + tmpHex.getRow());
                            }
                        }
                        // likewise, if the cadidateCol - destinationCol  is negative, consider adjacent
                        // hexes with column values greater tahn the candidate hex's
                        else if ((adjCol < 0) && (tmpHex.getCol() >= candidateHex.getCol())) {
                            if ((tmpHex.isOccupied() == false) && (beenThere.contains(tmpHex) == false)) {
                                shortList.addLast(tmpHex);
                                System.out.println("2 Considering " + tmpHex.getCol() + "," + tmpHex.getRow());
                            }
                        }
                        
                        if ((adjRow > 0) && (tmpHex.getRow() < candidateHex.getRow())) {
                            if ((tmpHex.isOccupied() == false) && (beenThere.contains(tmpHex) == false)) {
                                shortList.addLast(tmpHex);
                                System.out.println("3 Considering " + tmpHex.getCol() + "," + tmpHex.getRow());
                            }
                            
                        }
                        
                        else if ((adjRow < 0) && (tmpHex.getRow() >= candidateHex.getRow())) {
                            if ((tmpHex.isOccupied() == false) && (beenThere.contains(tmpHex) == false)) {
                                shortList.addLast(tmpHex);
                                System.out.println("4 Considering " + tmpHex.getCol() + "," + tmpHex.getRow());
                            }
                        }
                           
                    }
                    

                    //shortList.remove(candidateHex);

                    
                    // If there was anything in the short list, choose a random hex and use it for next iteration
                    if (shortList.size() > 0) {
                        
                        candidateHex = shortList.get(rando.nextInt(shortList.size()));
                        gameMaster.ogrePanel.hexMap.highlightedHexes.add(candidateHex);
                        
                        adjHexes.remove(candidateHex);
                        shortList.removeAll(adjHexes);
                        
                        System.out.println(thisUnit.unitName + " (" + i + ") CHOSE " + thisUnit.xLocation + "," + thisUnit.yLocation + " -> " + candidateHex.getCol() + "," + candidateHex.getRow());
                        shortList.clear();
                    }
                    
                    // If all else fails, chose a random hex to move to...
                    else {
                        System.out.println("Choosing a rando from adjacents...");
                        candidateHex = adjHexes.get(rando.nextInt(adjHexes.size()));
                    }
                    
                    adjHexes.clear();
                    
                }
                
                
            }
            
            // If there were no legal moves, return null
            if (candidateHex == null)
                return null;
            
            //MoveEvent(String typ, Unit agt, Hex src, Hex dest, int phase, String msg, boolean undo)
            move = new MoveEvent("MOVE", thisUnit, from, candidateHex, gameMaster.gamePhase, "", false);
            
        }
        
        // Proceed to a specific hex...
        else {
            
        }
        
        return move;

    }
    */
}
